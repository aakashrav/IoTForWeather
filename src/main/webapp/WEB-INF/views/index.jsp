<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html lang="en" class="no-js">
	<head>
		<meta charset="UTF-8" />
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"> 
		<meta name="viewport" content="width=device-width, initial-scale=1.0"> 
		<title>Sticky Table Headers Revisited | Demo 1</title>
		<meta name="description" content="Sticky Table Headers Revisited: Creating functional and flexible sticky table headers" />
		<meta name="keywords" content="Sticky Table Headers Revisited" />
		<meta name="author" content="Codrops" />
		
		<link rel="shortcut icon" href="../favicon.ico">
		
		<link href="<c:url value="/resources/css/normalize.css" />" rel="stylesheet">
		<link href="<c:url value="/resources/css/demo.css" />" rel="stylesheet">
		<link href="<c:url value="/resources/css/component.css" />" rel="stylesheet">
		<!--[if IE]>
  		<script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script>
		<![endif]-->
	</head>
	<body>
		<div class="container">
			<!-- Top Navigation -->
			<div class="codrops-top clearfix">
				<a class="codrops-icon codrops-icon-prev" href="http://tympanus.net/Tutorials/ShapeHoverEffectSVG/"><span>Previous Demo</span></a>
				<span class="right"><a class="codrops-icon codrops-icon-drop" href="http://tympanus.net/codrops/?p=18116"><span>Back to the Codrops Article</span></a></span>
			</div>
			<header>
				<h1>Sticky Table Headers <em>Revisited</em> <span>Creating functional and flexible sticky table headers</span></h1>	
				<nav class="codrops-demos">
					<a class="current-demo" href="index.html" title="Basic Usage">Basic Usage</a>
					<a href="index2.html" title="Biaxial Headers">Biaxial Headers</a>
					<a href="index3.html" title="Wide Tables">Wide Tables</a>
				</nav>
			</header>
			<div class="component">
				<h2>Basic usage</h2>
				<p>This is a basic usage example. Scroll down to see the sticky table header in action. And of course, multiple instances are supported. Tables are pre-populated with random user data retrieved from the <a href="http://randomuser.me/" title="Random User Generator">Random User Generator</a>.</p>
				<table>
					<thead>
						<tr>
							<th>Lat</th>
							<th>Lon</th>
							<th>MAC Address</th>
						</tr>
					</thead>
					<tbody>
					<c:forEach var="current" items="${deviceDataArray}" >
							<tr>
							    <li>
                                	<tr><td class="user-name">${current.getLat()}</td><td class="user-email">${current.getLon()}</td><td class="user-phone">${current.getMac()}</td></tr>
                            	</li>
							</tr>
					</c:forEach>
				</table>
				
			</div>
		</div><!-- /container -->
		
		<script src="http://ajax.googleapis.com/ajax/libs/jquery/1/jquery.min.js"></script>
		<script src="http://cdnjs.cloudflare.com/ajax/libs/jquery-throttle-debounce/1.1/jquery.ba-throttle-debounce.min.js"></script>
		<script src="<c:url value="/resources/js/jquery.stickyheader.js" />"></script>
	</body>
</html>