package com.shopme.admin.product;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ProductRepositoryTests {

	@Autowired
	private ProductRepository repo;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateProduct() {
		Brand brand = entityManager.find(Brand.class, 37);
		Category category = entityManager.find(Category.class, 5);
		
		Product product = new Product();
		product.setName("Acer Aspire Desktop");
		product.setAlias("acer_aspire_desktop");
		product.setShortDescription("Short descript for Acer Aspire Desktop");
		product.setFullDescription("Full descript for Acer Aspire Desktop");
		
		product.setBrand(brand);
		product.setCategory(category);
		
		product.setPrice(567);
		product.setCost(350);
		product.setCreatedTime(new Date());
		product.setUpdatedTime(new Date());
		
		product.setEnabled(true);
		product.setInStock(true);
		
		Product savedProduct = repo.save(product);
		
		assertThat(savedProduct).isNotNull();
		assertThat(savedProduct.getId()).isGreaterThan(0);
		
	}
	
	@Test
	public void testListAllProducts() {
		Iterable<Product> iterableProduct = repo.findAll();
		
		iterableProduct.forEach(product -> System.out.println(product));
	}
	
	@Test
	public void testGetProduct() {
		Integer id = 2;
		
		Product product = repo.findById(id).get();
		
		System.out.println(product);
		
		assertThat(product).isNotNull();
	}
	
	@Test
	public void testUpdateProduct() {
		Integer id = 1;
		Product product = repo.findById(id).get();
		
		product.setPrice(699);
		
		repo.save(product);
		
		Product updatedProduct = entityManager.find(Product.class, 1);
		
		assertThat(updatedProduct.getPrice()).isEqualTo(699);
	}
	
	@Test
	public void testDeleteProduct() {
		Integer id = 3;
		repo.deleteById(id);
		
		Optional<Product> result = repo.findById(id);
		
		assertThat(!result.isPresent());
	}
}




//Khi bạn sử dụng @AutoConfigureTestDatabase(replace = Replace.NONE), 
//Spring Boot sẽ giữ nguyên cấu hình cơ sở dữ liệu 
//được định nghĩa trong ứng dụng thực tế của bạn trong quá trình 
//thực hiện các bài test thay vì thay thế nó bằng một cấu hình 
//cơ sở dữ liệu tạm thời được sử dụng trong bài test.
//	====>>> Lựa chọn này thường hữu ích khi bạn muốn sử dụng 
//cùng một cấu hình cơ sở dữ liệu trong bài test 
//như cấu hình trong ứng dụng thực tế của bạn để 
//đảm bảo tính nhất quán giữa môi trường kiểm thử và môi trường sản xuất.