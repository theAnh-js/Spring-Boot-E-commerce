package com.shopme.admin.product;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.brand.BrandService;
import com.shopme.admin.category.CategoryService;
import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;
import com.shopme.common.exception.ProductNotFoundException;

@Controller
public class ProductController {

	@Autowired
	private ProductService productService;

	@Autowired
	private BrandService brandService;

	@Autowired
	private CategoryService categoryService;

	@GetMapping("/products")
	public String listFirstPage(Model model) {

//		List<Product> listProducts = productService.listAll();
//
//		model.addAttribute("listProducts", listProducts);
//		return "products/products";

		return listByPage(1, model, null, "name", "asc", null);
	}

	@GetMapping("/products/page/{pageNum}")
	public String listByPage(@PathVariable("pageNum") int pageNum, Model model, @Param("categoryId") Integer categoryId,
			@Param("sortField") String sortField, @Param("sortDir") String sortDir, @Param("keyword") String keyword) {

		Page<Product> page = productService.listByPage(pageNum, sortField, sortDir, keyword, categoryId);

		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

		long totalItems = page.getTotalElements();
		long totalPages = page.getTotalPages();
		long startCount = (pageNum - 1) * ProductService.PRODUCTS_PER_PAGE + 1;
		long endCount = startCount + ProductService.PRODUCTS_PER_PAGE - 1;
		if (endCount > totalItems) {
			endCount = totalItems;
		}

		List<Product> listProducts = page.getContent();

		List<Category> listCategories = categoryService.listCategoriesUsedInform();

		if (categoryId != null)
			model.addAttribute("categoryId", categoryId);
		model.addAttribute("listProducts", listProducts);
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("totalItems", totalItems);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("keyword", keyword);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("currentPage", pageNum);

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
		model.addAttribute("numOfExistingExtraImages", 0);

		return "products/products_form";
	}

	@PostMapping("/products/save")
	public String saveProduct(Product product,
			@RequestParam(name = "fileImage", required = false) MultipartFile mainImageMultipart,
			@RequestParam(name = "extraImage", required = false) MultipartFile[] extraImageMultiparts,
			@RequestParam(name = "detailIDs", required = false) String[] detailIDs,
			@RequestParam(name = "detailNames", required = false) String[] detailNames,
			@RequestParam(name = "detailValues", required = false) String[] detailValues,
			@RequestParam(name = "imageIDs", required = false) String[] imageIDs,
			@RequestParam(name = "imageNames", required = false) String[] imageNames,
			@AuthenticationPrincipal ShopmeUserDetails loggedUser, RedirectAttributes redirectAttributes)
			throws IOException {

		if (loggedUser.hasRole("Salesperson")) { // neu la Salesperson thi khi luu CHI CHO LUU phan gia ca: cost, price,
													// discountPercent
													// phong truong hop, salesperson sua co sua ca nhung noi khong duoc
													// phep.
			productService.saveProductPrice(product);
			redirectAttributes.addFlashAttribute("message", "The product has been saved successfully.");
			return "redirect:/products";
		}

		ProductSaveHelper.setMainImageName(mainImageMultipart, product);
		ProductSaveHelper.setExistingExtraImageNames(imageIDs, imageNames, product);
		ProductSaveHelper.setNewExtraImageNames(extraImageMultiparts, product);
		ProductSaveHelper.setProductDetails(detailIDs, detailNames, detailValues, product);

		Product savedProduct = productService.save(product);

		ProductSaveHelper.savedUploaedImages(mainImageMultipart, extraImageMultiparts, savedProduct);

		ProductSaveHelper.deleteExtraImagesWeredRemvedOnForm(product);

		redirectAttributes.addFlashAttribute("message", "The product has been saved successfully.");
		return "redirect:/products";
	}

	@GetMapping("/products/{id}/enabled/{status}")
	public String updateStatusProduct(@PathVariable("id") Integer id, @PathVariable("status") boolean status,
			RedirectAttributes redirectAttributes, HttpServletRequest request) {

		productService.updateStatusProduct(status, id);

		String messageStatus = status ? "enabled" : "disabled";
		redirectAttributes.addFlashAttribute("message", "The product with ID " + id + " has been " + messageStatus);

		return "redirect:" + request.getHeader("referer");

	}

	@GetMapping("/products/delete/{id}")
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

	@GetMapping("/products/edit/{id}")
	public String editProduct(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {

		try {
			Product product = productService.get(id);
			List<Brand> listBrands = brandService.listAll();
			int numOfExistingExtraImages = product.getImages().size();

			model.addAttribute("product", product);
			model.addAttribute("listBrands", listBrands);
			model.addAttribute("pageTitle", "Edit Product (ID: " + id + ")");
			model.addAttribute("numOfExistingExtraImages", numOfExistingExtraImages);

			return "products/products_form";

		} catch (ProductNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());

			return "redirect:/products";
		}
	}

	@GetMapping("/products/detail/{id}")
	public String viewProductDetails(@PathVariable("id") Integer id, Model model,
			RedirectAttributes redirectAttributes) {

		try {
			System.out.println("id: " + id);

			Product product = productService.get(id);

			int numOfExistingExtraImages = product.getImages().size();

			model.addAttribute("product", product);
			model.addAttribute("numOfExistingExtraImages", numOfExistingExtraImages);
			return "products/products_detail_modal";

		} catch (ProductNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());

			return "redirect:/products";
		}
	}
}
