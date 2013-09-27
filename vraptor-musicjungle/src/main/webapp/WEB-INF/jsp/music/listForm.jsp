<%@ include file="/header.jsp" %> 


<div class="row">
	<div class="col-lg-4 col-lg-offset-4">
		<form class="form-inline" action="${linkTo[MusicController].listAs}">
			<div class="form-group">
				<select name="_format" class="form-control">
					<option value="xml">XML</option>
					<option value="json">JSON</option>	
				</select>
			</div>	
		
			<button type="submit" class="btn btn-primary">
				<fmt:message key="send"/> 
			</button>
		</form>
	</div>	
</div>	
<br><br>
<%@ include file="/footer.jsp" %> 