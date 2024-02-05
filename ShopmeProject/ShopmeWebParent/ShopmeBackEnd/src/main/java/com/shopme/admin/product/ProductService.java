package com.shopme.admin.product;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Product;

@Service
@Transactional
public class ProductService {
	
	@Autowired
	private ProductRepository repo;
	
	public List<Product> listAll(){
		return (List<Product>) repo.findAll();
	}
	
	
	public Product save(Product product) {
		
		if(product.getId() == null) {
			product.setCreatedTime(new Date());
		}else {
			product.setCreatedTime(product.getCreatedTime());
		}
		
		product.setName(product.getName().trim());
		
		if(product.getAlias() == null || product.getAlias().isEmpty()) {
			product.setAlias(product.getName().replaceAll(" ", "-"));			
		}else {
			product.setAlias(product.getAlias().replaceAll(" ", "-"));
		}
		
		product.setUpdatedTime(new Date());
		
		return repo.save(product);
	}
	
	public String checkUnique(Integer id, String name) {
		
		boolean isCreating = (id == null) || (id == 0);
		
		Product product = repo.findByName(name);
		
		if(isCreating) {
			if(product != null) {
				return "Duplicate";
			}
		}else {
			if(product != null && product.getId() != id) {
				return "Duplicate";
			}
		}
		return "OK";
	}
	
	public void updateStatusProduct(boolean status, Integer id) {
			repo.updateStatusProduct(status, id);
	}
	
	public void deleteProduct(Integer id) throws ProductNotFoundException {
		
		Long countProduct = repo.countById(id);
		
		if(countProduct == null || countProduct == 0) {
			throw new ProductNotFoundException("Not found any product with ID " + id);
		}
		
		repo.deleteById(id);
	}
	
	public Product get(Integer id) throws ProductNotFoundException {
		try {
			return repo.findById(id).get();
		}catch(NoSuchElementException e) {
			throw new ProductNotFoundException("Counld not find any product with ID " + id);
		}
		
	}
	

}
