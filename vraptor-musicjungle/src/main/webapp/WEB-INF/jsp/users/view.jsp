<%@ include file="/header.jsp" %> 

<h1>${user.name}</h1>

<table class="table table-striped table-bordered table-hover">
	<thead>
		<tr>
			<th><fmt:message key="music.title"/></th>
			<th><fmt:message key="music.description"/></th>
			<th><fmt:message key="music.type"/></th>
			<th></th>
			<th></th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="music" items="${user.musics}">
			<tr>
			   <td>${music.title}</td>
			   <td>${music.description}</td>
			   <td><fmt:message key="${music.type}"/></td>
			   <td width="1px">
					<form action="${linkTo[UsersController].addToMyList(userInfo.user, music)}" method="post">
						<input type="hidden" name="_method" value="PUT"/>
						<button type="submit" class="btn btn-primary">
							<span class="glyphicon glyphicon-plus"></span>
							<fmt:message key="add_to_my_list"/>
						</button>
					</form>
			   </td>
			   <td width="1px">
					<a href="${linkTo[MusicController].download(music)}" class="btn btn-primary" download>
						<span class="glyphicon glyphicon-download-alt"></span>
						download
					</a>
			   </td>
		    </tr>
		</c:forEach>
	</tbody>
</table>

<%@ include file="/footer.jsp" %>