About:
=====
This sample application demonstrates how to integrate Access Control Service (ACS) for authentication.

Prerequisites:
=============
Create a Relying party application in ACS with Google and Yahoo! specified as identity providers. Specify values for Realm as 'http://localhost:8080/ACSExample' and for Return URL as 'http://localhost:8080/ACSExample/claims.do'. 
This example assumes that you are running this application locally. Here 'localhost:8080' will be replaced with <your-app-name>.cloudapp.net if application is deployed on Windows Azure.

Setup Instructions:
==================
Edit acs.properties file and provide values for 

	acs.url=https://<namespace-created-for-ACS>.accesscontrol.windows.net
	acs.realm=http://localhost:8080/ACSExample
	
How the sample application works:
================================
	The sample application displays the claims received from ACS after authentication. If user is not already logged-in and tries to access the application URL then user is redirected to ACS login page. 
	ACS login page presents various pre-configured identity providers' list. User needs to select the desired identity provider, after selection the user is redirected to login page of that identity provider. 
	After successful authentication user is presented with page displaying the claims received from ACS.

Persistent Systems Blog:
========================
For more information on this topic please visit "Related posts" section at
http://blog.persistentsys.com/index.php/2012/04/20/cloudninja-for-java/