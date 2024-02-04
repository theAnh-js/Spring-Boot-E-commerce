package com.shopme.admin.brand;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Brand;

@Service
public class BrandService {
	
	public static final int BRANDS_PER_PAGE = 10;
	
	@Autowired
	private BrandRepository repo;
	
	public List<Brand> listAll(){
		
		return repo.findAll();
	}
	
	public Page<Brand> listByPage(int pageNum, String sortField, String sortDir, String keywork){
		
		Sort sort = Sort.by(sortField);
		
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
				
		Pageable pageable = PageRequest.of(pageNum - 1, BRANDS_PER_PAGE, sort);
		
		if(keywork == null) {
			return repo.findAll(pageable); // paging tren tat ca
		}
		
		return repo.findAll(keywork, pageable); // paging tren danh sach tim bang 'keyword' thong qua query -> BrandRepository
	}
	
	public Brand save(Brand brand) {
		return repo.save(brand);
	}
	
	public Brand get(int id) throws BrandNotFoundException {
		
		try {
			return repo.findById(id).get();
		}catch(NoSuchElementException e) {
			throw new BrandNotFoundException("Not found any brand with ID " + id );
		}
		
	}
	
	public void delete(int id) throws BrandNotFoundException {
		Brand brand = this.get(id);
		repo.delete(brand);
		
		//C2:
//		Long countById = repo.countById(id);
//		if(countById == null || countById == 0) {
//			throw new BrandNotFoundException("Not found any brand with ID " + id );
//		}
//		repo.deleteById(id);
	}
	
	
	public String checkUnique(Integer id, String name) {
		
		boolean isCreatingNew = (id == null || id == 0);
		
		Brand brand = repo.findByName(name);
		
		if(isCreatingNew) {		
			if(brand != null) {
				return "Duplicate";
			}
			
		}else {
			if(brand != null && brand.getId() != id) { // truong hop chinh sua name, nhung name lai trung voi brand khac 
				return "Duplicate";
			}
		}
		
		return "OK";
	}
	
	

}