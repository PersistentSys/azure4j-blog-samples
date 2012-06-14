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
package com.persistent.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.persistent.model.Employee;
import com.persistent.util.DBConnectionUtil;

/**
 * Data access object for employee table.
 *
 */
public class EmployeeDao {
	
	/**
	 * Inserts an employee in Employee table.
	 * 
	 * @param employee : employee to insert
	 * @throws SQLException
	 */
	public void addEmployee(Employee employee) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = DBConnectionUtil.getConnection();
			preparedStatement =
				connection.prepareStatement("insert into Employees(firstName, lastName, department) values (?, ?, ?)");
			preparedStatement.setString(1, employee.getFirstName());
			preparedStatement.setString(2, employee.getLastName());
			preparedStatement.setString(3, employee.getDepartment());
			preparedStatement.executeUpdate();
		} finally {
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Updates an employee.
	 * 
	 * @param employee : employee to update.
	 * @throws SQLException
	 */
	public void updateEmployee(Employee employee) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = DBConnectionUtil.getConnection();
			preparedStatement =
				connection.prepareStatement("update Employees set firstName=?, lastName=?, department=? where empId=?");
			preparedStatement.setString(1, employee.getFirstName());
			preparedStatement.setString(2, employee.getLastName());
			preparedStatement.setString(3, employee.getDepartment());
			preparedStatement.setInt(4, employee.getEmpId());
			preparedStatement.executeUpdate();
		} finally {
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Deletes an employee.
	 * 
	 * @param empId : id of employee to delete.
	 * @throws SQLException
	 */
	public void deleteEmployee(int empId) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = DBConnectionUtil.getConnection();
			preparedStatement =
				connection.prepareStatement("delete from Employees where empId=?");
			preparedStatement.setInt(1, empId);
			preparedStatement.executeUpdate();
		} finally {
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Returns the list of all employees.
	 * 
	 * @return the list of employees.
	 * @throws SQLException
	 */
	public List<Employee> getAllEmployees() throws SQLException {
		Connection connection = null;
		Statement statement = null;
		List<Employee> employees = new ArrayList<Employee>();
		try {
			connection = DBConnectionUtil.getConnection();
			statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("select * from Employees");
			Employee employee = null;
			while (resultSet.next()) {
				employee = new Employee();
				employee.setEmpId(resultSet.getInt("empId"));
				employee.setFirstName(resultSet.getString("firstName"));
				employee.setLastName(resultSet.getString("lastName"));
				employee.setDepartment(resultSet.getString("department"));
				employees.add(employee);
			}
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return employees;
	}
	
	/**
	 * Retrieves an employee using provided id.
	 * 
	 * @param empId : the id of employee to retrieve.
	 * @return employee
	 * @throws SQLException
	 */
	public Employee getEmployeeById(int empId) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		Employee employee = null;
		try {
			connection = DBConnectionUtil.getConnection();
			preparedStatement =
				connection.prepareStatement("select * from Employees where empId=?");
			preparedStatement.setInt(1, empId);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				employee = new Employee();
				employee.setEmpId(resultSet.getInt("empId"));
				employee.setFirstName(resultSet.getString("firstName"));
				employee.setLastName(resultSet.getString("lastName"));
				employee.setDepartment(resultSet.getString("department"));
			}
		} finally {
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return employee;
	}

}
