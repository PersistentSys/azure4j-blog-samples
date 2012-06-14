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
package com.persistent.azure;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.persistent.azure.entity.Employee;

public class HibernateAccess {

	/**
	 * main method
	 * @param args
	 */
public static void main(String[] args) {
	SessionFactory sessionFactory = buildSessionFactory();
	Session session;
    // get a new session
	session = sessionFactory.openSession();

	session.beginTransaction();

	try {
		@SuppressWarnings("unchecked")
		List<Employee> employeeList = session.createQuery("from Employee").list();
		Employee employee = null;

		for (int index = 0; index < employeeList.size(); index++) {
			employee = employeeList.get(index);
			System.out.println(employee.getName());
		}
	} catch (Exception e) {} 
	finally { session.close(); }
}

/**
 * This method loads default hibernate.cfg.xml file and all the
 * configuration mentioned in it.
 * 
 * @return The session factory object
 */
private static SessionFactory buildSessionFactory() {
	try {
		// Create the SessionFactory from hibernate.cfg.xml
		return new Configuration().configure().buildSessionFactory();
	} catch (Throwable ex) {
		System.err.println("Initial SessionFactory creation failed." + ex);
		throw new ExceptionInInitializerError(ex);
	}
}
}
