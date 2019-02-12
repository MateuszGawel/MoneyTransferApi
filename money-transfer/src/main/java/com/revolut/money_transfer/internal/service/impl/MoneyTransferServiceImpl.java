package com.revolut.money_transfer.internal.service.impl;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.revolut.money_transfer.internal.exception.AccountNotFoundException;
import com.revolut.money_transfer.internal.exception.NotEnoughMoneyException;
import com.revolut.money_transfer.internal.model.Account;
import com.revolut.money_transfer.internal.repository.AccountRepository;
import com.revolut.money_transfer.internal.service.MoneyTransferService;

@Singleton
public class MoneyTransferServiceImpl implements MoneyTransferService {

	private AccountRepository accountRepository;

	@Inject
	public MoneyTransferServiceImpl(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	@Override
	public void transferMoney(String accountFromNumber, String accountToNumber, BigDecimal amount) throws AccountNotFoundException, NotEnoughMoneyException {
		
		validateInput(accountFromNumber, accountToNumber, amount);
		
		Account fromAccount = getAccountByNumber(accountFromNumber);
		Account toAccount = getAccountByNumber(accountToNumber);

		// get locks always in the same order to avoid deadlock
		Account firstLock = fromAccount.getNumber().compareTo(toAccount.getNumber()) > 0 ? fromAccount : toAccount;
		Account secondLock = fromAccount.getNumber().compareTo(toAccount.getNumber()) > 0 ? toAccount : fromAccount;

		synchronized (firstLock) {
			synchronized (secondLock) {
				performTransfer(fromAccount, toAccount, amount);
			}
		}
	}

	private void performTransfer(Account fromAccount, Account toAccount, BigDecimal amount)
			throws NotEnoughMoneyException {
		if (fromAccount.isBalanceSufficientForWithdrawal(amount)) {
			fromAccount.withdraw(amount);
			toAccount.deposit(amount);
		} else {
			throw new NotEnoughMoneyException("Balance on account: " + fromAccount.getNumber() + " is not sufficient to transfer: " + amount);// TODO add currency
		}
	}

	private void validateInput(String accountFrom, String accountTo, BigDecimal amount) {
		if(amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Provided amount must be positive!");
		}
		
		if(accountFrom == null || accountTo == null || accountFrom.equals(accountTo)) {
			throw new IllegalArgumentException("Accounts must be provided and must be different!");
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
