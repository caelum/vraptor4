
<%@ include file="../../../header.jsp" %> 

<div class="well login-logo ">
	<span class="logo">VRaptor Music Jungle</span>
	<span class="logo-message">a place to upload, download and share your favorite musics.</span> 
</div>

<div class="container">
		<form action="<c:url value="/home/login"/>" method="post" class="form form-sign">
		<h3>Sign in</h3>
			<input id="login" class="input-block-level" type="text" name="login" placeholder="<fmt:message key="login"/>"/>
			<input id="password" class="input-block-level" type="password" name="password" placeholder="<fmt:message key="password"/>"/>
			<button type="submit" id="submit" class="btn btn-large btn-primary">
				<fmt:message key="send"/>
			</button>
		</form>

		<form action="<c:url value="/users"/>" method="post" class="form form-sign">
		<h3>Sign Up</h3>
			<input type="text" class="input-block-level" id="newname" name="user.name" value="${user.name}" placeholder="<fmt:message key="name"/>"/>
			<input type="text" class="input-block-level" id="newlogin" name="user.login" value="${user.login}" placeholder="<fmt:message key="login"/>"/>
			<input type="password" class="input-block-level" id="newpass" name="user.password" value="${user.password}" placeholder="<fmt:message key="password"/>"/>
			<button type="submit" class="btn btn-large btn-primary">
				<fmt:message key="send"/>
			</button>
		</form>
</div>
<%@ include file="../../../footer.jsp" %> 