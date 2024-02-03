package com.shopme.admin.user.export;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.shopme.admin.AbstractExporter;
import com.shopme.common.entity.User;

public class UserCsvExporter extends AbstractExporter{

	
	public void export(List<User> listUsers, HttpServletResponse response) throws IOException {
		
		super.setResponseHeader(response, "user_", "text/csv", ".csv");
		
		/*
		 * DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss"); String
		 * timestamp = dateFormat.format(new Date());
		 * 
		 * String fileName = "user_" + timestamp + ".csv";
		 * 
		 * response.setContentType("text/csv");
		 * 
		 * String headerKey = "Content-Disposition"; String headerValue =
		 * "attachment; filename=" + fileName; response.setHeader(headerKey,
		 * headerValue);
		 */
		
		ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(),
				CsvPreference.STANDARD_PREFERENCE);
		
		String[] csvHeader = {"User ID", "E-mail", "First Name", "Last Name", "Roles", "Enabled"};
		String[] fieldMapping = {"id", "email", "firstName", "lastName", "roles", "enabled"};
		csvWriter.writeHeader(csvHeader);
		
		for(User user : listUsers) {
			csvWriter.write(user, fieldMapping);
		}
		csvWriter.close();
	}
}
