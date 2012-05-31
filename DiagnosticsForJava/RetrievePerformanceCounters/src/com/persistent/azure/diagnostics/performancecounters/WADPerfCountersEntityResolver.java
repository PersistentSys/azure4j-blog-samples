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

import java.util.Date;
import java.util.HashMap;

import com.microsoft.windowsazure.services.core.storage.StorageException;
import com.microsoft.windowsazure.services.table.client.EntityProperty;
import com.microsoft.windowsazure.services.table.client.EntityResolver;

/**
 * This class provides mapping between the entities that are retrieved from WADPerformanceCountersTable 
 * and WADPerfCountersEntity.
 * 
 */
public class WADPerfCountersEntityResolver implements EntityResolver<WADPerfCountersEntity> {
	
	@Override
	public WADPerfCountersEntity resolve(String partitionKey, String rowKey,
			Date timeStamp, HashMap<String, EntityProperty> properties,
			String etag) throws StorageException {
		WADPerfCountersEntity perfCountersEntity = new WADPerfCountersEntity();
		perfCountersEntity.setPartitionKey(partitionKey);
		perfCountersEntity.setRowKey(rowKey);
		perfCountersEntity.setEtag(etag);
		perfCountersEntity.setTimestamp(timeStamp);
		perfCountersEntity.setDeploymentId(properties.get("DeploymentId").getValueAsString());
		perfCountersEntity.setEventTickCount(properties.get("EventTickCount").getValueAsDouble());
		perfCountersEntity.setRole(properties.get("Role").getValueAsString());
		perfCountersEntity.setRoleInstance(properties.get("RoleInstance").getValueAsString());
		perfCountersEntity.setCounterName(properties.get("CounterName").getValueAsString());
		perfCountersEntity.setCounterValue(properties.get("CounterValue").getValueAsString());
		
		return perfCountersEntity;
	}
}
