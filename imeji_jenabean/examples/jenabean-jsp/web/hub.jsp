<%@ include file="header.jsp" %>
<stripes:layout-render name="/layout/default.jsp">
<stripes:layout-component name="contents">

<c:forEach items="${actionBean.posts}" var="row" varStatus="loop">

<div class="entry">
<h2><a href="${pageContext.request.contextPath}/blog/home/${row.id}">${row.title}</a></h2> 
<span>${row.createdAt} : </span>
${row.content}<br/>
- ${row.author.screenName} ${row.commentsCount} comments | Filed Under:
<c:forEach items="${row.tags}" var="tag" varStatus="loop"><c:if test="${!loop.first}">, </c:if>${tag.name}</c:forEach>
<br/>
<c:if test="${! empty(actionBean.p)}">
<c:forEach items="${row.comments}" var="comment">${comment.content}<br/></c:forEach>
</c:if>
</div>
<c:if test="${! empty(actionBean.p)}">
<stripes:form beanclass="action.CommentAction" method="post">
Comments:<br/>
<stripes:textarea name="comment.content" rows="5" cols="60"/><br/>
<stripes:hidden name="p" value="${row.id}"/>
<stripes:submit name="comment" value="comment"/>
</stripes:form>
</c:if>
</c:forEach>


</stripes:layout-component>
</stripes:layout-render>
