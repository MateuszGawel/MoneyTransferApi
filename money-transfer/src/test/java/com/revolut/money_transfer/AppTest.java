package com.revolut.money_transfer;

import static io.restassured.RestAssured.get;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import java.math.BigDecimal;

import org.hamcrest.Matchers;
import org.jooby.Status;
import org.jooby.test.JoobyRule;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import com.revolut.money_transfer.internal.App;
import com.revolut.money_transfer.internal.exception.AccountNumberDuplicateException;
import com.revolut.money_transfer.internal.repository.AccountRepository;
import com.revolut.money_transfer.internal.repository.InMemoryAccountRepository;
import static io.restassured.RestAssured.*;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;
import static org.hamcrest.Matchers.*;
import io.restassured.RestAssured;
import io.restassured.RestAssured;

import org.jooby.test.JoobyRule;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static io.restassured.RestAssured.*;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;
import static org.hamcrest.Matchers.*;

public class AppTest {

	public static App app = new App();
	@ClassRule
	public static JoobyRule joobyRule = new JoobyRule(app);

	@BeforeClass
	public static void config() {
		RestAssured.config = RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL));
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
		createAccount("111", BigDecimal.ZERO);
		createAccount("222", BigDecimal.ZERO);
		createAccount("333", BigDecimal.ZERO);
		createAccount("444", BigDecimal.ZERO);
		createAccount("555", BigDecimal.ZERO);

		when()
			.get("/accounts")
			.then()
			.assertThat()
			.body("$", hasSize(5));
		
	}
	
	@Test
    public void testGetAccount() throws AccountNumberDuplicateException {

		createAccount("111", BigDecimal.ONE);
		createAccount("222", BigDecimal.TEN);

        when()
            .get("/accounts/111")
            .then()
            .body("number", is("111"))
            .body("balance", is("1.00"));

        when()
	        .get("/accounts/222")
	        .then()
	        .body("number", is("222"))
	        .body("balance", is("10.00"));
    }
	
	

	private void createAccount(String accountNumber, BigDecimal balance) throws AccountNumberDuplicateException {
		app.require(AccountRepository.class).createAccount(accountNumber, balance);
	}

}
