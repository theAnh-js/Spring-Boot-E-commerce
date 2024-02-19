package com.shopme.admin.setting.country;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Country;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CountryRepositoryTests {

	@Autowired
	private CountryRepository repo;
	
	
	
	@Test
	public void testCreateCountry() {
		Country vn = new Country("Vietnam", "VN");
		Country cn = new Country("China", "CN");
		Country us = new Country("America", "US");
		
		List<Country> savedListCountry = (List<Country>) repo.saveAll(Arrays.asList(vn, cn, us));
		
		for(Country country : savedListCountry) {
			System.out.println(country);
		}
		
		assertThat(savedListCountry.size()).isEqualTo(3);
	}
	
	@Test
	public void testListCountries() {
		
		List<Country> listCountries = repo.findAllByOrderByNameAsc();
		
		for(Country country : listCountries) {
			System.out.println(country);
		}
		
		assertThat(listCountries.size()).isEqualTo(3);
		
	}
	
	@Test
	public void testUpdateCountry() {
		Country vn = repo.findById(1).get();
		
		vn.setName("VietNam");
		
		Country updatedVN = repo.save(vn);
		
		System.out.println(updatedVN);
		
		assertThat(updatedVN.getName()).isEqualTo("VietNam");
	}
	
	// skip test delete country :))
}
