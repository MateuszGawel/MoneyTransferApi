package com.revolut.money_transfer.internal;

import org.jooby.Jooby;

import com.revolut.money_transfer.internal.repository.AccountRepository;
import com.revolut.money_transfer.internal.repository.InMemoryAccountRepository;
import com.revolut.money_transfer.internal.utils.RepositoryFiller;
import com.revolut.money_transfer.internal.utils.RepositoryFillerImpl;

/**
 * @author jooby generator
 */
public class App extends Jooby {

  {
    get("/", () -> "Hello World!");
  }

  public static void main(final String[] args) {
    run(App::new, args);

    AccountRepository accountRepository = new InMemoryAccountRepository();
    RepositoryFiller repositoryFiller = new RepositoryFillerImpl(accountRepository);
    
    repositoryFiller.fillDummyAccounts();
    
  }

}
