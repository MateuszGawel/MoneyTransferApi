package com.revolut.money_transfer.internal.service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;

import com.google.testing.threadtester.AnnotatedTestRunner;
import com.google.testing.threadtester.MethodOption;
import com.google.testing.threadtester.ThreadedAfter;
import com.google.testing.threadtester.ThreadedBefore;
import com.google.testing.threadtester.ThreadedMain;
import com.google.testing.threadtester.ThreadedSecondary;
import com.revolut.money_transfer.internal.exception.AccountNotFoundException;
import com.revolut.money_transfer.internal.exception.AccountNumberDuplicateException;
import com.revolut.money_transfer.internal.exception.NotEnoughMoneyException;
import com.revolut.money_transfer.internal.repository.AccountRepository;
import com.revolut.money_transfer.internal.repository.impl.InMemoryAccountRepository;
import com.revolut.money_transfer.internal.service.impl.MoneyTransferServiceImpl;

public class MoneyTransferServiceImplMultithreadTest {
	private static final Logger LOGGER = Logger.getLogger(MoneyTransferServiceImpl.class.getName());
	
	private static final String ACCOUNT_NO_1 = "1111111111";
	private static final String ACCOUNT_NO_2 = "2222222222";
	private static final BigDecimal ACCOUNT_BALANCE_1 = new BigDecimal(1500);
	private static final BigDecimal ACCOUNT_BALANCE_2 = BigDecimal.ZERO;
	private static final BigDecimal TRANSFER_AMOUNT = new BigDecimal(1000);
	
	private MoneyTransferServiceImpl moneyTransferService;
	private AccountRepository accountRepository;
	
	@Test
	public void testMultithreadingTransfer() {
		AnnotatedTestRunner runner = new AnnotatedTestRunner();
		HashSet<String> methods = new HashSet<String>();
		methods.add("com.revolut.money_transfer.internal.service.impl.MoneyTransferServiceImpl.transferMoney");
		runner.setMethodOption(MethodOption.LISTED_METHODS, methods);
		runner.setDebug(true);
		runner.runTests(this.getClass(), MoneyTransferServiceImpl.class);
	}

	@ThreadedBefore
	public void before() throws AccountNumberDuplicateException {
		accountRepository = new InMemoryAccountRepository();
		accountRepository.createAccount(ACCOUNT_NO_1, ACCOUNT_BALANCE_1);
		accountRepository.createAccount(ACCOUNT_NO_2, ACCOUNT_BALANCE_2);
		moneyTransferService = new MoneyTransferServiceImpl(accountRepository);
	}

	@ThreadedMain
	public void main(){
		try {
			moneyTransferService.transferMoney(ACCOUNT_NO_1, ACCOUNT_NO_2, TRANSFER_AMOUNT);
		} catch (AccountNotFoundException | NotEnoughMoneyException e) {
			LOGGER.log(Level.FINE, "Money transfer failed", e);
		}
	}

	@ThreadedSecondary
	public void secondary(){
		try {
			moneyTransferService.transferMoney(ACCOUNT_NO_1, ACCOUNT_NO_2, TRANSFER_AMOUNT);
		} catch (AccountNotFoundException | NotEnoughMoneyException e) {
			LOGGER.log(Level.FINE, "Money transfer failed", e);
		}
	}

	@ThreadedAfter
	public void after() {
		Assert.assertEquals(ACCOUNT_BALANCE_1.subtract(TRANSFER_AMOUNT), accountRepository.getByAccountNumber(ACCOUNT_NO_1).get().getBalance());
		Assert.assertEquals(ACCOUNT_BALANCE_2.add(TRANSFER_AMOUNT), accountRepository.getByAccountNumber(ACCOUNT_NO_2).get().getBalance());
	}
	
}
