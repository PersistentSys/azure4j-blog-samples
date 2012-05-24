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
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="com.microsoft.windowsazure.serviceruntime.RoleEnvironment"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.HashMap"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
	<h2>Hello World!</h2>
<%
   Map<String, String> configSettingsMap = new HashMap<String, String>();
   String jdbcDriver = null;
   String jdbcUrl = null;
   String jdbcUserName = null;
   String jdbcPassword = null;
   //  Check whether the role instance is running in the Windows Azure environment.
   if (RoleEnvironment.isAvailable()) {
       configSettingsMap = RoleEnvironment.getConfigurationSettings();
       jdbcDriver = configSettingsMap.get("jdbc.driverClassName");
       jdbcUrl = configSettingsMap.get("jdbc.url");
       jdbcUserName = configSettingsMap.get("jdbc.username");
       jdbcPassword = configSettingsMap.get("jdbc.password");
  } 

%>
<%  if (!configSettingsMap.isEmpty()) { %>  
       <table border='1'>
          <tr>
             <th>Property</th>
             <th>Value</th>
          </tr>
          <tr>
             <td>jdbc.driverClassName</td>
             <td><%=jdbcDriver%></td>
          </tr>
          <tr>
             <td>jdbc.url</td>
             <td><%=jdbcUrl%></td>
          </tr>
          <tr>
             <td>jdbc.username</td>
             <td><%=jdbcUserName%></td>
         </tr>
         <tr>
            <td>jdbc.password</td>
            <td><%=jdbcPassword %></td>
         </tr>
      </table>
<%	} %>
</body>
</html>