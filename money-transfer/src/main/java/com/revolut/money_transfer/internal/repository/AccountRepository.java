package com.revolut.money_transfer.internal.repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Optional;

import com.revolut.money_transfer.internal.exception.AccountNumberDuplicateException;
import com.revolut.money_transfer.internal.model.Account;

public interface AccountRepository {
	/**
	 * Creates account with provided number and initial balance and stores it in repository.
	 * 
	 * @param accountNumber unique number to define account
	 * @param balance initial balance of created account
	 * @throws AccountNumberDuplicateException
	 */
	void createAccount(String accountNumber, BigDecimal balance) throws AccountNumberDuplicateException;
	
	/**
	 * Returns account from repository based on provided unique number.
	 * 
	 * @param accountNumber
	 * @return Optional with Account
	 */
	Optional<Account> getByAccountNumber(String accountNumber);
	
	
	/**
	 * @return all accounts in repository.
	 */
	Collection<Account> getAll();
	
	
	/**
	 * Clears repository from all data. Use with care.
	 */
	void clear();
}
