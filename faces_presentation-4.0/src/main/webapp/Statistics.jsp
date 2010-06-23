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
							
										<!--<jsp:directive.include file="headerPages/Breadcrumb.jspf" />-->
				
										<div id="contentSkipLinkAnchor" class="clear headLine">
											<!-- Headline starts here -->
											<h1>
												<h:outputText value="#{msg.statistic_00_headline}"/>
											</h1>
											<!-- Headline ends here -->
										</div>
									</div>
									<div class="small_marginLIncl subHeaderSection">
										<div class="contentMenu">
											<!-- content menu starts here -->
											<div class="free_area0 sub"> 
												<!-- content menu upper line starts here -->
												&#160;
												<!-- content menu upper line ends here -->
											</div>
											<h:panelGroup layout="block" styleClass="free_area0 sub action" >
												<!-- content menu lower line starts here -->
													&#160;
												<!-- content menu lower line ends here -->
											</h:panelGroup>
										<!-- content menu ends here -->
										
										</div>

										<h:panelGroup layout="block" styleClass="subHeader">
											<h:outputText style="font-style: normal;" value="#{msg.statistics_info}" />
										</h:panelGroup>

										<h:panelGroup layout="block" styleClass="subHeader" rendered="#{(SessionBean.urlQuery != 'error' || SessionBean.pageNotFound) and ((SessionBean.message != null) || (SessionBean.information != null))}">
											<!-- Application Events -->
											<jsp:directive.include file="include/Messages.jspf"/>
										</h:panelGroup>
									</div>
							</div>
							
							<h:panelGroup styleClass="full_area0" layout="block" rendered="#{!SessionBean.admin}">
								<h:outputText value="Only administrator are allowed to see this page." />
							</h:panelGroup>

							<h:panelGroup layout="block" styleClass="full_area0 fullItem" rendered="#{SessionBean.admin}">
								<div class="full_area0 fullItemControls">
									<span class="full_area0_p5">
										<b class="free_area0 small_marginLExcl">&#160;</b>
										<h:panelGroup styleClass="seperator" />
										<h:outputLink styleClass="free_area0" id="lnkStatisticImages" value="#{Navigation.statisticsUrl}/images" rendered="#{StatisticsBean.statisticsType != 'images'}">
											<h:outputText value="#{lbl.statistics_pictures}"></h:outputText>
										</h:outputLink>
										<h:outputLink styleClass="free_area0 actual" id="lnkStatisticImagesActive" value="#{Navigation.statisticsUrl}/images" rendered="#{StatisticsBean.statisticsType == 'images'}">
											<h:outputText value="#{lbl.statistics_pictures}"></h:outputText>
										</h:outputLink>
										<h:panelGroup styleClass="seperator" />
										<h:outputLink styleClass="free_area0" id="lnkStatisticExports" value="#{Navigation.statisticsUrl}/export" rendered="#{StatisticsBean.statisticsType != 'export'}">
											<h:outputText value="#{lbl.statistics_export}"></h:outputText>
										</h:outputLink>
										<h:outputLink styleClass="free_area0 actual" id="lnkStatisticExportsActive" value="#{Navigation.statisticsUrl}/export" rendered="#{StatisticsBean.statisticsType == 'export'}">
											<h:outputText value="#{lbl.statistics_export}"></h:outputText>
										</h:outputLink>
										<h:panelGroup styleClass="seperator" />
										<h:outputLink styleClass="free_area0" id="lnkStatisticLogins" value="#{Navigation.statisticsUrl}/login" rendered="#{StatisticsBean.statisticsType != 'login'}">
											<h:outputText value="#{lbl.statistics_login}"></h:outputText>
										</h:outputLink>
										<h:outputLink styleClass="free_area0 actual" id="lnkStatisticLoginsActive" value="#{Navigation.statisticsUrl}/login" rendered="#{StatisticsBean.statisticsType == 'login'}">
											<h:outputText value="#{lbl.statistics_login}"></h:outputText>
										</h:outputLink>
										<h:panelGroup styleClass="seperator" />
										<h:outputLink styleClass="free_area0" id="lnkStatisticVisits" value="#{Navigation.statisticsUrl}/visit" rendered="#{StatisticsBean.statisticsType != 'visit'}">
											<h:outputText value="#{lbl.statistics_visit}"></h:outputText>
										</h:outputLink>
										<h:outputLink styleClass="free_area0 actual" id="lnkStatisticVisitsActive" value="#{Navigation.statisticsUrl}/visit" rendered="#{StatisticsBean.statisticsType == 'visit'}">
											<h:outputText value="#{lbl.statistics_visit}"></h:outputText>
										</h:outputLink>
										<h:panelGroup styleClass="seperator" />
									</span>
								</div>
								<h:panelGroup layout="block" styleClass="full_area0 fullItem" rendered="#{SessionBean.admin}">
									<h:outputLabel styleClass="large_label" style="text-align:center">
										<h:outputText value="#{lbl.statistics_from}" />
									</h:outputLabel>
									<h:selectOneMenu id="selMonth" value="#{StatisticsBean.firstMonth}" valueChangeListener="#{StatisticsBean.monthListener}" styleClass="small_select" immediate="true">
										<f:selectItem itemLabel="1" itemValue="1"/>
										<f:selectItem itemLabel="2" itemValue="2"/>
										<f:selectItem itemLabel="3" itemValue="3"/>
										<f:selectItem itemLabel="4" itemValue="4"/>
										<f:selectItem itemLabel="5" itemValue="5"/>
										<f:selectItem itemLabel="6" itemValue="6"/>
										<f:selectItem itemLabel="7" itemValue="7"/>
										<f:selectItem itemLabel="8" itemValue="8"/>
										<f:selectItem itemLabel="9" itemValue="9"/>
										<f:selectItem itemLabel="10" itemValue="10"/>
										<f:selectItem itemLabel="11" itemValue="11"/>
										<f:selectItem itemLabel="12" itemValue="12"/>
									</h:selectOneMenu>
									<h:outputLabel styleClass="xTiny_label" style="text-align:center">
										<h:outputText value="  /  " />
									</h:outputLabel>
									<h:inputText id="inputYear" maxlength="4" value="#{StatisticsBean.firstYear}" valueChangeListener="#{StatisticsBean.yearListener}"  styleClass="small_txtInput" immediate="true" ></h:inputText>
									<h:outputLabel styleClass="tiny_label" style="text-align:center">
										<h:outputText value=" " />
									</h:outputLabel>
									<h:commandLink id="cmdDate" rendered="true"  value="  #{lbl.change}" action="#{StatisticsBean.changeDate}" immediate="true" styleClass="free_area0">
									</h:commandLink>
								</h:panelGroup>
								<h:panelGroup layout="block" styleClass="full_area0 itemBlock" rendered="#{StatisticsBean.statisticsType == 'visit'}">
									<h3 class="xLarge_area0_p8 endline blockHeader">
										&#160;<h:outputText value="#{lbl.statistics_visit} #{StatisticsBean.statisticsNumberOfVisits}" />
									</h3>
									<h:panelGroup styleClass="seperator"></h:panelGroup>
									<h:panelGroup layout="block" styleClass="free_area0 itemBlockContent endline">
										<h:panelGroup layout="block" styleClass="free_area0 endline itemLine noTopBorder">
											<b class="xLarge_area0_p8 endline labelLine clear">
												&#160;<h:panelGroup styleClass="noDisplay"> </h:panelGroup>
											</b>
											<h:panelGroup styleClass="xHuge_area0 xTiny_marginLExcl endline">
												<h:outputText value="#{msg.statistics_description_visits}" />
											</h:panelGroup>
										</h:panelGroup>
										<h:panelGroup layout="block" styleClass="free_area0 endline itemLine noTopBorder">
											<h:panelGroup styleClass="xLarge_area0 endline labelLine clear">
												<b class="xLarge_area0">
													&#160;<h:outputText value="#{lbl.visits} per #{lbl.month}" /><h:panelGroup styleClass="noDisplay"> </h:panelGroup>
												</b>
												<tr:iterator id="iteratorStatisticVisits" value="#{StatisticsBean.visitTable.list}" var="stat">
													<h:panelGroup styleClass="xLarge_area0">
														<h:panelGroup styleClass="medium_area0 labelLine endline">
															<h:outputText id="lblStatisticVisitsRequest" value="#{stat.requests}"/>
														</h:panelGroup>
														<h:panelGroup styleClass="small_area0">
															<h:outputText value="&#160;(#{stat.date})"/>
														</h:panelGroup>
													</h:panelGroup>
												</tr:iterator>
											</h:panelGroup>
											<h:panelGroup styleClass="free_area0 xTiny_marginLExcl endline">
												<tr:chart type="verticalBar" YMajorGridLineCount="1" value="#{StatisticsBean.numberOfVisitCharts}" inlineStyle="width:540px; height:400px;" legendPosition="none" perspective="false" animationDuration="200"></tr:chart>
											</h:panelGroup>
										</h:panelGroup>
									</h:panelGroup>
								</h:panelGroup>
								<h:panelGroup layout="block" styleClass="full_area0 itemBlock" rendered="#{StatisticsBean.statisticsType == 'login'}">
									<h3 class="xLarge_area0_p8 endline blockHeader">
										&#160;<h:outputText value="#{lbl.statistics_login} #{StatisticsBean.statisticsNumberOfLogin}" />
									</h3>
									<h:panelGroup styleClass="seperator"></h:panelGroup>
									<h:panelGroup layout="block" styleClass="free_area0 itemBlockContent endline">
										<h:panelGroup layout="block" styleClass="free_area0 endline itemLine noTopBorder">
											<b class="xLarge_area0_p8 endline labelLine clear">
												&#160;<h:panelGroup styleClass="noDisplay"> </h:panelGroup>
											</b>
											<h:panelGroup styleClass="xHuge_area0 xTiny_marginLExcl endline">
												<h:outputText value="#{msg.statistics_description_login}" />
											</h:panelGroup>
										</h:panelGroup>
										<h:panelGroup layout="block" styleClass="free_area0 endline itemLine noTopBorder">
											<h:panelGroup styleClass="xLarge_area0 endline labelLine clear">
												<b class="xLarge_area0">
													&#160;<h:outputText value="#{lbl.login_statistics} per #{lbl.month}" /><h:panelGroup styleClass="noDisplay"> </h:panelGroup>
												</b>
												<tr:iterator id="iteratorStatisticLogins" value="#{StatisticsBean.loginTable.list}" var="stat">
													<h:panelGroup styleClass="xLarge_area0">
														<h:panelGroup styleClass="medium_area0 labelLine endline">
															<h:outputText id="lblStatisticLoginsRequest" value="#{stat.requests}"/>
														</h:panelGroup>
														<h:panelGroup styleClass="small_area0">
															<h:outputText value="&#160;(#{stat.date})"/>
														</h:panelGroup>
													</h:panelGroup>
												</tr:iterator>
											</h:panelGroup>
											<h:panelGroup styleClass="free_area0 xTiny_marginLExcl endline">
												<tr:chart type="verticalBar" YMajorGridLineCount="1" value="#{StatisticsBean.numberOfLoginCharts}" inlineStyle="width:540px; height:400px;" legendPosition="none" perspective="false" animationDuration="200"></tr:chart>
											</h:panelGroup>
										</h:panelGroup>
									</h:panelGroup>
								</h:panelGroup>
								<h:panelGroup layout="block" styleClass="full_area0 itemBlock" rendered="#{StatisticsBean.statisticsType == 'export'}">
									<h3 class="xLarge_area0_p8 endline blockHeader">
										&#160;<h:outputText value="#{lbl.statistics_export}#{StatisticsBean.statisticsNumberOfExport}" />
									</h3>
									<h:panelGroup styleClass="seperator"></h:panelGroup>
									<h:panelGroup layout="block" styleClass="free_area0 itemBlockContent endline">
										<h:panelGroup layout="block" styleClass="free_area0 endline itemLine noTopBorder">
											<b class="xLarge_area0_p8 endline labelLine clear">
												&#160;<h:panelGroup styleClass="noDisplay"> </h:panelGroup>
											</b>
											<h:panelGroup styleClass="xHuge_area0 xTiny_marginLExcl endline">
												<h:outputText value="#{msg.statistics_description_download}" />
											</h:panelGroup>
										</h:panelGroup>
										<h:panelGroup layout="block" styleClass="free_area0 endline itemLine noTopBorder">
											<h:panelGroup styleClass="xLarge_area0 endline labelLine clear">
												<b class="xLarge_area0">
													&#160;<h:outputText value="#{lbl.exports} per #{lbl.month}" /><h:panelGroup styleClass="noDisplay"> </h:panelGroup>
												</b>
												<tr:iterator id="iteratorStatisticDownloads" value="#{StatisticsBean.exportTable.list}" var="stat">
													<h:panelGroup styleClass="xLarge_area0">
														<h:panelGroup styleClass="medium_area0 labelLine endline">
															<h:outputText id="lblStatisticLoginsRequest" value="#{stat.requests}"/>
														</h:panelGroup>
														<h:panelGroup styleClass="small_area0">
															<h:outputText value="&#160;(#{stat.date})"/>
														</h:panelGroup>
													</h:panelGroup>
												</tr:iterator>
											</h:panelGroup>
											<h:panelGroup styleClass="free_area0 xTiny_marginLExcl endline">
												<tr:chart type="verticalBar" YMajorGridLineCount="1" value="#{StatisticsBean.numberOfExportCharts}" inlineStyle="width:540px; height:400px;" legendPosition="none" perspective="false" animationDuration="200"></tr:chart>
											</h:panelGroup>
										</h:panelGroup>
									</h:panelGroup>
								</h:panelGroup>
								<h:panelGroup layout="block" styleClass="full_area0 itemBlock" rendered="#{StatisticsBean.statisticsType == 'images'}">
									<h3 class="xLarge_area0_p8 endline blockHeader">
										&#160;<h:outputText value="#{lbl.statistics_pictures} #{StatisticsBean.statisticsNumberOfImagesExported}" />
									</h3>
									<h:panelGroup styleClass="seperator"></h:panelGroup>
									<h:panelGroup layout="block" styleClass="free_area0 itemBlockContent endline">
										<h:panelGroup layout="block" styleClass="free_area0 endline itemLine noTopBorder">
											<b class="xLarge_area0_p8 endline labelLine clear">
												&#160;<h:panelGroup styleClass="noDisplay"> </h:panelGroup>
											</b>
											<h:panelGroup styleClass="xHuge_area0 xTiny_marginLExcl endline">
												<h:outputText value="#{msg.statistics_description_images}" />
											</h:panelGroup>
										</h:panelGroup>
										<h:panelGroup layout="block" styleClass="free_area0 endline itemLine noTopBorder">
											<h:panelGroup styleClass="xLarge_area0 endline labelLine clear">
												<b class="xLarge_area0">
													&#160;<h:outputText value="#{lbl.exported_pictures} per #{lbl.month}" /><h:panelGroup styleClass="noDisplay"> </h:panelGroup>
												</b>
												<tr:iterator id="iteratorStatisticImages" value="#{StatisticsBean.imagesTable.list}" var="stat">
													<h:panelGroup styleClass="xLarge_area0">
														<h:panelGroup styleClass="medium_area0 labelLine endline">
															<h:outputText id="lblStatisticLoginsRequest" value="#{stat.requests}"/>
														</h:panelGroup>
														<h:panelGroup styleClass="small_area0">
															<h:outputText value="&#160;(#{stat.date})"/>
														</h:panelGroup>
													</h:panelGroup>
												</tr:iterator>
											</h:panelGroup>
											<h:panelGroup styleClass="free_area0 xTiny_marginLExcl endline">
												<tr:chart type="verticalBar" YMajorGridLineCount="1" value="#{StatisticsBean.numberOfImagesExportedCharts}" inlineStyle="width:540px; height:400px;" legendPosition="none" perspective="false" animationDuration="200"></tr:chart>
											</h:panelGroup>
										</h:panelGroup>
									</h:panelGroup>
								</h:panelGroup>
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