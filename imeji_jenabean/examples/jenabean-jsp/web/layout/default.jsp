<%@ include file="../header.jsp" %>
<stripes:layout-definition>
  <html>
    <head>
      <title>Jenabean Blog</title>
      <link rel="stylesheet"
            type="text/css"
            href="${pageContext.request.contextPath}/style/default.css"/>
      <stripes:layout-component name="html-head"/>     
    </head>

    <body>
	<div id="page">
    <div id="header">
	<div style="float:left"><h1><a href="${pageContext.request.contextPath}/">Jenabean Blog</a></h1></div>
	<div style="float:right">
	<c:choose>
    <c:when test="${not empty login}">
    logged in as ${login.screenName} | 
    <stripes:link beanclass="action.PostAction">new post</stripes:link> |
    <stripes:link beanclass="action.LogoutAction">logout</stripes:link>
    <a href="http://code.google.com/p/jenabean">Jena bean home</a>
    </c:when>
    <c:when test="${empty login}">
    <stripes:link beanclass="action.LoginAction">sign in</stripes:link>
    </c:when>
    </c:choose>   
	</div>
    <div class="br"> </div>
    </div>
    <div id="content">
    <stripes:layout-component name="contents"/>
    </div>
    </div>
    </body>
  </html>
</stripes:layout-definition>

