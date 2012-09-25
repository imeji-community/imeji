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

function addItemListFunctions() {
	/*
	 * for default:
	 * hide all elements that shows details
	 */
	jQuery('.itemList').each(
		function(i,ele){
			var list = jQuery(ele);
			list.find(".mediumView, .collapse").hide();
			list.find(".expand").show();
			
			var listItems = list.find(".listItem");
			listItems.mouseover(
						function() { jQuery(this).addClass('listBackground');}
					).mouseout(
						function() { jQuery(this).removeClass('listBackground'); }
					);
		}
	);
	
	/*jQuery('.checkboxSelectButton').click(function(){jQuery(this).siblings('.selectMenu').show();});
	jQuery('.checkBoxCloseSelectButton').click(function(){jQuery(this).parent().hide();});
		jQuery('.selectMenu').find('.selectTotal').click(function(){jQuery(this).parents('.itemList').find('.itemCheckBox').each(function(i, elem){elem.checked=true;}); jQuery(this).parents('.selectMenu').hide();});
		jQuery('.selectMenu').find('.selectAll').click(function(){jQuery(this).parents('.itemList').find('.itemCheckBox').each(function(i, elem){elem.checked=true;}); jQuery(this).parents('.selectMenu').hide();});
		jQuery('.selectMenu').find('.selectNone').click(function(){jQuery(this).parents('.itemList').find('.itemCheckBox').each(function(i, elem){elem.checked=false;});});
		jQuery('.selectMenu').find('a').each(function(i,elem){jQuery(elem).click(function(){jQuery(this).parents('.selectMenu').hide()});});
	*/
	
	// Openration of the select menu for checkboxes
	// Start with event on document to close the select menu on click elswhere
	jQuery('body').ready(function(e){
		
		jQuery('html').click(function(){
			// $('.selectMenu').hide();
		});
		
		jQuery('.selectMenu').click(function(evt){
			//evt.preventDefault();
			evt.stopPropagation();
			evt.stopImmediatePropagation();
		});
		
		function hideElement(element) {
			element.hide(30);
		} 
		
		function check4OpenSelectMenues() {
			jQuery('.selectMenu').each(function(e){
				if (jQuery(this).is(':visible')) {
					hideElement(jQuery(this));
				}
			});
		}
		
		jQuery('.checkboxSelectButton').click(function(evt){
			evt.preventDefault();
			evt.stopPropagation();
			evt.stopImmediatePropagation();
			
			check4OpenSelectMenues();
			
			jQuery('body').unbind("click");
			jQuery('body').unbind("keydown");
			var cbsButton = jQuery(this);
			var cbsButtonPosition = jQuery(this).position();
			var slctMenu = jQuery(this).siblings('.selectMenu');
			slctMenu.css("top",  cbsButtonPosition.top + 20);
			slctMenu.css("left",  cbsButtonPosition.left + 15);
			jQuery('body').one("click", function(evt) {
				hideElement(slctMenu);
			});
			jQuery('body').one('keydown', function(evt){
				if (Number(evt.which) === 27) { //check the key-number for number of escape
					hideElement(slctMenu);
				}
			});
			slctMenu.toggle(30, function(){
				if (cbsButtonPosition.left > (jQuery(document).width() / 2)) {
					var tmpPos = cbsButtonPosition.left + cbsButton.width() / 2 - slctMenu.width();
					slctMenu.css("left", tmpPos);
				}
			});
			
		}); 
	});
	

	/* itemList handling for more/less view */
	jQuery('.headerSwitchView').find('.expand').click(
			function(){
				var curEl = jQuery(this);
				var itemList = curEl.parents(".itemList");
				itemList.find(".listItem .expand:visible").trigger("click");
				curEl.hide();
				curEl.siblings('.collapse').show();
			});
	jQuery('.headerSwitchView').find('.collapse').click(
			function(){
				var curEl = jQuery(this);
				var itemList = curEl.parents(".itemList");
				itemList.find(".listItem .collapse:visible").trigger("click");
				curEl.hide();
				curEl.siblings('.expand').show();
			});
	
	jQuery('.listItem .shortView .expand').each(
			function(i,ele){
				jQuery(ele).click(
						function(){
							var curEl = jQuery(this);
							
							var listItem = jQuery(this).parents('.listItem');
							listItem.find(".mediumView").show(350, function() {
								curEl.hide();
								curEl.siblings('.collapse').show();
							});/*
							listItem.find('.mediumView').slideToggle(400, function() {
								curEl.hide();
								curEl.siblings('.collapse').show();
							});
							*/
						}
				)
			});
	jQuery('.listItem .shortView .collapse').each(
			function(i,ele){
				jQuery(ele).click(
						function(){
							var curEl = jQuery(this);
							
							var listItem = jQuery(this).parents('.listItem');
							listItem.find(".mediumView").hide(350, function() {
								curEl.hide();
								curEl.siblings('.expand').show();
							});/*
							listItem.find('.mediumView').slideToggle(400, function() {
								curEl.hide();
								curEl.siblings('.collapse').show();
							});
							*/
						}
				)
			});
}

function installItemList() {
	/*ADD LISTENERS TO CHANGED DOM*/
	addItemListFunctions();
}