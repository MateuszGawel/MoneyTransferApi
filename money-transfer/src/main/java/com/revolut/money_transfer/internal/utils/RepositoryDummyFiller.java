package com.revolut.money_transfer.internal.utils;

import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.revolut.money_transfer.internal.exception.AccountNumberDuplicateException;
import com.revolut.money_transfer.internal.repository.AccountRepository;

public class RepositoryDummyFiller{
	private static final Logger LOGGER = Logger.getLogger(RepositoryDummyFiller.class.getName());

	public static void fillAccounts(AccountRepository accountRepository) {
		createAccount(accountRepository, "542312353523512352352355", BigDecimal.ZERO);
		createAccount(accountRepository, "215323513254756783547563", BigDecimal.ZERO);
		createAccount(accountRepository, "109010140000071219811234", new BigDecimal(200));
		createAccount(accountRepository, "122124124214214214214421", new BigDecimal(1000));
	}

	private static void createAccount(AccountRepository accountRepository, String accountNumber, BigDecimal balance) {
		try {
			accountRepository.createAccount(accountNumber, balance);
		} catch (AccountNumberDuplicateException e) {
			LOGGER.log(Level.WARNING, "Account: " + accountNumber + " can't be created", e);
		}
	}
}
