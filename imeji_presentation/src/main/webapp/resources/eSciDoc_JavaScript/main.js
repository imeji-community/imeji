/* 
CDDL HEADER START

The contents of this file are subject to the terms of the
Common Development and Distribution License, Version 1.0 only
(the "License"). You may not use this file except in compliance
with the License.

You can obtain a copy of the license at license/ESCIDOC.LICENSE
or http://www.escidoc.de/license.
See the License for the specific language governing permissions
and limitations under the License.

When distributing Covered Code, include this CDDL HEADER in each
file and include the License file at license/ESCIDOC.LICENSE.
If applicable, add the following below this CDDL HEADER, with the
fields enclosed by brackets "[]" replaced with your own identifying
information: Portions Copyright [yyyy] [name of copyright owner]

CDDL HEADER END


Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
fÃƒÂ¼r wissenschaftlich-technische Information mbH and Max-Planck-
Gesellschaft zur FÃƒÂ¶rderung der Wissenschaft e.V.	
All rights reserved. Use is subject to license terms.
 */

function autosuggestGoogleGeoAPI(suggestionBox, index, pos, type) {
	var items = suggestionBox.getSelectedItems();
	var address, longitude, latitude;
	if (items && items.length > 0) {
		for ( var i = 0; i < items.length; i++) {
			try {
				address = items[i].adress;
			} catch (e) {
				address = ' ';
			}
			try {
				longitude = items[i].longitude;
			} catch (e) {
				longitude = ' ';
			}
			try {
				latitude = items[i].latitude;
			} catch (e) {
				latitude = ' ';
			}
		}

		var baseId = 'formular:statementList:' + pos + ':metadata:' + index
				+ ':MetadataInput:';

		if (type == 0) {
			if (index >= 0) {
				baseId = 'formular:imagesList:' + index
						+ ':metadata:0:MetadataInput:';
			} else {
				baseId = 'formular:MetadataInput:';
			}
		}

		setInputTextValue(baseId + 'inputAddress', address);
		setInputTextValue(baseId + 'inputLongitude', longitude);
		setInputTextValue(baseId + 'inputLatitude', latitude);

		autoSuggestWrite(suggestionBox, index, pos, type);

	}
}

function autoSuggestWrite(suggestionBox, index, pos, type) {
	var items = suggestionBox.getSelectedItems();
	var familyName, firstName, alternative, id, org, title, complete;
	if (items && items.length > 0) {
		for ( var i = 0; i < items.length; i++) {
			complete = items[i];
			try {
				familyName = items[i].http_xmlns_com_foaf_0_1_family_name;
			} catch (e) {
				familyName = ' ';
			}
			try {
				firstName = items[i].http_xmlns_com_foaf_0_1_givenname;
			} catch (e) {
				firstName = ' ';
			}
			try {
				id = items[i].id;
			} catch (e) {
				id = ' ';
			}
			try {
				alternative = items[i].http_purl_org_dc_terms_alternative;
			} catch (e) {
				alternative = ' ';
			}
			try {
				org = items[i].http_purl_org_escidoc_metadata_terms_0_1_position.http_purl_org_eprint_terms_affiliatedInstitution;
			} catch (e) {
				org = ' ';
			}
			try {
				org = items[i].http_purl_org_escidoc_metadata_terms_0_1_position.http_purl_org_eprint_terms_affiliatedInstitution;
			} catch (e) {
				org = ' ';
			}
			try {
				title = items[i].http_purl_org_dc_elements_1_1_title;
			} catch (e) {
				title = ' ';
			}

		}

		var baseId = 'formular:statementList:' + pos + ':metadata:' + index
				+ ':MetadataInput:';

		if (type == 0) {
			if (index >= 0) {
				baseId = 'formular:imagesList:' + index
						+ ':metadata:0:MetadataInput:';
			} else {
				baseId = 'formular:MetadataInput:';
			}
		}

		setInputTextValue(baseId + 'inputFamilyName', familyName);
		setInputTextValue(baseId + 'inputFirstName', firstName);
		setInputTextValue(baseId + 'inputAlternative', alternative);
		setInputTextValue(baseId + 'inputOrganization', org);
		setInputTextValue(baseId + 'inputIdentifier', id);
		setInputTextValue(baseId + 'inputLanguageIdentifier', id);
		setInputTextValue(baseId + 'inputLanguageName', title)

		if (title != null) {
			setInputTextValue(baseId + 'inputText', title);
		} else {
			setInputTextValue(baseId + 'inputText', complete);
		}

	}
}

function setInputTextValue(id, value) {
	if (document.getElementById(id)) {
		if (value && value != 'undefined') {
			document.getElementById(id).value = value;
		} else {
			document.getElementById(id).value = '';
		}
	}
}

function collapse(firstPart, secondPart) {
	return firstPart + '.' + secondPart;
}

function submitPanel(button, message) {
	var panel = button.parentNode.parentNode.parentNode.parentNode.parentNode.parentNode.parentNode.parentNode;
	panel.innerHTML = ' <h2><span class="free_area0_p8 xTiny_marginLExcl">'
			+ message + '</span></h2>'
	panel.style.opacity = 0.8;
}


function clickOnDiscard(index, panelId, errorMessage)
{
	var listId = '';
	if (index != '')
	{
		listId = ':list:' + index;	
	}
	var textArea = document.getElementById('formular' + listId + ":" + panelId + ':discardComment');
	var button =  document.getElementById('formular' + listId + ":" + panelId + ':btnDiscard');
	if (textArea.value != '')
	{
		return true;
	}
	else
	{
		var message = document.getElementById('formular' + listId +  ":" + panelId + ':errorMessage');
		
		message.innerHTML= errorMessage;
		return false;
	}
}