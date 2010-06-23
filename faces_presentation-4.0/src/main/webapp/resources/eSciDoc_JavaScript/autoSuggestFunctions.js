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
* Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
* f�r wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur F�rderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

/*
* JavaScript functions for pubman_presentation
*/

	var languageSuggestURL = '';
	var journalSuggestURL = '';
	var subjectSuggestURL = '';
	var personSuggestURL = '';
	var journalDetailsBaseURL = '';
	var autopasteDelimiter = ' ||##|| ';
	var journalSuggestCommonParentClass = 'sourceArea';
	var journalSuggestTrigger = 'JOURNAL';

	function getJournalDetails(details)
	{
		var parent = $input.parents('.'+journalSuggestCommonParentClass);
		var title = (typeof details.http_purl_org_dc_elements_1_1_title != 'undefined' ? details.http_purl_org_dc_elements_1_1_title : null);
		var altTitle = (typeof details.http_purl_org_dc_terms_alternative != 'undefined' ?
				(typeof details.http_purl_org_dc_terms_alternative == 'object' ?
						details.http_purl_org_dc_terms_alternative[0] : details.http_purl_org_dc_terms_alternative) : null);
		var publisher = (typeof details.http_purl_org_dc_elements_1_1_publisher != 'undefined' ? details.http_purl_org_dc_elements_1_1_publisher : null);
		var place = (typeof details.http_purl_org_dc_terms_publisher != 'undefined' ? details.http_purl_org_dc_terms_publisher : null);
		
		var identifier = (typeof details.http_purl_org_dc_elements_1_1_identifier != 'undefined' ?
				details.http_purl_org_dc_elements_1_1_identifier : null);

		var allAltTitles = '';
		if((typeof(altTitle)=='object') && (altTitle != null) ){
			allAltTitles = altTitle[0];
			for(var i=1; i<altTitle.length; i++) {
					allAltTitles = allAltTitles + autopasteDelimiter + altTitle[i];
			}
		} else {
			allAltTitles = altTitle;
		}
		
		var allIDs = '';
		
		
		
		if((typeof(identifier)=='object') && (identifier != null)){
			for(var i = 0; i<identifier.length; i++) {
				
				var identifierType = identifier[i]['http_escidoc_mpg_de_idtype'];
				var identifierValue = identifier[i]['http_www_w3_org_1999_02_22_rdf_syntax_ns#_value'];

				if (i > 0)
				{
					allIDs += autopasteDelimiter;
				}
				if (typeof identifierType != 'undefined' && identifierType != null)
				{
					allIDs += identifierType + '|';
				}
				allIDs += identifierValue;
			}
		} else {
			var identifierType = identifier['http_escidoc_mpg_de_idtype'];
			var identifierValue = identifier['http_www_w3_org_1999_02_22_rdf_syntax_ns#_value'];
			if (typeof identifierType != 'undefined' && identifierType != null)
			{
				allIDs += identifierType + '|';
			}
			allIDs += identifierValue;
		}
		
		
		
		fillField('journalSuggest', title, parent);
		fillField('sourceAltTitlePasteField', allAltTitles, parent);
		fillField('publisher', publisher, parent);
		fillField('place', place, parent);
		fillField('sourceIdentifierPasteField', allIDs, parent);
		$(parent).find('.hiddenAutosuggestUploadBtn').click();
	}

	function getPersonDetails(details)
	{
		var parent = $input.parents('.' + personSuggestCommonParentClass);
		
		var completeName = (typeof details.http_purl_org_dc_elements_1_1_title != 'undefined' ? details.http_purl_org_dc_elements_1_1_title : null);
		
		var chosenName = $input.resultValue;
		var orgName = null;
		var orgId = null;
		
		if (chosenName.indexOf('(') >= 0)
		{
			orgName = chosenName.substring(chosenName.indexOf('(') + 1, chosenName.indexOf(')')).replace(/^\s*(.*\S)\s*$/, '$1');
			chosenName = chosenName.substring(0, chosenName.indexOf('(')).replace(/^\s*(.*\S)\s*$/, '$1');
		}
		var familyName = '';
		var givenName = '';
		if (chosenName.indexOf(',') >= 0)
		{
			familyName = chosenName.split(',')[0].replace(/^\s*(.*\S)\s*$/, '$1');
			givenName = chosenName.split(',')[1].replace(/^\s*(.*\S)\s*$/, '$1');
		}
		else
		{
			familyName = chosenName;
		}
		if (orgName != null)
		{
			if (typeof details.http_escidoc_mpg_de_position != 'undefined'
				&& typeof details.http_escidoc_mpg_de_position.length != 'undefined')
			{
				for (var i = 0; i < details.http_escidoc_mpg_de_position.length; i++)
				{
					if (details.http_escidoc_mpg_de_position[i].http_escidoc_mpg_de_organization == orgName
						&& typeof details.http_escidoc_mpg_de_position[i].http_purl_org_dc_elements_1_1_identifier != 'undefined')
					{
						orgId = details.http_escidoc_mpg_de_position[i].http_purl_org_dc_elements_1_1_identifier;
						break;
					}
				}
			}
			else if (typeof details.http_escidoc_mpg_de_position != 'undefined'
				&& typeof details.http_escidoc_mpg_de_position.http_escidoc_mpg_de_organization != 'undefined'
				&& typeof details.http_escidoc_mpg_de_position.http_purl_org_dc_elements_1_1_identifier != 'undefined'
				&& details.http_escidoc_mpg_de_position.http_escidoc_mpg_de_organization == orgName)
			{
				orgId = details.http_escidoc_mpg_de_position.http_purl_org_dc_elements_1_1_identifier;
			}
		}
		else
		{
			if (typeof details.http_escidoc_mpg_de_position != 'undefined'
				&& typeof details.http_escidoc_mpg_de_position.length != 'undefined')
			{
				orgName = (typeof details.http_escidoc_mpg_de_position[0].http_escidoc_mpg_de_organization != 'undefined' ? details.http_escidoc_mpg_de_position[0].http_escidoc_mpg_de_organization : null);
				orgId = (typeof details.http_escidoc_mpg_de_position[0].http_purl_org_dc_elements_1_1_identifier != 'undefined' ? details.http_escidoc_mpg_de_position[0].http_purl_org_dc_elements_1_1_identifier : null);
			}
			else if (typeof details.http_escidoc_mpg_de_position != 'undefined')
			{
				orgName = (typeof details.http_escidoc_mpg_de_position.http_escidoc_mpg_de_organization != 'undefined' ? details.http_escidoc_mpg_de_position.http_escidoc_mpg_de_organization : null);
				orgId = (typeof details.http_escidoc_mpg_de_position.http_purl_org_dc_elements_1_1_identifier != 'undefined' ? details.http_escidoc_mpg_de_position.http_purl_org_dc_elements_1_1_identifier : null);
			}
		}
		var personId = $input.resultID;

		fillField('familyName', familyName, parent);
		fillField('givenName', givenName, parent);
		fillField('orgName', orgName, parent);
		fillField('orgIdentifier', orgId, parent);
		$input.blur();
		$input.focus();
		fillField('personIdentifier', personId, parent);
	}
	
	function removeConeId(element)
	{
		var $input = $(element);
		var parent = $input.parents('.' + personSuggestCommonParentClass);
		if ($(parent).find('.personIdentifier').val() != '')
		{
			fillField('personIdentifier', '', parent);
		}
	}
	
	function fillField(name, value, commonParent)
	{

		$(commonParent).find('.' + name).val(value);
	}
	
	function fillFields()
	{
		$input = $(this);
		$.getJSON(journalDetailsBaseURL + this.resultID, getJournalDetails);
	}
	
	function fillPersonFields()
	{
		$input = $(this);
		$input.resultValue = this.resultValue;
		$input.resultID = this.resultID;
		$.getJSON(personDetailsBaseURL + this.resultID, getPersonDetails);
	}
	
	function bindJournalSuggest()
	{
		$('.journalSuggest').suggest(journalSuggestURL, { onSelect: fillFields});
	}
	
	function bindSuggests()
	{
		$('select.journalPulldown[value="'+journalSuggestTrigger+'"]').parents('.'+journalSuggestCommonParentClass).find('.sourceTitle').addClass('journalSuggest');
		$('span.journalPulldown').find('input[type=hidden][value="'+journalSuggestTrigger+'"]').parents('.'+journalSuggestCommonParentClass).find('.sourceTitle').addClass('journalSuggest');
		
		$('select.journalPulldown').change(
				function(){
					if($(this).val() == journalSuggestTrigger) {
						$(this).parents('.'+journalSuggestCommonParentClass).find('.sourceTitle').addClass('journalSuggest');
					} else { 
						$(this).parents('.'+journalSuggestCommonParentClass).find('.sourceTitle').removeClass('journalSuggest');
						$(this).parents('.'+journalSuggestCommonParentClass).find('.sourceTitle').unbind('keypress');
						$(this).parents('.'+journalSuggestCommonParentClass).find('.sourceTitle').unbind('keydown');
						$('.autoSuggestsArea').hide();
					};
					var t = window.setTimeout('bindJournalSuggest()', 500);
				});

		$('span.journalPulldown').find('input[type=hidden]').change(
				function(){
					if($(this).val() == journalSuggestTrigger) {
						$(this).parents('.'+journalSuggestCommonParentClass).find('.sourceTitle').addClass('journalSuggest');
					} else {
						$(this).parents('.'+journalSuggestCommonParentClass).find('.sourceTitle').removeClass('journalSuggest');
						$(this).parents('.'+journalSuggestCommonParentClass).find('.sourceTitle').unbind('keypress');
						$(this).parents('.'+journalSuggestCommonParentClass).find('.sourceTitle').unbind('keydown');
						$('.autoSuggestsArea').hide();
					};
					var t = window.setTimeout('bindJournalSuggest()', 500);
				});

		bindJournalSuggest();
		
		$('.languageSuggest').suggest(languageSuggestURL, { onSelect: function() { $(this).siblings('select').val( (this.resultID.split(':'))[3] ); $(this).siblings('span.replace').replaceValue( (this.resultID.split(':'))[3] ); }   });
		$('.subjectSuggest').suggest(subjectSuggestURL, { onSelect: function() {$(this).val(this.currentResult)}});
		$('.personSuggest').suggest(personSuggestURL, { onSelect: fillPersonFields });
	};