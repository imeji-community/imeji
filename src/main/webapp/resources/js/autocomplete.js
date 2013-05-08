/*
 * make use Jquery UI auto-complete component
 * official sample see: http://jqueryui.com/autocomplete/#multiple-remote
 */

var datasourceUrl;
var result;
// After how many character the suggest is started
var offset = 2;
/*
 * Update remote calling source url Called when input field focus:
 * onfocus="getDatasourceUrl('#{statement.vocabulary}')"
 */
function getDatasourceUrl(url) {
	datasourceUrl = url;
	 offset = 2;
}

function getDatasourceUrl(url, startAfter) {
	datasourceUrl = url;
	offset = startAfter;
}
function split(val) {
	return val.split(/,\s*/);
}
function extractLast(term) {
	return split(term).pop();
}
$(function() {
	// This add auto-complete to all input fields on page,
	// i.e, field has class "xHuge_txtInput"
	$(":input")
			// don't navigate away from the field on tab when selecting an item
			.bind(
					"keydown",
					function(event) {
						if (event.keyCode === $.ui.keyCode.TAB
								&& $(this).data("autocomplete").menu.active) {
							event.preventDefault();
						}
					})
			.autocomplete(
					{
						// source: datasourceUrl,
						// source retrieve data to display popup

						/*
						 * jsonData is result returned from servlet
						 * /imeji/autocompleter According to
						 * http://api.jqueryui.com/autocomplete/#option-source
						 * jsonData has to be either Array:[ "Choice1",
						 * "Choice2" ] Or An array of objects with label and
						 * value properties: [ { label: "Choice1", value:
						 * "value1" }, ... ] In our case, servlet add
						 * label&value into result.
						 */
						source : function(request, response) {
							$.getJSON("/autocompleter", {
								searchkeyword : request.term,
								datasource : datasourceUrl
							}, function(jsonData) {
								result = jsonData;
								response(result);
							});
						},
						minLength : 0,
						messages : {
							noResults : '',
							results : function() {
								return '';
							}
						},
						// this search event fired before search beginning
						// and it is used to cancel "unqualified" search,
						// i.e.,return false;
						search : function() {
							// custom minLength, currently start query after
							// entering x
							// characters,
							var term = extractLast(this.value);
							if (term.length < offset) {
								return false;
							}
						},
						focus : function() {
							// prevent value inserted on focus
							return false;
						},
						/**
						 * Action triggered when a value is selected in the
						 * autocomplete. Fill in the input values
						 * 
						 * @param event
						 * @param ui
						 * @returns {Boolean}
						 */
						select : function(event, ui) {
							/*
							 * User the id of the current input to set the
							 * values of the others input
							 */
							var idEls = this.id.split(":");
							var inputId = "";
							for ( var i = 0; i < idEls.length - 1; i++) {
								inputId = inputId + idEls[i] + ":";
							}
							// Write the value of the current input
							if (ui.item.value != null) {
								document.getElementById(this.id).value = ui.item.value;
							}
							if (ui.item.family != null) {
								document.getElementById(inputId
										+ "inputFamilyName").value = ui.item.family;
							}
							if (ui.item.givenname != null) {
								document.getElementById(inputId
										+ "inputFirstName").value = ui.item.givenname;
							}
							if (ui.item.alternatives) {
								document.getElementById(inputId
										+ "inputAlternative").value = ui.item.alternatives;
							}
							if (ui.item.id != null) {
								document.getElementById(inputId
										+ "inputIdentifier").value = ui.item.id;
							}
							if (ui.item.orgs != null) {
								document.getElementById(inputId
										+ "inputOrganization").value = ui.item.orgs;
							}
							if (ui.item.latitude != null) {
								document.getElementById(inputId
										+ "inputLatitude").value = ui.item.latitude;
							}
							if (ui.item.longitude != null) {
								document.getElementById(inputId
										+ "inputLongitude").value = ui.item.longitude;
							}
							if (ui.item.licenseId != null) {
								document.getElementById(inputId
										+ "inputLicenseId").value = ui.item.licenseId;
							}
							return false;
						}
					}).focus(function() {
				// if the offset is 0, then show results on focus
				if (offset == 0) {
					this.value = " ";
					$(this).autocomplete("search");
				}
			});
});