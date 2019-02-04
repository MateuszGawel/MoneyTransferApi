package com.revolut.money_transfer.api;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "number", "balance" })
public class AccountDto {

	private String number;
	private BigDecimal balance;

	public AccountDto() {
	}

	public AccountDto(String number, BigDecimal balance) {
		this.number = number;
		this.balance = balance;
	}

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
