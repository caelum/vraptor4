<%@ include file="/header.jsp" %> 

<h1>${music.title}</h1>

<p><strong>Description:</strong> ${music.description}</p>

<p><strong>Owners:</strong></p>
<ul>
	<c:forEach items="${music.musicOwners}" var="musicOwner">
		<li>${musicOwners.owner}</li>
	</c:forEach>
</ul>

<%@ include file="/footer.jsp" %> 