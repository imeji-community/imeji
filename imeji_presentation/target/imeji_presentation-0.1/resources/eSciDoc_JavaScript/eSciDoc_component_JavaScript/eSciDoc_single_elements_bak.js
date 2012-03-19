var BrowserDetect = {
	init: function () {
		this.browser = this.searchString(this.dataBrowser) || "An unknown browser";
		this.version = this.searchVersion(navigator.userAgent)
			|| this.searchVersion(navigator.appVersion)
			|| "an unknown version";
		this.OS = this.searchString(this.dataOS) || "an unknown OS";
	},
	searchString: function (data) {
		for (var i=0;i<data.length;i++)	{
			var dataString = data[i].string;
			var dataProp = data[i].prop;
			this.versionSearchString = data[i].versionSearch || data[i].identity;
			if (dataString) {
				if (dataString.indexOf(data[i].subString) != -1)
					return data[i].identity;
			}
			else if (dataProp)
				return data[i].identity;
		}
	},
	searchVersion: function (dataString) {
		var index = dataString.indexOf(this.versionSearchString);
		if (index == -1) return;
		return parseFloat(dataString.substring(index+this.versionSearchString.length+1));
	},
	dataBrowser: [
		{
			string: navigator.userAgent,
			subString: "Chrome",
			identity: "Chrome"
		},
		{ 	string: navigator.userAgent,
			subString: "OmniWeb",
			versionSearch: "OmniWeb/",
			identity: "OmniWeb"
		},
		{
			string: navigator.vendor,
			subString: "Apple",
			identity: "Safari",
			versionSearch: "Version"
		},
		{
			prop: window.opera,
			identity: "Opera"
		},
		{
			string: navigator.vendor,
			subString: "iCab",
			identity: "iCab"
		},
		{
			string: navigator.vendor,
			subString: "KDE",
			identity: "Konqueror"
		},
		{
			string: navigator.userAgent,
			subString: "Firefox",
			identity: "Firefox"
		},
		{
			string: navigator.vendor,
			subString: "Camino",
			identity: "Camino"
		},
		{		// for newer Netscapes (6+)
			string: navigator.userAgent,
			subString: "Netscape",
			identity: "Netscape"
		},
		{
			string: navigator.userAgent,
			subString: "MSIE",
			identity: "Explorer",
			versionSearch: "MSIE"
		},
		{
			string: navigator.userAgent,
			subString: "Gecko",
			identity: "Mozilla",
			versionSearch: "rv"
		},
		{ 		// for older Netscapes (4-)
			string: navigator.userAgent,
			subString: "Mozilla",
			identity: "Netscape",
			versionSearch: "Mozilla"
		}
	],
	dataOS : [
		{
			string: navigator.platform,
			subString: "Win",
			identity: "Windows"
		},
		{
			string: navigator.platform,
			subString: "Mac",
			identity: "Mac"
		},
		{
			string: navigator.platform,
			subString: "Linux",
			identity: "Linux"
		}
	]

};

BrowserDetect.init();

/*EASTER EGG*/
function bunny() {
	if(BrowserDetect.browser == 'Firefox') {
		$('.quickSearchTextInput').dblclick(function(){
			if($(this).val() == 'PubWoman') {
				showPubWomanStyle();
			};
		});
	};
}

function raiseBunny() {
	if(BrowserDetect.browser == 'Firefox') {
		$($('link[id]')[0]).before('<link href="' + jsURL + 'eSciDoc_component_JavaScript/DateJS/easterEggs/skin_PubWoman/styles/theme.css" id="PubWomanTheme" type="text/css" rel="alternate stylesheet"/>');
		applyCookieStyle();
	};
}

function enableHiddenStyle(){
	var now = new Date();
	var exp = new Date(now.getTime() + (1000*60*60*24*30));
	document.cookie = "enableHiddenSchemes=true;" +
						"expires=" + exp.toGMTString() + ";" +
						"path=/";
}

function setStyle(styleValue) {
	if(document.getElementsByTagName) {
		var el = document.getElementsByTagName("link");
		for (var i = 0; i < el.length; i++ ) {
			if (el[i].getAttribute("rel").indexOf("style") != -1 && el[i].getAttribute("id")) {
				if(styleValue == el[i].getAttribute("id")){
					el[i].disabled = false;
				} else {
					el[i].disabled = true;
				}
			}
		}
	}
}

function showPubWomanStyle() {
	$('#PubWomanTheme').attr('title','PubWoman');
	enableHiddenStyle();
	setStyle('PubWomanTheme');
	alert('PubWoman style activated! Happy easter!');
}

raiseBunny();

/*QUICK SEARCH INITIALISATION*/

function addQuickSearchFunction(){
	bunny();
	$('.quickSearchTextInput').keyup(function(keyEvent){
		if(keyEvent.keyCode == '13'){
			$(this).parents('.searchMenu').find('.quickSearchBtn').click();
		};
	});
};


/*DATE INPUT FIELD*/

function validateDate(inputField) {
	/*DATE VALIDATION ACCORDING TO THE GREGORIAN CALENDAR*/
	var input_empty = "";
	var isValidDate = true;
	var isBC = false;
	var validChars = "0123456789-";
	var validNumbers = "0123456789";
	var bcString = "BC";
	var possibleDate = inputField.value;
	if((inputField.value=="")||(inputField.value==input_empty)) {
		$(inputField).val(input_empty).addClass("blankInput");
	}
	if(!(inputField.value == input_empty)) {
		/*REMOVE SPACES*/
		while(inputField.value.match(' -')) inputField.value = inputField.value.replace(/ -/, '-');
		while(inputField.value.match('- ')) inputField.value = inputField.value.replace(/- /, '-');
		/*REMOVE LEADING SPACES*/
		while(inputField.value.indexOf(' ')==0) inputField.value = inputField.value.substring(1,inputField.value.length);
		/*REMOVE SPACES AT THE END*/
		while(inputField.value.lastIndexOf(' ')==inputField.value.length-1) inputField.value = inputField.value.substring(0,inputField.value.length-1);
		while(inputField.value.match(' '+bcString)) inputField.value = inputField.value.replace(/ BC/, bcString);
		inputField.value = inputField.value.replace(bcString, ' '+bcString);
		possibleDate = inputField.value;
		/*CHECK FOR BC*/
		if( (possibleDate.indexOf(bcString))== (possibleDate.length-2)  ) {
			possibleDate = possibleDate.replace(' '+bcString, '');
			isBC= true;
		}
		/*VALIDATE DATE*/
		for (j = 0; j < possibleDate.length && isValidDate == true; j++) {
			Char = possibleDate.charAt(j); 
    		if (!(validChars.indexOf(Char) == -1)) 
    	     {
    	     	var subType = possibleDate.split(/-/);
    	     	if((subType.length < 4) && (subType.length > 0) && (possibleDate.lastIndexOf('-')<possibleDate.length-1) && (possibleDate.indexOf('-')!=0) ) {
    	     		for(var k=0; k < subType.length; k++) {
    	     			switch(k) {
    	     				/*FIRST NUMBER HAS NOT FOUR DIGITS*/
    	     				case 0:	if(subType[k].length!=4) isValidDate = false;
    	     						break;
    	     				/*SECOND NUMBER HAS NOT TWO DIGITS AND/OR IS LESS THAN 1 OR BIGGER THAN 12*/
    	     				case 1: if((subType[k].length!=2) || (subType[k]>12) || (subType[k]<1)) isValidDate = false;
        	 						break;
        	 				/*THIRD NUMBER HAS NOT TWO DIGITS AND/OR IS LESS THAN 1 OR BIGGER THAN 31*/
        	 				case 2: if((subType[k].length!=2) || (subType[k]>31) || (subType[k]<1)) isValidDate = false;
        	 						else {
        	 								/*APRIL, JUNE, SEPTEMBER AND NOVEMBER HAVE MORE THAN 30 DAYS*/
        	 								if(((subType[k-1]=='04') || (subType[k-1]=='06') || (subType[k-1]=='09') || (subType[k-1]=='11')) && (subType[k]>30)) isValidDate = false;
        	 								/*
        	 								*FEBRUARY HAS MORE THAN 28 DAYS IN REGULAR YEARS (YEAR mod 4 is bigger than 0 OR year mod 100=0 AND year mod 400 is bigger than 0)
        	 								*FEBRUARY HAS MORE THAN 29 DAYS IN LEAP YEARS (all others)
        	 								*/
        	 								if((subType[k-1]=='02') && ( ((subType[k]>29) && ((subType[k-2]%4) == 0)) || (   ((subType[k]>28) && ((subType[k-2]%4)>0)) || (((subType[k-2]%100)==0) && ((subType[k-2]%400)>0) && (subType[k]>28)  )   )  ) ) isValidDate = false;
        	  							}
        	 						break;
        	 			}
        	 		}
        	 	}
        	 	else isValidDate = false;
       		}
       		else isValidDate = false;
		}

		if(!(isValidDate)) {
			$(inputField).addClass("falseValue");
		} else $(inputField).removeClass("falseValue");
	}
}

function addDateJSLabels() {
	/*
	*This function adds the following HTML code
	*
	*<div class="dateJSBox *LENGTH VALUE HERE*_area0">
	*	GIVEN INPUT FIELD HERE
	*	<label class="dateJSLabel *LENGTH VALUE HERE*_negMarginLIncl noDisplay" for="*INPUT FIELD ID*"></label>
	*</div>
	*
	*/
	$(".dateJSInput").each(function(){
		var classNameString = $(this).attr("class");
		var lengthValue;
		var possibleLengthValues = classNameString.split(' ');
		for(var i=0; i<possibleLengthValues.length; i++) {
			if(possibleLengthValues[i].match('_txtInput')) {
				var wholeLengthValue = possibleLengthValues[i].split('_');
				lengthValue = wholeLengthValue[0];
			}
		}
		$(this).wrap('<div class="dateJSBox '+lengthValue+'_area0"></div>');
		$(this).after('<label class="dateJSLabel '+lengthValue+'_label '+lengthValue+'_negMarginLIncl noDisplay" for="'+$(this).attr("id")+'"></label>');
	});
}

function addDateJSFunctions() {
	$(".dateJSInput").each(function(){
		$(this).focus(function() {
			var input_empty = "", empty_string = "";
			
			$(this).removeClass("falseValue");
			
			if($(this).val() === input_empty)
			{
				$(this).val(empty_string);
				$(this).removeClass("blankInput");
			}
	
			if($(this).val() != "")
			{
				var date = null;
				date = Date.parse($(this).val());
				if(date!=null)
				{
					$(".dateJSLabel[for='"+$(this).attr("id")+"']").removeClass("noDisplay").text(date.toString("yyyy-MM-dd"));
				}
			}
	        return false;    
		});
		$(this).blur(function(){
			var input_empty = "", empty_string = "";
		
			$(".dateJSLabel[for='"+$(this).attr("id")+"']").addClass("noDisplay").text("");
			
			if($(this).val() === empty_string)
			{
				$(this).val(input_empty).addClass("blankInput");
			}
			validateDate(this);
		});
		$(this).keyup(function(event){
			var message = "";
			var input_empty = "", empty_string = "";
			var date = null;

			$(".dateJSLabel[for='"+$(this).attr("id")+"']").text("");
			if($(this).val() != "")
			{
				date = Date.parse($(this).val());
				
				if(date != null)
				{
					$(".dateJSLabel[for='"+$(this).attr("id")+"']").removeClass("noDisplay").text(date.toString("yyyy-MM-dd"));
					var oEvent = event || window.event;
					if(oEvent.keyCode == 13)
					{
						$(this).val(date.toString("yyyy-MM-dd"));
						$(".dateJSLabel[for='"+$(this).attr("id")+"']").addClass("noDisplay").text("");
					};
				} else
					{
						$(".dateJSLabel[for='"+$(this).attr("id")+"']").addClass("noDisplay").text(message);
					}
			}
			else
				{
					$(".dateJSLabel[for='"+$(this).attr("id")+"']").addClass("noDisplay").text("");
				}			
	      	var evt = event || window.event;
	      	if(evt.stopPropagation) evt.stopPropagation();
		 	evt.cancelBubble = true;
		});
		validateDate(this);
	});
}

function installQuickSearchShortcut() {
	addQuickSearchFunction();
}

function installDateTextbox() {
	/*GET LANGUAGE*/
	var language = '';
	language = document.body.lang;
	if(language != '') language = '-'+language;
	/*INCLUDE RIGHT LANGUAGE HERE*/
	include_dom(jsURL + 'eSciDoc_component_JavaScript/DateJS/date'+language+'.js');
	addDateJSLabels();
	addDateJSFunctions();
}

function installSameHeight() {
	$('.sameHeightSlave').each(function(i,elem){$(elem).height($('.sameHeightMaster').height());});
}