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
		input.value = '';
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

/*
 * global function to load content via ajax, function use jQuery
 * the callback function get the target and returndata
 */ 
function loadContent(loadURL, target, callback) {
	$.ajax({
		type: "GET",
		url: loadURL,
		cache: false,
		success: function(returndata) {
			if (target) { $(target).html(returndata); }
			if (callback) {	setTimeout(callback, 15, target, returndata); }
		}
	});
}


// Initialize a global swc object for easy handling
var swcObject = {};

/*
 * initialize the rendering of a SWC file
 * @param swcdata: swc file content in clear format
 */ 
function initSWC(swcdomelement) {
	var shark, canvas, placeholder;
	swcObject.data = $(swcdomelement).text();
	swcObject.json = swc_parser(swcObject.data);
	canvas = document.createElement('canvas');
	
	if ( window.WebGLRenderingContext && (canvas.getContext("webgl") || canvas.getContext("experimental-webgl")) ) {
		placeholder = $('*[id*='+swcObject.placeholderID+']');
		placeholder.get(0).style.display="none";
		shark = new SharkViewer({swc: swcObject.json, dom_element: swcObject.displayID, WIDTH: swcObject.width, HEIGHT: swcObject.height, center_node: -1, show_stats:false, screenshot: false});
		shark.init();
		shark.animate();
	} else {
		document.getElementById(swcObject.failedMsgID).style.display="block";
	}
}

/*
 * start function to load the SWC file
 * @param src: dom-source element with parameter
 */ 
function loadSWC(src, element_name) {
	var source, swc;
	source = $(src);
	swcObject = {
		domSource: src,
		dataURL: source.data("swc-source") || undefined, 
		serviceURL: source.data("swc-service") || undefined, 
		elementID: element_name, 
		displayID: (source.data("target-id")[0] == '#') ? source.data("target-id").substring(1) : source.data("target-id"),
		width: source.data("target-width"),
		height: source.data("target-height"),
		placeholderID: (source.data("placeholder-id")[0] == '#') ? source.data("placeholder-id").substring(1) : source.data("placeholder-id"),
		failedMsgID: (source.data("failed-msg-id")[0] == '#') ? source.data("failed-msg-id").substring(1) : source.data("failed-msg-id")
	};
	// loadContent(swcObject.dataURL, '#'+swcObject.elementID, initSWC);
	initSWC('#'+swcObject.elementID);
}


/**
 * Avoid double click submit for all submit buttons
 * @param data
 */
function handleDisableButton(data) {
    if (data.source.type != "submit") {
        return;
    }

    switch (data.status) {
        case "begin":
            data.source.disabled = true;
            break;
        case "complete":
            data.source.disabled = false;
            break;
    }    
}


/** START **/
if (typeof jsf !== 'undefined') {
jsf.ajax.addOnEvent(function(data) {
    if (data.status == "success") {
        fixViewState(data.responseXML);
    }
    handleDisableButton(data);
});
}

function fixViewState(responseXML) {
    var viewState = getViewState(responseXML);

    if (viewState) {
        for (var i = 0; i < document.forms.length; i++) {
            var form = document.forms[i];

            if (form.method == "post") {
                if (!hasViewState(form)) {
                    createViewState(form, viewState);
                }
            }
            else { // PrimeFaces also adds them to GET forms!
                removeViewState(form);
            }
        }
    }
}

function getViewState(responseXML) {
    var updates = responseXML.getElementsByTagName("update");

    for (var i = 0; i < updates.length; i++) {
        var update = updates[i];

        if (update.getAttribute("id").match(/^([\w]+:)?javax\.faces\.ViewState(:[0-9]+)?$/)) {
            return update.firstChild.nodeValue;
        }
    }

    return null;
}

function hasViewState(form) {
    for (var i = 0; i < form.elements.length; i++) {
        if (form.elements[i].name == "javax.faces.ViewState") {
            return true;
        }
    }

    return false;
}

function createViewState(form, viewState) {
    var hidden;

    try {
        hidden = document.createElement("<input name='javax.faces.ViewState'>"); // IE6-8.
    } catch(e) {
        hidden = document.createElement("input");
        hidden.setAttribute("name", "javax.faces.ViewState");
    }

    hidden.setAttribute("type", "hidden");
    hidden.setAttribute("value", viewState);
    hidden.setAttribute("autocomplete", "off");
    form.appendChild(hidden);
}

function removeViewState(form) {
    for (var i = 0; i < form.elements.length; i++) {
        var element = form.elements[i];
        if (element.name == "javax.faces.ViewState") {
            element.parentNode.removeChild(element);
        }
    }
}

/** END **/