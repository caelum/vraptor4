<%@ include file="/header.jsp" %> 

<h1><fmt:message key="search_results"/></h1>

<table class="table table-striped table-bordered table-hover">
	<thead>
		<tr>
			<th>Title</th>
			<th>Description</th>
			<th>Type</th>
			<td>Owners</td>
			<td></td>
			<td></td>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="music" items="${musics}">
			<tr>
				<td><a href="${linkTo[MusicController].show(music)}">${music.title}</a></td>
				<td>${music.description}</td>
				<td><fmt:message key="${music.type}"/></td>
				<td>
					<c:forEach var="musicOwner" items="${music.musicOwners}">
						${musicOwner.owner.name}<br/>
					</c:forEach>
				</td>
				<td width="1px">
					<form action="${linkTo[MusicOwnerController].addToMyList(userInfo.user, music)}" method="post">
						<input type="hidden" name="_method" value="PUT"/>
						<button type="submit" class="btn btn-primary">
							<span class="glyphicon glyphicon-plus"></span>
							<fmt:message key="add_to_my_list"/>
						</button>
					</form>
				</td>
				<td width="1px">
					<a href="${linkTo[MusicController].download(music)}" class="btn btn-primary" download>
						<i class="glyphicon glyphicon-download-alt"></i>
						download
					</a>
			   </td>
			</tr>
		</c:forEach>
	</tbody>
</table>

<%@ include file="/footer.jsp" %> 