<%@ include file="/header.jsp" %> 

<form class="form-inline" 
		action="${linkTo[MusicController].listAs}">

	<select name="_format">
		<option value="xml">XML</option>
		<option value="json">JSON</option>	
	</select>

	<button type="submit" class="btn btn-primary">
		<fmt:message key="send"/> 
	</button>
	
</form>

<%@ include file="/footer.jsp" %> 