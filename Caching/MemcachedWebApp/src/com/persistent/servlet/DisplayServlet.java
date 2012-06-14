/*******************************************************************************
 * Copyright 2012 Persistent Systems Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.persistent.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.persistent.model.Employee;
import com.persistent.service.EmployeeService;

public class DisplayServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private EmployeeService employeeService;
	
	public DisplayServlet() {
		employeeService = new EmployeeService();
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String action = request.getParameter("action");
		String forwardToPage = "/WEB-INF/pages/%s.jsp";
		if (action.equalsIgnoreCase("listEmployees")) {
			request.setAttribute("employees", employeeService.getAllEmployees());
			forwardToPage = String.format(forwardToPage, "listEmployees");
		} else if(action.equalsIgnoreCase("updateEmployee")) {
			String strEmpId = request.getParameter("empId");
			int empId = Integer.parseInt(strEmpId);
			Employee employee = employeeService.getEmployeeById(empId);
			request.setAttribute("employee", employee);
			forwardToPage = String.format(forwardToPage, "insertOrEditEmployee");
		} else if(action.equalsIgnoreCase("deleteEmployee")) {
			String strEmpId = request.getParameter("empId");
			int empId = Integer.parseInt(strEmpId);
			employeeService.deleteEmployee(empId);
			request.setAttribute("employees", employeeService.getAllEmployees());
			forwardToPage = String.format(forwardToPage, "listEmployees");
		} else {
			forwardToPage = String.format(forwardToPage, "insertOrEditEmployee");
		}
		RequestDispatcher reqDispatcher = request.getRequestDispatcher(forwardToPage);
		reqDispatcher.forward(request, response);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String empId = request.getParameter("empId");
		
		Employee employee = new Employee();
		employee.setFirstName(request.getParameter("firstName"));
		employee.setLastName(request.getParameter("lastName"));
		employee.setDepartment(request.getParameter("department"));
		
		if (empId == null || empId.isEmpty()) {
			employeeService.addEmployee(employee);
		} else {
			employee.setEmpId(
					Integer.parseInt(empId));
			employeeService.updateEmployee(employee);
		}
		
		String forwardToPage = "/WEB-INF/pages/%s.jsp";
		request.setAttribute("employees", employeeService.getAllEmployees());
		forwardToPage = String.format(forwardToPage, "listEmployees");
		
		RequestDispatcher reqDispatcher = request.getRequestDispatcher(forwardToPage);
		reqDispatcher.forward(request, response);
	}
}
