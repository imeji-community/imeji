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
				<title>
					<h:outputText value="#{ApplicationBean.applicationName}"/>
				</title>

				<jsp:directive.include file="headerPages/ui/StandardImports.jspf" />

				<style type="text/css">
					.p_OraTreeIcon {
						heigth: 0px !important;
						width: 0px !important;
					}
				</style>

			</head>
			<body lang="#{SessionBean.locale}">
			
			<h:outputText value="#{SessionBean.checkLogin}" styleClass="noDisplay" />
			<h:outputText value="#{Detail.init}" styleClass="noDisplay" />
			
			<h:form id="formDetails">
			<div class="full wrapper">
			<h:inputHidden id="offset"></h:inputHidden>

				<!-- import header -->
				<jsp:directive.include file="headerPages/Header.jspf" />

				<div id="content" class="full_area0 clear">
				<!-- begin: content section (including elements that visualy belong to the header (breadcrumb, headline, subheader and content menu)) -->
					<div class="clear">
						<div class="headerSection">
							
						<jsp:directive.include file="headerPages/Breadcrumb.jspf" />
				
							<div id="contentSkipLinkAnchor" class="clear headLine">
								<!-- Headline starts here -->
								<h1>
									<h:outputText value="#{lbl.details}" />
								</h1>
								<!-- Headline ends here -->
							</div>
						</div>
						<div class="small_marginLIncl subHeaderSection">
							<div class="contentMenu">
							<!-- content menu starts here -->
								<div class="free_area0 sub">
								<!-- content menu upper line starts here -->
									<h:outputText id="txtMenue" styleClass="free_area0" value="#{lbl.show}"/>
								<!-- content menu upper line ends here -->
								</div>
								<h:panelGroup layout="block" styleClass="free_area0 sub action">
								<!-- content menu lower line starts here -->									
									<!-- Toggle between sets -->
									<h:outputLink styleClass="free_area0" value="#{SessionBean.viewPictureForComparisonLink}" rendered="#{AlbumSession.current.version.objectId == null and Detail.item.type == 'face'}">
										<h:outputText value="#{lbl.picture_for_comparison}"/>
									</h:outputLink>
									<h:outputText styleClass="seperator" rendered="#{AlbumSession.current.latestVersion.objectId == null and Detail.item.type == 'face'}"/>

									<!-- View all pictures of this person -->
									<h:outputLink id="lnkShowAll" styleClass="free_area0" value="#{Navigation.applicationUrl}person/#{Detail.item.mdRecords.screen.mdMap['face.person.identifier'].simpleValue}/#{Navigation.defaultBrowsingKeepShow}" rendered="#{Detail.item.type == 'face' and SessionBean.currentUrl != 'detailsFromAlbum'}">
										<h:outputText value="#{lbl.show_persons_images}"/>
									</h:outputLink>		

									<!--<h:outputText styleClass="seperator" rendered="#{Detail.item.type == 'face'}" />-->
									
									<!-- Add/Remove to current album -->	
									<!--<h:outputLink id="add" styleClass="active" rendered="#{AlbumSession.active.version.objectId != null and !Detail.item.inAlbum}" value="#{Navigation.albumInterfaceUrl}/#{AlbumSession.active.version.objectId}/ADDPICTURE/#{Detail.item.item.objid}">
										<h:outputText value="#{lbl.add}"/>
									</h:outputLink>-->

									<!--<h:outputLink id="lnkRemoveFromAlbum" styleClass="free_area0" rendered="#{(AlbumSession.current.size != '0') and (AlbumSession.current.state == 'PENDING')}" value="#{Navigation.albumInterfaceUrl}/#{AlbumSession.current.latestVersion.objectId}/REMOVEPICTURE/#{Detail.item.item.objid}/back">
										<h:outputText value="Remove from Album"/>
									</h:outputLink>-->

								<!-- content menu lower line ends here -->
								</h:panelGroup>
							<!-- content menu ends here -->
							</div>
							
							<h:panelGroup layout="block" styleClass="subHeader" rendered="#{(SessionBean.urlQuery != 'error' || SessionBean.pageNotFound) and ((SessionBean.message != null) || (SessionBean.information != null))}">
								<!-- Application Events -->
								<jsp:directive.include file="include/Messages.jspf"/>
								&#160;
							</h:panelGroup>
						</div>
					</div>

					<h:panelGroup layout="block" styleClass="full_area0">
						<h:panelGroup layout="block" styleClass="large_area0 small_marginLIncl rangeSelector">&#160;</h:panelGroup>
						<h:panelGroup layout="block" styleClass="small_marginLExcl pageBrowser" rendered="true">
							<h:outputLink id="lnkPaginatorPrevious" type="submit" styleClass="backward" value="#{Detail.previous}" disabled="#{Detail.previous == null}">
								<h:outputText value="#{lbl.paginatorButton_prev_label}"></h:outputText>
							</h:outputLink>

						<h:panelGroup styleClass="seperator" />
							<h:outputLink id="lnkPaginatorNext" type="submit" styleClass="forward" value="#{Detail.next}" disabled="#{Detail.next == null}">
								<h:outputText value="#{lbl.paginatorButton_next_label}"></h:outputText>
							</h:outputLink>
						</h:panelGroup>
					</h:panelGroup>

					<jsp:directive.include file="picturesPages/ui/ActiveAlbumMenu.jspf" />
					
					<h:panelGroup layout="block" styleClass="full_area0 fullItem">
						<div class="small_area0">&#160;</div>
						<jsp:directive.include file="picturesPages/ui/Details_Generic.jspf" />
						<jsp:directive.include file="picturesPages/ui/DetailImage.jspf" />
					</h:panelGroup>
						
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
				</script>				
			</body>
		</html>
	</f:view>
</jsp:root>
