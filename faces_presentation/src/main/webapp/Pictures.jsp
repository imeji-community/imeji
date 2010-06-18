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

<jsp:root version="2.1" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html" xmlns:jsp="http://java.sun.com/JSP/Page">

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
			<body lang="#{SessionBean.locale}" onload="setRendering(); return null;">
				<h:outputText value="#{SessionBean.checkLogin}" styleClass="noDisplay" />
				<h:outputText value="#{AlbumSession.initCurrentAlbum}" styleClass="noDisplay"></h:outputText>
				<h:outputText value="#{HomePage.init}" styleClass="noDisplay" />
				<h:outputText rendered="#{(SessionBean.user == null) and (SessionBean.currentUrl != 'resource') and (SessionBean.currentUrl == 'viewAlbum')}" value="#{HomePage.resolveAlbum}" />
				
				<h:form id="formular">
					<div class="full wrapper">
						<h:inputHidden id="offset"></h:inputHidden>
						
						<!-- import header -->
						
						<jsp:directive.include file="headerPages/Header.jspf" />
							<div id="content" class="full_area0 clear">
								<!-- begin: content section (including elements that visualy belong to the header (breadcrumb, headline, subheader and content menu)) -->
								<div class="clear">
									<div class="headerSection">
							
										<jsp:directive.include file="headerPages/Breadcrumb.jspf"/>
				
										<div id="contentSkipLinkAnchor" class="clear headLine">
											<!-- Headline starts here -->
											<h1>
												<h:outputText value="#{msg.browse_headline}" rendered="#{SessionBean.currentUrl == 'home'}" />
												<h:outputText value="#{msg.search_results_headline}" rendered="#{ SessionBean.currentUrl == 'searchResult'}"/>
												<h:outputText value="#{AlbumSession.current.mdRecord.title}" rendered="#{SessionBean.currentUrl == 'viewAlbum' || SessionBean.currentUrl == 'resource'}"/>
												<h:outputText value="#{msg.all_pictures_person_headline}" rendered="#{SessionBean.currentUrl == 'person'}" />
											</h1>
											<!-- Headline ends here -->
										</div>
									</div>
									<div class="small_marginLIncl subHeaderSection">
										<div class="contentMenu">
											<!-- content menu starts here -->
											<div class="free_area0 sub"> 
												<!-- content menu upper line starts here -->
												<h:outputLink id="lnkSort" styleClass="free_area0" value="?tab=SORTING" rendered="#{SessionBean.selectedMenu != 'SORTING' and HomePage.totalNumberOfItems != '0'}">
													<h:outputText value="#{lbl.sort}"></h:outputText>
												</h:outputLink>
												<h:outputText styleClass="free_area0" value="#{lbl.sort}" rendered="#{(SessionBean.selectedMenu == 'SORTING') and (HomePage.totalNumberOfItems != '0')}" />
												<h:outputText styleClass="seperator void" />
												<h:outputLink id="lnkOrganize" styleClass="free_area0" value="?tab=ORGANIZE" rendered="#{SessionBean.selectedMenu != 'ORGANIZE' and (AlbumSession.current.version.objectId != null or AlbumSession.active.version.objectId != null)}">
													<h:outputText value="#{lbl.organize}"></h:outputText>
												</h:outputLink>
												<h:outputText styleClass="free_area0" value="#{lbl.organize}" rendered="#{SessionBean.selectedMenu == 'ORGANIZE' and (AlbumSession.current.version.objectId != null or AlbumSession.active.version.objectId != null)}" />
												<h:outputText styleClass="seperator void" />
												<h:outputLink id="lnkExportAlbum" styleClass="free_area0" value="?tab=EXPORT" rendered="#{SessionBean.selectedMenu != 'EXPORT' and AlbumSession.current.version.objectId != null and (SessionBean.currentUrl != 'resource') and (AlbumSession.current.state != 'WITHDRAWN') and (AlbumSession.current.size != '0') and (SessionBean.allowed)}">
													<h:outputText value="#{lbl.export}" />
												</h:outputLink>
												<h:outputText styleClass="free_area0" value="#{lbl.export}" rendered="#{SessionBean.selectedMenu == 'EXPORT' and AlbumSession.current.version.objectId != null and (SessionBean.currentUrl != 'resource') and (AlbumSession.current.state != 'WITHDRAWN') and (AlbumSession.current.size != '0') and (SessionBean.allowed)}" />
												<h:outputLink styleClass="free_area0" id="lnkDownload" value="#{Navigation.confirmationUrl}/download" rendered="#{SessionBean.user != null and SessionBean.currentUrl == 'home'}">
													<h:outputText value="#{lbl.download_collection}"></h:outputText>
												</h:outputLink>
												
												<h:outputText styleClass="seperator void" />
												
												
												<!-- content menu upper line ends here -->
											</div>
											<h:panelGroup layout="block" styleClass="free_area0 sub action" rendered="#{HomePage.totalNumberOfItems != '0' and SessionBean.selectedMenu == 'SORTING'}">
												<!-- SORTING content menu lower line starts here -->
												<h:outputText id="txtSortBy" value="Sorted by " styleClass="free_area0"/>
												<h:selectOneMenu id="selSortList1" value="#{SortingBean.selectedValue1}" styleClass="free_select">
													<f:selectItems value="#{SortingBean.sortcriterialist1}" />
												</h:selectOneMenu>
												<h:outputLink id="lnkSort1" styleClass="#{SortingBean.style1}" onclick="javascript:toggleorder(this); return false" value="#">&#160;</h:outputLink>
												<h:outputText styleClass="seperator void" />
												<h:selectOneMenu id="selSortList2" value="#{SortingBean.selectedValue2}" styleClass="free_select">
													<f:selectItems value="#{SortingBean.sortcriterialist2}" />
												</h:selectOneMenu>
												<h:outputLink id="lnkSort2" styleClass="#{SortingBean.style2}" onclick="javascript:toggleorder(this); return false" value="#">&#160;</h:outputLink>
												<h:outputText styleClass="seperator void" />
												<h:selectOneMenu id="selSortList3" value="#{SortingBean.selectedValue3}" styleClass="free_select">
													<f:selectItems value="#{SortingBean.sortcriterialist3}" />
												</h:selectOneMenu>
												<h:outputLink id="lnkSort3" styleClass="#{SortingBean.style3}" onclick="javascript:toggleorder(this); return false" value="#">&#160;</h:outputLink>
												<h:outputText styleClass="seperator void" />
												<h:outputLink value="#" onclick="javascript:callsorting(); return null;" styleClass="free_area1_p2 activeButton">
													<h:outputText value="Sort" />
												</h:outputLink>
												<!-- SORTING content menu lower line ends here -->
											</h:panelGroup>
											
											<!-- ORGANIZE content menu lower line FOR ALBUMS starts here -->
											<h:panelGroup layout="block" styleClass="free_area0 sub action" rendered="#{SessionBean.selectedMenu == 'ORGANIZE' and AlbumSession.current.version.objectId != null}">
												<h:outputLink id="lnkEditAlbum" styleClass="free_area0" value="#{Navigation.editAlbumUrl}/#{AlbumSession.current.latestVersion.objectId}" rendered="#{AlbumSession.current.state == 'PENDING'}">
													<h:outputText value="#{lbl.album_edit}"></h:outputText>
												</h:outputLink>
												<h:outputText styleClass="seperator" rendered="#{AlbumSession.current.state == 'PENDING'}" />
												<h:outputLink id="lnkPublishAlbum" styleClass="free_area0" value="#{Navigation.confirmationUrl}/publish/#{AlbumSession.current.latestVersion.objectId}" rendered="#{(AlbumSession.current.state == 'PENDING') and (AlbumSession.current.size != '0')}">
													<h:outputText value="#{lbl.album_publish}"></h:outputText>
												</h:outputLink>
												<h:outputText styleClass="seperator" rendered="#{(AlbumSession.current.state == 'PENDING') and (AlbumSession.current.size != '0')}" />
												<h:outputLink id="lnkDeleteAlbum" styleClass="free_area0" value="#{Navigation.confirmationUrl}/delete/#{AlbumSession.current.latestVersion.objectId}" rendered="#{AlbumSession.current.state == 'PENDING'}">
													<h:outputText value="#{lbl.album_delete}"></h:outputText>
												</h:outputLink>
												<h:outputText styleClass="seperator" rendered="#{AlbumSession.current.state == 'PENDING'}" />
												<h:outputLink id="lnkWithdrawAlbum" styleClass="free_area0" value="#{Navigation.confirmationUrl}/withdraw/#{AlbumSession.current.latestVersion.objectId}" rendered="#{(AlbumSession.current.state == 'RELEASED') and (AlbumSession.current.owner.objectId == SessionBean.user.reference.objectId)}">
													<h:outputText value="#{lbl.album_withdraw}"></h:outputText>
												</h:outputLink>
												
												<h:outputLink id="lnkRemovePage" styleClass="free_area0" value="#{Navigation.albumInterfaceUrl}/#{AlbumSession.current.version.objectId}/REMOVEPAGE" rendered="#{AlbumSession.current.state == 'PENDING'}">
													<h:outputText value="#{lbl.batch_remove_page}"></h:outputText>
												</h:outputLink>
												<h:outputText styleClass="seperator" rendered="#{AlbumSession.current.state == 'PENDING'}" />
												<h:outputLink id="lnkRemoveAll" styleClass="free_area0" value="#{Navigation.albumInterfaceUrl}/#{AlbumSession.current.version.objectId}/REMOVEALL" rendered="#{AlbumSession.current.state == 'PENDING'}">
													<h:outputText value="#{lbl.batch_remove_all}"></h:outputText>
												</h:outputLink>
											</h:panelGroup>
											<!-- ORGANIZE content menu lower line FOR ALBUMS ends here -->
											
											<!-- ORGANIZE content menu lower line FOR PICTURES starts here -->
											<h:panelGroup layout="block" styleClass="free_area0 sub action" rendered="#{SessionBean.selectedMenu == 'ORGANIZE'  and AlbumSession.current.version.objectId == null and AlbumSession.active.version.objectId != null}">
												<h:outputLink id="lnkAddPage" styleClass="free_area0" value="#{Navigation.albumInterfaceUrl}/#{AlbumSession.active.version.objectId}/ADDPAGE" rendered="true">
													<h:outputText value="#{lbl.batch_add_page}"></h:outputText>
												</h:outputLink>
												<h:outputText styleClass="seperator" rendered="true" />
												<h:outputLink id="lnkAddAll" styleClass="free_area0" value="#{Navigation.albumInterfaceUrl}/#{AlbumSession.active.version.objectId}/ADDALL" rendered="true">
													<h:outputText value="#{lbl.batch_add_all}"></h:outputText>
												</h:outputLink>
											</h:panelGroup>
											<!-- ORGANIZE content menu lower line FOR PICTURES ends here -->
											
											<h:panelGroup layout="block" styleClass="free_area0 sub action" rendered="#{SessionBean.selectedMenu == 'EXPORT' and AlbumSession.current.version.objectId != null and (SessionBean.currentUrl != 'resource') and (AlbumSession.current.state != 'WITHDRAWN') and (AlbumSession.current.size != '0') and (SessionBean.allowed)}">
												<!-- EXPORT content menu lower line starts here -->
												<h:selectOneMenu id="selExportFormat" label="#{lbl.attributes}" value="#{AlbumSession.exportManager.parameters.exportFormatAsString}" valueChangeListener="#{AlbumSession.exportFormatListener}" immediate="true"  onclick="changeDisabled(this.value); return null;">
													<f:selectItem  itemLabel="#{lbl.attributes_in_xml}" itemValue="XML" />
													<f:selectItem itemLabel="#{lbl.attributes_in_csv}" itemValue="CSV"/>
													<f:selectItem itemLabel="#{lbl.pictures_and_xml}" itemValue="XML_AND_PICTURES" />
													<f:selectItem itemLabel="#{lbl.pictures_and_csv}" itemValue="CSV_AND_PICTURES"/>
													<f:selectItem itemLabel="#{lbl.pictures}" itemValue="PICTURES" />
												</h:selectOneMenu>
												
												<h:panelGroup layout="block" styleClass="free_checkbox">
													 <h:selectBooleanCheckbox id="selThumbnails" value="#{AlbumSession.exportManager.parameters.thumbnails}" valueChangeListener="#{AlbumSession.thumbnailsResolutionListener}" immediate="true"></h:selectBooleanCheckbox>
													<h:outputLabel value="#{lbl.thumbnails}" ></h:outputLabel>
												</h:panelGroup>
												<h:panelGroup layout="block" styleClass="free_checkbox">
													 <h:selectBooleanCheckbox id="selWeb" value="#{AlbumSession.exportManager.parameters.web}" valueChangeListener="#{AlbumSession.webResolutionListener}" immediate="true"></h:selectBooleanCheckbox>
													<h:outputLabel value="#{lbl.web_resolution}" ></h:outputLabel>
												</h:panelGroup>
												
												<h:commandLink id="cmdDownload" action="#{AlbumSession.submitExportFormular}" value="#{lbl.download}" immediate="true" styleClass="free_area0_p8 activeButton"></h:commandLink>
												
												<!-- EXPORT content menu lower line ends here -->
											</h:panelGroup>
											
											<h:panelGroup layout="block" styleClass="free_area0 sub action" rendered="#{SessionBean.selectedMenu == 'EXPORTALL' and SessionBean.currentUrl == 'home'}">

											</h:panelGroup>
										<!-- content menu ends here -->
										
										</div>

										<h:panelGroup layout="block" styleClass="subHeader" id="welcome" rendered="#{(SessionBean.user == null) and (SessionBean.currentUrl == 'home')}">
											<!-- Welcome (not logged in) -->
											<h:outputText value="#{HomePage.totalNumberOfItems} #{msg.browse_not_logged_in}" />
										</h:panelGroup>
	
										<h:panelGroup layout="block" styleClass="subHeader" rendered="#{(SessionBean.query != '') and (SessionBean.query != null) and (SessionBean.currentUrl == 'searchResult')}">
											<a class="free_area0 xTiny_marginRExcl" href="#" onclick="$(this).parents('.subHeaderSection').find('.searchQuery').slideToggle('slow'); $(this).hide();"><h:outputText id="lblShowQuery" value="Show Query"/></a>								
											<h:outputLink id="lnkReviseSearcg" title="Revise the last Search"  value="#{Navigation.searchUrl}?action=revise">Revise Search</h:outputLink>
										</h:panelGroup>
	
										<h:panelGroup layout="block" styleClass="subHeader" rendered="#{(SessionBean.query != '') and (SessionBean.query != null) and (SessionBean.currentUrl == 'searchResult')}">
											<!-- Search Query -->
											<h:panelGroup layout="block" styleClass="full_area0 searchQuery" style="display: none;">
												<h:outputText value="#{msg.search_results_query_01}" ></h:outputText>
												<h:outputText value=" the whole Faces Collection for " rendered="#{SessionBean.user == null}" />
												<h:outputText value=" #{HomePage.collectionName} for " rendered="#{(SessionBean.user != null) and (HomePage.collectionName != null)}" />
												<h:outputText value=" #{lbl.collection_list_default} for " rendered="#{(SessionBean.user != null) and (HomePage.collectionName == null)}" />
												<h:outputText value="#{HomePage.queryDisplayed}" />
											</h:panelGroup>
										</h:panelGroup>
	
										<h:panelGroup layout="block" styleClass="subHeader" rendered="#{(SessionBean.urlQuery != 'error' || SessionBean.pageNotFound) and ((SessionBean.message != null) || (SessionBean.information != null))}">
											<!-- Application Events -->
											<jsp:directive.include file="include/Messages.jspf"/>
										</h:panelGroup>
									</div>
							</div>
							

							<h:panelGroup styleClass="full_area0" rendered="#{HomePage.totalNumberOfItems == '0'}">
								<h:outputText id="txtEmptySearch" styleClass="free_area0 small_marginLExcl" value="#{msg.message_search_results_empty}" rendered="#{SessionBean.currentUrl != 'viewAlbum'}"></h:outputText>
							</h:panelGroup>

							<jsp:directive.include file="picturesPages/ui/PaginationTop.jspf"/>		
					
							<jsp:directive.include file="picturesPages/ui/ActiveAlbumMenu.jspf"/>			
					
							<h:panelGroup layout="block" styleClass="full_area0 tiledList" rendered="#{(AlbumSession.current.version.objectId == null) and !(SessionBean.currentUrl == 'viewAlbum') }">
								<jsp:directive.include file="picturesPages/Browse.jspf" />
							</h:panelGroup>
							
							<h:panelGroup layout="block" styleClass="full_area0 tiledList" rendered="#{(AlbumSession.current != null) and (AlbumSession.current.version.objectId != null)}">
								<jsp:directive.include file="picturesPages/Album.jspf" />
							</h:panelGroup>

							<jsp:directive.include file="picturesPages/ui/PaginationBottom.jspf"/>

						</div>
					<!-- end: content section -->
					</div>
					<jsp:directive.include file="footerPages/Footer.jspf" />
				</h:form>
				<script type="text/javascript">
				$("input[id$='offset']").submit(function() {
					$(this).val($(window).scrollTop());
				});
				$(document).ready(function () {
					$(window).scrollTop($("input[id$='offset']").val());
					$(window).scroll(function(){$("input[id$='offset']").val($(window).scrollTop())});
				});

				function changeDisabledToTrue()
				{
					document.getElementById('formular:selWeb').disabled = true;
					document.getElementById('formular:selThumbnails').disabled = true;
				}
				function changeDisabledToFalse()
				{
					document.getElementById('formular:selWeb').disabled = false;
					document.getElementById('formular:selThumbnails').disabled = false;
				}
				function setRendering()
				{
					if ('XML' == '<h:outputText value="#{AlbumSession.exportManager.parameters.exportFormatAsString}"/>' 
						|| 'CSV' == '<h:outputText value="#{AlbumSession.exportManager.parameters.exportFormatAsString}"/>')
					{
						changeDisabledToTrue();
					}
					else
					{
						changeDisabledToFalse();
					}
				}
				function changeDisabled(value)
				{
					if ('XML' == value || 'CSV' == value) 
					{
						changeDisabledToTrue();
					}
					else
					{
						changeDisabledToFalse();
					}
				}
				</script>	
			</body>
		</html>
	</f:view>
</jsp:root>