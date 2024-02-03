package com.shopme.admin.brand;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import com.shopme.admin.category.CategoryService;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;

@Controller
public class BrandController {
	
	@Autowired
	private BrandService brandService;
	
	@Autowired
	private CategoryService categoryService;
	
	@GetMapping("/brands")
	public String listAll(Model model) {
		
		return listByPage(1, model, "name", "asc", null);
	}
	
	@GetMapping("/brands/page/{pageNum}")
	public String listByPage(@PathVariable("pageNum") int pageNum, 
							Model model,
							@Param("sortField") String sortField,
							@Param("sortDir") String sortDir,
							@Param("keyword") String keyword) {
		
		Page<Brand> page = brandService.listByPage(pageNum, sortField, sortDir, keyword);
		
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		
		long totalItems = page.getTotalElements();
		long totalPages = page.getTotalPages();
		long startCount = (pageNum - 1) * BrandService.BRANDS_PER_PAGE + 1;
		long endCount = startCount + BrandService.BRANDS_PER_PAGE - 1;
		if(endCount > totalItems) {
			endCount = totalItems;
		}
		
		List<Brand> listBrands = page.getContent();
		
		model.addAttribute("listBrands",listBrands);
		model.addAttribute("totalItems", totalItems);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("keyword", keyword);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("currentPage", pageNum);
		
		return "brands/brands";
	}

	@GetMapping("/brands/new")
	public String createNewBrand(Model model) {
		
		List<Category> listCategories = categoryService.listCategoriesUsedInform();
		
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("pageTitle", "Create New Brand");
		model.addAttribute("brand", new Brand());
		
		return "brands/brands_form";
		
	}
	
	@PostMapping("/brands/save")
	public String saveBrand(Brand brand, 
							@RequestParam("fileImage") MultipartFile multipartFile, 
							RedirectAttributes redirectAttributes) throws IOException {
		
		if(!multipartFile.isEmpty()) {
			
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			
			brand.setLogo(fileName);
			
			Brand savedBrand = brandService.save(brand);
			
			String uploadDir = "../brands-logos/" + savedBrand.getId();
			
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
			
		}else {
			if(brand.getId() != null) {
				brand.setLogo(brand.getLogo());
			}else {
				brand.setLogo("");
			}			
			
			brandService.save(brand);
		}
		
		redirectAttributes.addFlashAttribute("message", " The brand has been saved successfully.");
		return "redirect:/brands";
	}
	
	@GetMapping("/brands/edit/{id}")
	public String editBrand(@PathVariable("id") Integer id, 
							Model model,
							RedirectAttributes redirectAttributes) {
	
		try {
			Brand brand = brandService.get(id);
			
			model.addAttribute("brand", brand);
			model.addAttribute("pageTitle", "Edit Brand ID" + id);
			model.addAttribute("listCategories", categoryService.listCategoriesUsedInform());
			
			return "brands/brands_form";
			
		} catch (BrandNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		
		return "redirect:/brands";
		
	}
	
	@GetMapping("/brands/delete/{id}")
	public String deleteBrand(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
		
		try {
			brandService.delete(id);
			redirectAttributes.addFlashAttribute("message", "The brand with ID " + id + " has been deleted.");
		} catch (BrandNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		
		return "redirect:/brands";
		
	}
}
