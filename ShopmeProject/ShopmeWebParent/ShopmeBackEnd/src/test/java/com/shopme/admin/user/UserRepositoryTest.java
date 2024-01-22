package com.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTest {

	@Autowired
	private UserRepository repo;

	@Autowired
	private TestEntityManager entityManager;

	@Test
	public void testCreateUser() {
		Role roleAdmin = entityManager.find(Role.class, 2);
		User theanh = new User("minh@gmail.com", "minh123", "MInh", "Nguyen");
		theanh.addRole(roleAdmin);

		User savedUser = repo.save(theanh);

		assertThat(savedUser.getId()).isGreaterThan(0);
	}

	@Test
	public void testCreateNewUserWithTwoRole() {
		User nami = new User("nami@gmail.com", "nami123", "Nami", "Mugiwara");

		Role roleEditor = new Role(3);
		Role roleAssistant = new Role(5);

		nami.addRole(roleAssistant);
		nami.addRole(roleEditor);

		User savedUser = repo.save(nami);
		assertThat(savedUser.getId()).isGreaterThan(0);
	}

	@Test
	public void testListAllUsers() {
		Iterable<User> listUsers = repo.findAll();
		listUsers.forEach(user -> System.out.println(user));
	}

	@Test
	public void testGetUserById() {
		User userLuffy = repo.findById(7).get();
		System.out.println(userLuffy);
		assertThat(userLuffy).isNotNull();
	}
	
	@Test
	public void testUpdateUserDetails() {
		User userLuffy = repo.findById(7).get();
		userLuffy.setEnabled(true);
		userLuffy.setEmail("monkey@gmail.com");
		
		repo.save(userLuffy);
	}
	
	@Test
	public void testUpdateUserRoles() {
		User userHa = repo.findById(5).get();
		
		Role roleAssistant = new Role(2);
		Role roleAdmin = new Role(1);
		
		userHa.getRoles().remove(roleAssistant); 
		// do bên class Role ta đã định nghĩa lại hàm equals, id bằng nhau-> 2 object bằng nhau
		// remove của Set dựa vào đấy để xóa đi role có id là 2
		
		userHa.addRole(roleAdmin);
		
		repo.save(userHa);
	}
	
	@Test
	public void testDeleteUser() {
		Integer userId = 6;
		repo.deleteById(userId);
	}
	
	@Test
	public void testGetUserByEmail() {
		String email = "naddm@gmail.com";
		User user = repo.getUserByEmail(email);
		
		assertThat(user).isNotNull();
	}
	
	@Test
	public void testCountById() {
		Integer id = 1;
		Long countById = repo.countById(id);
		assertThat(countById).isNotNull().isGreaterThan(0);
	}
	
	@Test
	public void testDisableUser() {
		Integer id = 1;
		repo.updateEnabledStatus(id, false);
	}
	
	@Test
	public void testEnableUser() {
		Integer id = 8;
		repo.updateEnabledStatus(id, true);
	}
}
