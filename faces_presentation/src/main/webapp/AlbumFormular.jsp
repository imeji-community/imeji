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
			<body lang="#{SessionBean.locale}">
				<h:outputText value="#{SessionBean.checkLogin}" styleClass="noDisplay" />
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
							
										<!--<jsp:directive.include file="header/Breadcrumb.jspf" />-->
				
										<div id="contentSkipLinkAnchor" class="clear headLine">
											<!-- Headline starts here -->
											<h1>
												<h:outputText value="#{msg.create_album_00_headline}" rendered="#{AlbumFormular.type == 'CREATE'}"/>
												<h:outputText value="#{msg.edit_album_00_headline}" rendered="#{AlbumFormular.type == 'EDIT'}"/>
											</h1>
											<!-- Headline ends here -->
										</div>
									</div>
									<div class="small_marginLIncl subHeaderSection">
										<!-- Start currently not used -->
										<div class="contentMenu">
											<!-- content menu starts here -->
											<div class="free_area0 sub">
												<!-- content menu upper line starts here -->
												<h:commandLink id="lnkOrganize" styleClass="free_area0" value="ORGANIZE" rendered="false" />
												<h:outputText styleClass="free_area0" value="ORGANIZE" rendered="false" />
												<h:outputText styleClass="seperator void" />
												<!-- content menu upper line ends here -->
											</div>
											<h:panelGroup layout="block" styleClass="free_area0 sub action" rendered="#{false and (SessionBean.user != null) and (AlbumSession.current != 'viewAlbum') and (AlbumSession.current.version.objectId != null)}">
												<!-- ORGANIZE content menu lower line starts here -->
												<h:outputLink id="lnkEditAlbum" styleClass="free_area0" value="#{Navigation.editAlbumUrl}/#{AlbumSession.current.latestVersion.objectId}" rendered="#{AlbumSession.current.stateString == 'pending'}">
													<h:outputText value="Edit  Album"></h:outputText>
												</h:outputLink>
												<h:outputText styleClass="seperator" rendered="#{AlbumSession.current.stateString == 'pending'}" />
												<h:outputLink id="lnkPublishAlbum" styleClass="free_area0" value="#{Navigation.confirmationUrl}/publish/#{AlbumSession.current.latestVersion.objectId}" rendered="#{(AlbumSession.current.stateString == 'pending') and (AlbumSession.current.size != '0')}">
													<h:outputText value="Publish Album"></h:outputText>
												</h:outputLink>
												<h:outputText styleClass="seperator" rendered="#{(AlbumSession.current.stateString == 'pending') and (AlbumSession.current.size != '0')}" />
												<h:outputLink id="lnkDeleteAlbum" styleClass="free_area0" value="#{Navigation.confirmationUrl}/delete/#{AlbumSession.current.latestVersion.objectId}" rendered="#{AlbumSession.current.stateString == 'pending'}">
													<h:outputText value="Delete Album"></h:outputText>
												</h:outputLink>
												<h:outputText styleClass="seperator" rendered="#{AlbumSession.current.stateString == 'pending'}" />
												<h:outputLink id="lnkWithdrawAlbum" styleClass="free_area0" value="#{Navigation.confirmationUrl}/withdraw/#{AlbumSession.current.latestVersion.objectId}" rendered="#{(AlbumSession.current.stateString == 'released') and (AlbumSession.current.owner.objectId == SessionBean.user.reference.objectId)}">
													<h:outputText value="Withdraw Album"></h:outputText>
												</h:outputLink>
												<!-- ORGANIZE content menu lower line ends here -->
											</h:panelGroup>
										<!-- content menu ends here -->										
										</div>
										<!-- End currently not used -->			

										<h:panelGroup layout="block" styleClass="subHeader" id="welcome" rendered="#{(SessionBean.user == null) and (SessionBean.currentUrl == 'home')}">
											<!-- Welcome (not logged in) -->
											<h:outputText value="#{HomePage.totalNumberOfItems} #{msg.browse_not_logged_in}" />
										</h:panelGroup>
	
										<h:panelGroup layout="block" styleClass="subHeader" rendered="#{(SessionBean.urlQuery != 'error' || SessionBean.pageNotFound) and ((SessionBean.message != null) || (SessionBean.information != null))}">
											<!-- Application Events -->
											<jsp:directive.include file="include/Messages.jspf"/>
										</h:panelGroup>
									</div>
							</div>

							<jsp:directive.include file="picturesPages/ui/PaginationTop.jspf"/>	
					
							<jsp:directive.include file="picturesPages/ui/ActiveAlbumMenu.jspf"/>			
							
							<h:panelGroup layout="block" styleClass="full_area0 tiledList" rendered="#{(SessionBean.allowed)}">
								<jsp:directive.include file="albumPages/ui/AlbumFormular.jspf" />
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
					$(window).scroll(function(){$("input[id$='offset']").val($(window).scrollTop());});
				});

				personSuggestURL = '<h:outputText value="#{ConeHelper.coneUrl}"/>json/persons/query';
				personSuggestCommonParentClass = 'suggestAnchor';
				
				</script>				
			</body>
		</html>
	</f:view>
</jsp:root>