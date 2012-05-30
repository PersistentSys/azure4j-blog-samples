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
package com.persistent.azure.acs.filter;

import java.io.IOException;
import java.util.ResourceBundle;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This filter implementation checks for cookie presence in each Http request.
 * If cookie is present then access to requested resource is allowed else user
 * is redirected to login page.
 */
public class AuthorizationFilter implements Filter {

	private static ResourceBundle resourceBundle;
	
	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		
		// capture ACS response
		String acsToken = httpServletRequest.getParameter("wresult");
		if (null != acsToken && acsToken.trim().length() == 0) {
			acsToken = null;
		}
		
		if (!(httpServletRequest.getRequestURI().contains("claims.do") && acsToken != null)) {
			// Check whether the browser already has a cookie
			Cookie[] cookies = httpServletRequest.getCookies();
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
			
			if (currentCookie == null) {
				// if cookie is not present then redirect to login page.
				StringBuffer loginURL = new StringBuffer();
				loginURL.append(resourceBundle.getString("acs.url"));
				loginURL.append("/v2/wsfederation?wa=wsignin1.0&wtrealm=");
				loginURL.append(resourceBundle.getString("acs.realm"));
				httpServletResponse.sendRedirect(loginURL.toString());
			} else {
				chain.doFilter(request, response);
			}
		} else {
			chain.doFilter(request, response);
		}
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		resourceBundle = ResourceBundle.getBundle("acs");
	}

}
