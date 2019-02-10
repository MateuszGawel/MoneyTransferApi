package com.revolut.money_transfer.api.converter;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import com.revolut.money_transfer.api.AccountDto;
import com.revolut.money_transfer.internal.model.Account;

public class AccountConverter {

	public static AccountDto convertFromAccount(Account account) {
		BigDecimal balance = account.getBalance().setScale(2, BigDecimal.ROUND_HALF_DOWN);
		
		DecimalFormat df = new DecimalFormat("#0.00##");
		String formattedBalance = df.format(balance);
		
		return new AccountDto(account.getNumber(), formattedBalance);
	}
	
}
