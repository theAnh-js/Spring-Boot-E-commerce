package com.shopme.admin.setting.state;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopme.admin.setting.country.CountryRepository;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.State;

@SpringBootTest
@AutoConfigureMockMvc
public class StateRestControllerTests {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	CountryRepository countryRepo;

	@Autowired
	StateRepository stateRepo;

	@Test
	@WithMockUser(username = "theanh@gmail.com", password = "anh12345", roles = "ADMIN")
	public void testListStatesByCountry() throws Exception {

		Integer countryId = 3;
		String url = "/states/" + countryId;
		MvcResult result = mockMvc.perform(get(url)).andExpect(status().isOk()).andDo(print()).andReturn();

		String jsonResponse = result.getResponse().getContentAsString();

		State[] states = objectMapper.readValue(jsonResponse, State[].class);
		for (State state : states) {
			System.out.println(state);
		}

		assertThat(states).hasSizeGreaterThan(0);
	}

	@Test
	@WithMockUser(username = "theanh@gmail.com", password = "anh12345", roles = "ADMIN")
	public void testCreateState() throws Exception {

		Integer countryId = 2;
		Country country = countryRepo.findById(countryId).get();
		String url = "/states/save";

		String stateName = "Hongkong";
		State newState = new State(stateName, country);

		MvcResult result = mockMvc.perform(post(url).contentType("application/json")
				.content(objectMapper.writeValueAsString(newState)).with(csrf())).andDo(print())
				.andExpect(status().isOk()).andReturn();

		String response = result.getResponse().getContentAsString();

		Integer countryIdResult = Integer.parseInt(response);

		State savedState = stateRepo.findById(countryIdResult).get();

		assertThat(savedState.getName()).isEqualTo(stateName);

	}
}