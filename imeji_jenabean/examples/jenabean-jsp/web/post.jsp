<%@ include file="header.jsp" %>
<stripes:layout-render name="/layout/default.jsp">

<stripes:layout-component name="html-head">
</stripes:layout-component>

<stripes:layout-component name="contents">
<stripes:form beanclass="action.PostAction" method="post">
<stripes:errors/>
Post Title:<br/>
<stripes:text name="post.title" class="text" size="80"/><br/>
Content:<br/>
<stripes:textarea name="post.content" class="text" rows="15" cols="60" /><br/>
<stripes:submit name="post" value="post" />
<stripes:submit name="addTag" value="add tag" />

<c:forEach items="${actionBean.tags}" var="tag" varStatus="loop">${tag.name} |
  <stripes:hidden name="tags[${loop.index}].name" value="tag.name"/>
  <c:set var="newIndex" value="${loop.index + 1}" scope="page"/> 
</c:forEach>
<stripes:text name="tags[${newIndex}]" class="text" size="15"/>

<stripes:hidden name="post.id"/>
</stripes:form>

</stripes:layout-component>
</stripes:layout-render>