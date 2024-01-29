function clearFilter() {
	window.location = moduleURL
}

function showDeleteConfirmModal(link, entityName) {
	entityId = link.attr("entityId");
	$("#yesBtn").attr("href", link.attr("href")); // set href /users/delete/... vao href cua button YES
	$("#confirmText").text("Are you sure you want to delete this" + entityName + " ID" + entityId + "?"); // chen text vao modal kem user id
	$("#confirmModal").modal(); // mo ra modal
}