package com.shopme.admin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.web.multipart.MultipartFile;

public class FileUploadUtil {

	public static void saveFile(String uploadDir, String fileName, 
			MultipartFile multipartFile) throws IOException {
		
		Path uploadPath = Paths.get(uploadDir); // Ví dụ, nếu uploadDir là "/path/to/uploads", thì Paths.get(uploadDir) sẽ tạo ra một đối tượng Path biểu diễn đường dẫn "/path/to/uploads".
		
		if(!Files.exists(uploadPath)) { // nếu thư mục chưa tồn tại thì tạo thư mục đó.
			Files.createDirectories(uploadPath);
		}
		
		try(InputStream inputStream = multipartFile.getInputStream()) {  //sử dụng multipartFile.getInputStream() để có được luồng đọc từ MultipartFile.
			
			Path filePath = uploadPath.resolve(fileName); // resolve là một phương thức của đối tượng Path trong Java. Nó được sử dụng để nối một đường dẫn con vào một đường dẫn cha.
			// Ví dụ, nếu uploadPath là "/path/to/uploads" và fileName là "myFile.txt", thì uploadPath.resolve(fileName) sẽ trả về "/path/to/uploads/myFile.txt".
			
			Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
			// Files.copy() để sao chép dữ liệu từ luồng đọc vào một tệp tin tại đường dẫn filePath.
			// StandardCopyOption.REPLACE_EXISTING được sử dụng để ghi đè lên tệp tin nếu tệp tin đã tồn tại.
		
		} catch (IOException e) {
			throw new IOException("Counld not save file: " + fileName, e);
		}	
	}
	
	//Tham số:
		//uploadDir: Đường dẫn đến thư mục nơi bạn muốn lưu trữ tệp tin.
		//fileName: Tên của tệp tin mà bạn muốn lưu trữ.
		//multipartFile: Đối tượng MultipartFile chứa dữ liệu của tệp tin được tải lên từ giao diện người dùng.
}
