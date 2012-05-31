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
package com.persistent.azure.diagnostics.performancecounters;

import java.util.Calendar;

import com.microsoft.windowsazure.services.core.storage.CloudStorageAccount;
import com.microsoft.windowsazure.services.table.client.CloudTableClient;
import com.microsoft.windowsazure.services.table.client.TableConstants;
import com.microsoft.windowsazure.services.table.client.TableQuery;
import com.microsoft.windowsazure.services.table.client.TableQuery.Operators;
import com.microsoft.windowsazure.services.table.client.TableQuery.QueryComparisons;

/**
 * Retrieves performance counters from 'WADPerformanceCountersTable'.
 *
 */
public class RetrievePerfCounters {
	public static final String storageConnectionString = 
	    "DefaultEndpointsProtocol=https;" +
		"AccountName=<account-name>;" +
		"AccountKey=<account-key>";
	
	public static void main(String[] args) {
		try {
			String tableName = "WADPerformanceCountersTable";
			
			// Retrieve the storage account
			CloudStorageAccount storageAccount = 
				CloudStorageAccount.parse(storageConnectionString);
			// Create the table client
			CloudTableClient tableClient = 
				storageAccount.createCloudTableClient();
			
			Calendar currentTime = Calendar.getInstance();
			// Create a filter for 'timestamp less than current time'
			String upperBound = TableQuery.generateFilterCondition(
					TableConstants.TIMESTAMP,
					QueryComparisons.LESS_THAN,
					currentTime.getTime());
			
			currentTime.add(Calendar.MINUTE, -5);
			
			// Create a filter for 'timestamp greater than (current time - 5 min)'
			String lowerBound = TableQuery.generateFilterCondition(
					TableConstants.TIMESTAMP,
					QueryComparisons.GREATER_THAN,
					currentTime.getTime());
			
			// Combine both filters with AND operator which will result in filter
			// selecting entities generated in last 5 minutes.
			String filter = TableQuery.combineFilters(
					upperBound, Operators.AND, lowerBound);
			
			// Create a table query by specifying the table name,
			// WADPerfCountersEntity as entity and the filter expression
			TableQuery<WADPerfCountersEntity> query = TableQuery.from(
					tableName, WADPerfCountersEntity.class).where(filter);
			WADPerfCountersEntityResolver resolver =
				new WADPerfCountersEntityResolver();
			// Iterate over the results
			for (WADPerfCountersEntity perfCountersEntity : tableClient.execute(query, resolver)) {
				System.out.println("\nCounterName :: " + perfCountersEntity.getCounterName()
						+ "\nCounterValue :: " + perfCountersEntity.getCounterValue());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
