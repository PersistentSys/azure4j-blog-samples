About:
=====
This sample application demonstrates how to create a Relying party application in ACS programmatically using ACS Management Service. 

Prerequisites:
=============
Windows Azure account.

Setup Instructions:
==================

1) com.persistent.azure.acs.EntityClassesGenerator
    This class calls Generator that takes an argument array object consisting of 'namespace url' and location where entity classes will be generated.
    Edit this class to provide the value for namespace url in the format 'https://<namespace-created-for-ACS>.accesscontrol.windows.net/v2/mgmt/service' and the location where entity classes will be generated. 
2) com.persistent.azure.acs.RelyingPartyCreator
    This is a standalone class which creates relying party programmatically.
	Edit this class to provide the values for variables
		a) acsNameSpace = <namespace-created-for-ACS>
		b) acsMgmtPassword = <password> (to be retrieved from Management service section of	ACS portal).
		c) ruleGroupName = <name-of-rule-group-created-for-identity-providers>

Persistent Systems Blog:
========================
For more information on this topic please visit "Related posts" section at http://blog.persistentsys.com/index.php/2012/04/20/cloudninja-for-java/
