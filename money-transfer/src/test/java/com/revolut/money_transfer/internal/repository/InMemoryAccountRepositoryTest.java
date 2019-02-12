package com.revolut.money_transfer.internal.repository;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.revolut.money_transfer.internal.exception.AccountNumberDuplicateException;
import com.revolut.money_transfer.internal.model.Account;
import com.revolut.money_transfer.internal.repository.impl.InMemoryAccountRepository;

public class InMemoryAccountRepositoryTest {
	private static final String ACCOUNT_NO_1 = "1111111111";
	private static final String ACCOUNT_NO_2 = "2222222222";
	private static final BigDecimal ACCOUNT_1_INIT_BALANCE = new BigDecimal(1500);
	private static final BigDecimal ACCOUNT_2_INIT_BALANCE = BigDecimal.ZERO;
	
	private AccountRepository accountRepository;
	
	@Before
	public void prepare() throws AccountNumberDuplicateException {
		accountRepository = new InMemoryAccountRepository();
	}
	
	@Test
	public void testCreateAccount_OK() throws AccountNumberDuplicateException {
		//WHEN
		accountRepository.createAccount(ACCOUNT_NO_1, ACCOUNT_1_INIT_BALANCE);
		accountRepository.createAccount(ACCOUNT_NO_2, ACCOUNT_2_INIT_BALANCE);
		
		//THEN
		Assert.assertTrue(accountRepository.getByAccountNumber(ACCOUNT_NO_1).isPresent());
		Assert.assertTrue(accountRepository.getByAccountNumber(ACCOUNT_NO_2).isPresent());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testCreateAccount_nullAccountNumber() throws AccountNumberDuplicateException {
		//WHEN
		accountRepository.createAccount(null, ACCOUNT_1_INIT_BALANCE);
	}
	
	@Test
	public void testCreateAccount_nullBalance() throws AccountNumberDuplicateException {
		//WHEN
		accountRepository.createAccount(ACCOUNT_NO_1, null);
		
		//THEN
		Assert.assertTrue(accountRepository.getByAccountNumber(ACCOUNT_NO_1).isPresent());
		Assert.assertEquals(BigDecimal.ZERO, accountRepository.getByAccountNumber(ACCOUNT_NO_1).get().getBalance());
	}
	
	@Test(expected = AccountNumberDuplicateException.class)
	public void testCreateAccount_duplicate() throws AccountNumberDuplicateException {
		//WHEN
		accountRepository.createAccount(ACCOUNT_NO_1, ACCOUNT_1_INIT_BALANCE);
		accountRepository.createAccount(ACCOUNT_NO_1, ACCOUNT_2_INIT_BALANCE);
	}
	
	@Test
	public void testGetAll_OK() throws AccountNumberDuplicateException {
		//GIVEN
		Account account1 = new Account(ACCOUNT_NO_1, ACCOUNT_1_INIT_BALANCE);
		Account account2 = new Account(ACCOUNT_NO_2, ACCOUNT_2_INIT_BALANCE);
		
		//WHEN
		accountRepository.createAccount(account1.getNumber(), account1.getBalance());
		accountRepository.createAccount(account2.getNumber(), account2.getBalance());
		
		//THEN
		Assert.assertTrue(accountRepository.getAll().contains(account1));
		Assert.assertTrue(accountRepository.getAll().contains(account2));
	}
}
