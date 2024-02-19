package com.shopme.admin.setting.state;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.admin.setting.country.CountryRepository;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.State;

@RestController
public class StateRestController {

	@Autowired
	private StateRepository stateRepo;
	
	@Autowired
	private CountryRepository countryRepo;
	
	@GetMapping("/states/list_by_country/{countryId}")
	public List<StateDTO> listByCountry(@PathVariable("countryId") Integer countryId){
		List<StateDTO> listStatesDTO = new ArrayList<>();
		Optional<Country> optionalCountry = countryRepo.findById(countryId);
		Country country = null;
		if(optionalCountry.isPresent()) country = optionalCountry.get();
		
		System.out.println(country);
		List<State> listState = stateRepo.findByCountryOrderByNameAsc(country);
		listState.forEach(state -> {
			System.out.println(state);
			StateDTO stateDTO = new StateDTO(state.getId(), state.getName());
			listStatesDTO.add(stateDTO);
		});
		
		return listStatesDTO;
	}
	
	@PostMapping("/states/save")
	public String save(@RequestBody State state) {
		State savedState = stateRepo.save(state);
		
		return String.valueOf(savedState.getId());
	}
	
	@DeleteMapping("/states/delete/{id}")
	public void delete(@PathVariable("id") Integer id) {
		stateRepo.deleteById(id);
	}
	
}
