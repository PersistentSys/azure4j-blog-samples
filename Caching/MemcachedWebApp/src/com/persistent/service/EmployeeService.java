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
package com.persistent.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import net.spy.memcached.MemcachedClient;

import com.persistent.dao.EmployeeDao;
import com.persistent.memcached.client.MCacheClient;
import com.persistent.model.Employee;

/**
 * Service class for employee.
 *
 */
public class EmployeeService {
	private static final String CACHE_KEY = "listOfEmployees";
	// expiry time = one hour
	private static final int CACHE_ENTRY_EXPIRY = 3600;
	private static final Logger LOGGER = Logger.getLogger(EmployeeService.class.getName());
	private EmployeeDao employeeDao;  
	
	public EmployeeService() {
		employeeDao = new EmployeeDao();
		try {
			FileHandler fileHandler = new FileHandler("C:\\memcached_log.txt", true);
			fileHandler.setFormatter(new SimpleFormatter());
			LOGGER.addHandler(fileHandler);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Inserts an employee and updates the cache to include that employee.
	 * 
	 * @param employee
	 */
	public void addEmployee(Employee employee) {
		try {
			employeeDao.addEmployee(employee);
			MemcachedClient cacheClient = MCacheClient.getClient();
			Object cacheValue = cacheClient.get(CACHE_KEY);
			if (cacheValue != null) {
				List<Employee> employees = new ArrayList<Employee>();
				employees = employeeDao.getAllEmployees();
				cacheClient.set(CACHE_KEY, CACHE_ENTRY_EXPIRY, employees);
				LOGGER.info("####addEmployee## added the entry in DB & cache");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/** 
	 * Updates the employee in database and also updates the entry in cache.
	 *  
	 * @param employee : employee to update.
	 */
	public void updateEmployee(Employee employee) {
		try {
			employeeDao.updateEmployee(employee);
			int empId = employee.getEmpId();
			MemcachedClient cacheClient = MCacheClient.getClient();
			Object cacheValue = cacheClient.get(CACHE_KEY);
			if (cacheValue != null) {
				List<Employee> employees = new ArrayList<Employee>();
				employees = (List<Employee>) cacheValue;
				
				for (Employee emp : employees) {
					if (emp.getEmpId() == empId) {
						int index = employees.indexOf(emp);
						employees.set(index, employee);
						break;
					}
				}
				cacheClient.set(CACHE_KEY, CACHE_ENTRY_EXPIRY, employees);
				LOGGER.info("####updateEmployee## Updated the entry in DB & cache");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Deletes employee from database 
	 * and removes that employee from cache to update the cache.
	 * 
	 * @param empId : id of employee to delete.
	 */
	public void deleteEmployee(int empId) {
		try {
			employeeDao.deleteEmployee(empId);
			MemcachedClient cacheClient = MCacheClient.getClient();
			Object cacheValue = cacheClient.get(CACHE_KEY);
			if (cacheValue != null) {
				List<Employee> employees = new ArrayList<Employee>();
				employees = (List<Employee>) cacheValue;
				
				for (Employee emp : employees) {
					if (emp.getEmpId() == empId) {
						int index = employees.indexOf(emp);
						employees.remove(index);
						break;
					}
				}
				cacheClient.set(CACHE_KEY, CACHE_ENTRY_EXPIRY, employees);
				LOGGER.info("####deleteEmployee## deleted from DB & cache");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * If cache contains the list of employees then it is retrieved from cache
	 * and returned else the list is retrieved from database 
	 * and the cache is updated to contain that list.
	 * 
	 * @return : list of employees.
	 */
	public List<Employee> getAllEmployees() {
		List<Employee> employees = new ArrayList<Employee>();
		try {
			MemcachedClient cacheClient = MCacheClient.getClient();
			Object cacheValue = cacheClient.get(CACHE_KEY);
			if (cacheValue == null) {
				employees = employeeDao.getAllEmployees();
				cacheClient.add(CACHE_KEY, CACHE_ENTRY_EXPIRY, employees);
				LOGGER.info("####getAllEmployees## Not in cache.Querying DB");
			} else {
				employees = (List<Employee>) cacheValue;
				LOGGER.info("####getAllEmployees## Found in cache");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employees;
	}
	
	/**
	 * If the cache contains the list of employees then the employee is retrieved
	 * from cache and returned else the employee is retrieved from database.
	 * 
	 * @param empId : id of employee to retrieve.
	 * @return
	 */
	public Employee getEmployeeById(int empId) {
		Employee employee = null;
		try {
			MemcachedClient cacheClient = MCacheClient.getClient();
			Object cacheValue = cacheClient.get(CACHE_KEY);
			if (cacheValue == null) {
				employee = employeeDao.getEmployeeById(empId);
				LOGGER.info("####getEmployeeById## Not in cache.Querying DB");
			} else {
				List<Employee> employees = new ArrayList<Employee>();
				employees = (List<Employee>) cacheValue;
				for (Employee emp : employees) {
					if (emp.getEmpId() == empId) {
						employee = emp;
						break;
					}
				}
				LOGGER.info("####getEmployeeById## Found in cache");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employee;
	}
}
