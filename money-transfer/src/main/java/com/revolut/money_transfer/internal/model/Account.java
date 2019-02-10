package com.revolut.money_transfer.internal.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Account {

	private static final Logger LOGGER = Logger.getLogger(Account.class.getName());
	private static final String TRANSACTION_TYPE_DEPOSIT = "DEPOSIT";
	private static final String TRANSACTION_TYPE_WITHRAWAL = "WITHRAWAL";
	
	private String number;
	private BigDecimal balance;
	
	public Account(String number, BigDecimal balance) {
		this.number = number;
		this.balance = balance;
	}

	public String getNumber() {
		return number;
	}

	public BigDecimal getBalance() {
		return balance;
	}
	
	public void deposit(BigDecimal amount) {
		balance = balance.add(amount);
		logTransaction(TRANSACTION_TYPE_DEPOSIT, amount);
	}

	public void withdraw(BigDecimal amount) {
		balance = balance.subtract(amount);
		logTransaction(TRANSACTION_TYPE_WITHRAWAL, amount);
	}
	
	public boolean isBalanceSufficientForWithdrawal(BigDecimal amount) {
		return balance.compareTo(amount) >= 0;
	}
	
	private void logTransaction(String transactionType, BigDecimal amount) {
		LOGGER.log(Level.INFO, transactionType + " [Account: " + number + ", Amount: " + amount.setScale(2, RoundingMode.HALF_DOWN) + " CurrentBalance: " + balance.setScale(2, RoundingMode.HALF_DOWN) + "]");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((number == null) ? 0 : number.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Account other = (Account) obj;
		if (number == null) {
			if (other.number != null)
				return false;
		} else if (!number.equals(other.number))
			return false;
		return true;
	}
}
