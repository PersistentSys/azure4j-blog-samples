About:
=====
This sample demonstrates how to configure Memcached server and use Memcached for caching data for an application.

Prerequisites:
=============
Create a database 'Company' in SQLAzure and 'Employee' table in this database. The script to create Employee table is

create table Employees (
    empId int Primary key identity,
    firstName varchar(50),
    lastName varchar(50),
    department varchar(50)
)

Setup Instructions:
==================

1) com.persistent.util.DBConnectionUtil
	Provide the values for connection url, user and password.
2) com.persistent.memcached.client.MCacheClient
	Provide the log file location as an argument to FileHandler initialization, in static code block.
3) com.persistent.service.EmployeeService
	Provide the log file location as an argument to FileHandler initialization, in constructor.
4) After these modifications export the application 'MemcachedWebApp' as war file to 'WorkerRole2\approot' folder of Memcached (Windows Azure Project).  
	
Memcached (Windows Azure Project):
---------------------------------
	1) This is a Windows Azure project that contains two worker roles. One worker role is for running Memcached server on default port 11211 and other has an application that accesses Memcached server for caching data.
	   Note: In case you have configured Memcached to run on different port then edit ServiceDefinition.csdef to specify this port as port value for internal endpoint 'Memcached.Endpoint'. This endpoint 'Memcached.Endpoint' is referred in MCacheClient class.
	2) The 'WorkerRole2' of this project has an application that accesses Memcached server and is dependent on Tomcat and jre. Tomcat 7 and jre 7 (64-bit version) have to be uploaded to blobs. For WorkerRole2 startup, edit startup.cmd and specify the respective blob urls for Tomcat and jre. These urls will be used to download and configure the binaries.
	3) Memcached binaries (64-bit version) can be downloaded from http://downloads.northscale.com/memcached-1.4.5-amd64.zip.
	4) Extract the memcached zip file and copy 'memcached.exe' along with 'pthreadGC2.dll' to 'WorkerRole1\approot'.  

How the sample works:
====================
	After deploying Memcached (Windows Azure Project) to Windows Azure, application can be accessed at 'http://<your-app-name>.cloudapp.net/MemcachedWebApp'. Accessing this url for the first time will display the list of employees (if present) from Employees table and list will be cached. Subsequent requests for list of employees will be served from cache. 
	After successful add/update/delete employee operation, the cache is updated accordingly.  
		
Persistent Systems Blog:
========================
For more information on this topic please visit "Related posts" section at
http://blog.persistentsys.com/index.php/2012/04/20/cloudninja-for-java/
