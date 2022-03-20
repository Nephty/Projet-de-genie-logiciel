package com.example.demo.service;

import com.example.demo.exception.throwables.ConflictException;
import com.example.demo.exception.throwables.ResourceNotFound;
import com.example.demo.model.Account;
import com.example.demo.model.AccountAccess;
import com.example.demo.model.AccountType;
import com.example.demo.model.CompositePK.AccountAccessPK;
import com.example.demo.model.User;
import com.example.demo.repository.AccountAccessRepo;
import com.example.demo.repository.AccountRepo;
import com.example.demo.repository.UserRepo;
import com.example.demo.request.AccountAccessReq;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        Optional<Account> account = Optional.of(tmpAccount);
        when(accountRepo.findById(accessReq.getAccountId()))
                .thenReturn(account);

        User tmpUser = new User();
        tmpUser.setUserID(accessReq.getUserId());
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
        Optional<Account> account = Optional.of(tmpAccount);
        when(accountRepo.findById(accessReq.getAccountId()))
                .thenReturn(account);

        User tmpUser = new User();
        tmpUser.setUserID(accessReq.getUserId());
        Optional<User> user = Optional.of(tmpUser);
        when(userRepo.findById(accessReq.getUserId()))
                .thenReturn(user);

        //When
        underTest.changeAccountAccess(accessReq);

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
    void changeShouldThrowWhenAccountIdNotFound(){
        //given
        AccountAccessReq accessReq = new AccountAccessReq(
                "accountId",
                "userId",
                true,
                false
        );

        //then
        assertThatThrownBy(()->underTest.changeAccountAccess(accessReq))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining(accessReq.getAccountId());

        verify(accessRepo,never()).save(any());
    }

    @Test
    void changeShouldThrowWhenUserIdNotFound(){
        //given
        AccountAccessReq accessReq = new AccountAccessReq(
                "accountId",
                "userId",
                true,
                false
        );

        Account tmpAccount = new Account();
        tmpAccount.setIban(accessReq.getAccountId());
        Optional<Account> account = Optional.of(tmpAccount);
        when(accountRepo.findById(accessReq.getAccountId()))
                .thenReturn(account);

        //then
        assertThatThrownBy(()->underTest.changeAccountAccess(accessReq))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining(accessReq.getUserId());

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

        AccountAccessPK accessPK = new AccountAccessPK(accountId,userId);
        when(accessRepo.findById(accessPK)).thenReturn(Optional.of(new AccountAccess()));

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

        //when
        underTest.getAccountAccessByUserId(userId);

        //then
        verify(accessRepo).getAllByUserId(userId);
        //We don't have to test if the method returns the good list because it's already tested in the repositories.
    }

    @Test
    void canGetAllCustomers(){
        //Given
        String swift = "swift";
        //When
        underTest.getAllCustomers(swift);
        //Then
        verify(accessRepo).getAllCustomersInBank(swift);
    }
}