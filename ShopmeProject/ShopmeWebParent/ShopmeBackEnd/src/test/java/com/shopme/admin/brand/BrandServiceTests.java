package com.shopme.admin.brand;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.shopme.common.entity.Brand;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class BrandServiceTests {
	
	@MockBean
	private BrandRepository repo;
	
	@InjectMocks
	private BrandService service;
	
	
	@Test
	public void testCheckUniqueInNewModeReturnDuplate() {
		
		String name = "Acer";
		Integer id = null;
		
		Brand brand = new Brand(id, name);
		
		Mockito.when(repo.findByName(name)).thenReturn(brand);
		
		String result = service.checkUnique(id, name);
		
		assertThat(result).isEqualTo("Duplicate");
	}
	
	@Test
	public void testCheckUniqueInNewModeReturnOK() {
		
		String name = "Nokia";
		Integer id = null;
		
		
		Mockito.when(repo.findByName(name)).thenReturn(null);
		
		String result = service.checkUnique(id, name);
		assertThat(result).isEqualTo("OK");
	}
	
	@Test
	public void testCheckUniqueInEditModeReturnDuplicate() {
		
		Integer id = 1;
		String name = "abc";
		
		Brand brand = new Brand(2, name);
		
		Mockito.when(repo.findByName(name)).thenReturn(brand); // khi goi findByName(name) => return brand co id = 2;
		
		String result = service.checkUnique(id, name); // truyen id = 1, va name giong nhau -> trung voi bran khac
		
		assertThat(result).isEqualTo("Duplicate");
	}
	
	@Test
	public void testCheckUniqueInEditModeReturnOK() {
		
		Integer id = 1;
		String name = "abc";
		
		Brand brand = new Brand(id, name);
		
		Mockito.when(repo.findByName(name)).thenReturn(brand);
		
		String result = service.checkUnique(id, name);
		
		assertThat(result).isEqualTo("OK");
		
	}

}
