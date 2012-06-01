About:
=====
 This sample demonstrates how to configure diagnostics.

Setup Instructions:
==================

 1) RetrievePerformanceCounters ( Java project)
     com.persistent.azure.diagnostics.performancecounters.RetrievePerfCounters
     Provide the connection string for Windows Azure Storage.

 2) ConfigureDiagnostics (Windows Azure Project)
     a) Edit 'WorkerRole1\approot\DiagnosticsConfiguration.xml' to provide values for AccountName and AccountKey.
     b) The 'WorkerRole1' of this project has an sample application (HelloWorld) that is dependent on Tomcat and jre. Tomcat 7 and jre 7 (64-bit version) have to be uploaded to blobs. 
	For WorkerRole1 startup, edit startup.cmd and specify the respective blob urls for Tomcat and jre. These urls will be used to download and configure the binaries.
	
 Dependencies:
 ------------
  The Azure diagnostics tool has dependency on following DLLs : 

   1) Microsoft.WindowsAzure.Diagnostics.dll
   2) Microsoft.WindowsAzure.StorageClient.dll

   These DLLs can be obtained after installing Windows Azure SDK for .NET from location below
   http://www.microsoft.com/download/en/details.aspx?displaylang=en&id=28045.

   For 32-bit machine installation use WindowsAzureSDK-x86.msi and for 64-bit machine use WindowsAzureSDK-x64.msi

   After installation of SDK, navigate to installation directory, Windows Azure SDK folder -> folder with name as SDK version number -> ref (e.g. C:\Program Files\Windows Azure SDK\v1.6\ref). 
   Here you will find the two DLLs
    * Microsoft.WindowsAzure.Diagnostics.dll
    * Microsoft.WindowsAzure.StorageClient.dll

  You need to copy the two DLLs to 'WorkerRole1\approot' folder.
	
How the sample application works:
================================
 Deploying ConfigureDiagnostics enables diagnostics that captures the configured performance counters and monitors the folder (for changes and pushes to blobs) specified in 'Directory' node of 'DiagnosticsConfiguration.xml'.

Persistent Systems Blog:
========================
For more information on this topic please visit "Related posts" section at
http://blog.persistentsys.com/index.php/2012/04/20/cloudninja-for-java/
