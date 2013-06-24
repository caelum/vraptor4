
<%@ include file="../../../header.jsp" %> 

<div class="container pull-right">
	
		<form action="<c:url value="/home/login"/>" method="post" class="form-inline">
		
			<fieldset class="pull-right">
			
				<legend>Sign In</legend>
				
				<input type="text" name="login" 
					placeholder="<fmt:message key="login"/>"/>	
				
				<input type="password" name="password" 
					placeholder="<fmt:message key="password"/>"/>
				
				<button type="submit" id="submit" class="btn btn-primary">
					<fmt:message key="send"/>
				</button>
				
			</fieldset>
			
		</form>
		
		<form action="<c:url value="/users"/>" method="post" class="form-inline">
		
			<fieldset class="pull-right">
			
				<legend>Sign Up</legend>
					
				<input type="text" name="user.name" value="${user.name}" 
					placeholder="<fmt:message key="name"/>"/>
				
				<input type="text" name="user.login" value="${user.login}"
					placeholder="<fmt:message key="login"/>"/>
				
				<input type="password" name="user.password" value="${user.password}"
					placeholder="<fmt:message key="password"/>"/>
				
				<button type="submit" class="btn btn-primary">
					<fmt:message key="send"/>
				</button>
				
			</fieldset>	
		</form>
</div>

<%@ include file="../../../footer.jsp" %> 