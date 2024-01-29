package com.shopme.admin.user.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.shopme.admin.user.UserNotFoundException;
import com.shopme.admin.user.UserService;
import com.shopme.admin.user.export.UserCsvExporter;
import com.shopme.admin.user.export.UserExcelExporter;
import com.shopme.admin.user.export.UserPdfExporter;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@Controller
public class UserController {

	@Autowired
	private UserService service;

	@GetMapping("/users")
	public String listFirstPage(Model model) {
		/*
		 * List<User> listUsers = service.listAll();
		 * 
		 * model.addAttribute("listUsers", listUsers); return "users";
		 */
		
		return listByPage(1, model, "firstName", "asc", null);
	}
	
	@GetMapping("/users/page/{pageNum}")
	public String listByPage(
				@PathVariable("pageNum") int pageNum, 
				Model model,
				@Param("sortField") String sortField,
				@Param("sortDir") String sortDir,
				@Param("keyword") String keyword) {

		Page<User> page = service.listByPage(pageNum, sortField, sortDir, keyword);
		List<User> listUsers = page.getContent();
		
		long startCount = (pageNum - 1) * UserService.USERS_PER_PAGE + 1;
		long endCount = startCount + UserService.USERS_PER_PAGE - 1;
		if(endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		
		model.addAttribute("keyword", keyword);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", page.getTotalElements());	
		model.addAttribute("listUsers",listUsers);
		
		return "users/users";
	}

	@GetMapping("/users/new")
	public String newUser(Model model) {
		
		List<Role> listRoles = service.listRoles();
		
		User user = new User();
		user.setEnabled(true);
		
		model.addAttribute("user", user);
		model.addAttribute("listRoles", listRoles);
		model.addAttribute("pageTitle", "Create new user");
		return "users/user_form";
	}

	@PostMapping("/users/save")
	public String saveUser(User user, RedirectAttributes redirectAttributes,
							@RequestParam("image") MultipartFile multipartFile) throws IOException {
		
		if(!multipartFile.isEmpty()) { // nếu multipartFile có dữ liệu (có ảnh)
			
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			// cleanPath: Phương thức này được sử dụng để loại bỏ các phần của đường dẫn có thể 
			// dẫn đến các vấn đề an ninh hoặc truy cập không mong muốn, chẳng hạn như ..
			// Ví dụ : "/path/to/../file.txt" -> "path/to/file.txt"
			
			user.setPhotos(fileName);
			User savedUser = service.save(user);
			
			String uploadDir = "user-photos/" + savedUser.getId();
			
			FileUploadUtil.cleanDir(uploadDir); // xóa bỏ ảnh đã tồn tại trong thư mục đó đi
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		}else {
			if(user.getPhotos().isEmpty()) user.setPhotos(null);
			service.save(user);
		}
		
		//service.save(user);
	    redirectAttributes.addFlashAttribute("message", "The user has been saved successfully.");
	    
	    return getRedirectUrlToAffectedUser(user);
	}

	private String getRedirectUrlToAffectedUser(User user) {
		String firstPartOfEmail = user.getEmail().split("@")[0];
		return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" + firstPartOfEmail;
	}
	
	@GetMapping("/users/edit/{id}")
	public String editUser(@PathVariable("id") Integer id, 
						   RedirectAttributes redirectAttributes,
						   Model model) {
		try {
			User user = service.get(id);
			List<Role> listRoles = service.listRoles();
			
			model.addAttribute("user", user);
			model.addAttribute("pageTitle", "Edit user (ID: " + id + ")");
			model.addAttribute("listRoles", listRoles);
			
			return "users/user_form";
		} catch (UserNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		
		return "redirect:/users";
	}
	
	@GetMapping("/users/delete/{id}")
	public String deleteUser(@PathVariable("id") Integer id, 
			   RedirectAttributes redirectAttributes,
			   Model model) {
		
		try {
			service.delete(id);
			redirectAttributes.addFlashAttribute("message", 
					"The user ID " + id + " has been deleted successfully");	
		} catch (UserNotFoundException e) { // bắt exception UserNotFoundException được throw ra từ service.delete(id); nếu service.delete(id) gặp lỗi
			redirectAttributes.addFlashAttribute("message", e.getMessage());		
		}
		return "redirect:/users";
	}
	
	@GetMapping("/users/{id}/enabled/{status}")
	public String updateUserEnabledStatus(
			@PathVariable("id") Integer id,
			@PathVariable("status") boolean status,
			RedirectAttributes redirectAttributes,
			HttpServletRequest request
			) {
		
		service.updateUserEnabledStatus(id, status);
		
		String statusMessage = status ? "enabled" : "disabled";
		String message = "The user ID " + id + " has been " + statusMessage;
		redirectAttributes.addFlashAttribute("message", message);
		
		//System.out.println(request.getHeader("referer")); // in ra url của request gốc
		//return "redirect:/users";
		
		return "redirect:" + request.getHeader("referer"); 
		// mục đích: sau khi xử lý enabled xong thì vẫn đứng tại trang đó,
		// chứ không chạy sang :/users
	}
	
	@GetMapping("/users/export/csv")
	public void exportToCSV(HttpServletResponse response) throws IOException {
		List<User> listUsers = service.listAll();
		UserCsvExporter exporter = new UserCsvExporter();
		
		exporter.export(listUsers, response);
	}
	
	@GetMapping("/users/export/excel")
	public void exportToExcel(HttpServletResponse response) throws IOException {
		List<User> listUsers = service.listAll();
		UserExcelExporter exporter = new UserExcelExporter();
		
		exporter.export(listUsers, response);
	}
	
	@GetMapping("/users/export/pdf")
	public void exportToPDF(HttpServletResponse response) throws IOException {
		List<User> listUsers = service.listAll();
		UserPdfExporter exporter = new UserPdfExporter();
		
		exporter.export(listUsers, response);
	}
	//addFlashAttribute là một phương thức trong interface RedirectAttributes trong Spring Framework, 
	//được sử dụng để chuyển dữ liệu từ một request sang một request khác thông qua redirect. 
	//Thông thường, khi bạn chuyển từ một trang (controller) sang trang khác bằng cách sử dụng redirect, 
	//dữ liệu không thể trực tiếp được chuyển theo dạng model attribute, 
	//vì redirect tạo ra một request mới.
}
