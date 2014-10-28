<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="author" content="Caelum - Ensino e Inovação"/>
	<meta name="reply-to" content="contato@caelum.com.br"/>
	<meta name="description" content="<fmt:message key="meta.description"/>"/>
	<meta name="keywords" content="sites, web, desenvolvimento, development, java, opensource"/>

	<title>VRaptor Music Jungle</title>
	<link href="<c:url value="/bootstrap/css/bootstrap.min.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/musicjungle.css"/>" rel="stylesheet" type="text/css"/>
	
	<script type="text/javascript" src="<c:url value='/js/jquery.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/bootstrap/js/bootstrap.min.js'/>"></script>	
	
    <!--[if lt IE 7]>
	    <script src="http://ie7-js.googlecode.com/svn/version/2.0(beta3)/IE7.js" 
	    	type="text/javascript"></script>
    <![endif]-->
</head>
<body>
    <c:set var="path"><c:url value="/"/></c:set>

	<c:if test="${not empty param.language}">
		<c:set var="lang" value="${param.language}" scope="session"/>
		<fmt:setLocale value="${param.language}" scope="session"/>
	</c:if>
	
	<div class="navbar navbar-default">
		<div class="navbar-inner">
			<div class="collapse navbar-collapse navbar-ex1-collapse">
				<ul class="nav navbar-nav">
					<li class="active"><a href="${path}"></i> Home</a></li>
					<li><a href="${linkTo[HomeController].about}"></i> About</a></li>
					<li class="active">
						<a href="${path}"> <fmt:message key="menu.home"/> </a>
					</li>
					<li>
						<a href="${path}"> <fmt:message key="menu.about"/> </a>
					</li>
					<li>
						<a href="${linkTo[UsersController].list}"> 
							<fmt:message key="list_users" />
						</a>
					</li>
					<li>
						<a href="${linkTo[MusicController].listForm}"> 
							<fmt:message key="export_all_musics" /> 
						</a>
					</li>
				</ul>
            
				<ul class="nav navbar-nav">
					<li class="divider-vertical"></li>
					<li class="${lang eq 'en' ? 'active' : ''}">
						<a href="?language=en">ENGLISH</a>
					</li>
					<li class="${lang eq 'pt_BR' ? 'active' : ''}">
						<a href="?language=pt_BR">PORTUGUÊS</a>
					</li>
					<li class="divider-vertical"></li>
				</ul>

				<span class="pull-right ${not empty userInfo.user ? '' : 'hidden'}">
					${userInfo.user.name} (<a href="${linkTo[HomeController].logout}"><fmt:message key="logout"/></a>)
				</span>
			</div>
		</div>
	</div>

	<c:if test="${not empty userInfo.user}">
		<div class="navbar navbar-default">
			<form class="navbar-form navbar-right" action="${path}musics/search">
				<div class="form-group">
					<input type="text" class="form-control" name="music.title" placeholder="<fmt:message key="search.music"/>"/>
				</div>	
				<button type="submit" class="btn btn-primary">
					<fmt:message key="search"/>
				</button>
			</form>
		</div>
	</c:if>
    
	<c:if test="${not empty errors}">
		<div class="alert alert-danger">
			<button type="button" class="close" data-dismiss="alert">&times;</button>
			<c:forEach items="${errors}" var="error">
				<b><fmt:message key="${error.category}"/></b> - ${error.message} <br/>
			</c:forEach>
		</div>
	</c:if>
	
	<c:if test="${not empty notice}">
		<div class="alert alert-success"> 
			<button type="button" class="close" data-dismiss="alert">&times;</button>
			${notice} 
		</div>
	</c:if>
	
	<div id="contentWrap">
