package com.revolut.money_transfer.internal.exception;

public class AccountNumberDuplicateException extends Exception {

	private static final long serialVersionUID = -8910462641201572260L;

	public AccountNumberDuplicateException(String message) {
		super(message);
	}
	
}
