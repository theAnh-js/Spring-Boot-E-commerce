package com.shopme.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.shopme.category.CategoryService;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;
import com.shopme.common.exception.CategoryNotFoundException;
import com.shopme.common.exception.ProductNotFoundException;

@Controller
public class ProductController {
	
	@Autowired
	private ProductService productService;

	@Autowired
	private CategoryService categoryService;
	
	@GetMapping("/c/{category_alias}")
	public String viewFirstPage(@PathVariable("category_alias") String alias, 
								Model model) throws CategoryNotFoundException {	
		
		return viewCategoryByPage(alias, model, 1);
	}
	
	@GetMapping("/c/{category_alias}/page/{pageNum}")
	public String viewCategoryByPage(
			@PathVariable("category_alias") String alias,
			Model model, 
			@PathVariable("pageNum") Integer pageNum){
		
		try {
			Category category = categoryService.getCategory(alias);
			
			if(category == null) {
				return "error/404";
			}
			
			List<Category> listCategoryParents = categoryService.getCategoryParent(category);
			Page<Product> pageProducts = productService.listByCategory(pageNum, category.getId());
			
			long totalItems = pageProducts.getTotalElements();
			long totalPages = pageProducts.getTotalPages();
			long startCount = (pageNum - 1) * ProductService.PRODUCTS_PER_PAGE + 1;
			long endCount = startCount + ProductService.PRODUCTS_PER_PAGE - 1;
			if (endCount > totalItems) {
				endCount = totalItems;
			}

			List<Product> listProducts = pageProducts.getContent();
			
			model.addAttribute("listProducts", listProducts);
			model.addAttribute("totalItems", totalItems);
			model.addAttribute("totalPages", totalPages);
			model.addAttribute("startCount", startCount);
			model.addAttribute("endCount", endCount);
			model.addAttribute("currentPage", pageNum);
			model.addAttribute("listCategoryParents", listCategoryParents);
			model.addAttribute("pageTitle", category.getName());
			model.addAttribute("category", category);

			return "products_by_category";

		} catch (CategoryNotFoundException e) {
			return "error/404";
		}		
	}
	
	@GetMapping("/p/{product_alias}")
	public String viewProductDetail(@PathVariable("product_alias") String alias, Model model) {
		try {
			Product product = productService.getProduct(alias);
			List<Category> listCategoryParents = categoryService.getCategoryParent(product.getCategory());
			
			model.addAttribute("listCategoryParents", listCategoryParents);
			model.addAttribute("product", product);
			return "product_detail";
		} catch (ProductNotFoundException e) {
			return "error/404";
		}
	}
}