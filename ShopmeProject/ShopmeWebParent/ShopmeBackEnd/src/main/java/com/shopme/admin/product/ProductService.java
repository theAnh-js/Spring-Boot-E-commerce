package com.shopme.admin.product;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Product;
import com.shopme.common.exception.ProductNotFoundException;

@Service
@Transactional
public class ProductService {
	
	public static final int PRODUCTS_PER_PAGE = 5;
	
	@Autowired
	private ProductRepository repo;
	
	public List<Product> listAll(){
		return (List<Product>) repo.findAll();
	}
	
	public Page<Product> listByPage(int pageNum, String sortField, String sortDir, 
			String keywork, Integer categoryId){
		
		Sort sort = Sort.by(sortField);
		
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
				
		Pageable pageable = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE, sort);
		
		if(keywork != null) {
			
			if(categoryId != null) { // khi vua search bang car keyword va categoryId
				String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
				return repo.searchInCategory(categoryId, categoryIdMatch, keywork, pageable);
			}
			// chi search bang keyword
			return repo.findAll(keywork, pageable); // paging tren danh sach tim bang 'keyword' thong qua query -> BrandRepository			
		}
		
		if(categoryId != null) { // chi search bang categoryId
			String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
			return repo.findAllInCategory(categoryId, categoryIdMatch, pageable);
		}
		
		return repo.findAll(pageable);  // ko search theo gi ca.
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
	
	public void saveProductPrice(Product productInForm) {
		Product	productInBD = repo.findById(productInForm.getId()).get();
		productInBD.setCost(productInForm.getCost());
		productInBD.setPrice(productInForm.getPrice());
		productInBD.setDiscountPercent(productInForm.getDiscountPercent());
		
		repo.save(productInBD);
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
