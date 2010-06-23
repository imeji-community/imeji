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

			</head>
			<body lang="#{SessionBean.locale}">
				<h:outputText value="#{SessionBean.checkLogin}" styleClass="noDisplay" />
				<h:form id="form1">
					<div class="full wrapper">
						<h:inputHidden id="offset"></h:inputHidden>
						<h:inputHidden id="rssFeedUrl" value="#{BlogBean.rssFeedUrl}"></h:inputHidden>
		
						<div id="content" class="full_area0 clear">
						<!-- begin: content section (including elements that visualy belong to the header (breadcrumb, headline, subheader and content menu)) -->
							
							<div class="full_area0_p8">
								
										<h:panelGroup rendered="#{SessionBean.locale.language == 'en'}">
											<h:outputText  escape="false" value="#{StaticContent.helpContent}"></h:outputText>
										</h:panelGroup>
										<h:panelGroup rendered="#{SessionBean.locale.language == 'de'}">
											<h:outputText escape="false" value="#{StaticContent.helpContent}"></h:outputText>
										</h:panelGroup>
									
							</div>
						</div>
					</div>
				</h:form>
				<!-- end: content section -->		
			</body>
		</html>
	</f:view>
</jsp:root>