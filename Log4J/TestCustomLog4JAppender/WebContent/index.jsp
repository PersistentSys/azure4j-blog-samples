<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>Custom Log4J Appender Example</title>
	</head>
	<body>
	<% out.println(request.getAttribute("result")); %>
	<form name="myform" method="post" action="log.do">
			Message: <input type="text" name="message" value="" />
			<input type="submit" value="Submit" />
	</form>
	</body>
</html>