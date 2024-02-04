package com.shopme.admin;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		
		// user
//		String dirName = "user-photos";
//		
//		Path userPhotosDir = Paths.get(dirName);
//		
//		String userPhotosPath = userPhotosDir.toFile().getAbsolutePath();
//		
//		registry.addResourceHandler("/" + dirName + "/**")
//				.addResourceLocations("file:/" + userPhotosPath + "/");
		
		// category
//		String categoryImagesDirName = "../categories-images"; // do ta de folder categories-images cung level voi fol der ShopmeBackEnd
//		Path categoryImagesDir = Paths.get(categoryImagesDirName);
//		
//		String categoryImagesPath = categoryImagesDir.toFile().getAbsolutePath();
//		
//		registry.addResourceHandler("/categories-images/**")
//				.addResourceLocations("file:/" + categoryImagesPath + "/");
		
		// brand
//		String brandLogoDirName = "../brands-logos"; // do ta de folder brands-logos cung level voi fol der ShopmeBackEnd
//		Path brandLogoDir = Paths.get(brandLogoDirName);
//		
//		String brandLogoPath = brandLogoDir.toFile().getAbsolutePath();
//		
//		registry.addResourceHandler("/brands-logos/**")
//				.addResourceLocations("file:/" + brandLogoPath + "/");
		
		//After Refactor
		exposeDirectory("user-photos", registry); 
		exposeDirectory("../categories-images", registry);
		exposeDirectory("../brands-logos", registry);
		exposeDirectory("../product-images", registry);
		
	}
	
	private void exposeDirectory(String pathPattern, ResourceHandlerRegistry registry) {
		
		Path path = Paths.get(pathPattern);
		String absolutePath = path.toFile().getAbsolutePath(); // C:\Project\java\ShopmeEcommerce\ShopmeProject\ShopmeWebParent\ShopmeBackEnd\ user-photos
		
		String logicalPath = pathPattern.replace("../", "") + "/**";
		
		registry.addResourceHandler(logicalPath)
				.addResourceLocations("file:/" + absolutePath + "/");
	}

	//String dirName = "user-photos";: 
		//Đây là tên thư mục chứa các tài nguyên tĩnh, trong trường hợp này là hình ảnh người dùng.

	//Path userPhotosDir = Paths.get(dirName);: 
		//Tạo một đối tượng Path đại diện cho đường dẫn đến thư mục user-photos.

	//String userPhotosPath = userPhotosDir.toFile().getAbsolutePath();: 
		//Lấy đường dẫn tuyệt đối của thư mục user-photos dưới dạng chuỗi.

	//registry.addResourceHandler("/" + dirName + "/**"): 
		//Đăng ký một Resource Handler cho các tài nguyên trong thư mục user-photos. 
		//Khi có một yêu cầu đến đường dẫn có prefix /user-photos/, 
		//Spring sẽ tìm kiếm các tài nguyên trong thư mục user-photos.

	//.addResourceLocations("file:/" + userPhotosPath + "/");: 
		//Xác định đường dẫn vật lý tới thư mục user-photos. 
		//Cụ thể, file:/ cho biết rằng đây là tài nguyên vật lý trên hệ thống tệp tin 
		//và userPhotosPath là đường dẫn tuyệt đối tới thư mục chứa các tài nguyên.
	
	//	.addResourceLocations: Phương thức này xác định địa chỉ vật lý của tài nguyên trên hệ thống tệp.
	//	"file:/" + absolutePath + "/": Đây là địa chỉ vật lý trên hệ thống tệp. 
	//	absolutePath là đường dẫn tuyệt đối đến thư mục chứa các tài nguyên tĩnh. 
	//	Phần "file:/", khi được sử dụng trong Spring, nói cho framework biết rằng 
	//	đây là đường dẫn trên hệ thống tệp.
	
}
