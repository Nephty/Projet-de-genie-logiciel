package com.example.demo.service;

import com.example.demo.exception.throwables.ConflictException;
import com.example.demo.exception.throwables.ResourceNotFound;
import com.example.demo.model.*;
import com.example.demo.model.CompositePK.AccountAccessPK;
import com.example.demo.repository.AccountAccessRepo;
import com.example.demo.repository.AccountRepo;
import com.example.demo.repository.UserRepo;
import com.example.demo.request.AccountAccessReq;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountAccessServiceTest {

    @Mock
    private AccountAccessRepo accessRepo;
    @Mock
    private UserRepo userRepo;
    @Mock
    private AccountRepo accountRepo;

    private AccountAccessService underTest;

    @BeforeEach
    void setUp() {
        underTest = new AccountAccessService(accessRepo,userRepo,accountRepo);
    }

    @Test
    void canCreateAccountAccess() {
        //given
        AccountAccessReq accessReq = new AccountAccessReq(
                "accountId",
                "userId",
                true,
                false
        );

        Account tmpAccount = new Account();
        tmpAccount.setIban(accessReq.getAccountId());
        tmpAccount.setDeleted(false);
        Optional<Account> account = Optional.of(tmpAccount);
        when(accountRepo.findById(accessReq.getAccountId()))
                .thenReturn(account);

        User tmpUser = new User();
        tmpUser.setUserId(accessReq.getUserId());
        Optional<User> user = Optional.of(tmpUser);
        when(userRepo.findById(accessReq.getUserId()))
                .thenReturn(user);

        //When
        underTest.createAccountAccess(accessReq);

        //then
        ArgumentCaptor<AccountAccess> userArgumentCaptor = ArgumentCaptor.forClass(AccountAccess.class);
        verify(accessRepo).save(userArgumentCaptor.capture());
        AccountAccess captorValue = userArgumentCaptor.getValue();

        // If it saves the good accountAccess
        assertEquals(tmpAccount,captorValue.getAccountId());
        assertEquals(tmpUser,captorValue.getUserId());
        assertEquals(accessReq.getAccess(),captorValue.getAccess());
        assertEquals(accessReq.getHidden(),captorValue.getHidden());
    }

    @Test
    void createShouldThrowWhenAccountIdNotFound(){
        //given
        AccountAccessReq accessReq = new AccountAccessReq(
                "accountId",
                "userId",
                true,
                false
        );

        //then
        assertThatThrownBy(()->underTest.createAccountAccess(accessReq))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining(accessReq.getAccountId());

        verify(accessRepo,never()).save(any());
    }

    @Test
    void createShouldThrowWhenUserIdNotFound(){
        //given
        AccountAccessReq accessReq = new AccountAccessReq(
                "accountId",
                "userId",
                true,
                false
        );

        Account tmpAccount = new Account();
        tmpAccount.setIban(accessReq.getAccountId());
        tmpAccount.setDeleted(false);
        Optional<Account> account = Optional.of(tmpAccount);
        when(accountRepo.findById(accessReq.getAccountId()))
                .thenReturn(account);

        //then
        assertThatThrownBy(()->underTest.createAccountAccess(accessReq))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining(accessReq.getUserId());

        verify(accessRepo,never()).save(any());
    }

    @Test
    void createShouldThrowWhenAccountAccessAlreadyExists(){
        //given
        AccountAccessReq accessReq = new AccountAccessReq(
                "accountId",
                "userId",
                true,
                false
        );

        when(accessRepo.existsById(any())).thenReturn(true);

        //Then
        assertThatThrownBy(() -> underTest.createAccountAccess(accessReq))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("account already exist "+accessReq);
    }

    @Test
    void createShouldThrowWhenAccountIsDeleted(){
        // Given
        AccountAccessReq accessReq = new AccountAccessReq(
                "accountId",
                "userId",
                true,
                false
        );

        Account tmpAccount = new Account();
        tmpAccount.setIban(accessReq.getAccountId());
        tmpAccount.setDeleted(true);
        Optional<Account> account = Optional.of(tmpAccount);
        when(accountRepo.findById(accessReq.getAccountId()))
                .thenReturn(account);

        //then
        assertThatThrownBy(()->underTest.createAccountAccess(accessReq))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Can't add access to a deleted account: "+accessReq);

        verify(accessRepo,never()).save(any());
    }

    @Test
    void canChangeAccountAccess() {
        //given
        AccountAccessReq accessReq = new AccountAccessReq(
                "accountId",
                "userId",
                true,
                false
        );

        Account tmpAccount = new Account();
        tmpAccount.setIban(accessReq.getAccountId());

        User tmpUser = new User();
        tmpUser.setUserId(accessReq.getUserId());

        AccountAccess access = new AccountAccess(
                tmpAccount,
                tmpUser,
                false,
                true
        );

        when(accessRepo.findById(new AccountAccessPK(accessReq.getAccountId(),accessReq.getUserId())))
                .thenReturn(Optional.of(access));

        //When
        underTest.changeAccountAccess(accessReq);

        //then
        ArgumentCaptor<AccountAccess> argumentCaptor = ArgumentCaptor.forClass(AccountAccess.class);
        verify(accessRepo).save(argumentCaptor.capture());
        AccountAccess captorValue = argumentCaptor.getValue();

        // If it saves the good accountAccess
        assertEquals(tmpAccount,captorValue.getAccountId());
        assertEquals(tmpUser,captorValue.getUserId());
        assertEquals(accessReq.getAccess(),captorValue.getAccess());
        assertEquals(accessReq.getHidden(),captorValue.getHidden());
    }

    @Test
    void changeShouldThrowWhenAccountAccessNotFound(){
        //given
        AccountAccessReq accessReq = new AccountAccessReq(
                "accountId",
                "userId",
                true,
                false
        );

        //then
        assertThatThrownBy(()->underTest.changeAccountAccess(accessReq))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessageContaining(accessReq.toString());

        verify(accessRepo,never()).save(any());
    }

    @Test
    void canDeleteAccountAccess() {
        //Given
        String accountId = "accountId";
        String userId = "userId";

        //When
        underTest.deleteAccountAccess(accountId,userId);

        //Then
        verify(accessRepo).deleteAccountAccessByAccountIdAndUserId(accountId,userId);
    }

    @Test
    void canFindAccountAccess() {
        //Given
        String accountId = "accountId";
        String userId = "userId";

        // We have to instantiate all of these objects because of the constructor of AccountAccessReq.

        //-- USER
        User user = new User();
        user.setUserId(userId);

        // -- BANK
        Bank bank = new Bank();
        bank.setDefaultCurrencyType(new CurrencyType());

        // -- ACCOUNT
        Account acc = new Account();
        acc.setIban(accountId);
        acc.setSwift(bank);
        acc.setAccountTypeId(new AccountType());
        acc.setUserId(user);

        AccountAccessPK accessPK = new AccountAccessPK(accountId,userId);

        AccountAccess returnedAccess = new AccountAccess();
        returnedAccess.setAccountId(acc);
        returnedAccess.setUserId(user);

        when(accessRepo.findById(accessPK)).thenReturn(Optional.of(returnedAccess));

        //when
        underTest.findAccountAccess(accountId,userId);

        //then
        verify(accessRepo).findById(accessPK);
    }

    @Test
    void findShouldThrowWhenIdNotFound() {
        //Given
        String accountId = "accountId";
        String userId = "userId";

        assertThatThrownBy(() -> underTest.findAccountAccess(accountId,userId))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessageContaining(accountId + " : " + userId);
    }

    @Test
    void canGetAccountAccessByUserId() {
        //Given
        String userId = "userId";
        User tmpUser = new User();
        tmpUser.setUserId(userId);
        Optional<User> user = Optional.of(tmpUser);
        //when
        when(userRepo.findById(userId))
                .thenReturn(user);

        underTest.getAccountAccessByUserId(userId);

        //then
        verify(accessRepo).findAllByUserId(tmpUser);
        //We don't have to test if the method returns the good list because it's already tested in the repositories.
    }

    @Test
    void getByUserIdShouldThrowWhenUserIdNotFound(){
        //Given
        String userId = "notAnUserId";

        //Then
        assertThatThrownBy(() -> underTest.getAccountAccessByUserId(userId))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessageContaining("No user with such id: " + userId);
    }

    @Test
    void canGetAccessToDeletedAccount(){
        //Given
        String userId = "userId";
        User tmpUser = new User();
        tmpUser.setUserId(userId);
        Optional<User> user = Optional.of(tmpUser);
        //when
        when(userRepo.findById(userId))
                .thenReturn(user);

        underTest.getAccessToDeletedAccount(userId);

        //then
        verify(accessRepo).findAllDeletedAccountByUserId(tmpUser);
    }

    @Test
    void getAccessToDeletedAccountShouldThrowWhenUserNotFound(){
        //Given
        String userId = "notAnUserId";

        //Then
        assertThatThrownBy(() -> underTest.getAccessToDeletedAccount(userId))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessageContaining("No user with such id: " + userId);
    }

    @Test
    void canGetAccessToHiddenAccount(){
        //Given
        String userId = "userId";
        User tmpUser = new User();
        tmpUser.setUserId(userId);
        Optional<User> user = Optional.of(tmpUser);
        //when
        when(userRepo.findById(userId))
                .thenReturn(user);

        underTest.getAccessToHiddenAccount(userId);

        //then
        verify(accessRepo).findAllHiddenByUserId(tmpUser);
    }

    @Test
    void getAccessToHiddenAccountShouldThrowWhenUserNotFound(){
        //Given
        String userId = "notAnUserId";

        //Then
        assertThatThrownBy(() -> underTest.getAccessToHiddenAccount(userId))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessageContaining("No user with such id: " + userId);
    }


    
    @Test
    void canGetAllOwners(){
        // Given
        String accountId = "accountId";

        Account acc = new Account();
        acc.setIban(accountId);

        //-- USER 1
        User user = new User();
        user.setUserId("userID");

        // -- USER 2
        User user2 = new User();
        user2.setUserId("user2Id");

        when(accountRepo.findById(accountId)).thenReturn(Optional.of(acc));

        ArrayList<User> shouldReturn = new ArrayList<>();
        shouldReturn.add(user);
        shouldReturn.add(user2);

        when(accessRepo.getAllOwners(acc)).thenReturn(shouldReturn);

        // When
        List<User> result = underTest.findAllOwners(accountId);

        // Then
        assertEquals(2,result.size());

    }

    @Test
    void canGetAllOwnersShouldThrowWhenAccountNotFound(){
        //Given
        String accountId = "accountId";

        // Then
        assertThatThrownBy(()->underTest.findAllOwners(accountId))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessageContaining("No account with such id: "+accountId);
    }
}