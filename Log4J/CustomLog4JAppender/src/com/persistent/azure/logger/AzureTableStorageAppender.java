/*******************************************************************************
* Copyright 2012 Persistent Systems Ltd.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
******************************************************************************/
/*
 *@Disclaimer 
 * This Class demonstrates an implementation of custom Log4J appender for 
 * Windows Azure Java Application.On each logging activity depending on 
 * the log level, this appender will directly update the respective Azure 
 * Log Table. As each log entry is being updated directly ( not a batch update )
 * the latency will be higher. The sole purpose of the sample is to show how a 
 * Log4J custom appender can be implemented for Windows Azure. Its up to the 
 * individual to enhance the appender to improve the latency time, may be by 
 * implementing support for batch update. 
 */
package com.persistent.azure.logger;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.ErrorCode;
import org.apache.log4j.spi.LoggingEvent;

import com.microsoft.windowsazure.serviceruntime.RoleEnvironment;
import com.microsoft.windowsazure.serviceruntime.RoleInstance;
import com.microsoft.windowsazure.services.core.storage.StorageException;
import com.microsoft.windowsazure.services.table.client.CloudTableClient;
import com.microsoft.windowsazure.services.table.client.TableOperation;
import com.persistent.azure.entity.LogEntity;

/**
 * The custom appender class logs messages 
 * to Windows Azure Table Storage.
 */
public class AzureTableStorageAppender extends AppenderSkeleton{
	private String tableName;
	private String storageEndpoint;
	private String accountName;
	private String accountKey;
	
	/**
	 * Gets the Windows Azure Table name.
	 * @return tableName
	 */
	public String getTableName() {
		return tableName;
	}
	
	/**
	 * Sets the the Windows Azure Table name.
	 * @param tableName
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	/**
	 * Gets the Storage end point.
	 * @return storageEndpoint
	 */
	public String getStorageEndpoint() {
		return storageEndpoint;
	}
	
	/**
	 * Sets the Storage end point.
	 * @param storageEndpoint
	 */
	public void setStorageEndpoint(String storageEndpoint) {
		this.storageEndpoint = storageEndpoint;
	}
	
	/**
	 * Gets the account name.
	 * @return accountName
	 */
	public String getAccountName() {
		return accountName;
	}
	
	/**
	 * Sets the account name.
	 * @param accountName
	 */
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	
	/**
	 * Gets the account key.
	 * @return accountKey
	 */
	public String getAccountKey() {
		return accountKey;
	}
	
	/**
	 * Sets the account key.
	 * @param accountKey
	 */
	public void setAccountKey(String accountKey) {
		this.accountKey = accountKey;
	}
	
	/**
	 * The method adds the log message to the Windows Azure Table storage
	 * depending upon the layout.
	 * 
	 * @param event 
	 */
	@Override
	protected synchronized void append(LoggingEvent event) {
        String messageLevel = event.getLevel().toString();
		if(this.layout == null) {
			errorHandler.error("layout not specified", null, ErrorCode.MISSING_LAYOUT );
			return;
		}
		String message = this.layout.format(event);
		try {
			CloudTableClient cloudTableClient = TableClient.getTableClient(
					storageEndpoint, accountName, accountKey, tableName);
			makeEntryIntoAzureTableStorage(cloudTableClient,messageLevel, message);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (StorageException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * The method makes the entry of log message in Windows Azure Table.
	 * 
	 * @param cloudTableClient The table client
	 * @param messageLevel The log message level
	 * @param message The message to be logged
	 */
	private void makeEntryIntoAzureTableStorage(
			CloudTableClient cloudTableClient, String messageLevel, String message) {
		try {
			String instanceDetail = getInstanceDetails();
						
			Date date = new Date();
			Long milliseconds = date.getTime();
			LogEntity logEntity = new LogEntity();
			logEntity.setPartitionKey(messageLevel);
			logEntity.setRowKey(instanceDetail + "$" + milliseconds.toString());
			logEntity.setTimestamp(new Timestamp(new Date().getTime()));
			logEntity.setLevel(messageLevel);
			logEntity.setMessage(message);
	
			cloudTableClient.execute(tableName, TableOperation.insert(logEntity));
		} catch (StorageException storageException) {
			storageException.printStackTrace();
		} 
	}
	
	/**
	 * Gets Azure role name and instance id.
	 * 
	 * @return instanceDetails
	 */
	private String getInstanceDetails() {
		String instanceDetails = "standalone";
		if(RoleEnvironment.isAvailable()) {
			RoleInstance roleInstance = RoleEnvironment.getCurrentRoleInstance();
			String roleName = roleInstance.getRole().getName();
			String instance = roleInstance.getId();
			instanceDetails = roleName + "$" + instance;
		}
		return instanceDetails;
	}

	@Override
	public synchronized void close() {
	}
	
	@Override
	public boolean requiresLayout() {
		return true;
	}
}
