package com.shopme.admin.category;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
import com.shopme.common.entity.Category;

@Controller
public class CategoryController {

	
	@Autowired
	private CategoryService service;
	
	
	
	@GetMapping("/categories-KHI-CHUA-PHAN-TRANG")
	public String listAllV0(@Param("sortDir") String sortDir,  Model model) {
		
		if(sortDir == null || sortDir.isEmpty()) {
			sortDir = "asc";
		}
		//String order = sortDir == null ? "asc" : sortDir; // khi url ko co tham so sortDir -> default "asc"
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc"; // khi click vao Category Name
		
		List<Category> listCategories = service.listAll(sortDir);
		
		List<Category> listCategoriesNoChild = service.findCategoryNoChild();
		
		model.addAttribute("listCategories",listCategories);
		model.addAttribute("listCategoriesNoChild", listCategoriesNoChild);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("sortDir", sortDir); // su dung cho ben categories.html: <span th:class="${sortDir == 'asc' ? 'fas fa-sort-up' : 'fas fa-sort-down'}"></span>
		
		return "categories/categories";
	}
	
	@GetMapping("/categories")
	public String listAll(@Param("sortDir") String sortDir,  Model model) {
		return listByPage(1, sortDir, model);
	}
	
	@GetMapping("/categories/page/{pageNum}")
	public String listByPage(@PathVariable("pageNum") int pageNum, 
			@Param("sortDir") String sortDir,  Model model) {
		
		if(sortDir == null || sortDir.isEmpty()) {
			sortDir = "asc";
		}
		CategoryPageInfo pageInfo = new CategoryPageInfo();
		
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc"; // khi click vao Category Name
		
		List<Category> listCategories = service.listByPage(pageInfo, pageNum, sortDir);
		
		long startCount = (pageNum - 1) * CategoryService.ROOT_CATEGORIES_PER_PAGE + 1;
		long endCount = startCount + CategoryService.ROOT_CATEGORIES_PER_PAGE - 1;
		if(endCount > pageInfo.getTotalElements()) {
			endCount = pageInfo.getTotalElements();
		}
		List<Category> listCategoriesNoChild = service.findCategoryNoChild(); // ko dung den, ma dung hasChilren trong Category.java
		
		model.addAttribute("totalPages", pageInfo.getTotalPages());
		model.addAttribute("totalItems", pageInfo.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("sortField", "name");
		
		model.addAttribute("listCategories",listCategories);
		model.addAttribute("listCategoriesNoChild", listCategoriesNoChild);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("sortDir", sortDir); // su dung cho ben categories.html: <span th:class="${sortDir == 'asc' ? 'fas fa-sort-up' : 'fas fa-sort-down'}"></span>
		
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		
		return "categories/categories";
	}
	
	@GetMapping("/categories/new")
	public String newCategory(Model model) {
		
		List<Category> listCategories = service.listCategoriesUsedInform();
		
		model.addAttribute("category", new Category());
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("pageTitle", "Create New Category");
		
		return "categories/category_form";
		
	}
	
	@PostMapping("/categories/save")
	public String saveCategory(Category category,
			@RequestParam("fileImage") MultipartFile multipartFile,
			RedirectAttributes redirectAttributes) throws IOException, CategoryNotFoundException {
		
		if(!multipartFile.isEmpty()) { // truong hop co chon anh
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			category.setImage(fileName);
			
			Category savedCategory = service.save(category);
			
			String uploadDir = "../categories-images/" + savedCategory.getId();
			
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
			
		}else { // truong hop khong chon anh
			if(category.getId() != null) { // neu co id -> update va dung anh cu
				Category categoryInDB = service.get(category.getId());
				category.setImage(categoryInDB.getImage());
			}else { // khong co id -> create new va dung anh defaul
				category.setImage("");  // ko set null vi truong image ta de nullable=false, nen ta set ""
										// khi do, ben categories.html ta se check, neu la "" thi ta cho anh default vao
			}
			service.save(category);
			
		}
		
		redirectAttributes.addFlashAttribute("message", "The category has been saved successfully.");
		return "redirect:/categories";
	}
	
	@GetMapping("/categories/edit/{id}")
	public String editCategory(@PathVariable("id") int id,
			RedirectAttributes redirectAttributes,
			Model model) {
		
		try {
			Category category = service.get(id);
			model.addAttribute("listCategories", service.listCategoriesUsedInform());
			model.addAttribute("pageTitle", "Edit category ID " + id);
			model.addAttribute("category", category);
			
			return "categories/category_form";			
		} catch (CategoryNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
	
		return "redirect:/categories";
	}
	
	@GetMapping("/categories/{id}/enabled/{status}")
	public String updateCategoryEnabledStatus(
			@PathVariable("id") Integer id,
			@PathVariable("status") boolean status,
			RedirectAttributes redirectAttributes,
			HttpServletRequest request
			) {
		
		service.updateCategoryEnabledStatus(id, status);
		
		String statusMessage = status ? "enabled" : "disabled";
		String message = "The category ID " + id + " has been " + statusMessage;
		redirectAttributes.addFlashAttribute("message", message);
		
		//System.out.println(request.getHeader("referer")); // in ra url của request gốc
		//return "redirect:/users";
		
		return "redirect:" + request.getHeader("referer"); 
		// mục đích: sau khi xử lý enabled xong thì vẫn đứng tại trang đó,
		// chứ không chạy sang :/categories o trang dau
	}
	
	@GetMapping("/categories/delete/{id}")
	public String deleteCategory(
			@PathVariable("id") int id, 
			RedirectAttributes redirectAttributes){

		try {
			Category category = service.get(id);
			service.delete(category);
			String categoryDir = "../categories-images/" + id;
			FileUploadUtil.removeDir(categoryDir);
			
			redirectAttributes.addFlashAttribute("message", "Category ID " + id + " has been deleted");
		} catch (CategoryNotFoundException e) {		
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		
		return "redirect:/categories";
		
	}
}
