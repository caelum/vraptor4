<%@ include file="/header.jsp" %> 

<div class="well well-small">
	<h2><fmt:message key="list_users"/></h2>
</div>

<table class="table table-striped table-bordered table-hover">
	<thead>
		<tr class="info">
			<th></th>
			<th><fmt:message key='user.name'/></th>
			<th><fmt:message key='user.login'/></th>
		</tr>
  	</thead>
  	<tbody>
  	<c:forEach items="${users}" var="user">
		<tr>
			<td>
				<a href="${linkTo[UsersController].show(user)}">
					<fmt:message key="view"/>
				</a>
			</td>
			<td>${user.name}</td>
			<td>${user.login}</td>
		</tr>
	</c:forEach>
	</tbody>
</table>

<%@ include file="/footer.jsp" %> 