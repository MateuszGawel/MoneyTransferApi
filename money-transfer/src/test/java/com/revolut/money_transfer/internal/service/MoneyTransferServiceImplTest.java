package com.revolut.money_transfer.internal.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.revolut.money_transfer.internal.exception.AccountNotFoundException;
import com.revolut.money_transfer.internal.exception.AccountNumberDuplicateException;
import com.revolut.money_transfer.internal.exception.NotEnoughMoneyException;
import com.revolut.money_transfer.internal.repository.AccountRepository;
import com.revolut.money_transfer.internal.repository.InMemoryAccountRepository;

public class MoneyTransferServiceImplTest {
	private static final String ACCOUNT_NO_1 = "1111111111";
	private static final String ACCOUNT_NO_2 = "2222222222";
	private static final BigDecimal ACCOUNT_BALANCE_1 = new BigDecimal(1500);
	private static final BigDecimal ACCOUNT_BALANCE_2 = BigDecimal.ZERO;
	private static final BigDecimal TRANSFER_AMOUNT = new BigDecimal(1000);

	private AccountRepository accountRepository;
	private MoneyTransferService moneyTransferService;

	@Before
	public void prepareRepository() throws AccountNumberDuplicateException {
		initRepository();
	}

	private void initRepository() throws AccountNumberDuplicateException {
		accountRepository = new InMemoryAccountRepository();
		accountRepository.createAccount(ACCOUNT_NO_1, ACCOUNT_BALANCE_1);
		accountRepository.createAccount(ACCOUNT_NO_2, ACCOUNT_BALANCE_2);
		moneyTransferService = new MoneyTransferServiceImpl(accountRepository);
	}
	
	private void resetBalance() {
		accountRepository.getByAccountNumber(ACCOUNT_NO_1).get().setBalance(ACCOUNT_BALANCE_1);
		accountRepository.getByAccountNumber(ACCOUNT_NO_2).get().setBalance(ACCOUNT_BALANCE_2);
	}

	@Test
	public void testTransfer() throws AccountNotFoundException, NotEnoughMoneyException, InterruptedException {
		for (int i = 0; i <= 100; i++) {
			resetBalance();
			Callable<Boolean> callableTask = () -> {
				try {
					moneyTransferService.transferMoney(ACCOUNT_NO_1, ACCOUNT_NO_2, TRANSFER_AMOUNT);
					return true;
				} catch (AccountNotFoundException | NotEnoughMoneyException e) {
					e.printStackTrace();
					return false;
				}
			};

			List<Callable<Boolean>> callableTasks = new ArrayList<>();
			callableTasks.add(callableTask);
			callableTasks.add(callableTask);

			ExecutorService executorService = Executors.newFixedThreadPool(2);
			executorService.invokeAll(callableTasks);

			Assert.assertEquals(ACCOUNT_BALANCE_1.subtract(TRANSFER_AMOUNT), accountRepository.getByAccountNumber(ACCOUNT_NO_1).get().getBalance());
			Assert.assertEquals(ACCOUNT_BALANCE_2.add(TRANSFER_AMOUNT), accountRepository.getByAccountNumber(ACCOUNT_NO_2).get().getBalance());
		}
	}

}
