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

package com.persistent.azure.jdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 * This class demonstrate on how to query the SQLAzure database using JDBC
 * 
 */
public class ConnectToSQLAzure {
    
    public static List<Employee> getEmployees(String name, String department)
    {
          
        // Define JDBC objects
        Connection conn = null;
        ResultSet resultSet = null;
        CallableStatement proc = null;
        
        Map<String, String> configSettingsMap;
        String dbConnectionUrl;
        
        // Use Windows Azure SDK, RoleEnvironment to get the Connection URl
        //configSettingsMap = RoleEnvironment.getConfigurationSettings();
        //dbConnectionUrl = configSettingsMap.get("dbConnectionUrl");
         
         dbConnectionUrl = "jdbc:sqlserver://<serverName>.database.windows.net;"
            + "databaseName=Company;user=<user name>;password=<password>";

        List<Employee> employeeList = new ArrayList<Employee>();
        
        try {
            // Register the driver
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            // Establish database connection
            conn = DriverManager.getConnection(dbConnectionUrl);
            // Create CallableStatement object to call stored procedure
            proc = conn.prepareCall("{ call getEmployees(?,?)}");

            proc.setString(1, name);
            proc.setString(2, department);

            resultSet = proc.executeQuery();
            Employee emp;
            // iterate over the result set to create employee list
            while (resultSet.next()) {
                emp = new Employee();
                emp.setId(resultSet.getInt(1));
                emp.setName(resultSet.getString(2));
                emp.setDepartment(resultSet.getString(3));

                employeeList.add(emp);
            }
        } catch (ClassNotFoundException e) { e.printStackTrace(); }
          catch (SQLException e) { e.printStackTrace();}
        finally {
            if (null != resultSet)
                try { resultSet.close(); } catch (SQLException e) { }
            if (null != proc) try { proc.close(); } catch (SQLException e) { }
            if (null != conn) try { conn.close(); } catch (SQLException e) { }
        }
        return employeeList;
    }
}