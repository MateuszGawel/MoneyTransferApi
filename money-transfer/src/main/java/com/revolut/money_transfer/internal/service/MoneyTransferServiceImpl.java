package com.revolut.money_transfer.internal.service;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Random;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.revolut.money_transfer.internal.exception.AccountNotFoundException;
import com.revolut.money_transfer.internal.exception.NotEnoughMoneyException;
import com.revolut.money_transfer.internal.model.Account;
import com.revolut.money_transfer.internal.repository.AccountRepository;

@Singleton
public class MoneyTransferServiceImpl implements MoneyTransferService {

	private AccountRepository accountRepository;

	@Inject
	public MoneyTransferServiceImpl(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	@Override
	public void transferMoney(String accountFrom, String accountTo, BigDecimal amount) throws AccountNotFoundException, NotEnoughMoneyException {
		Account fromAccount = getAccountByNumber(accountFrom);
		Account toAccount = getAccountByNumber(accountTo);

		if (isBalanceSufficient(amount, fromAccount)) {

			//TODO this should be thread safe and transactional
			BigDecimal fromNewAmount = fromAccount.getBalance().subtract(amount);
			fromAccount.setBalance(fromNewAmount);
			
			// it's possible to fail junits with sleep but I dont want it in code obviously
			Random random = new Random();
			try {
				Thread.sleep(random.nextInt(100));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			BigDecimal toNewAmount = toAccount.getBalance().add(amount);
			toAccount.setBalance(toNewAmount);

		} else {
			throw new NotEnoughMoneyException("Balance on account: " + fromAccount.getNumber() + " is not sufficient to transfer: " + amount);//TODO add currency
		}
	}

	private boolean isBalanceSufficient(BigDecimal amount, Account from) {
		return from.getBalance().compareTo(amount) >= 0;
	}

	private Account getAccountByNumber(String accountNumber) throws AccountNotFoundException {
		try {
			return accountRepository.getByAccountNumber(accountNumber).get();
		} catch (NoSuchElementException e) {
			throw new AccountNotFoundException("Account " + accountNumber + " doesn't exist", e);
		}
	}

}
