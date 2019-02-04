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

	@Override
	public Collection<Account> getAll() {
		return accounts.values();
	}
	
	public void updateBalance(String accountNumber, BigDecimal balance) {
//		accounts.computeIfPresent(accountNumber, (k, v) -> v.setBalance(balance));
	}

}
