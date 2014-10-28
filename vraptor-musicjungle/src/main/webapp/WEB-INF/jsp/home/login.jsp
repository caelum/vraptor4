
<%@ include file="../../../header.jsp" %> 

<div class="well login-logo">
	<span class="logo">VRaptor Music Jungle</span>
	<span class="logo-message"><fmt:message key="project_description"/></span> 
</div>

<div class="container">
	<div class="row">
		<div class="col-lg-4 col-lg-offset-4">
			<h3>Sign in</h3>
			<form action="${linkTo[HomeController].login}" method="post" class="form-horizontal">
				<div class="form-group">
					<label class="sr-only" for="login"><fmt:message key="user.login"/></label>
					<input type="text" class="form-control" id="login" name="login" placeholder="<fmt:message key="user.login"/>"/>
				</div>
				<div class="form-group">
					<label class="sr-only" for="password"><fmt:message key="user.password"/></label>
					<input type="password" class="form-control" name="password" placeholder="<fmt:message key="user.password"/>" />
					<span class="no-emphasis"><fmt:message key="user.default"/></span>
				</div>
				<div class="form-group pull-right">	
					<button type="submit" id="submit" class="btn btn-large btn-primary">
						<fmt:message key="send"/>
					</button>
				</div>	
			</form>
		</div>	
	</div>
	<div class="row">
		<div class="col-lg-4 col-lg-offset-4">
			<h3>Sign Up</h3>
			<form action="${linkTo[UsersController].add}" method="post" class="form-horizontal">
				<div class="form-group">
					<label class="sr-only" for="login"><fmt:message key="user.login"/></label>
					<input type="text" class="form-control" id="newname" name="user.name" value="${user.name}" placeholder="<fmt:message key="user.name"/>" />
				</div>
				<div class="form-group">
					<label class="sr-only" for="login"><fmt:message key="user.login"/></label>
					<input type="text" class="form-control" id="newlogin" name="user.login" value="${user.login}" placeholder="<fmt:message key="user.login"/>" />
				</div>
				<div class="form-group">
					<label class="sr-only" for="password"><fmt:message key="user.password"/></label>
					<input type="password" class="form-control" name="user.password" value="${user.password}" placeholder="<fmt:message key="user.password"/>" />
				</div>
				<div class="form-group pull-right">					
					<button type="submit" class="btn btn-large btn-primary">
						<fmt:message key="send"/>
					</button>
				</div>
			</form>
		</div>
	</div>		
</div>
<%@ include file="../../../footer.jsp" %> 