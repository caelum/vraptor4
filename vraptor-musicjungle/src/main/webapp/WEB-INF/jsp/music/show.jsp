<%@ include file="/header.jsp" %> 

<div class="well well-small">
	<h1>${music.title}</h1>
</div>

<p> 
	<strong>
		<fmt:message key="music.description"/>:
	</strong> ${music.description}
<p>

<strong>
	<fmt:message key="music.owners"/>:
</strong>

<c:forEach items="${music.owners}" var="owner" varStatus="s">
	<a href="${linkTo[UsersController].show(owner)}">${owner.name}</a>
	${s.last ? '.' : ', ' }
</c:forEach>

<%@ include file="/footer.jsp" %> 