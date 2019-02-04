package com.revolut.money_transfer.internal.exception;

public class AccountNotFoundException extends Exception {

	private static final long serialVersionUID = -4264907324435219977L;

	public AccountNotFoundException(String message) {
		super(message);
	}

	public AccountNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}



}
