package com.revolut.money_transfer.internal.repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Singleton;

import com.revolut.money_transfer.internal.exception.AccountNumberDuplicateException;
import com.revolut.money_transfer.internal.model.Account;

@Singleton
public class InMemoryAccountRepository implements AccountRepository{
	
	private Map<String, Account> accounts = new ConcurrentHashMap<>();
	
	@Override
	public void createAccount(String accountNumber, BigDecimal initBalance) throws AccountNumberDuplicateException {
		if(accountNumber == null) {
			throw new IllegalArgumentException("Account number must be provided");
		}
		
		if(initBalance == null) {
			initBalance = BigDecimal.ZERO;
		}
			
		if(accounts.containsKey(accountNumber)) {
			throw new AccountNumberDuplicateException("Account number " + accountNumber + " is not unique.");
		}
		
		Account account = new Account(accountNumber, initBalance);
		accounts.put(accountNumber, account);
	}

	@Override
	public Optional<Account> getByAccountNumber(String accountNumber) {
		return Optional.ofNullable(accounts.get(accountNumber));
	}

	@Override
	public Collection<Account> getAll() {
		return accounts.values();
	}
	
	@Override
	public void clear() {
		accounts.clear();
	}

}
