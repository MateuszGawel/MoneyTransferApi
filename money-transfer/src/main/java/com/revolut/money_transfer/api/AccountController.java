package com.revolut.money_transfer.api;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.jooby.Err;
import org.jooby.Result;
import org.jooby.Results;
import org.jooby.Status;
import org.jooby.mvc.Body;
import org.jooby.mvc.GET;
import org.jooby.mvc.POST;
import org.jooby.mvc.Path;

import com.revolut.money_transfer.api.converter.AccountConverter;
import com.revolut.money_transfer.internal.exception.AccountNotFoundException;
import com.revolut.money_transfer.internal.exception.NotEnoughMoneyException;
import com.revolut.money_transfer.internal.repository.AccountRepository;
import com.revolut.money_transfer.internal.service.MoneyTransferService;


@Path("/accounts")
public class AccountController {

	private final MoneyTransferService moneyTransferService;
	private final AccountRepository accountRepository;

	@Inject
	public AccountController(MoneyTransferService moneyTransferService, AccountRepository accountRepository) {
		this.moneyTransferService = moneyTransferService;
		this.accountRepository = accountRepository;
	}

	@GET
	public List<AccountDto> getAll() {
		return accountRepository
				.getAll()
				.stream()
				.map(AccountConverter::convertFromAccount)
				.collect(Collectors.toList());
	}

	@Path("/{accountnumber}")
	@GET
	public AccountDto getByAccountNumber(String accountnumber) {
		return accountRepository
				.getByAccountNumber(accountnumber)
				.map(AccountConverter::convertFromAccount)
				.orElseThrow(() -> new Err(Status.NOT_FOUND));
	}

	@Path("/moneytransfer")
	@POST
	public Result moneyTransfer(@Body MoneyTransferRequest moneyTransferRequest) throws AccountNotFoundException, NotEnoughMoneyException {
		moneyTransferService.transferMoney(moneyTransferRequest.getFromAccount(), moneyTransferRequest.getToAccount(), moneyTransferRequest.getAmount());
		return Results.ok();
	}

}
