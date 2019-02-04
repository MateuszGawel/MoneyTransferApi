package com.revolut.money_transfer.internal;

import org.jooby.Jooby;
import org.jooby.json.Jackson;

import com.revolut.money_transfer.api.AccountController;
import com.revolut.money_transfer.internal.repository.AccountRepository;
import com.revolut.money_transfer.internal.repository.InMemoryAccountRepository;
import com.revolut.money_transfer.internal.service.MoneyTransferService;
import com.revolut.money_transfer.internal.service.MoneyTransferServiceImpl;
import com.revolut.money_transfer.internal.utils.RepositoryDummyFiller;

public class App extends Jooby {

  {
      use((env, conf, binder) -> {
    	  binder.bind(MoneyTransferService.class).to(MoneyTransferServiceImpl.class);
          binder.bind(AccountRepository.class).to(InMemoryAccountRepository.class);
      });
      use(AccountController.class);
      use(new Jackson());
      onStart(registry -> RepositoryDummyFiller.fillAccounts(
    		  registry.require(AccountRepository.class)));

  }
  public static void main(final String[] args) {
    run(App::new, args);
  }

}
