/**
 * true if the input is a number
 * 
 * @param n
 * @returns {Boolean}
 */
function isNumber(n) {
	n = formatNumber(n);
	return !isNaN(parseFloat(n)) && isFinite(n);
}
/**
 * Return a number which can be validated
 * @param n
 * @returns
 */
function formatNumber(n){
	return n.replace(",", '.').replace(" ", '');;
}

/**
 * Validate the value of the imput number
 * @param input
 */
function validateInputNumber(input) {
	input.value = formatNumber(input.value);
	if (!isNumber(input.value)) {
		input.value = input.value.substring(0,input.value.length - 1);
	}
}

/**
 * Parse the id define in the css class by id_class
 * 
 * @param classname
 *            [@param classRef]
 * @returns
 */
function parseId(classname, classRef) {
	var ptn;
	if (classRef) {
		ptn = new RegExp(classRef + '(\\S*)');
	} else {
		ptn = /id_(\S*)/;
	}
	ptn.exec(classname);
	if (RegExp.$1 != "") {
		return RegExp.$1;
	} else {
		return false;
	}
};

/**
 * JQuery event for Highlight methods
 */
function highlighter() {
	jQuery(function() {
		jQuery(".highlight_area").mouseover(function() {
			var itemId = parseId(jQuery(this).attr('class'));
			var prntId = parseId(jQuery(this).attr('class'), 'parent_');
			highlight(itemId); // highlight the current element
			checkForChilds(itemId);// highlight the child elements - recursive
			// - of the current item, if it's given
			if (prntId) { // check if the current item is a child of another
				highlight(prntId, 'id_'); // hightlight the next parent item,
				// if the current item is a child
			}
		}).mouseout(function() {
			reset_highlight();
		});
	});
};
/**
 * check if items given with the parent id. It's triggered through mouse-over
 * event in highlighter function.
 * 
 * @param id
 */
function checkForChilds(id) {
	var childs, curId;
	// all childs have a parent-class with the parent id
	childs = jQuery('.parent_' + id);

	if (childs.length > 0) { // if childs given
		childs.each(function(i, obj) { // loop through the childs an check if
			// themselves have also childs
			jQuery(this).addClass('imj_highlightDependencies');
			curId = parseId(jQuery(this).attr('class'));
			if (curId) {
				checkForChilds(curId);
			}
		});
	}
};

/**
 * Highlight the element with id passed in the parameter. If it has children
 * highlight them. This method should be triggered on mouse over. This element
 * is then recognized by the css class "id_ +id"
 * 
 * @param id
 *            [@param alter]
 */
function highlight(id, alter) {
	var items;
	items = (alter) ? jQuery('.' + alter + id) : jQuery('.id_' + id);
	items.addClass("imj_highlightDependencies");
};
/**
 * Reset highlighted element to their original value. Should be triggered on
 * mouse out DELETE FUNCTION HIGHLIGHT + DEPENDENCIES and create/use css
 * definitions
 */
function reset_highlight() {
	jQuery('.imj_highlightDependencies').removeClass(
			"imj_highlightDependencies");
};

/**
 * Trigger the highlight on page load
 */
jQuery(document).ready(function() {
	highlighter();
});

/*******************************************************************************
 * START : Function for the Metadata Profile pages:
 ******************************************************************************/
/**
 * JQUERY UI Drag and drop for metadata
 */
function dragAndDrop() {
	jQuery(function() {
		var dragged;
		jQuery(".draggable").draggable({
			zIndex : 100,
			opacity : 0.1,
			cursor : "move",
			axis : "y",
			revert : 'invalid',
		});
		jQuery(".draggable").on("dragstart", function(event, ui) {
			dragged = jQuery(this).attr('id') + 'DragButton';
			hide_non_droppable_area(parseId(jQuery(this).attr('class')));
		});
		jQuery(".draggable").on("dragstop", function(event, ui) {
			show_all_droppable_elements();
		});
		jQuery(".dropMetadata").droppable({
			hoverClass : "drop-hover",
			accept : '.draggable',
			activeClass : "dropMetadataActive",
			greedy : true,
			drop : function(event, ui) {
				setOpacity();
				var id = jQuery(this).attr('id') + "Button";
				document.getElementById(dragged).click();
				document.getElementById(id).click();
			}
		});
		jQuery(".dropChild").droppable(
				{
					hoverClass : "drop-hover",
					accept : '.draggable',
					activeClass : "dropChildActive",
					drop : function(event, ui) {
						setOpacity();
						var id = jQuery(this).attr('id').replace("metadata",
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
function write_options(select, value, optionsString) {
	select.innerHTML = '';
	var options = optionsString.split('|');
	for ( var i = 0; i < options.length; i++) {
		var option = document.createElement("option");
		option.value = options[i].split(',')[0];
		option.text = options[i].split(',')[1];
		if (option.value != '') {
			select.appendChild(option);
		}
		if (option.value == value) {
			option.selected = 'selected';
		}
	}
	select.value = value;
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
 * When a confirmation is confirmed, make the panel emty until the method called
 * is done
 * 
 * @param button
 * @param message
 */
// seems to be unused - March 31th, 2014
function submitPanel(panelId, message) {
	var panel = document.getElementById(panelId);
	if (panel != null) {
		panel.innerHTML = '<h2><span class="free_area0_p8 xTiny_marginLExcl">'
				+ message + '</span></h2>';
	}
}

/**
 * Part of the Patch for jsf
 */
var currentViewState;
if (typeof jsf !== 'undefined') {
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
							var updates = changes
									.getElementsByTagName('update');
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
}

/**
 * JSF patch for jsf for reload of ajax component after ajax request
 */
var patchJSF = function() {
	if (typeof jsf !== 'undefined') {
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
	}
};

/*
 * open a dialog functions are shifted and modified from old template.xhtml
 */
function openDialog(id) {
	/* set the dialog in center of the screen */
	var dialog = $(document.getElementById(id));
	dialog.css("left", Math.max(0, Math.round(($(window).width() - $(dialog)
			.outerWidth()) / 2)
			+ $(window).scrollLeft())
			+ "px");
	/* open the dialog */
	dialog.show();
	$(".imj_modalDialogBackground").show();
}
/* close a dialog */
function closeDialog(id) {
	var dialog = $(document.getElementById(id));
	$(".imj_modalDialogBackground").hide();
	dialog.hide();
}

$(window).resize(
		function(evt) {
			var dialog = $('.imj_modalDialogBox:visible');
			if (dialog.length > 0) {
				dialog.css("left", Math.max(0,
						Math
								.round(($(window).width() - $(dialog)
										.outerWidth()) / 2)
								+ $(window).scrollLeft())
						+ "px");
			}
		});

/*
 * Extended usability function to set the content width of overlay menu to the
 * minimum of the trigger width. It will be called one time after page loading
 * is finished
 */
$(function() {
	$('.imj_overlayMenu').each(function(i, obj) {
		var menuHeaderWidth = $(this).find(".imj_menuHeader").width();
		var menuBody = $(this).find(".imj_menuBody");
		if (menuHeaderWidth > menuBody.width()) {
			menuBody.width(menuHeaderWidth);
		}

	});
});