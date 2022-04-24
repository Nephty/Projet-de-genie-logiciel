package com.example.demo.service;

import com.example.demo.controller.TransactionController;
import com.example.demo.exception.throwables.AuthorizationException;
import com.example.demo.exception.throwables.ConflictException;
import com.example.demo.exception.throwables.ResourceNotFound;
import com.example.demo.model.AccountAccess;
import com.example.demo.model.CompositePK.AccountAccessPK;
import com.example.demo.model.CompositePK.SubAccountPK;
import com.example.demo.model.SubAccount;
import com.example.demo.model.TransactionLog;
import com.example.demo.model.TransactionType;
import com.example.demo.other.Sender;
import com.example.demo.repository.AccountAccessRepo;
import com.example.demo.repository.SubAccountRepo;
import com.example.demo.repository.TransactionLogRepo;
import com.example.demo.repository.TransactionTypeRepo;
import com.example.demo.request.NotificationReq;
import com.example.demo.request.TransactionReq;
import com.example.demo.security.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Links the {@link TransactionController} with the {@link TransactionLogRepo}.
 * In this class, all the modifications and the calls to the {@link TransactionLogRepo} are made.
 *
 * @see TransactionLog
 */
@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class TransactionLogService {

    private final TransactionLogRepo transactionLogRepo;
    private final SubAccountRepo subAccountRepo;
    private final TransactionTypeRepo transactionTypeRepo;
    private final AccountAccessRepo accountAccessRepo;
    private final NotificationService notificationService;

    /**
     * This method instantiate the transactions from the req body.
     * Each transaction is divided in 2 objects : one for the sender and one for the receiver.
     *
     * @param sender         The Sender of the Notification
     * @param transactionReq The request body to create the transactions {@link TransactionReq#isPostValid()}
     * @return An ArrayList with the 2 created transactions
     * @throws ConflictException if the FK provided by the client are inconsistent with the DB
     */
    public ArrayList<TransactionLog> addTransaction(Sender sender, TransactionReq transactionReq) {
        ArrayList<TransactionLog> transactions = instantiateTransaction(sender, transactionReq);
        ArrayList<TransactionLog> saved = new ArrayList<>();
        transactions.forEach(transaction -> saved.add(transactionLogRepo.save(transaction)));
        return saved;
    }

    /**
     * This method both retrieves and format the transaction from a subAccount
     * in the DB the transaction are in pairs, one ascending and one descending this method regroups the pairs into one
     * entity
     *
     * @param iban       subAccount iban
     * @param currencyId subAccount currency
     * @return An array of all the transaction made and received by the subAccount
     */
    public List<TransactionReq> getAllTransactionBySubAccount(String iban, Integer currencyId) {
        SubAccount subAccount = subAccountRepo.findById(new SubAccountPK(iban, currencyId))
                .orElseThrow(() -> new ResourceNotFound(iban + " : " + currencyId.toString()));
        ArrayList<TransactionLog> transactionLogs = transactionLogRepo.findAllLinkedToSubAccount(subAccount);
        if (transactionLogs.size() % 2 != 0) {
            //Shouldn't be happening but if it happens we have to manage it correctly.
            ArrayList<TransactionLog> badFormatTransaction = transactionLogRepo.findBadFormatTransaction();
            for (TransactionLog t : badFormatTransaction) {
                transactionLogRepo.deleteAllByTransactionId(t.getTransactionId());
                sendBadFormatTransaction(t);
            }
        }
        ArrayList<TransactionReq> response = new ArrayList<>();
        // mapping the ugliness from the DB to a nicer response
        transactionLogs.stream()
                .filter(transactionLog -> !transactionLog.getIsSender())
                .forEach(transactionReceived -> transactionLogs.stream()
                        .filter(TransactionLog::getIsSender)
                        .forEach(transactionSent -> {
                            if (transactionSent.getTransactionId().intValue()
                                    == transactionReceived.getTransactionId().intValue()) {
                                response.add(new TransactionReq(transactionSent, transactionReceived));
                            }
                        }));
        return response;
    }


    /**
     * Creates a transaction entity and raise an error if the request is incorrect
     *
     * @param transactionReq request made by the client
     * @return Transaction entity based on the client's request
     * @throws ConflictException if the FK provided by the client are inconsistent with the DB
     */
    private ArrayList<TransactionLog> instantiateTransaction(
            Sender sender,
            TransactionReq transactionReq
    ) throws ConflictException {
        // Can't make transaction to ourselves.
        if (transactionReq.getSenderIban().equals(transactionReq.getRecipientIban())) {
            log.warn("transaction to = transaction from");
            throw new AuthorizationException("You can't make a transaction to the same account you emitted it from");
        }
        TransactionLog transactionSent = new TransactionLog(transactionReq);
        TransactionLog transactionReceived = new TransactionLog(transactionReq);


        // -- SubAccount SENDER --
        SubAccount subAccountSender = subAccountRepo.findById(
                new SubAccountPK(transactionReq.getSenderIban(), transactionReq.getCurrencyId())
        ).orElseThrow(
                () -> new ConflictException(
                        "subAccount: " + transactionReq.getSenderIban() + " : " + transactionReq.getCurrencyId()
                )
        );

        if (subAccountSender.getIban().getDeleted()) {
            throw new AuthorizationException("Can't make a transaction from a deleted Account: "
                    + subAccountSender.getIban());
        }

        // -- SubAccount RECEIVER --
        SubAccount subAccountReceiver = subAccountRepo.findById(
                new SubAccountPK(transactionReq.getRecipientIban(), transactionReq.getCurrencyId())
        ).orElseThrow(
                () -> new ConflictException(
                        "subAccount: " + transactionReq.getRecipientIban() + " : " + transactionReq.getCurrencyId()
                )
        );

        if (subAccountReceiver.getIban().getDeleted()) {
            throw new AuthorizationException("Can't make a transaction to a deleted Account");
        }

        // -- TransactionType --
        TransactionType transactionType = transactionTypeRepo.findById(transactionReq.getTransactionTypeId())
                .orElseThrow(
                        () -> new ConflictException("transId:" + transactionReq.getTransactionTypeId().toString())
                );


        // -- SENDER --
        transactionSent.setSubAccount(subAccountSender);
        transactionSent.setTransactionTypeId(transactionType);
        transactionSent.setIsSender(true);

        // -- RECEIVER --
        transactionReceived.setSubAccount(subAccountReceiver);
        transactionReceived.setTransactionTypeId(transactionType);
        transactionReceived.setIsSender(false);

        // -- CAN INSTANTIATE ? --
        canInstantiateTransaction(sender, transactionSent);

        // -- ID GENERATION --
        Integer nextId = nextId();
        transactionSent.setTransactionId(nextId);
        transactionReceived.setTransactionId(nextId);

        if (transactionSent.getTransactionTypeId().getTransactionTypeId() == 2) {
            executeTransaction(transactionSent, transactionReceived);
            transactionSent.setProcessed(true);
            transactionReceived.setProcessed(true);
        }
        ArrayList<TransactionLog> transactionLogs = new ArrayList<>();
        transactionLogs.add(transactionSent);
        transactionLogs.add(transactionReceived);

        return transactionLogs;
    }

    /**
     * Checks whether a client has the authority and money to perform a transaction
     * @param sender id of the client
     * @param transactionSent transaction to be performed
     * @throws AuthorizationException if the client is not authorized to perform the request
     */
    private void canInstantiateTransaction(Sender sender, TransactionLog transactionSent)
            throws AuthorizationException {
        if (!transactionSent.getSubAccount().getIban().getPayment()) {
            throw new AuthorizationException("This account can't make payment");
        }
        if (transactionSent.getTransactionAmount() <= 0) {
            throw new AuthorizationException("Can't make transaction lower or equal to 0");
        }
        if (transactionSent.getSubAccount().getCurrentBalance() <
                transactionSent.getTransactionAmount() + transactionSent.getFee()) {
            throw new AuthorizationException("You don't have enough money");
        }
        if (!transactionSent.getSubAccount().getIban().getUserId().getUserId().equals(sender.getId())) {
            AccountAccess accountAccess = accountAccessRepo.findById(
                    new AccountAccessPK(transactionSent.getSubAccount().getIban().getIban(), sender.getId())
            ).orElseThrow(() -> {
                log.warn("User doesn't have access to this account");
                throw new AuthorizationException("You don't have access to this account");
            });
            if (!accountAccess.getAccess()) {
                log.warn("User access to this account is disabled");
                throw new AuthorizationException("Your access to this account is disabled");
            }
        }
    }

    /**
     * Execute the transaction from the subAccount of the sender to the SubAccount of the Receiver.
     * If the transaction couldn't be executed, it throws an exception.
     *
     * @param transactionSent    Sender transaction (Direction = 1)
     * @param transactionReceive Receiver transaction (Direction = 0)
     */
    public void executeTransaction(TransactionLog transactionSent, TransactionLog transactionReceive) {
        if (assertCanMakeTransaction(transactionSent, transactionReceive)) {
            // -- SEND --
            transactionSent.getSubAccount().setCurrentBalance(
                    transactionSent.getSubAccount().getCurrentBalance() -
                            (transactionSent.getTransactionAmount() + transactionSent.getFee())
            );
            transactionSent.setProcessed(true);

            // -- RECEIVE --
            transactionReceive.getSubAccount().setCurrentBalance(
                    transactionReceive.getSubAccount().getCurrentBalance() + transactionSent.getTransactionAmount()
            );
            transactionReceive.setProcessed(true);

            // -- SubAccount SAVE --
            subAccountRepo.save(transactionSent.getSubAccount());
            subAccountRepo.save(transactionReceive.getSubAccount());
            // -- Transaction SAVE --
            transactionLogRepo.save(transactionSent);
            transactionLogRepo.save(transactionReceive);
        }
    }

    /**
     * Test if the transaction can be done.
     * If not, it sends a notification to the owner of the account with the reason why the transaction couldn't be done.
     * It also deletes the notification from the DB.
     *
     * @param transactionSent The transaction that needs to be tested (direction = 1)
     * @return true if the transaction can be executed, false otherwise.
     */
    private boolean assertCanMakeTransaction(TransactionLog transactionSent, TransactionLog transactionReceive) {
        if (!transactionSent.getSubAccount().getIban().getPayment()) {
            transactionLogRepo.deleteAllByTransactionId(transactionSent.getTransactionId());
            sendDeletedNotification(
                    transactionSent,
                    formatReason(transactionSent, transactionReceive, "Your account can't make payment")
            );
            log.warn("Account {} can't make payment", transactionSent.getSubAccount().getIban().getIban());
            return false;
        } else if (transactionSent.getSubAccount().getCurrentBalance() <
                transactionSent.getTransactionAmount() + transactionSent.getFee()) {
            transactionLogRepo.deleteAllByTransactionId(transactionSent.getTransactionId());
            String reason = "Not enough fund : \n" +
                    "You had " + transactionSent.getSubAccount().getCurrentBalance() + " left on your account " +
                    "and the amount of the transaction was " + transactionSent.getTransactionAmount();
            sendDeletedNotification(transactionSent, formatReason(transactionSent, transactionReceive, reason));
            return false;
        } else if (transactionSent.getSubAccount().getIban().getDeleted()
                || transactionReceive.getSubAccount().getIban().getDeleted()) {
            transactionLogRepo.deleteAllByTransactionId(transactionSent.getTransactionId());
            sendDeletedNotification(
                    transactionSent,
                    formatReason(transactionSent, transactionReceive,
                            "Can't make a transaction to/from a deleted account")
            );
            return false;
        }
        return true;
    }

    /**
     * Send a notification to the owner of the account that sent the notification.
     * It warns the user that his transaction couldn't be executed.
     *
     * @param transactionSent The transaction that couldn't be executed.
     * @param reason          The reason why the transaction couldn't be executed.
     */
    public void sendDeletedNotification(TransactionLog transactionSent, String reason) {
        Sender bankSender = new Sender(
                transactionSent.getSubAccount().getIban().getSwift().getSwift(),
                Role.BANK
        );

        NotificationReq notification = new NotificationReq();
        notification.setNotificationType(5);
        notification.setComments(reason);
        notification.setIsFlagged(true);
        notification.setRecipientId(transactionSent.getSubAccount().getIban().getUserId().getUserId());

        notificationService.addNotification(bankSender, notification);
    }

    private String formatReason(TransactionLog send, TransactionLog receive, String reason) {
        String res = "Transaction couldn't be executed ";
        res += "From : " + send.getSubAccount().getIban().getIban();
        res += " To : " + receive.getSubAccount().getIban().getUserId().getFullName()
                + " (" + receive.getSubAccount().getIban().getIban() + ")\n";
        res += "Date : " + send.getTransactionDate();
        res += "Amount : " + send.getTransactionAmount();
        res += " reason : " + reason;
        return res;
    }

    /**
     * Find the maximum transactionId in the database and return the next value.
     *
     * @return the next possible transactionIf
     */
    private int nextId() {
        Integer tmp = transactionLogRepo.findMaximumId();
        return tmp == null ? 1 : tmp + 1;
    }

    /**
     * Send a notification to the owner of the transaction
     * if there is a problem with his transaction in the db.
     * The Sender of the notification is the Bank were the account sit.
     *
     * @param transaction The transaction that was bad formatted in the DB.
     */
    public void sendBadFormatTransaction(TransactionLog transaction) {
        String reason = "An error occur with your transaction : ";
        reason += "We've lost the ";
        reason += transaction.getIsSender() ? "Receiver.\n" : "Sender.\n";
        reason += "Account : " + transaction.getSubAccount().getIban().getIban();
        reason += " Date : " + transaction.getTransactionDate();
        reason += " Amount :" + transaction.getTransactionAmount();


        Sender bankSender = new Sender(
                transaction.getSubAccount().getIban().getSwift().getSwift(),
                Role.BANK
        );

        NotificationReq notification = new NotificationReq();
        notification.setNotificationType(5);
        notification.setComments(reason);
        notification.setIsFlagged(true);
        notification.setRecipientId(transaction.getSubAccount().getIban().getUserId().getUserId());

        notificationService.addNotification(bankSender, notification);
    }
}