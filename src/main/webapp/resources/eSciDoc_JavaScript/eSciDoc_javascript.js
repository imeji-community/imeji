/*
*
* CDDL HEADER START
*
* The contents of this file are subject to the terms of the
* Common Development and Distribution License, Version 1.0 only
* (the "License"). You may not use this file except in compliance
* with the License.
*
* You can obtain a copy of the license at license/ESCIDOC.LICENSE
* or http://www.escidoc.de/license.
* See the License for the specific language governing permissions
* and limitations under the License.
*
* When distributing Covered Code, include this CDDL HEADER in each
* file and include the License file at license/ESCIDOC.LICENSE.
* If applicable, add the following below this CDDL HEADER, with the
* fields enclosed by brackets "[]" replaced with your own identifying
* information: Portions Copyright [yyyy] [name of copyright owner]
*
* CDDL HEADER END
*/

/*
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* f�r wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur F�rderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

/*ADDS MULTIPLE EVENTS TO A EVENTLISTENER*/
function addEvent(obj, evType, fn){
    if (obj.addEventListener) {
        obj.addEventListener(evType, fn, false);
        return true;
    }
    else if (obj.attachEvent) {
        var r = obj.attachEvent("on" + evType, fn);
        return r;
    }
    else {
        return false;
    }
}

/*START ALL EXTERNAL JAVASCRIPTS*/
function install_javascripts() {
}



addEvent(window, 'load', function(){window.setTimeout('install_javascripts()', 1);});


function updateCustomSelectbox(selectbox) {
	var customSelectbox, select, val, textContainer, icon, textWidth;
	select = selectbox;
	customSelectbox = select.parents(".imj_customSelectbox");
	customSelectbox.width(select.width());
	
	text = customSelectbox.find(".imj_selectionText");
	icon = customSelectbox.find(".imj_buttonSortDescending_16");
	
	val = select.val(); // is the logic value of the current selectbox, but not the text which is displayed
	
	select.find("option").each(function(i, opt){ // find the right option to get the right text value
		if ($(opt).val() == val) {
			val = $(opt).text(); // is now the option text
			$(opt).parent().attr("title", val);
		}
	});
	text.text(val);
	textWidth = select.width() - icon.width() - Math.round(Number(icon.css("margin-left").replace("px", ""))) + 2;
	text.width( textWidth );
}

/* this function updates the selectText container with the selected item of selectbox */
function customSelectbox(obj) {
	if (obj) {
		// implement the onchange functionality
	} else {
		$(".imj_customSelectbox select").each(function(i) { // search for every select inside of .imj_customSelectbox in DOM and update the text
			updateCustomSelectbox($(this));
		});
	}
}
$(function(){
	customSelectbox();
});
