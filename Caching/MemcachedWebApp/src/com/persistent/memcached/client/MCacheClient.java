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
package com.persistent.memcached.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import net.spy.memcached.MemcachedClient;

import com.microsoft.windowsazure.serviceruntime.RoleEnvironment;
import com.microsoft.windowsazure.serviceruntime.RoleInstance;
import com.persistent.service.EmployeeService;

/**
 * Singleton class to get Memcached client. 
 *
 */
public class MCacheClient {
	private static MemcachedClient memcachedClient = null;
	private static final Logger LOGGER = Logger.getLogger(EmployeeService.class.getName());
	
	static {
		try {
			FileHandler fileHandler = new FileHandler("C:\\memcachedClient_log.txt", true);
			fileHandler.setFormatter(new SimpleFormatter());
			LOGGER.addHandler(fileHandler);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private MCacheClient() {
	}

	/**
	 * Retrieves the Memcached client object.
	 * 
	 * @return memcached client object.
	 * @throws IOException
	 */
	public static MemcachedClient getClient() throws IOException {
		if (memcachedClient == null) {
			LOGGER.info("#Creating new instance");
			boolean val = RoleEnvironment.isAvailable();
			if (val) {
				// get the instances of 'WorkerRole1' that is running Memcached
				Collection<RoleInstance> roleInstances =
					RoleEnvironment.getRoles().get("WorkerRole1").getInstances().values();
				
				List<InetSocketAddress> addresses = new ArrayList<InetSocketAddress>();
				InetSocketAddress socketAddress = null;
				for (Iterator<RoleInstance> iterator = roleInstances.iterator(); iterator.hasNext();) {
					RoleInstance instance = iterator.next();
					// get the endpoint of each Memcached instance.
					// 'Memcached.Endpoint' is the name of endpoint that is configured in
					// ServiceDefinition.csdef as internal endpoint.
					socketAddress = instance.getInstanceEndpoints().get("Memcached.Endpoint").getIpEndPoint();
					LOGGER.info("#Host :: " + socketAddress.getHostString() 
							+ "#Port :: " + socketAddress.getPort());
					addresses.add(socketAddress);
				}
				memcachedClient = new MemcachedClient(addresses);
			}
		}
		return memcachedClient;
	}
}
