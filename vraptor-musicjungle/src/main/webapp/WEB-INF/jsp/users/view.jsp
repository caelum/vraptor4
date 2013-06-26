<%@ include file="/header.jsp" %> 

<h1>${user.name}</h1>

<table class="table table-striped table-bordered table-hover">
	<thead>
		<tr>
			<th>Title</th>
			<th>Description</th>
			<th>Type</th>
			<th></th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="music" items="${user.musics}">
			<tr>
			   <td>${music.title}</td>
			   <td>${music.description}</td>
			   <td><fmt:message key="${music.type}"/></td>
			   <td class="td-options">
					<form action="<c:url value="/users/${userInfo
							.user.login}/musics/${music.id}"/>" method="post">
							
						<button type="submit" class="btn btn-primary">
							<input type="hidden" name="_method" value="PUT"/>
							<span class="icon icon-plus icon-white"></span>
							<fmt:message key="add_to_my_list"/>
						</button>
					</form>
					
					<button class="btn btn-primary">
						<span class="icon icon-download-alt icon-white"></span>
						download
					</button>
			   </td>
		    </tr>
		</c:forEach>
	</tbody>
</table>

<%@ include file="/footer.jsp" %>