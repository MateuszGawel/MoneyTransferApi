package com.revolut.money_transfer.internal.service;

import java.math.BigDecimal;

import com.revolut.money_transfer.internal.exception.AccountNotFoundException;
import com.revolut.money_transfer.internal.exception.NotEnoughMoneyException;

public interface MoneyTransferService {
	/**
	 * Transfer specified amount of money between provided accounts.
	 * Accounts must be provided and must exist in repository.
	 * Amount can't be higher than available for withdrawal and must be positive.
	 * 
	 * @param accountFromNumber account for money withdrawal
	 * @param accountToNumber account for money deposit
	 * @param amount of money to transfer
	 * @throws AccountNotFoundException
	 * @throws NotEnoughMoneyException
	 */
	void transferMoney(String accountFromNumber, String accountToNumber, BigDecimal amount) throws AccountNotFoundException, NotEnoughMoneyException;
}
