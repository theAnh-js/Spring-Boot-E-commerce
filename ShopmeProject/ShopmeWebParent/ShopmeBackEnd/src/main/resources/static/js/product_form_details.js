$(document).ready(function() {
	
	$("a[name='linkRemoveDetail']").each(function(index){
		$(this).click(function(){
			removeDetailSectionByIndex(index);
		})
	})

});
function addNextDetailSection(){
	allDivDetails = $("[id^='divDetail']")
	divDetailsCount = allDivDetails.length;
	
	nextDivDetailId = divDetailsCount;
	//alert(nextDivDetailId);
	
	htmlDetailSection = `
		<div class="form-inline" id="divDetail${nextDivDetailId}">
			<input type="hidden" name="detailIDs" value="0"/>
			<label class="m-3">Name:</label>
			<input type="text" class="form-control w-25" name="detailNames" maxLength="255"/>
			
			<label class="m-3">Value:</label>
			<input type="text" class="form-control w-25" name="detailValues" maxLength="255"/>
		</div>`;
	
	$("#divProductDetails").append(htmlDetailSection);
	
	previousDivDetailSection = allDivDetails.last();
	//console.log(allDivDetails.last());
	previousDivSectionID = previousDivDetailSection.attr("id");
	htmlLinkRemove = `
		<a class="btn fas fa-times-circle fa-2x icon-dark" 
			href="javascript:removeDetailSectionById('${previousDivSectionID}')"
			title="Remove this detail"></a>
	`;
	
	previousDivDetailSection.append(htmlLinkRemove);
	
	$("input[name='detailNames']").last().focus();
}

function removeDetailSectionById(id){
	$("#" + id).remove();
}

function removeDetailSectionByIndex(index){
	$("#divDetail" + index).remove();
}