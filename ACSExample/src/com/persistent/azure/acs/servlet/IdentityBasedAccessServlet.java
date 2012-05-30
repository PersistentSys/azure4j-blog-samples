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
package com.persistent.azure.acs.servlet;

import java.io.IOException;
import java.util.ResourceBundle;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This servlet implementation provides access to requests based on identity
 * provider (Yahoo!/Google).
 */
public class IdentityBasedAccessServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static ResourceBundle resourceBundle;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public IdentityBasedAccessServlet() {
        super();
        resourceBundle = ResourceBundle.getBundle("acs");
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// get identity provider from request
		String identityProvider = request.getParameter("idp");
		String currentIdentityProvider = null;
		String dispatchTo = null;
		// Check whether the browser already has a cookie
		Cookie[] cookies = request.getCookies();
		Cookie currentCookie = null;
		String cookieName = resourceBundle.getString("acs.cookie.name");

		if (cookies != null && cookies.length > 0) {
		    for (Cookie c : cookies) {
				if (cookieName.equals(c.getName())) {
				    currentCookie = c;
				    break;
				}
		    }
		}
		
		if (currentCookie != null) {
			String separator = resourceBundle.getString("acs.separator");
			String[] arrValue = currentCookie.getValue().split(separator);
			// get identity provider from cookie.
			currentIdentityProvider = arrValue[0];
			if (currentIdentityProvider.equalsIgnoreCase(identityProvider)) {
				// if identity provider from request is same as the one in cookie
				// then the request is authorized.
				dispatchTo = "/WEB-INF/pages/authorized.jsp";
			} else {
				dispatchTo = "/WEB-INF/pages/unAuthorized.jsp";
			}
			request.setAttribute("currentIdentityProvider", currentIdentityProvider);
		}
		RequestDispatcher reqDispatcher = request.getRequestDispatcher(dispatchTo);
		reqDispatcher.forward(request, response);
	}

}
