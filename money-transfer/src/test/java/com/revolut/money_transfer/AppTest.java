package com.revolut.money_transfer;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.jooby.Status;
import org.jooby.test.JoobyRule;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import com.revolut.money_transfer.api.MoneyTransferRequest;
import com.revolut.money_transfer.internal.exception.AccountNumberDuplicateException;
import com.revolut.money_transfer.internal.repository.AccountRepository;

import io.restassured.RestAssured;

public class AppTest {

	public static App app = new App();
	@ClassRule
	public static JoobyRule joobyRule = new JoobyRule(app);

	@BeforeClass
	public static void config() throws AccountNumberDuplicateException {
		RestAssured.config = RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL));
	}
	
	@Before
	public void prepareRepository() throws AccountNumberDuplicateException {
		clearRepository();
		createAccount("111", BigDecimal.ONE);
		createAccount("222", BigDecimal.TEN);
		createAccount("333", BigDecimal.ZERO);
		createAccount("444", new BigDecimal(5.12));
		createAccount("555", new BigDecimal(0.10));
	}

	@Test
	public void testAppResponding() {
		get("/accounts")
			.then()
			.assertThat()
			.body(notNullValue());
	}
	
	@Test
	public void testWrongAddress() {
		when()
			.get("/someWrongAddress")
			.then()
			.statusCode(Status.NOT_FOUND.value());
	}

	@Test
	public void testGetAll() throws AccountNumberDuplicateException {

		when()
			.get("/accounts")
			.then()
			.assertThat()
			.body("number", hasItems("111", "222", "333", "444", "555"));
		
	}
	
	@Test
    public void testGetAccount111() {

        when()
            .get("/accounts/111")
            .then()
            .body("number", is("111"))
            .body("balance", is(toStringAmount(BigDecimal.ONE)));
	}
	
	@Test
    public void testGetAccount222() {
        when()
	        .get("/accounts/222")
	        .then()
	        .body("number", is("222"))
	        .body("balance", is(toStringAmount(BigDecimal.TEN)));
	}
	
	@Test
    public void testGetAccount333() {
        when()
	        .get("/accounts/333")
	        .then()
	        .body("number", is("333"))
	        .body("balance", is(toStringAmount(BigDecimal.ZERO)));
	}
	
	@Test
    public void testGetAccount444() {
        when()
	        .get("/accounts/444")
	        .then()
	        .body("number", is("444"))
	        .body("balance", is(toStringAmount(new BigDecimal(5.12))));
	}
	
	@Test
    public void testGetAccount555() {
        when()
	        .get("/accounts/555")
	        .then()
	        .body("number", is("555"))
	        .body("balance", is(toStringAmount(new BigDecimal(0.10))));
    }
	
	@Test
	public void testTransfer() {
		MoneyTransferRequest request = new MoneyTransferRequest();
		request.setFromAccount("222");
		request.setToAccount("333");
		request.setAmount(new BigDecimal(0.01));
		
		given()
			.when()
		        .body(request)
		        .contentType("application/json")
		        .post("/accounts/moneytransfer")
		        .then().statusCode(Status.OK.value());
		
		// VERIFY
        when()
	        .get("/accounts/222")
	        .then()
	        .body("balance", is(toStringAmount(new BigDecimal(9.99))));
        
        when()
	        .get("/accounts/333")
	        .then()
	        .body("balance", is(toStringAmount(new BigDecimal(0.01))));
	}
	
	@Test
	public void testTransfer_moreThanPossible() {
		MoneyTransferRequest request = new MoneyTransferRequest();
		request.setFromAccount("222");
		request.setToAccount("333");
		request.setAmount(new BigDecimal(11));
		
		given()
			.when()
		        .body(request)
		        .contentType("application/json")
		        .post("/accounts/moneytransfer")
		        .then().statusCode(Status.SERVER_ERROR.value());
		
		// VERIFY
        when()
	        .get("/accounts/222")
	        .then()
	        .body("balance", is(toStringAmount(new BigDecimal(10.0))));
        
        when()
	        .get("/accounts/333")
	        .then()
	        .body("balance", is(toStringAmount(new BigDecimal(0.00))));
	}
	
	@Test
	public void testTransfer_sameAccount() {
		MoneyTransferRequest request = new MoneyTransferRequest();
		request.setFromAccount("333");
		request.setToAccount("333");
		request.setAmount(new BigDecimal(11));
		
		given()
			.when()
		        .body(request)
		        .contentType("application/json")
		        .post("/accounts/moneytransfer")
		        .then().statusCode(Status.BAD_REQUEST.value());
		
		// VERIFY
        when()
	        .get("/accounts/222")
	        .then()
	        .body("balance", is(toStringAmount(new BigDecimal(10.0))));
        
        when()
	        .get("/accounts/333")
	        .then()
	        .body("balance", is(toStringAmount(new BigDecimal(0.00))));
	}
	
	@Test
	public void testTransfer_zeroAmount() {
		MoneyTransferRequest request = new MoneyTransferRequest();
		request.setFromAccount("222");
		request.setToAccount("333");
		request.setAmount(new BigDecimal(0));
		
		given()
			.when()
		        .body(request)
		        .contentType("application/json")
		        .post("/accounts/moneytransfer")
		        .then().statusCode(Status.BAD_REQUEST.value());
		
		// VERIFY
        when()
	        .get("/accounts/222")
	        .then()
	        .body("balance", is(toStringAmount(new BigDecimal(10.0))));
        
        when()
	        .get("/accounts/333")
	        .then()
	        .body("balance", is(toStringAmount(new BigDecimal(0.00))));
	}
	
	@Test
	public void testTransfer_negativeAmount() {
		MoneyTransferRequest request = new MoneyTransferRequest();
		request.setFromAccount("222");
		request.setToAccount("333");
		request.setAmount(new BigDecimal(-10));
		
		given()
			.when()
		        .body(request)
		        .contentType("application/json")
		        .post("/accounts/moneytransfer")
		        .then().statusCode(Status.BAD_REQUEST.value());
		
		// VERIFY
        when()
	        .get("/accounts/222")
	        .then()
	        .body("balance", is(toStringAmount(new BigDecimal(10.0))));
        
        when()
	        .get("/accounts/333")
	        .then()
	        .body("balance", is(toStringAmount(new BigDecimal(0.00))));
	}
	
	@Test
	public void testTransfer_emptyAccount() {
		MoneyTransferRequest request = new MoneyTransferRequest();
		request.setFromAccount("");
		request.setToAccount("333");
		request.setAmount(new BigDecimal(11));
		
		given()
			.when()
		        .body(request)
		        .contentType("application/json")
		        .post("/accounts/moneytransfer")
		        .then().statusCode(Status.SERVER_ERROR.value());
		
		// VERIFY
        when()
	        .get("/accounts/222")
	        .then()
	        .body("balance", is(toStringAmount(new BigDecimal(10.0))));
        
        when()
	        .get("/accounts/333")
	        .then()
	        .body("balance", is(toStringAmount(new BigDecimal(0.00))));
	}
	
	@Test
	public void testTransfer_nullAccount() {
		MoneyTransferRequest request = new MoneyTransferRequest();
		request.setFromAccount(null);
		request.setToAccount("333");
		request.setAmount(new BigDecimal(11));
		
		given()
			.when()
		        .body(request)
		        .contentType("application/json")
		        .post("/accounts/moneytransfer")
		        .then().statusCode(Status.BAD_REQUEST.value());
		
		// VERIFY
        when()
	        .get("/accounts/222")
	        .then()
	        .body("balance", is(toStringAmount(new BigDecimal(10.0))));
        
        when()
	        .get("/accounts/333")
	        .then()
	        .body("balance", is(toStringAmount(new BigDecimal(0.00))));
	}
	
	@Test
	public void testTransfer_notExistingFromAccount() {
		MoneyTransferRequest request = new MoneyTransferRequest();
		request.setFromAccount("000");
		request.setToAccount("333");
		request.setAmount(new BigDecimal(11));
		
		given()
			.when()
		        .body(request)
		        .contentType("application/json")
		        .post("/accounts/moneytransfer")
		        .then().statusCode(Status.SERVER_ERROR.value());
		
		// VERIFY
        when()
	        .get("/accounts/222")
	        .then()
	        .body("balance", is(toStringAmount(new BigDecimal(10.0))));
        
        when()
	        .get("/accounts/333")
	        .then()
	        .body("balance", is(toStringAmount(new BigDecimal(0.00))));
	}
	
	@Test
	public void testTransfer_notExistingToAccount() {
		MoneyTransferRequest request = new MoneyTransferRequest();
		request.setFromAccount("222");
		request.setToAccount("000");
		request.setAmount(new BigDecimal(11));
		
		given()
			.when()
		        .body(request)
		        .contentType("application/json")
		        .post("/accounts/moneytransfer")
		        .then().statusCode(Status.SERVER_ERROR.value());
		
		// VERIFY
        when()
	        .get("/accounts/222")
	        .then()
	        .body("balance", is(toStringAmount(new BigDecimal(10.0))));
        
        when()
	        .get("/accounts/333")
	        .then()
	        .body("balance", is(toStringAmount(new BigDecimal(0.00))));
	}
	

	private void createAccount(String accountNumber, BigDecimal balance) throws AccountNumberDuplicateException {
		app.require(AccountRepository.class).createAccount(accountNumber, balance);
	}
	
	private void clearRepository() {
		app.require(AccountRepository.class).clear();
	}
	
	private String toStringAmount(BigDecimal value) {
		//locale may change between systems so we need to have the same separator in string amount values
		DecimalFormat df = new DecimalFormat("#0.00##");
		return df.format(value);
	}

}
