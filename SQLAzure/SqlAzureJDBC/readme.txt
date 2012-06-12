About:
=====
This sample demonstrate how to query SQL Azure using JDBC.

Prerequisites:
=============
1) A SQLAzure with let's say Company database having Employee table. The script to create the table is as below

	create table Employee (
	    Id int Primary key identity,
	    Name varchar(50),
	    Department varchar(50)
	)
2) Create a stored procedure let’s say getEmployees in company database that takes employee name and department as parameters to get the employees. 
	USE Company;
	GO
	CREATE PROCEDURE getEmployees 
	    @Name nvarchar(50), 
	    @Department nvarchar(50) 
	AS 
	
	    SET NOCOUNT ON;
	    SELECT Id, Name, Department
	    FROM Employee
	    WHERE Name = @Name AND Department = @Department
	GO
    
Testing Instructions:
==================
Edit ConnectToSQLAzure.java to specify the database connectionUrl. Write a method to call getEmployees method.
Note:- In Windows azure environment you you should always read the configuration settings e.g. connectionUrl using RoleEnvironment class

Persistent Systems Blog:
========================
For more information on this topic please visit "Related posts" section at
http://blog.persistentsys.com/index.php/2012/04/20/cloudninja-for-java/ 