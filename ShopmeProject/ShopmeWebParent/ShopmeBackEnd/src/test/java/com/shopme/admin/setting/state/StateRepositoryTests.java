package com.shopme.admin.setting.state;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.State;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class StateRepositoryTests {

	@Autowired
	private StateRepository repo;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateStateInUS() {
		Country us = entityManager.find(Country.class, 3);
		
		State cali = new State("California", us);
		State texas = new State("Texas", us);
		State ny = new State("New York", us);
		
		List<State> savedListStates = (List<State>) repo.saveAll(Arrays.asList(cali, texas, ny));
		
		savedListStates.forEach(state -> System.out.println(state));
		
		assertThat(savedListStates).isNotNull();
	}
	
	@Test
	public void testListStateByCountry() {
		Country us = entityManager.find(Country.class, 3);
		
		List<State> listStateInUS = repo.findByCountryOrderByNameAsc(us);
		
		listStateInUS.forEach(state -> System.out.println(state));
		
		assertThat(listStateInUS.size()).isEqualTo(3);
	}
	
	// skip testGetState, testUpdateState, testDeleteState :))
}
