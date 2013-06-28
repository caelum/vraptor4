<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<html xmlns="http://www.w3.org/1999/xhtml">
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

	<fmt:setLocale value="${locale}"/>

	<c:if test="${not empty param.language}">
		<fmt:setLocale value="${param.language}" scope="session"/>
	</c:if>
	
	<div class="navbar">
	
      <div class="navbar-inner">
      
        <div class="container">
        
            <ul class="nav">
              <li class="active"><a href="${path}"></i> Home</a></li>
              <li><a href="${path}"></i> About</a></li>
              <li>
              	<a href="${linkTo[UsersController].list}"> 
              		<fmt:message key="list_users" />
				</a>
			  </li>
            </ul>
            
             <ul class="nav">
              <li class="divider-vertical"></li>
            	<li class="active"><a href="?language=en">ENGLISH</a></li>
                <li><a href="?language=pt_BR">PORTUGUÊS</a></li>
				<li class="divider-vertical"></li>
            </ul>
            
            <span class="pull-right ${not empty userInfo.user ? '' : 'hidden'}">
            	${userInfo.user.name} (<a href="${linkTo[HomeController].logout}">Logout</a>)
            </span>
            
        </div>
      </div>
    </div>
	
    
    <c:if test="${not empty userInfo.user}">
    
    	<div class="navbar">
    	
			<form class="navbar-form pull-right" action="${path}musics/search">
			  <input type="text" name="music.title" class="span6"
			  		placeholder="<fmt:message key="search.music"/>"/>
			  <button type="submit" class="btn btn-primary">
			  		<fmt:message key="search"/> </button>
			</form>
			
	    </div>
	    
    </c:if>
    
	<c:if test="${not empty errors}">
		<div class="alert alert-error">
			<button type="button" class="close" data-dismiss="alert">&times;</button>
			<c:forEach items="${errors }" var="error">
				<b>${error.category}</b> - ${error.message}
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