package com.persistent.azure.logger;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;

import com.microsoft.windowsazure.services.core.storage.StorageCredentials;
import com.microsoft.windowsazure.services.core.storage.StorageException;
import com.microsoft.windowsazure.services.table.client.CloudTableClient;

public class TableClient {
	private static CloudTableClient cloudTableClient = null;
	
	private TableClient(){
	}
	
	public static CloudTableClient getTableClient(
			String endpoint, String acctName, String acctKey, String tableName) 
			throws InvalidKeyException, StorageException, URISyntaxException{
		if(cloudTableClient == null) {
			URI uri = new URI(endpoint);
			cloudTableClient = new CloudTableClient(
				uri,StorageCredentials.tryParseCredentials(
				"DefaultEndpointsProtocol=http;" +
				"AccountName="+acctName+";" +
				"AccountKey="+acctKey));
			cloudTableClient.createTableIfNotExists(tableName);
		}
		return cloudTableClient;
	}
}
