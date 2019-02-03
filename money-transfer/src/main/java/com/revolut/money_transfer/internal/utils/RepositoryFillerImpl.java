package com.revolut.money_transfer.internal.utils;

import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.revolut.money_transfer.internal.exception.AccountNumberDuplicateException;
import com.revolut.money_transfer.internal.repository.AccountRepository;

import javassist.bytecode.stackmap.TypeData.ClassName;

public class RepositoryFillerImpl implements RepositoryFiller {
	private static final Logger LOGGER = Logger.getLogger(ClassName.class.getName());

	private AccountRepository accountRepository;

	public RepositoryFillerImpl(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	@Override
	public void fillDummyAccounts() {
		createAccount("542312353523512352352355", BigDecimal.ZERO);
		createAccount("215323513254756783547563", BigDecimal.ZERO);
		createAccount("109010140000071219811234", new BigDecimal(200));
		createAccount("122124124214214214214421", new BigDecimal(1000));
	}

	private void createAccount(String accountNumber, BigDecimal balance) {
		try {
			accountRepository.createAccount(accountNumber, balance);
		} catch (AccountNumberDuplicateException e) {
			LOGGER.log(Level.WARNING, "Account: " + accountNumber + " can't be created", e);
		}
	}
}
