
dropdownBrands = $("#brand");
dropdownCategories = $("#category");
$(document).ready(function() {

	$("#shortDescription").richText();

	$("#fullDescription").richText();

	dropdownBrands.change(function() {  // khi chon brand khac
		dropdownCategories.empty();
		getCategories();
	});

	getCategories();  // khi mo form create product ra thi hien san nhung category cua brand cua product do.

});

function getCategories() {
	brandId = dropdownBrands.val();
	url = brandModuleURL + "/" + brandId + "/categories";

	$.get(url, function(responseJson) {
		$.each(responseJson, function(index, category) {
			//console.log(responseJson);
			//alert(index + ": " + category.name);
			$("<option>").val(category.id).text(category.name).appendTo(dropdownCategories);
		});
	});
};


function checkUnique(form) {
	productId = $("#id").val();
	productName = $("#name").val();

	csrfValue = $("input[name='_csrf']").val();
	params = { id: productId, name: productName, _csrf: csrfValue };

	$.post(checkUniqueUrl, params, function(response) {
			
		if (response == "OK") {
			form.submit();
		} else if (response == "Duplicate") {
			showWarningModal("There is another product having same name " + productName);
		} else {
			showErrorModal("Unknown response from server");
		}
	}).fail(function() {
		showErrorModal("Unknown response from server");
	});

	return false;
}