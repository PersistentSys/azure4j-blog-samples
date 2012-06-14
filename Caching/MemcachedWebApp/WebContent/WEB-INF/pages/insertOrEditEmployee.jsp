<%--
Copyright 2012 Persistent Systems Ltd.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Add or Edit</title>
</head>
<body>
	<form action="Employees" method="post" name="frmEmployees">
		
		<c:if test="${not empty employee.empId}">
			Employee ID:<input type="text" name="empId" readonly="readonly" 
			value="<c:out value="${employee.empId}"></c:out>"><br/>
		</c:if>
		
		First Name:<input type="text" name="firstName" 
		value="<c:out value="${employee.firstName}"></c:out>"><br/>
		Last Name:<input type="text" name="lastName" 
		value="<c:out value="${employee.lastName}"></c:out>"><br/>
		Department:<input type="text" name="department" 
		value="<c:out value="${employee.department}"></c:out>"><br/>
		
		<input type="submit" value="Submit">
	</form>
</body>
</html>