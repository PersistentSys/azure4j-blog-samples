About:
=====
This sample demonstrate the JDBC retry logic for transient faults in SQL Azure.

Prerequisites:
=============
A SQLAzure with let's say Company database having Employee table. The script to create the table is as below

create table Employee (
    Id int Primary key identity,
    Name varchar(50),
    Department varchar(50)
)

Using retry policy:
==================
The TestRetryPolicy class demonstrates the use of RetryPolicy in JDBC code.
For testing the policy you need to edit TestRetryPolicy.java and specify database connectionUrl.

Persistent Systems Blog:
========================
For more information on this topic please visit "Related posts" section at
http://blog.persistentsys.com/index.php/2012/04/20/cloudninja-for-java/ 