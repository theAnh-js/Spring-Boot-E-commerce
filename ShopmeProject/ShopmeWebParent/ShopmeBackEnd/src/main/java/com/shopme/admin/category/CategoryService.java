package com.shopme.admin.category;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Category;

@Service
@Transactional
public class CategoryService {
	
	public static final int ROOT_CATEGORIES_PER_PAGE = 4;
	
	@Autowired
	private CategoryRepository repo;
	
	
	public Category get(int id) throws CategoryNotFoundException {
		
		try {
			return repo.findById(id).get();
		}catch(NoSuchElementException e) {
			throw new CategoryNotFoundException("Could not find any category with ID " + id);
		}	
	}
	
	public List<Category> listAll(String sortDir){
		
		Sort sort = Sort.by("name");
		
		if(sortDir.equals("asc")) {
			sort = sort.ascending();
		}else if(sortDir.equals("desc")) {
			sort = sort.descending();
		}
		
		List<Category> rootCategories = repo.findRootCategories(sort);
		return listHierarchicalCategories(rootCategories, sortDir);
	}
	
	public List<Category> listByPage(CategoryPageInfo pageInfo, int pageNum, String sortDir){
		
		Sort sort = Sort.by("name");
		
		if(sortDir.equals("asc")) {
			sort = sort.ascending();
		}else if(sortDir.equals("desc")) {
			sort = sort.descending();
		}
		
		Pageable pageable = PageRequest.of(pageNum - 1, ROOT_CATEGORIES_PER_PAGE, sort);   // khi phan trang khong the thieu Pageable nay
		
		Page<Category> pageCategories = repo.findRootCategories(pageable); // De kieu du lieu la Page de ow duoi co the: getTotalElements, getTotalPages
	    
	    pageInfo.setTotalElements(pageCategories.getTotalElements());
	    pageInfo.setTotalPages(pageCategories.getTotalPages());
	    
		List<Category>	rootCategories = pageCategories.getContent();

		return listHierarchicalCategories(rootCategories, sortDir);
	}
	
	public List<Category> listHierarchicalCategories(List<Category> rootCategories, String sortDir){
		List<Category> hierarchicalCategories = new ArrayList<>();
		
		for(Category rootCategory : rootCategories) {
			hierarchicalCategories.add(Category.copyFull(rootCategory));
			
			Set<Category> children = sortSubCategories(rootCategory.getChildren(), sortDir);
			
			for(Category subCategory : children) {
				String name = "--" + subCategory.getName();
				hierarchicalCategories.add(Category.copyFull(subCategory, name));
				
				listSubHierarchicalCategories(hierarchicalCategories, sortDir, subCategory, 1);
			}
		}
		
		//for(Category cat : hierarchicalCategories) {
		//	System.out.println(cat.getName());
		//}
		return hierarchicalCategories;
	}
	
	public void listSubHierarchicalCategories(List<Category> hierarchicalCategories, String sortDir,
			Category parent, int subLevel) {
		
		Set<Category> children = sortSubCategories(parent.getChildren(), sortDir);
		int newSubLevel = subLevel + 1;
		
		for(Category subCategory : children) {
			String name = "";
			for(int i = 0; i < newSubLevel; i++) {
				name += "--";
			}
			name += subCategory.getName();
			
			hierarchicalCategories.add(Category.copyFull(subCategory, name));
			
			listSubHierarchicalCategories(hierarchicalCategories, sortDir, subCategory, newSubLevel);
		}
	}
	
	public Category save(Category category) {
		return repo.save(category);
	}
	
	public List<Category> listCategoriesUsedInform(){
		
		List<Category> categoriesUsedInForm = new ArrayList<>();
		
		Iterable<Category> categoriesInDB =  repo.findRootCategories(Sort.by("name").ascending());
		
		
		for(Category category : categoriesInDB) {
			if(category.getParent() == null) {
				categoriesUsedInForm.add(Category.copyIdAndName(category));
				
				Set<Category> children = sortSubCategories(category.getChildren());
				for(Category subCategory : children) {
					String name = "--" + subCategory.getName();
					categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));
					
					listSubCategoriesUsedInForm(categoriesUsedInForm, subCategory, 1);
				}
				
			}
		}
		
		return categoriesUsedInForm;
	}
	
	private void listSubCategoriesUsedInForm(List<Category> categoriesUsedInForm, Category parent, int subLevel) {
		int newSubLevel = subLevel + 1;
		
		Set<Category> children = sortSubCategories(parent.getChildren());
		
		for(Category subCategory : children) {
			String name = "";
			for(int i = 0; i < newSubLevel; i++) {
				name += "--";
			}
			name += subCategory.getName();
			categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));
			
			listSubCategoriesUsedInForm(categoriesUsedInForm, subCategory, newSubLevel);
		}
		
	}
	
	public void updateCategoryEnabledStatus(Integer id, boolean enabled) {
		repo.updateEnabledStatus(id, enabled);
	}
	
	public void delete(Category category) {
		repo.delete(category);
	}
	
	public String checkUnique(Integer id, String name, String alias) {
		boolean isCreatingNew = (id == null || id == 0);
		
		Category categoryByName = repo.findByName(name);
		
		if(isCreatingNew) {  // khi tao moi(create)
			if(categoryByName != null) {
				return "DuplicateName";
			}else {
				Category categoryByAlias = repo.findByAlias(alias);
				if(categoryByAlias != null) {
					return "DuplicateAlias";
				}
			}
		}else { // khi co id(khi edit)
			if(categoryByName != null && categoryByName.getId() != id) {
				return "DuplicateName";
			}
			
			Category categoryByAlias = repo.findByAlias(alias);
			if(categoryByAlias != null && categoryByAlias.getId() != id) {
				return "DuplicateAlias";
			}
		}
		return "OK";
	}
	
	private SortedSet<Category> sortSubCategories(Set<Category> children){
		return sortSubCategories(children, "asc");
	}
	
	private SortedSet<Category> sortSubCategories(Set<Category> children, String sortDir){
		SortedSet<Category> sortedChildren = new TreeSet<>(new Comparator<Category>() {

			@Override
			public int compare(Category cat1, Category cat2) {
				if(sortDir.equals("asc")) {
					return cat1.getName().compareTo(cat2.getName());
				}else {
					return cat2.getName().compareTo(cat1.getName());
				}
				
			}
		});
		
		sortedChildren.addAll(children);
		
		return sortedChildren;
	}
	
	public List<Category> findCategoryNoChild(){
		return repo.findCategoryNoChild();
	}
}
