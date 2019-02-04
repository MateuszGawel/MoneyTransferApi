package com.revolut.money_transfer.internal.repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Optional;

import com.revolut.money_transfer.internal.exception.AccountNumberDuplicateException;
import com.revolut.money_transfer.internal.model.Account;

public interface AccountRepository {
	void createAccount(String accountNumber, BigDecimal balance) throws AccountNumberDuplicateException;
	Optional<Account> getByAccountNumber(String accountNumber);
	Collection<Account> getAll();
}
