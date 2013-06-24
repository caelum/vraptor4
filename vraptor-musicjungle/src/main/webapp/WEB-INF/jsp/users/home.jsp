<%@ include file="/header.jsp" %> 

<link href="<c:url value="/css/bootstrap-fileupload.min.css"/>" rel="stylesheet" type="text/css"/>
<script type="text/javascript" src="<c:url value='/js/bootstrap-fileupload.js'/>"></script>

<h1>${userInfo.user.name}: <fmt:message key="your_dvds"/></h1>

<div class="well">

	<form action="<c:url value="/dvds"/>" class="form-inline pull-right" method="post">
		<label for="title"><fmt:message key="dvd.title"/>
			<input type="text" name="dvd.title" value="${dvd.title}"/>
		</label>
		
		<label for="description"><fmt:message key="dvd.description"/>
			<input type="text" name="dvd.description" value="${dvd.description}"/>
		</label>
		
		<label for="type"><fmt:message key="dvd.type"/>
			<select name="dvd.type" id="type">
				<c:forEach items="${dvdTypes}" var="type">
					<option value="${type}"><fmt:message key="${type}"/></option>
				</c:forEach>
			</select>
		</label>
		
		<div class="fileupload fileupload-new div-inline" data-provides="fileupload">
			<div class="input-append">
				<div class="uneditable-input span3 div-inline">
					<i class="icon-file fileupload-exists"></i> 
					<span class="fileupload-preview"></span>
				</div><span class="btn btn-file">
				<span class="fileupload-new">Select file</span>
				<span class="fileupload-exists">Change</span>
				<input type="file" /></span>
				<a href="#" class="btn fileupload-exists" data-dismiss="fileupload">Remove</a>
			</div>
		</div>
					
		<button type="submit" class="btn btn-primary"><fmt:message key="add_music"/></button>
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
		<c:forEach var="dvd" items="${userInfo.user.dvds}" varStatus="s">
			<tr>
				<td>${dvd.title}</td>
				<td>${dvd.description}</td>
				<td><fmt:message key="${dvd.type}"/></td>
				<td></td>
			</tr>
		</c:forEach>
	</tbody>
</table>

<%@ include file="/footer.jsp" %> 