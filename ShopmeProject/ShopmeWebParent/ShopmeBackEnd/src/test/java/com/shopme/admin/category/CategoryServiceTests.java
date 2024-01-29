package com.shopme.admin.category;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.shopme.common.entity.Category;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class CategoryServiceTests {
	
	// Kiem tra tang service su dung tang repository duoc mock 
	// de co the kiem thu ma khong can thuc su thao tac tren database.
	
	@MockBean
	private CategoryRepository repo; // tao 1 mock bean phien ban gia mao
	
	@InjectMocks
	private CategoryService service; // tiem mock tang service de kiem thu phuong thuc checkUnique cua no 
	
	@Test
	public void testCheckUniqueInNewModeReturnDuplicateName() {

		Integer id = null;
		String name = "Computers";
		String alias = "abc";
		
		Category category = new Category(id, name, alias);
		
		Mockito.when(repo.findByName(name)).thenReturn(category);
		Mockito.when(repo.findByAlias(alias)).thenReturn(null);
		
		String result = service.checkUnique(id, name, alias);
		
		assertThat(result).isEqualTo("DuplicateName");
	}
	
	@Test
	public void testCheckUniqueInNewModeReturnDuplicateAlias() {

		Integer id = null;
		String name = "Something";
		String alias = "books";
		
		Category category = new Category(id, name, alias);
		
		Mockito.when(repo.findByName(name)).thenReturn(null);
		Mockito.when(repo.findByAlias(alias)).thenReturn(category);
		
		String result = service.checkUnique(id, name, alias);
		
		assertThat(result).isEqualTo("DuplicateAlias");
	}
	
	@Test
	public void testCheckUniqueInNewModeReturnOk() {

		Integer id = null;
		String name = "Something";
		String alias = "abc";
		
		Mockito.when(repo.findByName(name)).thenReturn(null);
		Mockito.when(repo.findByAlias(alias)).thenReturn(null);
		
		String result = service.checkUnique(id, name, alias);
		
		assertThat(result).isEqualTo("OK");
	}
	
	@Test
	public void testCheckUniqueInEditModeReturnDuplicateName() {

		Integer id = 1;
		String name = "Computers";
		String alias = "abc";
		
		Category category = new Category(2, name, alias);
		
		Mockito.when(repo.findByName(name)).thenReturn(category); // gia su no se tra ve category co id = 2
		Mockito.when(repo.findByAlias(alias)).thenReturn(null);
		
		String result = service.checkUnique(id, name, alias); 
		
		assertThat(result).isEqualTo("DuplicateName");
	}
	
	@Test
	public void testCheckUniqueInEditModeReturnDuplicateAlias() {

		Integer id = 1;
		String name = "Computers";
		String alias = "abc";
		
		Category category = new Category(2, name, alias);
		
		//Mockito.when(repo.findByName(name)).thenReturn(null); 
		Mockito.when(repo.findByAlias(alias)).thenReturn(category);
		
		String result = service.checkUnique(id, name, alias); 
		
		assertThat(result).isEqualTo("DuplicateAlias");
	}
	
	@Test
	public void testCheckUniqueInEditModeReturnOK1() {

		Integer id = 1;
		String name = "Computers";
		String alias = "abc";
		
		Category category = new Category(id, name, alias);
		
		Mockito.when(repo.findByName(name)).thenReturn(null); 
		Mockito.when(repo.findByAlias(alias)).thenReturn(category);
		
		String result = service.checkUnique(id, name, alias); 
		
		assertThat(result).isEqualTo("OK");
	}
	
	@Test
	public void testCheckUniqueInEditModeReturnOK() {

		Integer id = 1;
		String name = "Computers";
		String alias = "abc";
		
		Category category = new Category(2, name, alias);
		
		Mockito.when(repo.findByName(name)).thenReturn(null); 
		Mockito.when(repo.findByAlias(alias)).thenReturn(null);
		
		String result = service.checkUnique(id, name, alias); 
		
		assertThat(result).isEqualTo("OK");
	}
	
	// Phuong thuc nay dung de memo.
	@Test
	public void testCheckUniqueInNewModeReturnDuplicateNameV0() {
		// chuan bi du lieu cho kiem thu
		Integer id = null;
		String name = "Computers";
		String alias = "abc";
		
		Category category = new Category(id, name, alias); // tao du lieu gia
		
		// gia lap khi goi findByName(name) thi se tra ra category nhu
		// dang tuong tac voi database that. Nhung thuc te thì KHONG.
		Mockito.when(repo.findByName(name)).thenReturn(category);
		
		//tien hanh kiem thu ham checkUnique trong service
		// khi vao den phuong thuc checkUnique, no se goij ham findByName de
		// khi nay, ta da gia mao o tren la neu name = "Computers" thi
		// se tra ve category cho du Computers co trong database hay khong
		// khong quan tam, vi tra ve category -> nghia la != null -> checkUnique se return "DuplicateName".
		String result = service.checkUnique(id, name, alias);
		
		assertThat(result).isEqualTo("DuplicateName");
	}
}
