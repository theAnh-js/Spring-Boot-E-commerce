package com.shopme.admin.brand;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class BrandRepositoryTests {
	
	
	@Autowired
	private BrandRepository repo;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateBrand() {
//		Category laptops = entityManager.find(Category.class, 6);	
		
//		Brand acer = new Brand("Acer", "brand-logo.png");
//		acer.addCategory(laptops);	
		
//		Brand savedBrand = repo.save(acer);	
//		assertThat(savedBrand.getId()).isGreaterThan(0);
		
		
		Category cellPhoneAndAccessories = entityManager.find(Category.class, 4);			
		Category tablets = entityManager.find(Category.class, 7);
		
		Brand apple = new Brand("Apple", "brand-logo.png");
		
		apple.addCategory(cellPhoneAndAccessories);	
		apple.addCategory(tablets);	
		
		Brand savedBrand = repo.save(apple);	
		assertThat(savedBrand.getId()).isGreaterThan(0);
		
//		Category memory = entityManager.find(Category.class, 29);			
//		Category internalHardDrives = entityManager.find(Category.class, 24);
//		
//		Brand samsung = new Brand("samsung", "brand-logo.png");
//		
//		samsung.addCategory(memory);	
//		samsung.addCategory(internalHardDrives);	
//		
//		Brand savedBrand = repo.save(samsung);	
//		assertThat(savedBrand.getId()).isGreaterThan(0);

	}
	
	@Test
	public void testFindAllBrands() {
		
		List<Brand> listBrands = (List<Brand>) repo.findAll();
		
		for(Brand brand : listBrands) {
			System.out.println(brand);// output like this: Brand [id=3, name=Samsung Electronics, logo=brand-logo.png, categories=[Internal Hard Drives, Memory]]
//			System.out.println(brand.getId() + ", " + brand.getName());
//			Set<Category> listCategories = brand.getCategories();
//			for(Category category : listCategories) {
//				System.out.println(category);
//			}
		}
		
		assertThat(listBrands.size()).isGreaterThan(0);
	}
	
	@Test
	public void testGetBrandById() {
		
		Brand brand = repo.findById(3).get();
		
		System.out.println(brand);
		
		assertThat(brand).isNotNull();
		
	}
	
	@Test
	public void testUpdateBrand() {
		
		Brand brand = repo.findById(3).get();
		
		brand.setName("Samsung Electronics");
		
		Brand updatedBrand = repo.save(brand);
		System.out.println(updatedBrand);
		
		assertThat(updatedBrand).isNotNull();
		
	}
	
	@Test
	public void deleteBrand() {
		Brand brand = repo.findById(11).get();
		repo.delete(brand);
		
		Optional<Brand> result = repo.findById(11);
		assertThat(result.isEmpty());
	}
}