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

if(typeof cookieVersion=='undefined') {
	var cookieVersion = "1.1";
}
if(typeof jsURL=='undefined') {
	var jsURL = './resources/eSciDoc_JavaScript/';
}
if(typeof coneURL=='undefined') {
	var coneURL = '../../cone/';
}
var hiddenThemesEnabled = false;

function applyCookieStyle() {
	var cookieValue = ""
	var cookie = "layout=";
	var dc = document.cookie;
	if (dc.length > 0) {
		var start = dc.indexOf(cookie);
		if (start != -1) {
			start += cookie.length;
			var stop = dc.indexOf(";", start);
			if (stop == -1) stop = dc.length;
			cookieValue = unescape(dc.substring(start,stop));
		}
	}
	var enableHiddenShemes = false;
	cookie = "enableHiddenSchemes=";
	if (dc.length > 0) {
		var start = dc.indexOf(cookie);
		if (start != -1) {
			start += cookie.length;
			var stop = dc.indexOf(";", start);
			if (stop == -1) stop = dc.length;
			if(unescape(dc.substring(start,stop)) == 'true') {enableHiddenShemes = true; hiddenThemesEnabled = true;};
		}
	}
	var isCorrectCookieVersion = false;
	cookie = "cVersion=";
	if (dc.length > 0) {
		var start = dc.indexOf(cookie);
		if (start != -1) {
			start += cookie.length;
			var stop = dc.indexOf(";", start);
			if (stop == -1) stop = dc.length;
			if(unescape(dc.substring(start,stop)) == cookieVersion) {isCorrectCookieVersion = true;};
		}
	}
	if (cookieValue != "" && isCorrectCookieVersion && document.getElementsByTagName) {
		var el = document.getElementsByTagName("link");
		for (var i = 0; i < el.length; i++ ) {
			if (el[i].getAttribute("rel").indexOf("style") != -1 && el[i].getAttribute("id") == cookieValue && enableHiddenShemes && (el[i].getAttribute("title") == null || el[i].getAttribute("title") == "" ) ) {
				el[i].setAttribute("title", el[i].getAttribute("id"));
			}
			if (el[i].getAttribute("rel").indexOf("style") != -1 && el[i].getAttribute("id")) {
				el[i].disabled = true;
				if (el[i].getAttribute("id") == cookieValue) el[i].disabled = false;
			}
		}
	}
}

function setStyleCookie() {
	var cookieValue = "";
	if(document.getElementsByTagName) {
		var el = document.getElementsByTagName("link");
		for (var i = 0; i < el.length; i++ ) {
			var enabledCounter = 0;
			if (el[i].getAttribute("rel").indexOf("style") != -1 && el[i].getAttribute("id") && el[i].getAttribute("title") && el[i].disabled == false && enabledCounter == 0) {
				cookieValue = el[i].getAttribute("id");
				enabledCounter++;
			}
		}
	}
	var now = new Date();
	var exp = new Date(now.getTime() + (1000*60*60*24*30));
	if(cookieValue != "") {
		if(hiddenThemesEnabled) {
			document.cookie = "layout=" + escape(cookieValue) + ";" +
								"cVersion=" + cookieVersion + ";" +
								"expires=" + exp.toGMTString() + ";" +
								"path=/";
			document.cookie = "cVersion=" + cookieVersion + ";" +
								"expires=" + exp.toGMTString() + ";" +
								"path=/";
			document.cookie = "enableHiddenSchemes=true;" +
								"expires=" + exp.toGMTString() + ";" +
								"path=/";
		} else {
			document.cookie = "layout=" + escape(cookieValue) + ";" +
								"cVersion=" + cookieVersion + ";" +
								"expires=" + exp.toGMTString() + ";" +
								"path=/";
			document.cookie = "cVersion=" + cookieVersion + ";" +
								"expires=" + exp.toGMTString() + ";" +
								"path=/";
		}
	}
}

var included = false;

 /*INCLUDES EXTERNAL JAVASCRIPT TO PAGE DOM*/
function include_dom(script_filename) {
    var html_doc = document.getElementsByTagName('head').item(0);
    var js = document.createElement('script');
    js.setAttribute('language', 'javascript');
    js.setAttribute('type', 'text/javascript');
    js.setAttribute('src', script_filename);
    html_doc.appendChild(js);
    return false;
}

/*ADDS MULTIPLE EVENTS TO A EVENTLISTENER*/
function addEvent(obj, evType, fn){
 if (obj.addEventListener){
   obj.addEventListener(evType, fn, false);
   return true;
 } else if (obj.attachEvent){
   var r = obj.attachEvent("on"+evType, fn);
   return r;
 } else {
   return false;
 }
}

/*START ALL EXTERNAL JAVASCRIPTS*/
function install_javascripts() {
	installExtPaginator();
	installItemList();
	installFullItem();
	installQuickSearchShortcut();
	installDateTextbox();
	installSameHeight();
	bindSuggests();
}

/*INCLUDES EXTERNAL JAVASCRIPTS*/
function include_javascripts() {
	if(!included){
		include_dom(jsURL + 'jquery/jquery.min.js');
		include_dom(jsURL + 'eSciDoc_component_JavaScript/eSciDoc_ext_paginator.js');
		include_dom(jsURL + 'eSciDoc_component_JavaScript/eSciDoc_item_list.js');
		include_dom(jsURL + 'eSciDoc_component_JavaScript/eSciDoc_full_item.js');
		include_dom(jsURL + 'eSciDoc_component_JavaScript/eSciDoc_single_elements.js');
		include_dom(coneURL + 'js/jquery.suggest.js')
		include_dom(jsURL + 'autoSuggestFunctions.js');
		/*REITERATION NEEDED TO START ALL INCLUDED JAVASCRIPTS*/
		included = true;
		include_javascripts();
	} else {
			addEvent(window, 'load', function(){window.setTimeout('install_javascripts()', 1);});
		}
}

include_javascripts();
applyCookieStyle();
window.onunload=function(e){setStyleCookie();};