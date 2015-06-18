if(typeof(cookieVersion) == 'undefined') {
	var cookieVersion = "1.1";
}
if (typeof(jsfURL) == 'undefined') {
	var jsfURL = endsWith(homeURL, '/') ? homeURL : homeURL + '/';
	
}
if(typeof(jsURL) == 'undefined') {
	var jsURL = endsWith(homeURL, '/') ? homeURL + 'resources/eSciDoc_JavaScript/' : homeURL + '/resources/eSciDoc_JavaScript/';
}
if(typeof(coneURL) == 'undefined') {
	/* var coneURL = '../../cone/'; */
	var coneURL = 'http://pubman.mpdl.mpg.de/cone/';
}

function endsWith(str, suffix) {
    return str.indexOf(suffix, str.length - suffix.length) !== -1;
}

function appendScript(link) {
	var script = document.createElement('script');
	script.setAttribute("type", "text/javascript");
	script.setAttribute("language", "JavaScript");
	script.setAttribute("src", link);
	document.getElementsByTagName('head')[0].appendChild(script);
}

//appendScript(coneURL + 'js/jquery.suggest.js');
//appendScript(jsURL + 'eSciDoc_component_JavaScript/eSciDoc_ext_paginator.js');
//appendScript(jsURL + 'eSciDoc_component_JavaScript/eSciDoc_item_list.js');
//appendScript(jsURL + 'eSciDoc_component_JavaScript/eSciDoc_full_item.js');
//appendScript(jsURL + 'eSciDoc_component_JavaScript/eSciDoc_single_elements.js');
//appendScript(jsURL + 'jquery.shiftcheckbox.js');
appendScript(jsURL + 'eSciDoc_javascript.js');
appendScript(jsURL + 'main.js');
