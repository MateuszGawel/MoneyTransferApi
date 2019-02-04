package com.revolut.money_transfer.api.converter;

import com.revolut.money_transfer.api.AccountDto;
import com.revolut.money_transfer.internal.model.Account;

public class AccountConverter {

	public static AccountDto convertFromAccount(Account account) {
		return new AccountDto(account.getNumber(), account.getBalance());
	}
	
}
