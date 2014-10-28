
<%@ include file="../../../header.jsp" %> 

<link rel="stylesheet" href="<c:url value='/css/about.css' />"/>

<main class="about-content">

	<p><fmt:message key="about.text"/></p>
	
	<a href="http://www.vraptor.org">
		<img id="logo" src="<c:url value='/images/logo.png' />"/>
	</a>
	
	<p class="powered-by"><fmt:message key="about.powered"/>
		<a href="http://www.caelum.com.br">
			<img id="caelum-logo" src="<c:url value='/images/caelum.png' />"/>
		</a>
	</p>
	
	
	<p><fmt:message key="about.available"/> 
		<a href="https://github.com/caelum/vraptor4/tree/master/vraptor-musicjungle">
			<fmt:message key="about.github"/>
		</a>
	</p>

</main>

<%@ include file="../../../footer.jsp" %> 
