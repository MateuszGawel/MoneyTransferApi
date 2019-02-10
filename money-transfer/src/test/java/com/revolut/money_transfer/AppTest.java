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

import org.jooby.Status;
import org.jooby.test.JoobyRule;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import com.revolut.money_transfer.api.MoneyTransferRequest;
import com.revolut.money_transfer.internal.App;
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
            .body("balance", is("1,00"));
	}
	
	@Test
    public void testGetAccount222() {
        when()
	        .get("/accounts/222")
	        .then()
	        .body("number", is("222"))
	        .body("balance", is("10,00"));
	}
	
	@Test
    public void testGetAccount333() {
        when()
	        .get("/accounts/333")
	        .then()
	        .body("number", is("333"))
	        .body("balance", is("0,00"));
	}
	
	@Test
    public void testGetAccount444() {
        when()
	        .get("/accounts/444")
	        .then()
	        .body("number", is("444"))
	        .body("balance", is("5,12"));
	}
	
	@Test
    public void testGetAccount555() {
        when()
	        .get("/accounts/555")
	        .then()
	        .body("number", is("555"))
	        .body("balance", is("0,10"));
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
		        .then().statusCode(200);
		
		// VERIFY
        when()
	        .get("/accounts/222")
	        .then()
	        .body("balance", is("9,99"));
        
        when()
	        .get("/accounts/333")
	        .then()
	        .body("balance", is("0,01"));
	}
	
	

	private static void createAccount(String accountNumber, BigDecimal balance) throws AccountNumberDuplicateException {
		app.require(AccountRepository.class).createAccount(accountNumber, balance);
	}
	
	private static void clearRepository() {
		app.require(AccountRepository.class).clear();
	}

}
