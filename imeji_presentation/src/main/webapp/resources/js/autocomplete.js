/*
 * make use Jquery UI auto-complete component
 * official sample see: http://jqueryui.com/autocomplete/#multiple-remote
 */


var datasourceUrl;
var result;
/*
 * Update remote calling source url Called when input field focus:
 * onfocus="getDatasourceUrl('#{statement.vocabulary}')"
 */
function getDatasourceUrl(url) {
	datasourceUrl = url
}

$(function() {
	//This add auto-complete to all input fields on page,
	//i.e, field has class "xHuge_txtInput"
	$(".xHuge_txtInput")
	// don't navigate away from the field on tab when selecting an item
	   .bind(
			"keydown",
			function(event) {
				if (event.keyCode === $.ui.keyCode.TAB
						&& $(this).data("autocomplete").menu.active) {
					event.preventDefault();
				}
			}).autocomplete({
		// source retrieve data to display popup 
		source : function(request, response) {
			$.getJSON("/imeji/autocompleter", {
				searchkeyword : request.term,
				datasource : datasourceUrl
			}, function(jsonData) {
				/*jsonData is result returned from servlet /imeji/autocompleter
				* According to http://api.jqueryui.com/autocomplete/#option-source
				* jsonData has to be either Array:[ "Choice1", "Choice2" ]
				* Or An array of objects with label and value properties: [ { label: "Choice1", value: "value1" }, ... ]
				* In our case, servlet add label&value into result.
				**/
				result=jsonData;
				response(result)

			});
		},
		// this search event fired before search beginning 
		//and it is used to cancel "unqualified" search, i.e.,return false;
		search : function() {
			//This limit search fired on input field with ids below only:
			if(this.id.indexOf("inputFamilyName")==-1||this.id.indexOf("inputLocationName")==-1){
				return false;
			}	
			// custom minLength, currently start query after entering 2
			// characters,
			var term = extractLast(this.value);
			if (term.length < 2) {
				return false;
			}
		},
		focus : function() {
			// prevent value inserted on focus
			return false;
		},
		// Action fired when use select a value from popup
		// fill-in inputLatitude and inputLongitude fields for geolocation search
		// fill-in all names, institute fields... for name search
		//Ref; http://jqueryui.com/autocomplete/#custom-data
		select : function(event, ui) {
			if(this.id.indexOf("inputLocationName")!=-1){
				//FIXME how to get complete ids for input fields below?
				//$( "#inputLatitude" ).val( ui.item.location.lat );	
				//$( "#inputLongitude" ).val( ui.item.location.lat );	
				alert("TODO: fillin Lat and Long fields with:"+[ui.item.location.lat,ui.item.location.lat])
			}
			return;
		}
	});
});