/**
 * JQuery event for Highlight methods
 */
function highlighter() {
	jQuery(function() {
		jQuery(".highlight_area").mouseover(function() {
			highlight(parseId(jQuery(this).attr('class')));
		}).mouseout(function() {
			reset_highlight();
		});
	});
}
/**
 * Trigger the highlight on page load
 */
jQuery(document).ready(function() {
	highlighter();
});
/**
 * Highlight the element wit th id passed in the parameter. If it has children
 * highlight them. This method should be triggered on mouse over. This element
 * is then recognized by the css class "id_ +id"
 * 
 * @param id
 */
/* TODO: DELETE FUNCTION HIGHLIGHT + DEPENDENCIES and create/use css definitions */
function highlight(id) {
	jQuery('.id_' + id).css('background-color', '#393939 !important');
	highlight_childs(id);
}
/**
 * Highlight the child of an element defined by the id passed in the parameters.
 * the children are recognized when they defined the css class 'parent_ + id'
 * 
 * @param id
 */
function highlight_childs(id) {
	var childs = jQuery('.parent_' + id);
	childs.css('background-color', '#494949');
	childs.each(function() {
		// find all non space character after the string "id_"
		var childId = parseId(jQuery(this).attr('class'));
		highlight_childs(childId);
	});
}
/**
 * Reset highlighted element to their original value. Should be triggered on
 * mouse out
 */
function reset_highlight() {
	jQuery('.highlight_area').css('background-color', '');
}
/**
 * Parse the id define in the css class by id_class
 * 
 * @param classname
 * @returns
 */
function parseId(classname) {
	var pattern = new RegExp("id_" + "\\S*");
	if (classname.match(pattern) != null)
		return classname.match(pattern)[0].substring(3);
	else
		return null;
}

/*******************************************************************************
 * START : Function for the Metadata Profile pages:
 ******************************************************************************/
/**
 * JQUERY UI Drag and drop for metadata
 */
function dragAndDrop() {
	$(function() {
		var dragged;
		$(".draggable").draggable({
			zIndex : 100,
			opacity : 0.1,
			cursor : "move",
			axis : "y",
			revert : 'invalid',
		});
		$(".draggable").on("dragstart", function(event, ui) {
			dragged = $(this).attr('id') + 'DragButton';
			hide_non_droppable_area(parseId($(this).attr('class')));
		});
		$(".draggable").on("dragstop", function(event, ui) {
			show_all_droppable_elements();
		});
		$(".dropMetadata").droppable({
			hoverClass : "drop-hover",
			accept : '.draggable',
			activeClass : "dropMetadataActive",
			greedy : true,
			drop : function(event, ui) {
				setOpacity();
				var id = $(this).attr('id') + "Button";
				document.getElementById(dragged).click();
				document.getElementById(id).click();
			}
		});
		$(".dropChild").droppable(
				{
					hoverClass : "drop-hover",
					accept : '.draggable',
					activeClass : "dropChildActive",
					drop : function(event, ui) {
						setOpacity();
						var id = $(this).attr('id').replace("metadata",
								"dropChildButton");
						document.getElementById(dragged).click();
						document.getElementById(id).click();
					}
				});
	});
}
/**
 * 
 * @param index
 */
function unSelectUnique(index) {
	var i = 0;
	while (document
			.getElementById('profileForm:profile:' + i + ':uniqueSelect') != null) {
		if (index != i) {
			document.getElementById('profileForm:profile:' + i
					+ ':uniqueSelect').checked = false;
			document.getElementById('profileForm:profile:' + i
					+ ':radioDescription:0').checked = false;
		} else if (document.getElementById('profileForm:profile:' + i
				+ ':uniqueSelect').checked) {
			document.getElementById('profileForm:profile:' + i
					+ ':uniqueSelect').checked = false;
			document.getElementById('profileForm:profile:' + i
					+ ':radioDescription:0').checked = false;
		} else {
			document.getElementById('profileForm:profile:' + i
					+ ':uniqueSelect').checked = true;
			document.getElementById('profileForm:profile:' + i
					+ ':radioDescription:0').checked = true;
		}
		i++;
	}
}
/**
 * Write the options a of drop down menu
 * 
 * @param select -
 *            the drop down menu
 * @param optionsString -
 *            the options as string
 */
function write_options(select, optionsString) {
	select.innerHTML = '';
	var options = optionsString.split('|');
	for (var i = 0; i < options.length; i++) {
		var option = document.createElement("option");
		option.value = options[i].split(',')[0];
		option.text = options[i].split(',')[1];
		if (option.value != '') {
			select.appendChild(option);
		}
	}
}
/**
 * Set the whole form opacity
 */
function setOpacity() {
	document.getElementById('profileForm:ajaxArea').style.opacity = '0.2';
}
/**
 * Show the area which are droppable
 */
function show_all_droppable_elements() {
	$('.statement_area').css('visibility', 'visible');
}
/**
 * Hide all areas where is is not possible to drop a metadata
 * 
 * @param statementId
 */
function hide_non_droppable_area(statementId) {
	var area = $('.statement_area_id_' + statementId);
	area.each(function() {
		jQuery(this).css('visibility', 'hidden');
		hide_childs(statementId);
	});
}
/**
 * Hide the metadata which are the child the one with the passed id
 * 
 * @param id
 */
function hide_childs(id) {
	var childs = jQuery('.statement_area_parent_' + id);
	childs.css('visibility', 'hidden');
	childs.each(function() {
		// find all non space character after the string "id_"
		var childId = parseId(jQuery(this).attr('class'));
		hide_childs(childId);
	});
}

/*******************************************************************************
 * END : Function for the Metadata Profile pages:
 ******************************************************************************/
/**
 * 
 * @param suggestionBox
 * @param index
 * @param pos
 * @param type
 */
function autosuggestGoogleGeoAPI(suggestionBox, index, pos, type) {
	var items = suggestionBox.getSelectedItems();
	var address, longitude, latitude;
	if (items && items.length > 0) {
		for ( var i = 0; i < items.length; i++) {
			try {
				address = items[i].adress;
			} catch (e) {
				address = ' ';
			}
			try {
				longitude = items[i].longitude;
			} catch (e) {
				longitude = ' ';
			}
			try {
				latitude = items[i].latitude;
			} catch (e) {
				latitude = ' ';
			}
		}

		var baseId = 'formular:statementList:' + pos + ':metadata:' + index
				+ ':MetadataInput:';

		if (type == 0) {
			if (index >= 0) {
				baseId = 'formular:imagesList:' + index
						+ ':metadata:0:MetadataInput:';
			} else {
				baseId = 'formular:MetadataInput:';
			}
		}

		setInputTextValue(baseId + 'inputAddress', address);
		setInputTextValue(baseId + 'inputLongitude', longitude);
		setInputTextValue(baseId + 'inputLatitude', latitude);

		autoSuggestWrite(suggestionBox, index, pos, type);

	}
}

function autoSuggestWrite(suggestionBox, index, pos, type) {
	var items = suggestionBox.getSelectedItems();
	var familyName, firstName, alternative, id, org, title, complete;
	if (items && items.length > 0) {
		for ( var i = 0; i < items.length; i++) {
			complete = items[i];
			try {
				familyName = items[i].http_xmlns_com_foaf_0_1_family_name;
			} catch (e) {
				familyName = ' ';
			}
			try {
				firstName = items[i].http_xmlns_com_foaf_0_1_givenname;
			} catch (e) {
				firstName = ' ';
			}
			try {
				id = items[i].id;
			} catch (e) {
				id = ' ';
			}
			try {
				alternative = items[i].http_purl_org_dc_terms_alternative;
			} catch (e) {
				alternative = ' ';
			}
			try {
				org = items[i].http_purl_org_escidoc_metadata_terms_0_1_position.http_purl_org_eprint_terms_affiliatedInstitution;
			} catch (e) {
				org = ' ';
			}
			try {
				org = items[i].http_purl_org_escidoc_metadata_terms_0_1_position.http_purl_org_eprint_terms_affiliatedInstitution;
			} catch (e) {
				org = ' ';
			}
			try {
				title = items[i].http_purl_org_dc_elements_1_1_title;
			} catch (e) {
				title = ' ';
			}

		}

		var baseId = 'formular:statementList:' + pos + ':metadata:' + index
				+ ':MetadataInput:';

		if (type == 0) {
			if (index >= 0) {
				baseId = 'formular:imagesList:' + index
						+ ':metadata:0:MetadataInput:';
			} else {
				baseId = 'formular:MetadataInput:';
			}
		}

		setInputTextValue(baseId + 'inputFamilyName', familyName);
		setInputTextValue(baseId + 'inputFirstName', firstName);
		setInputTextValue(baseId + 'inputAlternative', alternative);
		setInputTextValue(baseId + 'inputOrganization', org);
		setInputTextValue(baseId + 'inputIdentifier', id);
		setInputTextValue(baseId + 'inputLanguageIdentifier', id);
		setInputTextValue(baseId + 'inputLanguageName', title)

		if (title != null) {
			setInputTextValue(baseId + 'inputText', title);
		} else {
			setInputTextValue(baseId + 'inputText', complete);
		}

	}
}

function setInputTextValue(id, value) {
	if (document.getElementById(id)) {
		if (value && value != 'undefined') {
			document.getElementById(id).value = value;
		} else {
			document.getElementById(id).value = '';
		}
	}
}

function collapse(firstPart, secondPart) {
	return firstPart + '.' + secondPart;
}

/**
 * When a confirmation is confirmed, make the panel emty until the method called
 * is done
 * 
 * @param button
 * @param message
 */
function submitPanel(panelId, message) {
	var panel = document.getElementById(panelId);
	if (panel != null) {
		panel.innerHTML = '<h2><span class="free_area0_p8 xTiny_marginLExcl">'
				+ message + '</span></h2>';
	}
}

/**
 * Part of the Patch for jsf
 *//*
var currentViewState;
jsf.ajax
		.addOnEvent(function(e) {
			var xml = e.responseXML;
			var source = e.source;
			var status = e.status;
			if (status === 'success') {
				var response = xml.getElementsByTagName('partial-response')[0];
				if (response !== null) {
					var changes = response.getElementsByTagName('changes')[0];
					if (changes != undefined) {
						var updates = changes.getElementsByTagName('update');
						if (updates != undefined) {
							for ( var i = 0; i < updates.length; i++) {
								var update = updates[i];
								var id = update.getAttribute('id');
								if (id === 'javax.faces.ViewState') {
									currentViewState = update.firstChild.data;
									// update all forms
									var forms = document.forms;
									for ( var j = 0; j < forms.length; j++) {
										var form = forms[j];
										var field = form.elements["javax.faces.ViewState"];
										if (typeof field == 'undefined') {
											field = document
													.createElement("input");
											field.type = "hidden";
											field.name = "javax.faces.ViewState";
											form.appendChild(field);
										}
										field.value = currentViewState;
									}
								}
							}
						}
					}
				}
			}

		});
*/
/**
 * JSF patch for jsf for reaload of ajax component after ajax request
 *//*
var patchJSF = function() {
	jsf.ajax
			.addOnEvent(function(e) {
				if (e.status === 'success') {
					$(
							"partial-response:first changes:first update[id='javax.faces.ViewState']",
							e.responseXML)
							.each(
									function(i, u) {
										// update all forms
										$(document.forms)
												.each(
														function(i, f) {
															var field = $(
																	"input[name='javax.faces.ViewState']",
																	f);
															if (field.length == 0) {
																field = $(
																		"<input type=\"hidden\" name=\"javax.faces.ViewState\" />")
																		.appendTo(
																				f);
															}
															field
																	.val(u.firstChild.data);
														});
									});
				}
			});
};
*/
/* open a dialog
 * functions are shifted and modified from old template.xhtml
 */
function openDialog(id) {
	/*set the dialog in center of the screen*/
	var dialog = $(document.getElementById(id));
	dialog.css("left", Math.max(0, Math.round(($(window).width() - $(dialog).outerWidth()) / 2) + $(window).scrollLeft()) + "px");
	/*open the dialog*/
	dialog.show();
	$(".imj_modalDialogBackground").show();
}
/*close a dialog*/
function closeDialog(id) {
	var dialog = $(document.getElementById(id));
	$(".imj_modalDialogBackground").hide();
	dialog.hide();
}