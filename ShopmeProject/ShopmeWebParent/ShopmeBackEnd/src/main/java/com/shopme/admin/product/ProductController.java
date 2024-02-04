package com.shopme.admin.product;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.shopme.admin.brand.BrandService;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Product;

@Controller
public class ProductController {

	@Autowired
	private ProductService productService;

	@Autowired
	private BrandService brandService;

	@GetMapping("/products")
	public String listAll(Model model) {

		List<Product> listProducts = productService.listAll();

		model.addAttribute("listProducts", listProducts);
		return "products/products";
	}

	@GetMapping("/products/new")
	public String newProduct(Model model) {

		List<Brand> listBrands = brandService.listAll();

		Product product = new Product();
		product.setEnabled(true);
		product.setInStock(true);

		model.addAttribute("product", product);
		model.addAttribute("listBrands", listBrands);
		model.addAttribute("pageTitle", "Create New Product");

		return "products/products_form";
	}

	@PostMapping("/products/save")
	public String saveProduct(Product product, @RequestParam("fileImage") MultipartFile mainImageMultipart,
			@RequestParam("extraImage") MultipartFile[] extraImageMultiparts, RedirectAttributes redirectAttributes)
			throws IOException {

		setMainImageName(mainImageMultipart, product);
		setExtraImageName(extraImageMultiparts, product);

		Product savedProduct = productService.save(product);

		savedUploaedImages(mainImageMultipart, extraImageMultiparts, savedProduct);

		redirectAttributes.addFlashAttribute("message", "The product has been saved successfully.");
		return "redirect:/products";
	}

	private void savedUploaedImages(MultipartFile mainImageMultipart,
			MultipartFile[] extraImageMultiparts, Product savedProduct) throws IOException {
		
		if (!mainImageMultipart.isEmpty()) {  	
			String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
			
			String uploadDir = "../product-images/" + savedProduct.getId();

			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, mainImageMultipart);
		}
		
		if (extraImageMultiparts.length > 0) {			
			String uploadDir = "../product-images/" + savedProduct.getId() + "/extras";
			
			for (MultipartFile multipartFile : extraImageMultiparts) {
				if (multipartFile.isEmpty()) continue;
				
				String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
				FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
			}
		}		
	}

	private void setExtraImageName(MultipartFile[] extraImageMultiparts, Product product) {
		if (extraImageMultiparts.length > 0) {
			for (MultipartFile multipartFile : extraImageMultiparts) {
				if (!multipartFile.isEmpty()) {
					String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
					product.addExtraImage(fileName);
				}
			}
		}

	}

	private void setMainImageName(MultipartFile mainImageMultipart, Product product) {

		if (!mainImageMultipart.isEmpty()) {

			String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());

			product.setMainImage(fileName);
		}
	}

	@GetMapping("/products/{id}/enabled/{status}")
	public String updateStatusProduct(@PathVariable("id") Integer id, @PathVariable("status") boolean status,
			RedirectAttributes redirectAttributes, HttpServletRequest request) {

		productService.updateStatusProduct(status, id);

		String messageStatus = status ? "enabled" : "disabled";
		redirectAttributes.addFlashAttribute("message", "The product with ID " + id + " has been " + messageStatus);

		return "redirect:" + request.getHeader("referer");

	}

	@GetMapping("products/delete/{id}")
	public String deleteProduct(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {

		try {
			productService.deleteProduct(id);
			
			String productExtraImageDir = "../product-images/" + id + "/extras";
			String productImageDir = "../product-images/" + id;
			FileUploadUtil.removeDir(productExtraImageDir);
			FileUploadUtil.removeDir(productImageDir);
			
			redirectAttributes.addFlashAttribute("message", "The product with ID " + id + " has been deleted.");
		} catch (ProductNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}

		return "redirect:/products";

	}
}