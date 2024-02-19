var buttonLoadForState;
var dropDownCountryForState;
var dropDownState;

var buttonAddState;
var buttonUpdateState;
var buttonDeleteState;
var labelStateName;
var fieldStateName; 

$(document).ready(function(){
	buttonLoadForState = $("#buttonLoadCountriesForState");
	dropDownCountryForState = $("#dropDownCountriesForState");
	dropDownState = $("#dropDownStates");
	
	buttonAddState = $("#buttonAddState");
	buttonUpdateState = $("#buttonUpdateState");
	buttonDeleteState = $("#buttonDeleteState");
	labelStateName = $("#labelStateName");
	fieldStateName = $("#fieldStateName");	
	
	buttonLoadForState.click(function(){
		loadCountriesForState();
	});
	
	dropDownCountryForState.on("change", function(){
		loadStateForCountry();
	});
	
	dropDownState.on("change", function(){
		changeFormStateToSelectedState();
	});
	
	buttonAddState.click(function(){
		if(buttonAddState.val() == "Add"){
			addState();
		}else{
			changeFormStateToNew();
		}
	});
	
	buttonUpdateState.click(function(){
		updateState();
	})
	
	buttonDeleteState.click(function(){
		deleteState();
	});
	
});

function loadStateForCountry(){
	selectedCountry = $("#dropDownCountriesForState option:selected");
	
	countryId = dropDownCountryForState.val().split("-")[0];
	
	url = contextPath + "states/list_by_country/" + countryId;
	
	$.get(url, function(responseJSON){
		dropDownState.empty();
		
		$.each(responseJSON, function(index, state){			
			optionValue = state.id;
			$("<option>").val(optionValue).text(state.name).appendTo(dropDownState);
		});
	}).done(function(){
		changeFormStateToNew();
		showToastMessage("All States have been loaded for " + selectedCountry.text());
	}).fail(function(){
		showToastMessage("ERROR: Could not connect to server or server encountered an error")
	});
		
}

function loadCountriesForState(){
	
	fieldStateName.val("");
	
	url = contextPath + "countries/list";
	
	$.get(url, function(responseJSON){
		dropDownCountryForState.empty();
		dropDownState.empty();
		
		$.each(responseJSON, function(index, country){	
			optionValue = country.id + "-" + country.code;
			$("<option>").val(optionValue).text(country.name).appendTo(dropDownCountryForState);
		});
	}).done(function(){
		buttonLoadForState.val("Refresh Country List");
		showToastMessage("All countries have been load");
	}).fail(function(){
		showToastMessage("ERROR: Could not connect to server or server encountered an error")
	});
	
}

function changeFormStateToNew(){
	buttonAddState.val("Add");
	labelStateName.text("State/Province Name: ");
	
	buttonUpdateState.prop("disabled", true);
	buttonDeleteState.prop("disabled", true);
	
	fieldStateName.val("").focus();
	
}

function changeFormStateToSelectedState(){
	buttonAddState.prop("value", "New");
	labelStateName.text("Selected State/Province: ");
	
	buttonUpdateState.prop("disabled", false);
	buttonDeleteState.prop("disabled", false);
	
	selectedStateName = $("#dropDownStates option:selected").text();
	fieldStateName.val(selectedStateName);
}

function addState(){
	url = contextPath + "states/save";
	stateName = fieldStateName.val();
	
	selectedCountry = $("#dropDownCountriesForState option:selected");
	//countryId = selectedCountry.val(); ví dụ chọn Viet Nam thì value
	// cái này sẽ thành 1-VN (1-VN là value của select đó, 1 mới là id của country trong database)
	//console.log(selectedCountry.val());
	countryId = dropDownCountryForState.val().split("-")[0];
	countryName = selectedCountry.text();
	
	jsonData = {name: stateName, country: {id: countryId, name: countryName}};
	
	$.ajax({
		type: 'POST',
		url: url,
		beforeSend: function(xhr){
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		},
		data: JSON.stringify(jsonData),
		contentType: 'application/json'
	}).done(function(stateId){
		selectNewlyAddedState(stateId, stateName);
		showToastMessage("The new state has been added");
	}).fail(function(){
		showToastMessage("ERROR: Could not connect to server or server encountered an error")
	});
	
}

function selectNewlyAddedState(stateId, stateName){
	// them option vao select
	$("<option>").val(stateId).text(stateName).appendTo(dropDownState);
	
	// sau khi them vao select thi de option do duoc selected
	$("#dropDownStates option[value= '" + stateId + "']").prop("selected", true);
	
	fieldCountryName.val("").focus();
}

function updateState(){
	url = contextPath + "states/save";
	stateId = dropDownState.val();
	stateName = fieldStateName.val();
	
	countryId = dropDownCountryForState.val().split("-")[0];
	countryName = selectedCountry.text();
	
	jsonData = {id: stateId, name: stateName, country: {id: countryId, name: countryName}};
	
	$.ajax({
		type: 'POST',
		url: url,
		beforeSend: function(xhr){
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		},
		data: JSON.stringify(jsonData),
		contentType: 'application/json'
	}).done(function(stateId){
		$("#dropDownStates option:selected").text(stateName);
		showToastMessage("The state has been updated");
	}).fail(function(){
		showToastMessage("ERROR: Could not connect to server or server encountered an error")
	});
	
}

function deleteState(){
	
	stateId = dropDownState.val();
	url = contextPath + "states/delete/" + stateId;

	/*$.get(url, function(){
		$("#dropDownStates option[value= '" + stateId + "']").remove();
		changeFormStateToNew();
	})*/
	
	$.ajax({
		type: 'DELETE',
		url: url,
		beforeSend: function(xhr){
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
	}).done(function(){
		$("#dropDownStates option[value= '" + stateId + "']").remove();
		changeFormStateToNew();
		showToastMessage("The state have been deleted");
	}).fail(function(){
		showToastMessage("ERROR: Could not connect to server or server encountered an error")
	});		
}


function showToastMessage(message){
	$("#toastMessage").text(message);
	$(".toast").toast('show');
}