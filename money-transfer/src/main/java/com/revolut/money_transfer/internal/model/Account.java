package com.revolut.money_transfer.internal.model;

import java.math.BigDecimal;

public class Account {
	private String number;
	private BigDecimal balance;

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
}
