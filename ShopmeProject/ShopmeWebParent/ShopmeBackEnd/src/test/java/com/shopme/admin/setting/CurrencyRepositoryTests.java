package com.shopme.admin.setting;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Currency;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CurrencyRepositoryTests {

	
	@Autowired
	private CurrencyRepository repo;
	
	@Test
	public void testCreateCurrency() {
		Currency usd = new Currency("United States Dollar", "$", "USD");
		Currency gbp = new Currency("British Pound", "£", "GBP");
		Currency jpy = new Currency("Japanese Yen", "¥", "JPY");
		Currency eur = new Currency("Euro", "€", "EUR");
		Currency rub = new Currency("Russian Ruble", "₽", "RUB");
		Currency krw = new Currency("South Korean Won", "₩", "KRW");
		Currency cny = new Currency("Chinese Yuan", "¥", "CNY");
		Currency brl = new Currency("Brazilian Real", "R$", "BRL");
		Currency aud = new Currency("Australian Dollar", "$", "AUD");
		Currency cad = new Currency("Canadian Dollar", "$", "CAD");
		Currency vnd = new Currency("Vietnamese đồng", "đ", "VND");
		Currency inr = new Currency("Indian Rupee", "₹", "INR");
		
		repo.saveAll(List.of(usd, gbp, jpy, eur, rub, krw, cny, brl,
						     aud, cad, vnd, inr));
		
	}
	
	@Test
	public void testListAllOrderByNameAsc() {
		List<Currency> currencies = repo.findAllByOrderByNameAsc();
		
		currencies.forEach(currency -> System.out.println(currency));
		
		assertThat(currencies.size()).isGreaterThan(0);
	}
}
