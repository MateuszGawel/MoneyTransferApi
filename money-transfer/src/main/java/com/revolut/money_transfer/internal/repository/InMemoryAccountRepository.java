package com.revolut.money_transfer.internal.repository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.revolut.money_transfer.internal.exception.AccountNumberDuplicateException;
import com.revolut.money_transfer.internal.model.Account;

public class InMemoryAccountRepository implements AccountRepository{
	
	private Map<String, Account> accounts = new ConcurrentHashMap<>();
	
	@Override
	public void createAccount(String accountNumber) throws AccountNumberDuplicateException{
		createAccount(accountNumber, BigDecimal.ZERO);
	}

	@Override
	public void createAccount(String accountNumber, BigDecimal balance) throws AccountNumberDuplicateException {
		if(accounts.containsKey(accountNumber)) {
			throw new AccountNumberDuplicateException("Account number " + accountNumber + " is not unique.");
		}
		
		Account account = new Account();
		account.setNumber(accountNumber);
		account.setBalance(balance);
		accounts.put(accountNumber, account);
	}

	@Override
	public Optional<Account> getByAccountNumber(String accountNumber) {
		return Optional.ofNullable(accounts.get(accountNumber));
	}

}
