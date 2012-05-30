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
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Claims</title>
</head>
<body>
	<%@ include file="/WEB-INF/pages/header.jsp"%>
    <jsp:useBean id="userInfoBean" class="com.persistent.azure.acs.UserInfoBean" scope="request"/>
    <p style="margin-left: 27px;font-size: x-large; color: green;"><b>Claims</b></p>
    <p style="margin-left: 54px;color: blue;font-size: large;">
    	SAML token received from ACS is parsed to retrieve claims (email id, name and identity provider).
    	<br/>These claims are displayed in table below.
    </p>
    
    <table border="1" style="padding-left: 0px; border-left-width: 0px; margin-left: 54px; margin-top: 10px;">
		<thead>
			<tr>
				<th>Email Id</th>
				<th>Name</th>
				<th>Identity Provider</th>
			</tr>
		</thead>
		<tbody>
			<tr>
				<td><%= userInfoBean.getEmailAddress() %></td>
				<td><%= userInfoBean.getName() %></td>
				<td><%= userInfoBean.getIdentityProvider() %></td>
			</tr>
		</tbody>
	</table>
</body>
</html>