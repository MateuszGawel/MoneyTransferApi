# MoneyTransferApi
RESTful API for transfering money between accounts

# Technologies used

## Implementation

### Java
As implementation language

### Jooby
https://jooby.org/
For setting up RESTful API. 
In addition Jooby includes few other libraries which were used in my implementation:
- Jackson for JSON parsing
- Guice as dependency injection framework.

## Tests
### JUnit 4.12
Used for unit testing.

### Mockito
Mocking framework used for unit tests.

### Thread Weaver
Provided by mapdb team. Tool for multithreading testing.

### REST-assured
Used for integration test of the RESTful API.

### Hamcrest
Another tool which I used in integration testing. It provides handy set of matchers for assertions.


# API usage
By default application starts on localhost:8080. Available calls:
### Get all accounts
```GET  /accounts```
returns all accounts from repository

### Get account by number
```GET  /accounts/{accountnumber}```
  return one account specified by {accountnumber} or 404 if not found
  
### Transfer money
```POST /accounts/moneytransfer```
  
  body example: 
  ```
  {
    "fromAccount": "122124124214214214214421",
    "toAccount": "542312353523512352352355",
    "amount": 0.521
  }
  ```
  transfer money from one account to another. Both accounts must exists and fromAccount must have sufficient amount.
  - 200 - OK
  - 400 - Bad Request in case when some parameters are missing or in wrong format
  - 500 - Server Error in case when balance is not sufficient or account doesn't exist
  
  # Running application
  In order to run application use following command
   ```
   mvn jooby:run
   ```
   * Maven 3 is required
   
   # Additional notes
   - Controller passes requests to AccountRepository or MoneyTransferService depending of request.
   - API and internal implementation are separated to different packages.
   - AccountDto is used to follow the separation. 
   - AccountConverter converts Account to AccountDTO. Additionally it changes BigDecimal to String to make responses user-friendly. It all depends for what purposes API would have been used. In my case I assumed, that it is not necessary to send whole BigDecimal value with all decimal digits.
   - InMemoryAccountRepository stores Accounts in ConcurrentHashMap which locks on bucket level, what is enough, or even more than enough, because of further thread locking. Key is an accountnumber to ease getting accounts by their numbers.
   - Jooby handles requests in async way. Money transfer could cause many threads related issues so thread safe approach is required.
   - MoneyTransferService locks on Accounts always in the same order, to avoid deadlock and perform withdraw and deposit operations safely.
   - Exceptions are described and sent as a response to indicate error source. It is jooby feature
   - Test contains standard junits with edge cases, integration tests of RESTful API and multithreading tests which correctly simulated potential multithreading issues.
