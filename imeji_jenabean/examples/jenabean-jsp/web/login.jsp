<%@ include file="header.jsp" %>
<stripes:layout-render name="/layout/default.jsp">
<stripes:layout-component name="contents">
<stripes:form beanclass="action.LoginAction" method="post">
<stripes:errors/>
<label for="fScreenName">Screen name: </label>
<stripes:text name="screenName" id="fScreenName" class="text"/><br/>

<label for="fPassword">Password: </label>
<stripes:password name="password" id="fPassword" class="text"/><br/>
<br/>
<stripes:submit name="login" value="Login"/>
<stripes:link beanclass="action.JoinAction"> or create account...</stripes:link><br/>	
</stripes:form>
</stripes:layout-component>
</stripes:layout-render>
