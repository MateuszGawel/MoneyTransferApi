package com.revolut.money_transfer.internal.repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Optional;

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
		//GIVEN
		accountRepository.createAccount(ACCOUNT_NO_1, ACCOUNT_1_INIT_BALANCE);
		accountRepository.createAccount(ACCOUNT_NO_2, ACCOUNT_2_INIT_BALANCE);
		
		//WHEN
		Optional<Account> account1 = accountRepository.getByAccountNumber(ACCOUNT_NO_1);
		Optional<Account> account2 = accountRepository.getByAccountNumber(ACCOUNT_NO_2);
		
		//THEN
		Assert.assertTrue(account1.isPresent());
		Assert.assertTrue(account2.isPresent());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testCreateAccount_nullAccountNumber() throws AccountNumberDuplicateException {
		//WHEN
		accountRepository.createAccount(null, ACCOUNT_1_INIT_BALANCE);
	}
	
	@Test
	public void testCreateAccount_nullBalance() throws AccountNumberDuplicateException {
		//GIVEN
		accountRepository.createAccount(ACCOUNT_NO_1, null);
		
		//WHEN
		Optional<Account> account = accountRepository.getByAccountNumber(ACCOUNT_NO_1);
		
		//THEN
		Assert.assertTrue(account.isPresent());
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
		accountRepository.createAccount(account1.getNumber(), account1.getBalance());
		accountRepository.createAccount(account2.getNumber(), account2.getBalance());
		
		//WHEN
		Collection<Account> accounts = accountRepository.getAll();
		
		//THEN
		Assert.assertTrue(accounts.contains(account1));
		Assert.assertTrue(accounts.contains(account2));
	}
	
	@Test
	public void testGetAccountByNumber_OK() throws AccountNumberDuplicateException {
		//GIVEN
		Account account1 = new Account(ACCOUNT_NO_1, ACCOUNT_1_INIT_BALANCE);
		accountRepository.createAccount(account1.getNumber(), account1.getBalance());
		
		//WHEN
		Optional<Account> account = accountRepository.getByAccountNumber(ACCOUNT_NO_1);
		
		//THEN
		Assert.assertTrue(account.isPresent());
		Assert.assertEquals(ACCOUNT_NO_1, accountRepository.getByAccountNumber(ACCOUNT_NO_1).get().getNumber());
		Assert.assertEquals(ACCOUNT_1_INIT_BALANCE, accountRepository.getByAccountNumber(ACCOUNT_NO_1).get().getBalance());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testGetAccountByNumber_nullAccountNumber() throws AccountNumberDuplicateException {
		//GIVEN
		Account account1 = new Account(ACCOUNT_NO_1, ACCOUNT_1_INIT_BALANCE);
		accountRepository.createAccount(account1.getNumber(), account1.getBalance());
		
		//WHEN
		accountRepository.getByAccountNumber(null);
	}
	
	@Test
	public void testGetAccountByNumber_emptyNumber() throws AccountNumberDuplicateException {
		//GIVEN
		Account account1 = new Account(ACCOUNT_NO_1, ACCOUNT_1_INIT_BALANCE);
		accountRepository.createAccount(account1.getNumber(), account1.getBalance());
		
		//WHEN
		Optional<Account> account = accountRepository.getByAccountNumber("");
		
		//THEN
		Assert.assertFalse(account.isPresent());
	}
	
	@Test
	public void testGetAccountByNumber_notExistingNumber() throws AccountNumberDuplicateException {
		//GIVEN
		Account account1 = new Account(ACCOUNT_NO_1, ACCOUNT_1_INIT_BALANCE);
		accountRepository.createAccount(account1.getNumber(), account1.getBalance());
		
		//WHEN
		Optional<Account> account = accountRepository.getByAccountNumber("333333333");
		
		//THEN
		Assert.assertFalse(account.isPresent());
	}
}
