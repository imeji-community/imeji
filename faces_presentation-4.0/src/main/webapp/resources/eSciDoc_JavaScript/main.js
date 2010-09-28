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
	
function autoSuggestWrite(suggestionBox, index) 
 { 
    var items = suggestionBox.getSelectedItems();
    var familyName;
    var firstName;
    var alternative;
    var id;
    var org;
    if (items && items.length > 0) 
	{
     	for (var i = 0; i < items.length; i++) 
		{
     		familyName = items[i].http_xmlns_com_foaf_0_1_family_name;
     		try{firstName = items[i].http_xmlns_com_foaf_0_1_givenname;}
     		catch (e){firstName =' ';}
     		try{id = items[i].http_purl_org_dc_elements_1_1_identifier.http_www_w3_org_1999_02_22_rdf_syntax_ns_value;}
     		catch (e){id =' ';}
     		try{alternative = items[i].http_purl_org_dc_terms_alternative;}
     		catch (e){alternative =' ';}
     		try{org = items[i].http_purl_org_escidoc_metadata_terms_0_1_position.http_purl_org_eprint_terms_affiliatedInstitution;}
     		catch (e){org =' ';}
		}
     	setInputTextValue('formular:mdList:' + index + ':inputFamilyName', familyName);
     	setInputTextValue('formular:mdList:' + index + ':inputFirstName', firstName);
     	setInputTextValue('formular:mdList:' + index + ':inputAlternative', alternative);
     	setInputTextValue('formular:mdList:' + index + ':inputIdentifier', id);
     	setInputTextValue('formular:mdList:' + index + ':inputOrganization', org);
	}
}

function setInputTextValue(id, value)
{
	if (value && value != 'undefined')
	{
		document.getElementById(id).value  = value;
	}
	else
	{
		document.getElementById(id).value  = '';
	}
}

function collapse(firstPart, secondPart)
{
	return firstPart + '.' + secondPart;
}

function albumSearchOnEnter(event)
{
	if (event.keyCode == 13)
	{
		albumSearch();
	}
}

function albumSearch()
{
	var url = '?query=';
	if (document.getElementById('formAlbums:Albums:AlbumTableView:inputAlbumSearch').value != '')
	{
		url += document.getElementById('formAlbums:Albums:AlbumTableView:inputAlbumSearch').value;
		window.location.href= url;
	}
}


function modifyexportparameters()
{
	var url = '';
	var resolution = 'default';
	for ( var int = 0; int < document.formular.elements.length; int++) 
	{
		if (document.formular.elements[int].type == 'radio' && document.formular.elements[int].checked) 
		{
			url += document.formular.elements[int].value;
		}
		
		if (document.formular.elements[int].type == 'checkbox' && document.formular.elements[int].checked) 
		{
			if (resolution == 'default') 
			{
				url += '&resolutions=' + document.formular.elements[int].id.substr('SearchFormular:'.length, (document.formular.elements[int].id.length - 'SearchFormular:'.length));
				resolution = 'modify';
			}
			else
			{
				url+= ',' + document.formular.elements[int].id.substr('SearchFormular:'.length, (document.formular.elements[int].id.length - 'SearchFormular:'.length));
			}
		}
	}
	window.location.href = url;
}

function agreeexport(url)
{
	var agree = 'false';
	for ( var int = 0; int < document.formular.elements.length; int++) 
	{
		if (document.formular.elements[int].type == 'checkbox' && document.formular.elements[int].checked && document.formular.elements[int].id == 'confirmationFormular:agreement')
		{
			agree = 'true';
		}
	}
	if (agree == 'true') 
	{
		url += '&agree=true&action=doexport'; 
	}
	else
	{
		url = window.location.href + '&error=agreement';
	}
	window.location.href = url;
}
	