/**
 * @author Marco Schlender
 * @base plain JavaScript - 2011-04-07
 */
function returnMPDLUserAgent() {
	var user_agent = jQuery.browser;
	var txt = '';
	
	if (user_agent.msie && user_agent.version.match(/9/g)) {
		return 'ie9';
	} else if (user_agent.msie && user_agent.version.match(/8/g)) {
		return 'ie8';
	} else if (user_agent.msie && user_agent.version.match(/7/g)) {
		return 'ie7';
	} else if (user_agent.msie && user_agent.version.match(/6/g)) {
		return 'ie6';
	} else if (user_agent.safari) {
		return 'safari';
	} else if (user_agent.webkit) {
		return 'webkit';
	} else if (user_agent.mozilla) {
		return 'mozilla';
	} else {
		for (var key in user_agent) {
			txt += '\n'+key+': '+user_agent[key];
		}
		return txt;
	}
}

var mpdlCookie = new function() {
	
	this.cookieTimeout = 30;
	
	this.setCookie = function (name, wert, domain, expires, path, secure){
		var cooky = name+"="+unescape(wert);
		cooky += (domain) ? "; domain="+ domain : "";
		cooky += (expires) ? "; expires="+expires : "";
		cooky += (path) ? "; path="+path : "";
		cooky += (secure) ? "; secure" : "";
		document.cookie = cooky;
	}
	
	this.getCookie = function (name) {
		var i = 0  //Suchposition im Cookie
		var suche = name+"="
		while (i<document.cookie.length){
			if (document.cookie.substring(i, i+suche.length) == suche){
				var ende = document.cookie.indexOf(";", i+suche.length);
				ende = (ende>-1) ? ende : document.cookie.length;
				var cook = document.cookie.substring(i+suche.length, ende);
				return unescape(cook);
			}
			i++
		}
		return null
	}
	
	
	this.eraseCookie = function (name, domain, path){
		var cooky = name+"=; expires=Thu, 01-Jan-70 00:00:01 GMT";
		cooky += (domain) ? "domain=" + domain : "";
		cooky += (path) ? "path=" + path : "";
		document.cookie = cooky;
	}
	
	this.checkCookiesAllowed = function (){
	   setCookie("CookieTest", "OK");
	   if (!getCookie("CookieTest")) {
		   return false;
	   } else{
		   eraseCookie("CookieTest")
		   return true
	   }
	}
	
} /* END CLASS mpdlCookie */


var mpdlCSS = new function() {
	
	
	this.getDefaultStylesheet = function () {
		var tmpvalue = null;
		jQuery.each(jQuery('link'), function (index, element) {
			if (jQuery(element).attr('id') && jQuery(element).attr('rel').toLowerCase() === 'stylesheet' && jQuery.trim(jQuery(element).attr('title'))) {
				tmpvalue = jQuery(element).attr('id');
			}
		});
		return tmpvalue;
	}
	
	this.enablePageStyle = function (theme_id) {
		var el = document.getElementsByTagName("link");
		
		for (var i = 0; i < el.length; i++ ) {
			
			if (el[i].getAttribute("rel").toLowerCase().indexOf("style") != -1 && el[i].getAttribute("id")) {
				if (el[i].getAttribute("id") != 'holidaytheme') {
					el[i].disabled = true;
				}
				
				if(theme_id == el[i].getAttribute("id")){
					el[i].disabled = false;
				}
			}
		}
	}
	
	this.createThemebox = function (target) {
		var selectedTheme = mpdlCookie.getCookie("layout");
		var select_output = '<select name="themeSelectBox" onchange="changeThemeTo(this.value);">';
		jQuery.each(jQuery('link'), function (index, element) {
			var selected = '';
			if (selectedTheme == jQuery(element).attr('id')) {
				selected = ' selected="select"';
			}
			if (jQuery(element).attr('id') && jQuery(element).attr('rel').toLowerCase() === 'stylesheet' && jQuery.trim(jQuery(element).attr('title'))) {
				select_output += '<option value="'+jQuery(element).attr('id')+'"'+selected+'>'+jQuery(element).attr('title')+'</option>';
			} else if (jQuery(element).attr('id') && jQuery(element).attr('rel').toLowerCase() === 'alternate stylesheet' && jQuery.trim(jQuery(element).attr('title'))) {
				select_output += '<option value="'+jQuery(element).attr('id')+'"'+selected+'>'+jQuery(element).attr('title')+'</option>';
			}
		});
		select_output += '</select>';
		
		jQuery(target).html(select_output);
	}
}



var mpdlDate = new function () {
	this.hour = 0;
	
	this.getOneDay = function (format) {
		switch (format.toLowerCase()) {
			case 'minutes':
				return 60 * 24; /* like 60 minutes * 24 hours; */
				break;
			case 'seconds':
				return 60 * 60 * 24; /* like 60 seconds * 60 minutes * 24 hours; */
				break;
			case 'milli':
			case 'milliseconds':
				return 1000 * 60 * 60 * 24; /* like 1000 Milliseconds * 60 seconds * 60 minutes * 24 hours; */
				break;
		}
		return 
	}
	
	this.getNextDate = function (dayDiff, source_date) {
		return (new Date(source_date.getTime() + (dayDiff * this.getOneDay('milliseconds'))));
	}
	
	/**
	 * calculate the day difference between two dates
	 * @returns Number of days
	 * @param Date date1 - older date
	 * @param Date date2 - newest date
	 * @param String format - date input format
	 */
	this.getDateDiff = function (date_old, date_new, input_format) {
		switch (input_format) {
			case 'time':
				return Math.floor((date_new - date_old) / getOneDayMilli());
				break;
			default:
				return Math.floor((date_new.getTime() - date_old.getTime()) / (this.getOneDay('milliseconds')));
				break;
		}
	}
	
	this.now = function (format) {
		var tmpDate = new Date();
		switch (format) {
			case 'hour':
				return tmpDate.getHours();
				break;
			case 'minute':
				return tmpDate.getMinutes();
				break;
			case 'seconds':
				return tmpDate.getSeconds();
				break;
			case 'milliseconds':
				return tmpDate.getMilliseconds();
				break;
			case 'year':
				return tmpDate.getYear();
				break;
			case 'month':
				return tmpDate.getMonth();
				break;
			case 'day':
				return tmpDate.getDate();
				break;
			case 'date':
				return tmpDate;
				break;
			case 'time':
				return tmpDate.getTime();
				break;
			case 'today_date':
				return tmpDate.getYear()+'-'+tmpDate.getMonth()+'-'+tmpDate.getDate();
				break;
			default:
				return tmpDate;
				break;
		}
		return null;
	}
} /* END CLASS mpdlDate */




function changeThemeTo(theme_id) {
	mpdlCookie.eraseCookie("layout");
	mpdlCookie.eraseCookie("name");
	var timeOutDate = mpdlDate.getNextDate(mpdlCookie.cookieTimeout, mpdlDate.now('date'));
	mpdlCookie.setCookie("layout", theme_id, null, timeOutDate.toGMTString());
	mpdlCSS.enablePageStyle(theme_id);
}



var importantDate = new function () {
	this.year = null;
	
	this.roseMondayDiffDays = -48;
	this.fastnachtDiffDays = -47;
	this.aschermittwochDiffDays = -46;
	this.easterSaturdayDiffDays = -1;
	this.easterSunday = null;
	this.easterMondayDiffDays = 1;
	this.karfreitagDiffDays = -2;
	this.greenThursdayDiffDays = -3;
	this.motherdayDiffDays = 14;
	this.christiHimmelfahrtDiffDays = 39;
	this.pfingstSundayDiffDays = 49;
	this.pfingstMondayDiffDays = 50;
	this.fronleichnahmDiffDays = 60;
	this.sevenSleeperDiffDays = 64;
	
	this.importantDates = ['newyear', 'holy3kings', 'rosemonday', 'fastnacht', 'aschermittwoch', 'startsummertime', 'greenthursday', 'karfreyday', 'easterSunday', 'eastermonday', 'motherday', 'walpurgis', 'firstmay',
				'christihimmelfahrt', 'childrensday', 'pfingstsunday', 'pfingstmonday', 'fronleichnahm', 'sevensleeper', 'dayOfGermanUnit', 'thanksgiving', 'endsummertime', 'reformationday', 'allerheiligen', 
				'busbetday', 'deathsunday', 'firstAdvent', 'secondAdvent', 'thirdAdvent', 'fourthAdvent', 'holynight', 'xmas_1_day', 'xmas_2_day', 'silvester' ];
	
	this.calcEasterSunday = function(year) {
		if (year < 1584) {
			return false;
		} else if (this.year != Number(year)){
			this.year = eval(year);
		}
		
		//1
		var a = this.year%19;	//dividiere with 19 - rest is important
		//2
		var b = Math.floor(this.year/100) //dividiere with 100 and save the quotient
		var c = this.year%100;			//dividiere with 100 - rest is important
		//3
		var d = Math.floor(b/4);
		var e = b%4;
		//4
		var f = Math.floor((b+8)/25);
		//5
		var g = Math.floor((b-f+1)/3);
		//6
		var h = (19*a+b-d-g+15)%30;
		//7
		var i = Math.floor(c/4);
		var j = c%4;
		//8
		var k = (32+2*e+2*i-h-j)%7;
		//9
		var l = Math.floor((a+11*h+22*k)/451);
		//10
		var month = Math.floor((h+j-7*l+114)/31);
		var monthDay = ((h+k-7*l+114)%31) + 1;
		
		//alert(year+'-'+month+'-'+monthDay);
		this.easterSunday = new Date(this.year, month-1, monthDay, 12, 0, 0);
	};
	
	this.calcThanksgiving = function () {	//the first sunday in october
		for (i = 1; i < 8; i++) {
			var tmpDate = new Date(this.year, 9, i, 12, 0, 0);
			if (tmpDate.getDay() == 0) {
				return tmpDate;
				break;
			}
		}
	}
	
	this.calcMETSummertime = function (direction) {
		var tmpDate = null;
		switch (direction) {
			case 'start':
			case 'begin':
				for (i = 31; i > 23; i--) {
					tmpDate = new Date(this.year, 2, i, 3, 0, 0);
					if (tmpDate.getDay() == 0) {
						return tmpDate;
						break;
					}
				}
				break;
			case 'end':
			case 'stop':
				for (i = 30; i > 22; i--) {
					tmpDate = new Date(this.year, 9, i, 3, 0, 0);
					if (tmpDate.getDay() == 0) {
						return tmpDate;
						break;
					}
				}
				break;
		}
	}
	
	this.calcFourthAdvent = function() {
		var tmpDate = new Date(this.year, 11, 25, 12, 0, 0);
		tmpDate.setTime(tmpDate.getTime() - ((tmpDate.getDay() == 0) ? 7 : tmpDate.getDay()) * this.getOneDay('milliseconds'));
		return tmpDate;
	}
	
	this.calcDate = function(day, year) {
		if (this.year != Number(year)) {
			this.calcEasterSunday(year);
		}
		
		switch(day.toLowerCase()) {
			case 'rosemonday':
				return this.getNextDate(this.roseMondayDiffDays, this.easterSunday);
				break;
			case 'fastnacht':
				return this.getNextDate(this.fastnachtDiffDays, this.easterSunday);
				break;
			case 'aschermittwoch':
				return this.getNextDate(this.aschermittwochDiffDays, this.easterSunday);
				break;
			case 'greenthursday':
				return this.getNextDate(this.greenThursdayDiffDays, this.easterSunday);
				break;
			case 'karfreyday':
				return this.getNextDate(this.karfreitagDiffDays, this.easterSunday);
				break;
			case 'karsaturday':
			case 'eastersaturday':
				return this.getNextDate(this.easterSaturdayDiffDays, this.easterSunday);
				break;
			case 'eastersunday':
				return this.easterSunday;
				break;
			case 'eastermonday':
				return this.getNextDate(this.easterMondayDiffDays, this.easterSunday);
				break;
			case 'motherday':
				return this.getNextDate(this.motherdayDiffDays, this.easterSunday);
				break;
			case 'christihimmelfahrt':
				return this.getNextDate(this.christiHimmelfahrtDiffDays, this.easterSunday);
				break;
			case 'pfingstsunday':
				return this.getNextDate(this.pfingstSundayDiffDays, this.easterSunday);
				break;
			case 'pfingstmonday':
				return this.getNextDate(this.pfingstMondayDiffDays, this.easterSunday);
				break;
			case 'fronleichnahm':
				return this.getNextDate(this.fronleichnahmDiffDays, this.easterSunday);
				break;
			case 'sevensleeper':
				return this.getNextDate(this.sevenSleeperDiffDays, this.easterSunday);
				break;
			case 'dayofgermanunit':
				return new Date(this.year, 9, 3);
				break;
			case 'holynight':
				return new Date(this.year, 11, 24);
				break;
			case 'xmas_1_day':
				return new Date(this.year, 11, 25);
				break;
			case 'xmas_2_day':
				return new Date(this.year, 11, 26);
				break;
			case 'silvester':
				return new Date(this.year, 11, 31);
				break;
			case 'newyear':
				return new Date(this.year, 0, 1);
				break;
			case 'holy3kings':
				return new Date(this.year, 0, 6);
				break;
			case 'walpurgis':
				return new Date(this.year, 3, 30);
				break;
			case 'firstmay':
				return new Date(this.year, 4, 1);
				break;
			case 'childrensday':
				return new Date(this.year, 5, 1);
				break;
			case 'thanksgiving':
				return this.calcThanksgiving();
				break;
			case 'reformationday':
				return new Date(this.year, 9, 31);
				break;
			case 'allerheiligen':
				return new Date(this.year, 10, 1);
				break;
			case 'startsummertime':
				return this.calcMETSummertime('start');
				break;
			case 'endsummertime':
				return this.calcMETSummertime('end');
				break;
			case 'busbetday':
				return this.getNextDate(-((7*4)+4), this.calcFourthAdvent());
				break;
			case 'deathsunday':
				return this.getNextDate(-(7*4), this.calcFourthAdvent());
				break;
			case 'nikolaus':
				return new Date(this.year, 11, 6);
				break;
			case 'firstadvent':
				return this.getNextDate(-(7*3), this.calcFourthAdvent());
				break;
			case 'secondadvent':
				return this.getNextDate(-(7*2), this.calcFourthAdvent());
				break;
			case 'thirdadvent':
				return this.getNextDate(-(7), this.calcFourthAdvent());
				break;
			case 'fourthadvent':
				return this.calcFourthAdvent();
				break;
			
			default: 
				return ('not supported date');
		}
	}
	
	this.getNextDate = function (dayDiff, source_date) {
		return (new Date(source_date.getTime() + (dayDiff * this.getOneDay('milliseconds'))));
	}
	
	this.getOneDay = function (format) {
		switch (format.toLowerCase()) {
			case 'minutes':
				return 60 * 24; /* like 60 minutes * 24 hours; */
				break;
			case 'seconds':
				return 60 * 60 * 24; /* like 60 seconds * 60 minutes * 24 hours; */
				break;
			case 'milli':
			case 'milliseconds':
				return 1000 * 60 * 60 * 24; /* like 1000 Milliseconds * 60 seconds * 60 minutes * 24 hours; */
				break;
		}
		return 
	}
	
	this.checkDate = function(checkDateValue) {
		if (checkDateValue) {
			var today = new Date(checkDateValue);
		} else {
			var today = new Date();
		}
		
		
		for (ct = 0; ct < this.importantDates.length; ct++) {
			checkDate = this.calcDate(this.importantDates[ct], 2011);
			if (today.getMonth()+''+today.getDate() == checkDate.getMonth()+''+checkDate.getDate()) {
				return this.importantDates[ct];
				break;
			}
		}
	}
};



function appendStylesheet(href) {
	if (!document.getElementById('holidaytheme')) {
		var stylesheed = document.createElement('link');
		stylesheed.href = href;
		stylesheed.rel = 'stylesheet';
		stylesheed.type = 'text/css';
		stylesheed.id = 'holidaytheme';
		document.getElementsByTagName('head')[0].appendChild(stylesheed);
	} else {
		document.getElementById('holidaytheme').disabled = false;
	}
}



function checkForPublicHolidays() {
	var check = null;
	var days_left = 9999;
	
	for (var cd = 0; cd < 21; cd++) {
		if (check = importantDate.checkDate(mpdlDate.getNextDate(cd, new Date()))) {
			days_left = cd;
			break;
		};
	}
	
	switch (check) {
		case 'rosemonday':
		case 'fastnacht':
		case 'aschermittwoch':
			if (days_left == 0) {
				
			}
			break;
		case 'greenthursday':
			if (days_left < 3) {
//				appendStylesheet('http://localhost/common/resources/cssFramework/themes/skin_darkGrey/styles/easter.css');
			}
			break;
		case 'karfreyday':
			if (days_left < 4) {
//				appendStylesheet('http://localhost/common/resources/cssFramework/themes/skin_darkGrey/styles/easter.css');
			}
			break;
		case 'karsaturday':
			if (days_left < 5) {
//				appendStylesheet('http://localhost/common/resources/cssFramework/themes/skin_darkGrey/styles/easter.css');
			}
			break;
		case 'eastersaturday':
			if (days_left < 6) {
//				appendStylesheet('http://localhost/common/resources/cssFramework/themes/skin_darkGrey/styles/easter.css');
			}
			break;
		case 'eastersunday':
			if (days_left < 7) {
//				appendStylesheet('http://localhost/common/resources/cssFramework/themes/skin_darkGrey/styles/easter.css');
			}
			break;
		case 'eastermonday':
			if (days_left < 8) {
//				appendStylesheet('http://localhost/common/resources/cssFramework/themes/skin_darkGrey/styles/easter.css');
			}
			break;
		case 'motherday':
			if (days_left == 0) {
				
			}
			break;
		case 'christihimmelfahrt':
			if (days_left == 0) {
				
			}
			break;
		case 'pfingstsunday':
			if (days_left < 7) {
				
			}
			break;
		case 'pfingstmonday':
			if (days_left < 8) {
				
			}
			break;
		case 'fronleichnahm':
			
			break;
		case 'sevensleeper':
			break;
		case 'dayofgermanunit':
			break;
		case 'holy3kings':
			break;
		case 'walpurgis':
			break;
		case 'firstmay':
			break;
		case 'childrensday':
			break;
		case 'thanksgiving':
			break;
		case 'reformationday':
			break;
		case 'allerheiligen':
			break;
		case 'startsummertime':
			break;
		case 'endsummertime':
			break;
		case 'busbetday':
			break;
		case 'deathsunday':
			break;
		case 'nikolaus':
		case 'firstadvent':
		case 'secondadvent':
		case 'thirdadvent':
		case 'fourthadvent':
		case 'holynight':
		case 'xmas_1_day':
		case 'xmas_2_day':
			break;
		case 'silvester':
			break;
		case 'newyear':
			break;
		default: 
			return ('not supported date');
	}
}

function checkLayout(){
	
	if (!mpdlCookie.getCookie('layoutUpdateDate')) {
		mpdlCookie.eraseCookie("layout");
		var timeOutDate = mpdlDate.getNextDate(mpdlCookie.cookieTimeout, mpdlDate.now('date'));
		mpdlCookie.setCookie("layoutUpdateDate", mpdlDate.now('today_date'), null, timeOutDate.toGMTString());
		mpdlCookie.setCookie("layout", mpdlCSS.getDefaultStylesheet(), null, timeOutDate.toGMTString());
	} else {
		mpdlCSS.enablePageStyle(mpdlCookie.getCookie('layout'))
	}
	
	checkForPublicHolidays();
}

function returnValuesFromDOM(dom_element) {
	for (info in dom_element) {
		alert(info +': '+dom_element[info]);
	}
}


jQuery(document).ready(function() {
	checkLayout();
	mpdlCSS.createThemebox('#themeSelector');
});






