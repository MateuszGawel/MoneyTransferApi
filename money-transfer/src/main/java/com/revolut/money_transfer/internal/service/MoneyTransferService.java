package com.revolut.money_transfer.internal.service;

import java.math.BigDecimal;

public interface MoneyTransferService {
	void transferMoney(String accountFrom, String accountTo, BigDecimal amount);
}
