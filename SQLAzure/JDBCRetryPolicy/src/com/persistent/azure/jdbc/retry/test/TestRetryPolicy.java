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
package com.persistent.azure.jdbc.retry.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.persistent.azure.jdbc.retry.RetryPolicy;

/**
 * Class demonstrate use of RetryPolicy in your JDBC code
 * 
 */
public class TestRetryPolicy {

    public static void main(String args[]) throws Exception {

        try {
            // Load the driver
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            // User name should be of format userName@serverName
            String dbConnectionUrl = "jdbc:sqlserver://<serverName>.database.windows.net;"
                + "databaseName=Company;user=<user name>;password=<password>";
            
            List<Employee> empList = getEmployeeList(dbConnectionUrl);
            
            System.out.println("Number of employees = " + empList.size());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    /**
     *  Gets the employee list using retry policy
     * @param dbConnectionUrl The database conection url
     * @return List of Employees
     * @throws Exception
     */
    public static List<Employee> getEmployeeList(final String dbConnectionUrl) throws Exception {

        // The JDBC code encapsulated in the Callable task. This task will
        // return the list of employees and will be executed by RetryPlolicy
        // instance
        Callable<List<Employee>> task = new Callable<List<Employee>>() {
            Connection conn;
            Statement stmt;
            ResultSet resultSet;
            String query = "Select * from Employee";

            @Override
            public List<Employee> call() throws Exception {
                try {
                    conn = DriverManager.getConnection(dbConnectionUrl);
                    stmt = conn.createStatement();

                    List<Employee> employeeList = new ArrayList<Employee>();
                    Employee emp;

                    resultSet = stmt.executeQuery(query);
                    // Iterate through the query result and construct the
                    // employee list
                    while (resultSet.next()) {
                        emp = new Employee();
                        emp.setId(resultSet.getInt(1));
                        emp.setName(resultSet.getString(2));
                        emp.setDepartment(resultSet.getString(3));
                        employeeList.add(emp);
                    }
                    return employeeList;
                } finally {
                    if (null != resultSet)
                        try {
                            resultSet.close();
                        } catch (SQLException e) {}
                    if (null != stmt)
                        try {
                            stmt.close();
                        } catch (SQLException e) {}
                    if (null != conn)
                        try {
                            conn.close();
                        } catch (SQLException e) {}
                }
            }
        };

        // Define the retry policy with three tries with 2 seconds interval
        RetryPolicy<List<Employee>> retryPolicy =
                new RetryPolicy<List<Employee>>(3, 2000, task);
        // Execute the task
        List<Employee> EmployeeList = retryPolicy.executetask();
        return EmployeeList;
    }
}