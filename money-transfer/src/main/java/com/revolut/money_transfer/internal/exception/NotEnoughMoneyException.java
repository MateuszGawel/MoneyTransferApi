package com.revolut.money_transfer.internal.exception;

public class NotEnoughMoneyException extends Exception {

	private static final long serialVersionUID = -181149614834474572L;

	public NotEnoughMoneyException(String message) {
		super(message);
	}

}
