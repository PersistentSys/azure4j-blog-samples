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
package com.persistent.azure.entity;

import com.microsoft.windowsazure.services.table.client.TableServiceEntity;
/**
 * The entity class holds the log message 
 * and the level of the log message.
 */
public class LogEntity extends TableServiceEntity{

	private String message;
	private String level;
	
	/**
	 * Gets level of the log message.
	 * @return level
	 */
	public String getLevel() {
		return level;
	}
	
	/**
	 * Sets level of the log message.
	 * @param level
	 */
	public void setLevel(String level) {
		this.level = level;
	}

	/**
	 * Gets the log message.
	 * @return message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the log message.
	 * @param message
	 */
	public void setMessage(String message) {
		this.message = message;
	}
}
