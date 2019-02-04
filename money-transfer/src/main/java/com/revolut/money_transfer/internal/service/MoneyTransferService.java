package com.revolut.money_transfer.internal.service;

import java.math.BigDecimal;

import com.revolut.money_transfer.internal.exception.AccountNotFoundException;
import com.revolut.money_transfer.internal.exception.NotEnoughMoneyException;

public interface MoneyTransferService {
	void transferMoney(String accountFrom, String accountTo, BigDecimal amount) throws AccountNotFoundException, NotEnoughMoneyException;
}
