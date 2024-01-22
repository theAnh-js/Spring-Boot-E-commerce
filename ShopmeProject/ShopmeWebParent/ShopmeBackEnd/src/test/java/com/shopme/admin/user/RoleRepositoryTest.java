package com.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.admin.ShopmeBackEndApplication;
import com.shopme.common.entity.Role;

@SpringBootTest(classes = ShopmeBackEndApplication.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class RoleRepositoryTest {

	@Autowired
	private RoleRepository repo;
	
	@Test
	public void testCreateFirstRole() {
		Role roleAdmin = new Role("Admin", "manage everything");
		Role savedRole = repo.save(roleAdmin);
		
		assertThat(savedRole.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testCreateRestRole() {
		Role roleSalesperson = new Role("Salesperson", "manage product price, "
				+ "customer, shipping, orders and sales report");
		
		Role roleEditor = new Role("Editor", "manage categories, brands, "
				+ "products, articles and menus");
		
		Role roleShipper = new Role("Shipper", "view products, view orders, "
				+ "and update order status");
		
		Role roleAssistant = new Role("Assistant", "manage questions and reviews");
		
		repo.saveAll(List.of(roleSalesperson, roleEditor, roleShipper, roleAssistant));
	}
	
	//Bạn sử dụng assertThat để kiểm tra xem việc lưu trữ đã thành công hay không. 
	//Cụ thể, bạn kiểm tra rằng id của savedRole có giá trị lớn hơn 0. 
	//Nếu lưu trữ thành công, thì id của đối tượng Role sẽ được tạo và 
	//có giá trị lớn hơn 0.
	
	//@Rollback(false):
		//Annotation này được sử dụng để tắt tính năng tự động rollback của Spring Boot 
		//sau khi mỗi phương thức kiểm thử chạy. Nếu không có @Rollback(false), 
		//mọi thay đổi trong cơ sở dữ liệu được thực hiện trong phương thức kiểm thử 
		//sẽ bị rollback sau khi kiểm thử hoàn thành.
}
