package com.shopme.admin.category;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.shopme.common.entity.Category;

public interface CategoryRepository extends PagingAndSortingRepository<Category, Integer> {

	@Query("SELECT c FROM Category c WHERE c.parent.id IS NULL") // lay ra cac category root - category bac cao nhat
	public List<Category> findRootCategories(Sort sort);
	
	@Query("SELECT c FROM Category c WHERE c.parent.id IS NULL") // lay ra cac category root - category bac cao nhat
	public Page<Category> findRootCategories(Pageable pageable);
	
	@Query("SELECT c FROM Category c WHERE c.name LIKE %?1%")
	public Page<Category> search(String keyword, Pageable pageable);
	
	@Query("UPDATE Category c SET c.enabled = ?2 WHERE c.id = ?1")
	@Modifying  // để chỉ định rằng phương thức này là một truy vấn sửa đổi.
	public void updateEnabledStatus(Integer id, boolean enabled);
	// Queries that require a `@Modifying` annotation include INSERT, UPDATE, DELETE, and DDLstatements.	
	
	public Category findByName(String name);
	
	public Category findByAlias(String alias);
	
	//@Query("SELECT c FROM Category c WHERE c.id NOT IN (SELECT parent.id FROM Category c WHERE c.parent.id IS NOT NULL)")
	@Query(value = "select * from shopmedb.categories as c where c.id not in(\r\n"
			+ "			select parent_id from shopmedb.categories  WHERE parent_id IS NOT NULL  \r\n"
			+ "		)", nativeQuery = true) 
	public List<Category> findCategoryNoChild();

}

