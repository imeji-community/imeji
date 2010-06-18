<?xml version="1.0" encoding="UTF-8"?>
<!--

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


 Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->

<jsp:root version="2.1" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:tr="http://myfaces.apache.org/trinidad">

<jsp:output doctype-root-element="html"
        doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
        doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" />

	<jsp:directive.page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" />
	<f:view locale="#{SessionBean.locale}" xmlns:e="http://www.escidoc.de/jsf">
		<f:loadBundle var="lbl" basename="labels"/>
		<f:loadBundle var="msg" basename="messages"/>
		<html xmlns="http://www.w3.org/1999/xhtml">
			<head>

				<title><h:outputText value="#{ApplicationBean.applicationName}"/></title>

				<jsp:directive.include file="headerPages/ui/StandardImports.jspf" />

			</head>
			<body lang="#{SessionBean.locale}">
			
			<h:outputText value="#{SessionBean.checkLogin}" styleClass="noDisplay" />
			<h:outputText value="#{AlbumListBean.init}" styleClass="noDisplay" />
			
			<h:form id="formAlbums">
			<div class="full wrapper">
			<h:inputHidden id="offset"></h:inputHidden>

				<!-- import header -->
				<jsp:directive.include file="headerPages/Header.jspf" />

				<div id="content" class="full_area0 clear">
				<!-- begin: content section (including elements that visualy belong to the header (breadcrumb, headline, subheader and content menu)) -->
					<div class="clear">
						<div class="headerSection">
							
						<!--<jsp:directive.include file="header/Breadcrumb.jspf" />-->
				
							<div id="contentSkipLinkAnchor" class="clear headLine">
								<!-- Headline starts here -->
								<h1>
									<h:outputText value="#{msg.albums_headline}" />
								</h1>
								<!-- Headline ends here -->
							</div>
						</div>
						<div class="small_marginLIncl subHeaderSection">
							<div class="contentMenu">
							<!-- content menu starts here -->
								<div class="free_area0 sub">
								<!-- content menu upper line starts here -->
								<!--  	<h:outputLink id="lnkView" styleClass="free_area0" value="?tab=VIEW" rendered="#{AlbumSession.selectedMenu != 'VIEW'}">
										<h:outputText value="#{lbl.view}"/>
									</h:outputLink>
								
									<h:outputText id="txtView" styleClass="free_area0" value="#{lbl.view}" rendered="#{AlbumSession.selectedMenu == 'VIEW'}" />
									<h:outputText styleClass="seperator void" />
								-->
									<h:outputLink id="lnkFilter" styleClass="free_area0" value="?tab=FILTER" rendered="#{SessionBean.user != null and AlbumSession.selectedMenu != 'FILTER' and AlbumListBean.listType != 'search'}">
										<h:outputText value="#{lbl.filter}"/>
									</h:outputLink>
									<h:outputText id="txtFilterLink" styleClass="free_area0" value="#{lbl.filter}" rendered="#{SessionBean.user != null and AlbumSession.selectedMenu == 'FILTER'}" />
									<h:outputText styleClass="seperator void" />
									<h:outputLink id="lnkSort" styleClass="free_area0" value="?tab=SORTING" rendered="#{AlbumSession.selectedMenu != 'SORTING'}">
										<h:outputText value="#{lbl.sort}"/>
									</h:outputLink>
									<h:outputText id="txtSortLink" styleClass="free_area0" value="#{lbl.sort}" rendered="#{AlbumSession.selectedMenu == 'SORTING'}" />
									<h:outputText styleClass="seperator void" />
									<h:outputLink id="lnkOrganize" styleClass="free_area0" value="?tab=ORGANIZE" rendered="#{AlbumSession.selectedMenu != 'ORGANIZE' and SessionBean.user != null and AlbumListBean.listType != 'search'}">
										<h:outputText value="#{lbl.organize}"/>
									</h:outputLink>
									<h:outputText id="txtOrganizeLink" styleClass="free_area0" value="#{lbl.organize}" rendered="#{AlbumSession.selectedMenu == 'ORGANIZE' and SessionBean.user != null and AlbumListBean.listType != 'search' and SessionBean.currentUrl != 'home'}" />
									<h:outputText styleClass="seperator void" />
								<!-- content menu upper line ends here -->
								</div>
								<h:panelGroup layout="block" styleClass="free_area0 sub action" rendered="#{AlbumSession.selectedMenu == 'VIEW'}">
								<!-- VIEW content menu lower line starts here 
									<tr:iterator id="viewList" var="menu" varStatus="status" value="#{AlbumListBean.viewMenu}">
										<h:outputLink  id="lnkView" styleClass="free_area0" value="#{menu.value}" disabled="#{AlbumListBean.firstPage == menu.value}">
											<h:outputText value="#{menu.label}"></h:outputText>
										</h:outputLink>
									</tr:iterator>
								 VIEW content menu lower line ends here -->
								</h:panelGroup>
								<h:panelGroup layout="block" styleClass="free_area0 sub action" rendered="#{SessionBean.user != null and AlbumSession.selectedMenu == 'FILTER'}">
								<!-- FILTER content menu lower line starts here -->
									<h:outputText id="txtFilter" value="Filtered by " styleClass="free_area0"/>
									<h:selectOneMenu id="selFilter" value="#{AlbumListBean.url}?filter=#{AlbumSession.filter}" styleClass="free_select"  onchange="location.href=this.value">
										<f:selectItems id="filterItem" value="#{AlbumListBean.filterMenu}"/>
									</h:selectOneMenu>
								<!-- FILTER content menu lower line ends here -->
								</h:panelGroup>
								<h:panelGroup layout="block" styleClass="free_area0 sub action" rendered="#{AlbumSession.selectedMenu == 'SORTING'}">
								<!-- SORTING content menu lower line starts here -->
									<h:outputText id="txtSort" value="Sorted by " styleClass="free_area0"/>
									<h:selectOneMenu id="selSort" value="#{AlbumListBean.url}" styleClass="free_select"  onchange="location.href=this.value">
										<f:selectItems id="sortItem" value="#{AlbumListBean.sortMenu}"/>
									</h:selectOneMenu>
									<h:outputLink id="lnkSortAsc" styleClass="ascSort" value="#{AlbumListBean.toggleOrder}" rendered="#{AlbumListBean.list.parameters.orderBy == 'ASCENDING'}">&#160;</h:outputLink>
									<h:outputLink id="lnkSortDesc" styleClass="desSort" value="#{AlbumListBean.toggleOrder}" rendered="#{AlbumListBean.list.parameters.orderBy != 'ASCENDING'}">&#160;</h:outputLink>
									<h:outputText styleClass="seperator void" />
								<!-- SORTING content menu lower line ends here -->
								</h:panelGroup>
								<h:panelGroup layout="block" styleClass="free_area0 sub action" rendered="#{AlbumSession.selectedMenu == 'ORGANIZE' and SessionBean.user != null and AlbumListBean.listType != 'search' and SessionBean.currentUrl != 'home'}">
								<!-- ORGANIZE content menu lower line starts here -->
									<h:outputLink  id="lnkCreate" styleClass="free_area0" value="#{Navigation.albumUrl}/new?action=init">
											<h:outputText value="Create Album"></h:outputText>
									</h:outputLink>
									<h:outputText styleClass="seperator"/>
									<h:commandLink id="lnkDelete" styleClass="free_area0" action="#{AlbumListBean.deleteLink}" immediate="true">
											<h:outputText value="Delete Selected Albums"></h:outputText>
									</h:commandLink>
								<!-- ORGANIZE content menu lower line ends here -->
								</h:panelGroup>
							<!-- content menu ends here -->
							</div>

							<h:panelGroup layout="block" styleClass="subHeader">
								<!-- SubHeader -->
							</h:panelGroup>
							
							<h:panelGroup layout="block" styleClass="subHeader" rendered="#{(SessionBean.urlQuery != 'error' || SessionBean.pageNotFound) and ((SessionBean.message != null) || (SessionBean.information != null))}">
								<!-- Application Events -->
								<jsp:directive.include file="include/Messages.jspf"/>
							</h:panelGroup>

						</div>
					</div>
					
					<!--<h:panelGroup styleClass="full_area0" layout="block" rendered="#{SessionBean.user == null and AlbumListBean.listType != 'search'}">
						<h:outputText value="Sorry, you have to be logged in to view this page!" />
					</h:panelGroup>-->
					<h:panelGroup styleClass="full_area0" layout="block">
						<jsp:directive.include file="albumPages/Albums.jspf" />
					</h:panelGroup>

				</div>
				<!-- end: content section -->
				</div>
					<jsp:directive.include file="footerPages/Footer.jspf" />
				</h:form>
				<script type="text/javascript">
					function searchOnEnter(event)
					{
						if (event.keyCode == 13)
						{
							search();
						}
					}
					function search()
					{
						window.location.href= '<h:outputText value="#{Navigation.albumsSearchUrl}"/>' + '?query=' + document.getElementById('formAlbums:inputAlbumSearch').value;  
					}
					
				</script>				
			</body>
		</html>
	</f:view>
</jsp:root>