<%@ include file="/header.jsp" %> 

<link href="<c:url value="/css/bootstrap-fileupload.min.css"/>" rel="stylesheet" type="text/css"/>
<script type="text/javascript" src="<c:url value="/js/bootstrap-fileupload.min.js"/>"></script>

<h1>${userInfo.user.name}: <fmt:message key="your_musics"/></h1>

<div class="well">
	<div class="row">
		<div class="col-lg-4 col-lg-offset-4">
			<form action="${linkTo[MusicController].add}" class="form-horizontal" enctype="multipart/form-data" method="post">
				<div class="form-group">
					<label for="title" class="col-lg-2 control-label"><fmt:message key="music.title"/></label>
					<div class="col-lg-10">
						<input type="text" class="form-control" name="music.title" value="${music.title}"/>
					</div>	
				</div>
				<div class="form-group">
					<label for="description" class="col-lg-2 control-label"><fmt:message key="music.description"/></label>
					<div class="col-lg-10">
						<input type="text" class="form-control" name="music.description" value="${music.description}"/>
					</div>	
				</div>
				<div class="form-group">
					<label for="type" class="col-lg-2 control-label"><fmt:message key="music.type"/></label>
					<div class="col-lg-10">
						<select class="form-control" name="music.type" id="type">
							<c:forEach items="${musicTypes}" var="type">
								<option value="${type}"><fmt:message key="${type}"/></option>
							</c:forEach>
						</select>
					</div>	
				</div>
				<div class="form-group">
					<label for="upload" class="col-lg-2 control-label"><fmt:message key="music.upload"/></label>
					<div class="fileupload fileupload-new" data-provides="fileupload">
		  				<span class="btn btn-file">
		  					<span class="fileupload-new">Select file</span>
		  					<span class="fileupload-exists">Change</span>
		  					<input type="file" name="file" />
		  				</span>
		  				<span class="fileupload-preview"></span>
		  				<a href="#" class="close fileupload-exists" data-dismiss="fileupload" style="float: none">&times;</a>
					</div>
				</div>
				<div class="form-group pull-right">	
					<button type="submit" class="btn btn-primary"><fmt:message key="add_music"/></button>
				</div>	
			</form>
		</div>
	</div>		
</div>

<table class="table table-striped table-bordered table-hover">
	<thead>
		<tr>
			<th><fmt:message key="music.title"/></th>
			<th><fmt:message key="music.description"/></th>
			<th><fmt:message key="music.type"/></th>
			<td><fmt:message key="music.download"/></td>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="music" items="${userInfo.user.musics}" varStatus="s">
			<tr>
				<td><a href="${linkTo[MusicController].show(music)}">${music.title}</a></td>
				<td>${music.description}</td>
				<td><fmt:message key="${music.type}"/></td>
				<td><a href="${linkTo[MusicController].download(music)}" download>download</a></td>
			</tr>
		</c:forEach>
	</tbody>
</table>

<%@ include file="/footer.jsp" %> 