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
* Copyright 2006-2010 Fachinformationszentrum Karlsruhe Gesellschaft
* f�r wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur F�rderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

function addFullItemFunctions() {
	$('.itemBlock').each( function(i,ele){$(ele).find('.collapse').each(function(j,elem){$(elem).show();}); $(ele).find('.expand').each(function(j,elem){$(elem).hide();});  $(ele).not('.visibility').find('.blockHeader').each(function(j,elem){if($(elem).siblings('.itemBlockContent').length==0)$(elem).addClass('voidBlock');});})
	$('.fullItem').find('.visibility').find('.collapse').click(function(){$(this).hide(); $(this).parents('.itemBlock').find('.expand').show(); $(this).parents('.fullItem').find('.itemBlock:not(.visibility)').find('.collapse:visible').trigger('click');});
	$('.fullItem').find('.visibility').find('.expand').click(function(){$(this).hide(); $(this).parents('.itemBlock').find('.collapse').show(); $(this).parents('.fullItem').find('.itemBlock:not(.visibility)').find('.expand:visible').trigger('click');}); 
	$('.itemBlock:not(.visibility)').find('.expand').each(function(i,ele){$(ele).click(function(){$(this).hide(); $(this).parents('.itemBlock').children('.itemBlockContent').children('.lineToolSection').hide(); $(this).parents('.itemBlock').children('.itemBlockContent').slideToggle('normal', function(){$(this).parents('.itemBlock').find('.collapse').show();  $(this).parents('.itemBlock').children('.itemBlockContent').children('.lineToolSection').show(); if(($(this).parents('.fullItem').find('.itemBlock:not(.visibility)').find('.expand:visible').length)==0) { $(this).parents('.fullItem').find('.visibility').find('.collapse').show(); $(this).parents('.fullItem').find('.visibility').find('.expand').hide();} });})});
	$('.itemBlock:not(.visibility)').find('.collapse').each(function(i,ele){$(ele).click(function(){$(this).hide(); $(this).parents('.itemBlock').children('.itemBlockContent').children('.lineToolSection').hide(); $(this).parents('.itemBlock').children('.itemBlockContent').slideToggle('normal', function(){$(this).parents('.itemBlock').find('.expand').show(); if(($(this).parents('.fullItem').find('.itemBlock:not(.visibility)').find('.collapse:visible').length)==0) { $(this).parents('.fullItem').find('.visibility').find('.collapse').hide(); $(this).parents('.fullItem').find('.visibility').find('.expand').show();} });})});
	$('.hideBlockIfVoid').each(function(i,elem){ if( allInputsBelowVoid(elem) && ($(elem).find('.itemLine').length < 3 ) ) { $(elem).siblings('.expand').show(); $(elem).find('.collapse').hide(); $(elem).hide();  };   });
	$('.hideAdvSearchGenreBlockIfVoid').each(function(i,elem){ if( allInputsBelowVoid(elem) && ($(elem).find('.itemLine').length < 5 ) ) { $(elem).siblings('.expand').show(); $(elem).find('.collapse').hide(); $(elem).hide();  };   });
	$('.hideAdvSearchComplexBlockIfVoid').each(function(i,elem){ if( allInputsBelowVoid(elem) && ($(elem).find('.itemLine').length < 11 ) ) { $(elem).siblings('.expand').show(); $(elem).find('.collapse').hide(); $(elem).hide();  };   });
	
	$('.creator').each(function(i,ele){$(ele).hover(function(){
				$(this).addClass('affHover');
				var numbers = $(this).children('sup').text().split(',');
				for(var z=0; z<numbers.length; z++) {
					$(this).parents('.itemBlockContent').find('.affiliation').each(function(j, elem){if(jQuery.trim($(elem).prev().text())==jQuery.trim(numbers[z])){$(elem).addClass('affHover');}});
				}
			}, function(){
				$(this).removeClass('affHover');
				var numbers = $(this).children('sup').text().split(',');
				for(var z=0; z<numbers.length; z++) {
					$(this).parents('.itemBlockContent').find('.affiliation').each(function(j, elem){if(jQuery.trim($(elem).prev().text())==jQuery.trim(numbers[z])){$(elem).removeClass('affHover');}});
				}
			} )});
	$('.affiliation').each(function(i,ele){$(ele).hover(function(){
		$(this).addClass('affHover');
		var number = $(this).prev().text();
		$(this).parents('.itemBlockContent').find('.creator').each(function(j, elem){
				var numbers = $(elem).children('sup').text().split(',');
				for(var z=0; z<numbers.length; z++) {
					if(jQuery.trim(number)==jQuery.trim(numbers[z])){
						$(elem).addClass('affHover');
					}
				}
			});
	}, function(){
		$(this).removeClass('affHover');
		var number = $(this).prev().text();
		$(this).parents('.itemBlockContent').find('.creator').each(function(j, elem){
				var numbers = $(elem).children('sup').text().split(',');
				for(var z=0; z<numbers.length; z++) {
					if(jQuery.trim(number)==jQuery.trim(numbers[z])){
						$(elem).removeClass('affHover');
					}
				}
			});
	} )});
	
	$('.fullItem').find('.shortView').each(function(i,ele){$(ele).hide();});
	$('.fullItem').find('.itemInfoSwitch').each(function(i,ele){$(ele).click(function(){$(this).parents('.listItem').find('.shortView').slideToggle('normal'); });});

//	$('.fileUploadBtn').each(function(i, elem){ if($(elem).parents('.fileSection').find('.fileInput').val() == ''){ $(elem).parents('.fileSection').find('.fileUploadBtn').attr('disabled','disabled');}; });

	$('.showMultipleAuthors').click(function(){
		$(this).parents('.itemBlock').find('.multipleAuthors').slideDown('normal');
		$(this).parents('.itemBlock').find('.firstCreator').removeClass('noTopBorder');
		$(this).parents('.itemBlock').find('.multipleAuthors').find(':hidden').val('showPermanent');
		$(this).hide();
	});
	$('.multipleAuthors').hide();
	$('.showMultipleAuthors').each(function(i,elem){ if($(elem).parents('.itemBlock').find("input[type='hidden'][value='showPermanent']").length > 0) {$(elem).hide(); $(elem).parents('.itemBlock').find('.multipleAuthors').show(); $(elem).parents('.itemBlock').find('.firstCreator').removeClass('noTopBorder'); }; });
	
	$('.checkAll').click(function(){ $(this).parents('.itemLine').find('.checkboxDoubleGroup').find(':checkbox').attr('checked','true'); $(this).parents('.itemLine').find('.checkboxDoubleGroup').find('span:hidden').show(); $(this).parents('.itemLine').find('.checkboxDoubleGroup').find('.showMoreCheckboxes').hide(); });
	$('.showMoreCheckboxes').click(function(){ $(this).hide(); $(this).siblings().show(); });
	$('.checkboxDoubleGroup').each(function(i,elem){if($(elem).find('.large_checkbox:gt(0)').find(':checked').length == 0) {$(elem).find('.large_checkbox:gt(0)').hide();} else {$(elem).find('.showMoreCheckboxes').hide();};});
	
	$('.showMoreDates').click(function(){ $(this).hide(); $(this).siblings().show(); });
	$('.datesGroup').each(function(i,elem){if($(elem).find('span.large_area0:gt(0)').find(":text[value!='']").length == 0) {$(elem).find('span.large_area0:gt(0)').hide();} else {$(elem).find('.showMoreDates').hide();};});
}

function allInputsBelowVoid(topLevelElement) {
	return ( ($(topLevelElement).find(':checkbox:checked').length == 0) && 
			 ($(topLevelElement).find("textarea[value!=''], :text[value!='']").length == 0) && 
			 ($(topLevelElement).find('.languageSuggest').siblings("select[value!='']").length == 0) &&
			 ($(topLevelElement).find('.languageSuggest').siblings('span.replace').find("input:hidden[value!='']").length == 0)
			)
}

function installFullItem() {
	/*ADD LISTENERS TO CHANGED DOM*/
	addFullItemFunctions();
}