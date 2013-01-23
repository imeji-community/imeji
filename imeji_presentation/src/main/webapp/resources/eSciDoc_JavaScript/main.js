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

/**
 * When a confirmation is confirmed, make the panel emty until the method called is done
 * @param button
 * @param message
 */
function submitPanel(button, message) {
	
	var panel = button.parentNode.parentNode.parentNode.parentNode.parentNode.parentNode.parentNode.parentNode;
	if (panel != null) {
		panel.innerHTML = ' <h2><span class="free_area0_p8 xTiny_marginLExcl">'
				+ message + '</span></h2>'
		panel.style.opacity = 0.8;
	}
}

function clickOnDiscard(index, panelId, errorMessage) {
	var listId = '';
	if (index != '') {
		listId = ':list:' + index;
	}
	var textArea = document.getElementById('formular' + listId + ":" + panelId
			+ ':discardComment');
	var button = document.getElementById('formular' + listId + ":" + panelId
			+ ':btnDiscard');
	if (textArea.value != '') {
		return true;
	} else {
		var message = document.getElementById('formular' + listId + ":"
				+ panelId + ':errorMessage');

		message.innerHTML = errorMessage;
		return false;
	}
}

var currentViewState;
jsf.ajax
		.addOnEvent(function(e) {
			var xml = e.responseXML;
			var source = e.source;
			var status = e.status;
			if (status === 'success') {
				var response = xml.getElementsByTagName('partial-response')[0];
				if (response !== null) {
					var changes = response.getElementsByTagName('changes')[0];
					if (changes != undefined) {
						var updates = changes.getElementsByTagName('update');
						if (updates != undefined) {
							for ( var i = 0; i < updates.length; i++) {
								var update = updates[i];
								var id = update.getAttribute('id');
								if (id === 'javax.faces.ViewState') {
									currentViewState = update.firstChild.data;
									// update all forms
									var forms = document.forms;
									for ( var j = 0; j < forms.length; j++) {
										var form = forms[j];
										var field = form.elements["javax.faces.ViewState"];
										if (typeof field == 'undefined') {
											field = document
													.createElement("input");
											field.type = "hidden";
											field.name = "javax.faces.ViewState";
											form.appendChild(field);
										}
										field.value = currentViewState;
									}
								}
							}
						}
					}
				}
			}

		});

var patchJSF = function() {
	jsf.ajax
			.addOnEvent(function(e) {
				if (e.status === 'success') {
					$(
							"partial-response:first changes:first update[id='javax.faces.ViewState']",
							e.responseXML)
							.each(
									function(i, u) {
										// update all forms
										$(document.forms)
												.each(
														function(i, f) {
															var field = $(
																	"input[name='javax.faces.ViewState']",
																	f);
															if (field.length == 0) {
																field = $(
																		"<input type=\"hidden\" name=\"javax.faces.ViewState\" />")
																		.appendTo(
																				f);
															}
															field
																	.val(u.firstChild.data);
														});
									});
				}
			});
}