package com.revolut.money_transfer.internal.service;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

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
		
		if(amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Provided amount must be positive!");
		}
		
		if(accountFrom == null || accountTo == null || accountFrom.equals(accountTo)) {
			throw new IllegalArgumentException("Accounts must be provided and must be different");
		}
		
		Account fromAccount = getAccountByNumber(accountFrom);
		Account toAccount = getAccountByNumber(accountTo);

		// get locks always in the same order to avoid deadlock
		Account firstLock = fromAccount.getNumber().compareTo(toAccount.getNumber()) > 0 ? fromAccount : toAccount;
		Account secondLock = fromAccount.getNumber().compareTo(toAccount.getNumber()) > 0 ? toAccount : fromAccount;

		synchronized (firstLock) {
			synchronized (secondLock) {
				if (fromAccount.isBalanceSufficientForWithdrawal(amount)) {
					fromAccount.withdraw(amount);
					toAccount.deposit(amount);
				} else {
					throw new NotEnoughMoneyException("Balance on account: " + fromAccount.getNumber() + " is not sufficient to transfer: " + amount);// TODO add currency
				}
			}
		}
	}

	private Account getAccountByNumber(String accountNumber) throws AccountNotFoundException {
		try {
			return accountRepository.getByAccountNumber(accountNumber).get();
		} catch (NoSuchElementException e) {
			throw new AccountNotFoundException("Account " + accountNumber + " doesn't exist", e);
		}
	}

}
