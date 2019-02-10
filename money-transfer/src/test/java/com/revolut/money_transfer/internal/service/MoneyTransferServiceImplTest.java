package com.revolut.money_transfer.internal.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.revolut.money_transfer.internal.exception.AccountNotFoundException;
import com.revolut.money_transfer.internal.exception.AccountNumberDuplicateException;
import com.revolut.money_transfer.internal.exception.NotEnoughMoneyException;
import com.revolut.money_transfer.internal.model.Account;
import com.revolut.money_transfer.internal.repository.AccountRepository;

public class MoneyTransferServiceImplTest {
	
	private MoneyTransferServiceImpl moneyTransferService;

	private Account account1 = new Account("1111111111", new BigDecimal(1500));
	private Account account2 = new Account("2222222222", BigDecimal.ZERO);
	private final Account account1Agent = Mockito.spy(account1);
	private final Account account2Agent = Mockito.spy(account2);
	
	@Mock
	private AccountRepository accountRepository;
	
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule(); 
	
	@Before
	public void prepare() throws AccountNumberDuplicateException {
		moneyTransferService = new MoneyTransferServiceImpl(accountRepository);
		when(accountRepository.getByAccountNumber(account1.getNumber())).thenReturn(Optional.of(account1Agent));
		when(accountRepository.getByAccountNumber(account2.getNumber())).thenReturn(Optional.of(account2Agent));
	}

	@Test
	public void testMoneyTransfer_OK() throws AccountNotFoundException, NotEnoughMoneyException {
		//GIVEN
		final BigDecimal transferAmount = new BigDecimal(1000);
		
		//WHEN
		moneyTransferService.transferMoney(account1.getNumber(), account2.getNumber(), transferAmount);
		
		//THEN
		verifyPerformedTransactions(transferAmount);
	}
	
	@Test
	public void testMoneyTransfer_OK2() throws AccountNotFoundException, NotEnoughMoneyException {
		//GIVEN
		final BigDecimal transferAmount = new BigDecimal(12.98);
		
		//WHEN
		moneyTransferService.transferMoney(account1.getNumber(), account2.getNumber(), transferAmount);
		
		//THEN
		verifyPerformedTransactions(transferAmount);
	}
	
	@Test
	public void testMoneyTransfer_OK3() throws AccountNotFoundException, NotEnoughMoneyException {
		//GIVEN
		final BigDecimal transferAmount = BigDecimal.ONE;
		
		//WHEN
		moneyTransferService.transferMoney(account1.getNumber(), account2.getNumber(), transferAmount);
		
		//THEN
		verifyPerformedTransactions(transferAmount);
	}
	
	@Test(expected = NotEnoughMoneyException.class)
	public void testMoneyTransfer_moreThanPossible() throws AccountNotFoundException, NotEnoughMoneyException {
		//GIVEN
		final BigDecimal transferAmount = new BigDecimal(1600);
		
		//WHEN
		moneyTransferService.transferMoney(account1.getNumber(), account2.getNumber(), transferAmount);
		
		//THEN
		verifyPerformedTransactions(transferAmount);
	}
	
	@Test
	public void testMoneyTransfer_allAmount() throws AccountNotFoundException, NotEnoughMoneyException {
		//GIVEN
		final BigDecimal transferAmount = new BigDecimal(1500);
		
		//WHEN
		moneyTransferService.transferMoney(account1.getNumber(), account2.getNumber(), transferAmount);
		
		//THEN
		verifyPerformedTransactions(transferAmount);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testMoneyTransfer_negativeAmount() throws AccountNotFoundException, NotEnoughMoneyException {
		//GIVEN
		final BigDecimal transferAmount = new BigDecimal(-500);
		
		//WHEN
		moneyTransferService.transferMoney(account1.getNumber(), account2.getNumber(), transferAmount);
		
		//THEN
		verifyPerformedTransactions(transferAmount);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testMoneyTransfer_zeroAmount() throws AccountNotFoundException, NotEnoughMoneyException {
		//GIVEN
		final BigDecimal transferAmount = BigDecimal.ZERO;
		
		//WHEN
		moneyTransferService.transferMoney(account1.getNumber(), account2.getNumber(), transferAmount);
		
		//THEN
		verifyPerformedTransactions(transferAmount);
	}

	@Test(expected = AccountNotFoundException.class)
	public void testMoneyTransfer_notExistingAccount1() throws AccountNotFoundException, NotEnoughMoneyException {
		//GIVEN
		final BigDecimal transferAmount = new BigDecimal(500);
		
		//WHEN
		when(accountRepository.getByAccountNumber("444")).thenReturn(Optional.empty());
		moneyTransferService.transferMoney("444", account2.getNumber(), transferAmount);
		
		//THEN
		verifyPerformedTransactions(transferAmount);
	}
	
	@Test(expected = AccountNotFoundException.class)
	public void testMoneyTransfer_notExistingAccount2() throws AccountNotFoundException, NotEnoughMoneyException {
		//GIVEN
		final BigDecimal transferAmount = new BigDecimal(500);
		
		//WHEN
		when(accountRepository.getByAccountNumber("444")).thenReturn(Optional.empty());
		moneyTransferService.transferMoney(account1.getNumber(), "444", transferAmount);
		
		//THEN
		verifyPerformedTransactions(transferAmount);
	}
	
	@Test(expected = AccountNotFoundException.class)
	public void testMoneyTransfer_emptyAccountNumber() throws AccountNotFoundException, NotEnoughMoneyException {
		//GIVEN
		final BigDecimal transferAmount = new BigDecimal(500);
		
		//WHEN
		when(accountRepository.getByAccountNumber("")).thenReturn(Optional.empty());
		moneyTransferService.transferMoney("", account2.getNumber(), transferAmount);
		
		//THEN
		verifyPerformedTransactions(transferAmount);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testMoneyTransfer_sameAccountNumber() throws AccountNotFoundException, NotEnoughMoneyException {
		//GIVEN
		final BigDecimal transferAmount = new BigDecimal(500);
		
		//WHEN
		moneyTransferService.transferMoney(account1.getNumber(), account1.getNumber(), transferAmount);
		
		//THEN
		verifyPerformedTransactions(transferAmount);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testMoneyTransfer_nullAccountNumber() throws AccountNotFoundException, NotEnoughMoneyException {
		//GIVEN
		final BigDecimal transferAmount = new BigDecimal(500);
		
		//WHEN
		moneyTransferService.transferMoney(null, account2.getNumber(), transferAmount);
		
		//THEN
		verifyPerformedTransactions(transferAmount);
	}
	
	private void verifyPerformedTransactions(final BigDecimal transferAmount) {
		verify(account1Agent).withdraw(transferAmount);
		verify(account2Agent).deposit(transferAmount);
	}
	
}