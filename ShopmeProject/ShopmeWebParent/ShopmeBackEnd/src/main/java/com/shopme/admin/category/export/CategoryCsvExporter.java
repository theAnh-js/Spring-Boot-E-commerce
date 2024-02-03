package com.shopme.admin.category.export;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.shopme.admin.AbstractExporter;
import com.shopme.common.entity.Category;

public class CategoryCsvExporter extends AbstractExporter {
	
	public void export(List<Category> listCategories, HttpServletResponse response) throws IOException {

		super.setResponseHeader(response, "categories_", "text/csv", ".csv");

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

		ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

		String[] csvHeader = { "Category ID", "Category Name"};
		String[] fieldMapping = { "id", "name"};
		csvWriter.writeHeader(csvHeader);

		for (Category category : listCategories) {
			category.setName(category.getName().replace("--", "  "));
			csvWriter.write(category, fieldMapping);
		}
		csvWriter.close();
	}
}
