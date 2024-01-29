package com.shopme.admin.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.shopme.common.entity.User;

public interface UserRepository extends PagingAndSortingRepository<User, Integer>{

	@Query("SELECT u FROM User u WHERE u.email = :email")
	public User getUserByEmail(@Param("email") String email);
	
	@Query("SELECT u FROM User u WHERE CONCAT(u.id, ' ', u.email, ' ', "
			+ " u.firstName, ' ', u.lastName) LIKE %?1%")
	public Page<User> findAll(String keyword, Pageable pageable);
	
	// đếm số lượng bản ghi với ID cụ thể từ cơ sở dữ liệu.
	// neu kq = 0 ||kq = null -> id do khong ton tai
	public Long countById(Integer id); // đặt tên hàm đúng thì spring tự hiểu hàm có chức năng gì.
									   // nên ta ko cần triển khai nó
	
	@Query("UPDATE User u SET u.enabled = ?2 WHERE u.id = ?1")
	@Modifying  // để chỉ định rằng phương thức này là một truy vấn sửa đổi.
	public void updateEnabledStatus(Integer id, boolean enabled);
	// Queries that require a `@Modifying` annotation include INSERT, UPDATE, DELETE, and DDLstatements.
	
	
}
