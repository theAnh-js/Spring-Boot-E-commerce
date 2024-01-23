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
		
		String dirName = "user-photos";
		
		Path userPhotosDir = Paths.get(dirName);
		
		String userPhotosPath = userPhotosDir.toFile().getAbsolutePath();
		
		registry.addResourceHandler("/" + dirName + "/**")
				.addResourceLocations("file:/" + userPhotosPath + "/");
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
	
}
