<%@ include file="header.jsp" %>
<stripes:layout-render name="/layout/default.jsp">
<stripes:layout-component name="contents">

<stripes:form beanclass="action.JoinAction" method="post">
<stripes:errors/>
<label for="fScreenName">Screen name:</label>
<stripes:text name="screenName" id="fScreenName" class="text"/><br/>
<label for="fEmail">Email:</label>
<stripes:text name="email" id="fEmail" class="text"/><br/>
<label for="fPassword">Password:</label>
<stripes:password name="password" id="fPassword" class="text"/><br/>
<label for="fVerify">Verify:</label>
<stripes:password name="verify" id="fVerify" class="text"/><br/>
<label> </label><stripes:submit name="join" value="Join"/><br/>
</stripes:form>

</stripes:layout-component>
</stripes:layout-render>
