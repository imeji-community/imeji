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
	

function collapse(firstPart, secondPart)
{
	return firstPart + '.' + secondPart;
}

function redirectToSearchPageOld()
{
var query = '';
	
	for (var i=0;i<document.formular.elements.length; i++) 
	{
		var size = document.formular.elements[i].id.length - 15;
		var doc = document.formular.elements[i];
		var value = document.formular.elements[i].value;
		
		if(document.formular.elements[i].type == 'select-one' && document.formular.elements[i].value != 'Whole collection')
		{
			query += document.formular.elements[i].id.substr(15, size) + ':' + document.formular.elements[i].value;
		}
		
		if (document.formular.elements[i].checked) 
		{
			if (query == '') 
			{
				query += document.formular.elements[i].id.substr(15, size) ;
			}
			else
			{
				query += '_' + document.formular.elements[i].id.substr(15, size) ;
			}
		}
		if (document.formular.elements[i].type == 'text' && document.formular.elements[i].value != '') 
		{
			if (query == '') 
			{
				query += document.formular.elements[i].id.substr(15, size) + ':' +  document.formular.elements[i].value;
			}
			else
			{
				query += '_' + document.formular.elements[i].id.substr(15, size) + ':' + document.formular.elements[i].value ;
			}
		}
	}
	
	if (query == '') 
	{
		query += 'error';
	}
	
	if (document.getElementById('SearchFormular:currentalbum').value != '' && query != 'error') 
	{
		query = 'currentalbum/'+ document.getElementById('SearchFormular:currentalbum').value + '/' + query;
	}
	
	window.location.href= './search/result/' + query.toLowerCase();
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
	