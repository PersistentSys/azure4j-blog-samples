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

import com.persistent.azure.acs.UnmarshallXmlResponse;
import com.persistent.azure.acs.UserInfoBean;

/**
 * This servlet implementation parses the SAML token response received from ACS
 * and stores the claims in cookie.
 */
public class ParseSAMLTokenServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static ResourceBundle resourceBundle;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ParseSAMLTokenServlet() {
        super();
        resourceBundle = ResourceBundle.getBundle("acs");
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		UnmarshallXmlResponse unmarshallXmlResponse =
			new UnmarshallXmlResponse();
		UserInfoBean userInfoBean = null;
		String cookieName = resourceBundle.getString("acs.cookie.name");
		String cookieValSeparator = resourceBundle.getString("acs.separator");
		// 'wresult' contains the SAML token 
		String acsToken = request.getParameter("wresult");
		// if acsToken is not null then parse token and store the claims in cookie.
		if (acsToken != null) {
			// parse SAML token
			userInfoBean = unmarshallXmlResponse.unmarshallTokenXml(acsToken);
			
			// construct cookie value that contains the claims (identity provider,
			// email address and name).
			StringBuffer cookieVal = new StringBuffer();
			cookieVal.append(userInfoBean.getIdentityProvider());
			cookieVal.append(cookieValSeparator);
			cookieVal.append(userInfoBean.getEmailAddress());
			cookieVal.append(cookieValSeparator);
			cookieVal.append(userInfoBean.getName());
			
			// create cookie
			Cookie cookie = new Cookie(cookieName, null);
			int maxAge = Integer.parseInt(resourceBundle.getString("acs.cookie.maxage"));
			cookie.setMaxAge(maxAge);
			cookie.setPath("/");
			cookie.setValue(cookieVal.toString());
			// add cookie to response
			response.addCookie(cookie);
		} else {
			// if acsToken is null then retrieve the claim values from cookie
			Cookie[] cookies = request.getCookies();
			Cookie currentCookie = null;
		
			if (cookies != null && cookies.length > 0) {
				for (Cookie c : cookies) {
					if (cookieName.equals(c.getName())) {
						currentCookie = c;
						break;
					}
				}
			}
			String[] arrValue = currentCookie.getValue().split(cookieValSeparator);
			userInfoBean = new UserInfoBean();
			userInfoBean.setIdentityProvider(arrValue[0]);
			userInfoBean.setEmailAddress(arrValue[1]);
			userInfoBean.setName(arrValue[2]);
		}
		
		request.setAttribute("userInfoBean", userInfoBean);
		RequestDispatcher reqDispatcher = request.getRequestDispatcher("/WEB-INF/pages/claims.jsp");
		reqDispatcher.forward(request, response);
	}

}
