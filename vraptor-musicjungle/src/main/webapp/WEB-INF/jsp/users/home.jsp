<%@ include file="/header.jsp" %> 

<link href="<c:url value="/css/bootstrap-fileupload.min.css"/>" rel="stylesheet" type="text/css"/>
<script type="text/javascript" src="<c:url value='/js/bootstrap-fileupload.min.js'/>"></script>

<h1>${userInfo.user.name}: <fmt:message key="your_musics"/></h1>

<div class="well">

	<form action="<c:url value="/musics"/>" class="form form-music" enctype="multipart/form-data" method="post">
		<label for="title"><fmt:message key="music.title"/>
		</label>
			<input type="text" name="music.title" value="${music.title}"/>
		
		<label for="description"><fmt:message key="music.description"/>
		</label>
			<input type="text" name="music.description" value="${music.description}"/>
		
		<label for="type"><fmt:message key="music.type"/>
		</label>
			<select name="music.type" id="type">
				<c:forEach items="${musicTypes}" var="type">
					<option value="${type}"><fmt:message key="${type}"/></option>
				</c:forEach>
			</select>
			
			<div class="fileupload fileupload-new" data-provides="fileupload">
				<label for="upload"><fmt:message key="music.upload"/>
				</label>
  				<span class="btn btn-file">
  					<span class="fileupload-new">Select file</span>
  					<span class="fileupload-exists">Change</span>
  					<input type="file" name="file" />
  				</span>
  				<span class="fileupload-preview"></span>
  				<a href="#" class="close fileupload-exists" data-dismiss="fileupload" style="float: none">×</a>
			</div>
			<button type="submit" class="btn btn-primary pull-left"><fmt:message key="add_music"/></button>
	</form>
</div>

<table class="table table-striped table-bordered table-hover">
	<thead>
		<tr>
			<th>Title</th>
			<th>Description</th>
			<th>Type</th>
			<th>Download</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="music" items="${userInfo.user.musics}" varStatus="s">
			<tr>
				<td><a href="<c:url value="/musics/${music.id}"/>">${music.title}</a></td>
				<td>${music.description}</td>
				<td><fmt:message key="${music.type}"/></td>
				<td><a href="<c:url value="/musics/download/${music.id}"/>">download</a></td>
			</tr>
		</c:forEach>
	</tbody>
</table>

<%@ include file="/footer.jsp" %> 