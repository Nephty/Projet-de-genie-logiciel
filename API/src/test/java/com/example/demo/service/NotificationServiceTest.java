package com.example.demo.service;

import com.example.demo.exception.throwables.ConflictException;
import com.example.demo.exception.throwables.ResourceNotFound;
import com.example.demo.model.Bank;
import com.example.demo.model.Notification;
import com.example.demo.model.NotificationType;
import com.example.demo.model.User;
import com.example.demo.other.Sender;
import com.example.demo.repository.BankRepo;
import com.example.demo.repository.NotificationRepo;
import com.example.demo.repository.NotificationTypeRepo;
import com.example.demo.repository.UserRepo;
import com.example.demo.request.NotificationReq;
import com.example.demo.security.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepo notificationRepo;
    @Mock
    private NotificationTypeRepo notificationTypeRepo;
    @Mock
    private UserRepo userRepo;
    @Mock
    private BankRepo bankRepo;

    private NotificationService underTest;

    @BeforeEach
    void setUp() {
        underTest = new NotificationService(
                notificationRepo,
                notificationTypeRepo,
                userRepo,
                bankRepo
        );
    }

    @Test
    void canAddNotification() {
        //Given
        NotificationReq notificationReq = new NotificationReq();
        notificationReq.setNotificationType(1);
        notificationReq.setComments("testComments");
        notificationReq.setIsFlagged(false);
        notificationReq.setRecipientId("testIban");

        Sender sender = new Sender("testId", Role.USER);

        // -- NotificationType
        NotificationType notificationType = new NotificationType(
                1,
                "test"
        );
        when(notificationTypeRepo.findById(notificationReq.getNotificationType()))
                .thenReturn(Optional.of(notificationType));

        // -- Bank
        Bank bank = new Bank();
        when(bankRepo.findById(notificationReq.getRecipientId()))
                .thenReturn(Optional.of(bank));

        // --User
        User user = new User();
        when(userRepo.findById(sender.getId()))
                .thenReturn(Optional.of(user));

        //When
        underTest.addNotification(sender,notificationReq);

        //Then
        ArgumentCaptor<Notification> userArgumentCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepo).save(userArgumentCaptor.capture());
        Notification captorValue = userArgumentCaptor.getValue();

        assertEquals(notificationType,captorValue.getNotificationType());
        assertEquals(bank,captorValue.getBankId());
        assertEquals(user,captorValue.getUserId());
        assertEquals(notificationReq.getIsFlagged(),captorValue.getIsFlagged());
        assertEquals(notificationReq.getComments(),captorValue.getComments());
    }

    @Test
    void addShouldThrowWhenNotificationTypeNotFound(){
        //Given
        NotificationReq notificationReq = new NotificationReq();
        notificationReq.setNotificationType(1);
        notificationReq.setComments("testComments");
        notificationReq.setIsFlagged(false);
        notificationReq.setRecipientId("testIban");

        Sender sender = new Sender("testId", Role.USER);

        //then
        assertThatThrownBy(() -> underTest.addNotification(sender,notificationReq))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining(notificationReq.getNotificationType().toString());
    }

    @Test
    void addShouldThrowWhenBankNotFound(){
        //Given
        NotificationReq notificationReq = new NotificationReq();
        notificationReq.setNotificationType(1);
        notificationReq.setComments("testComments");
        notificationReq.setIsFlagged(false);
        notificationReq.setRecipientId("testIban");

        Sender sender = new Sender("testId", Role.USER);

        // -- NotificationType
        NotificationType notificationType = new NotificationType(
                1,
                "test"
        );
        when(notificationTypeRepo.findById(notificationReq.getNotificationType()))
                .thenReturn(Optional.of(notificationType));

        //then
        assertThatThrownBy(() -> underTest.addNotification(sender,notificationReq))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("no bank with such id: "+notificationReq.getRecipientId());
    }

    @Test
    void addShouldThrowWhenUserNotFound(){
        //Given
        NotificationReq notificationReq = new NotificationReq();
        notificationReq.setNotificationType(1);
        notificationReq.setComments("testComments");
        notificationReq.setIsFlagged(false);
        notificationReq.setRecipientId("testIban");

        Sender sender = new Sender("testId", Role.USER);

        // -- NotificationType
        NotificationType notificationType = new NotificationType(
                1,
                "test"
        );
        when(notificationTypeRepo.findById(notificationReq.getNotificationType()))
                .thenReturn(Optional.of(notificationType));

        // -- Bank
        Bank bank = new Bank();
        when(bankRepo.findById(notificationReq.getRecipientId()))
                .thenReturn(Optional.of(bank));

        //Then
        assertThatThrownBy(()->underTest.addNotification(sender,notificationReq))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("no user with such id: " + sender.getId());
    }

    @Test
    void canDeleteNotification() {
        //Given
        Integer notificationId = 0;
        //when
        underTest.deleteNotification(notificationId);
        //Then
        verify(notificationRepo).deleteById(notificationId);
    }

    @Test
    void canGetUserNotification() {
        //Given
        User user = new User();
        user.setUserId("id");
        when(userRepo.findById("id")).thenReturn(Optional.of(user));

        Sender sender = new Sender(user.getUserId(), Role.USER);

        //When
        underTest.getNotifications(sender);

        //Then
        verify(notificationRepo).findAllByUserId(user);
    }

    @Test
    void getUserNotificationShouldThrowWhenUserNotFound(){
        //Given
        String id = "id";
        Role role = Role.USER;
        Sender sender = new Sender(id, role);

        //Then
        assertThatThrownBy(()->underTest.getNotifications(sender))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessageContaining("no user with such id: "+id);
    }

    @Test
    void canGetBankNotification() {
        //Given
        Bank bank = new Bank();
        bank.setSwift("swift");
        Sender sender = new Sender(bank.getSwift(), Role.BANK);
        when(bankRepo.findById("swift")).thenReturn(Optional.of(bank));

        //When
        underTest.getNotifications(sender);

        //Then
        verify(notificationRepo).findAllByBankId(bank);
    }

    @Test
    void getBankNotificationShouldThrowWhenBankNotFound(){
        String swift = "id";
        Role role = Role.BANK;
        Sender sender = new Sender(swift, role);

        //Then
        assertThatThrownBy(() -> underTest.getNotifications(sender))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessageContaining("no bank with such id: "+swift);
    }

    @Test
    void canChangeNotification(){
        // Given
        NotificationReq notificationReq = new NotificationReq();
        notificationReq.setNotificationType(1);
        notificationReq.setComments("testComments");
        notificationReq.setIsFlagged(false);
        notificationReq.setRecipientId("testIban");
        Notification notification = new Notification(notificationReq);

        NotificationReq putReq = new NotificationReq();
        putReq.setNotificationId(notification.getNotificationId());
        putReq.setIsFlagged(true);

        Optional<Notification> optionalNotification = Optional.of(notification);
        when(notificationRepo.findById(notification.getNotificationId()))
                .thenReturn(optionalNotification);

        // When
        underTest.changeNotification(putReq);

        // Then
        ArgumentCaptor<Notification> notificationArgumentCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepo).save(notificationArgumentCaptor.capture());
        Notification result = notificationArgumentCaptor.getValue();

        assertEquals(true,result.getIsFlagged());
        assertEquals(notification.getUserId(),result.getUserId());
    }

    @Test
    void changeShouldThrowWhenNotificationNotFound(){
        // Given
        NotificationReq putReq = new NotificationReq();
        putReq.setNotificationId(0);
        putReq.setIsFlagged(false);

        // Then
        assertThatThrownBy(()->underTest.changeNotification(putReq))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessageContaining("No notification with such id: "+ putReq.getNotificationId());
    }

}