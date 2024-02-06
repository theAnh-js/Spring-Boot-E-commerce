package com.shopme.admin.product;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.shopme.common.entity.Product;

public interface ProductRepository extends PagingAndSortingRepository<Product, Integer> {

	public Product findByName(String name);
	
	@Query("UPDATE Product p SET p.enabled = ?1 WHERE p.id = ?2")
	@Modifying
	public void updateStatusProduct(boolean status, Integer id);
	
	public Long countById(Integer id);
}
