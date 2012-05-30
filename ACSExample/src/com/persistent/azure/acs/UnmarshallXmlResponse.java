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
package com.persistent.azure.acs;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import org.opensaml.Configuration;
import org.opensaml.DefaultBootstrap;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AttributeStatement;
import org.opensaml.saml2.core.impl.AssertionImpl;
import org.opensaml.ws.wsfed.RequestedSecurityToken;
import org.opensaml.ws.wsfed.impl.RequestSecurityTokenResponseImpl;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.parse.BasicParserPool;
import org.opensaml.xml.parse.XMLParserException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Parses SAML token.
 *
 */
public class UnmarshallXmlResponse {

	/**
	 * Parses SAML token and returns the claims in the form of a bean.
	 * 
	 * @param acsToken : the SAML token to parse.
	 * @return bean object containing claims.
	 */
	public UserInfoBean unmarshallTokenXml(String acsToken){
		UserInfoBean tokenBean = new UserInfoBean();
		
		// Initialize the library
		try {
			DefaultBootstrap.bootstrap();
		} catch (ConfigurationException e1) {
			e1.printStackTrace();
		}

		// Get parser pool manager
		BasicParserPool parserPoolMgr = new BasicParserPool();
		parserPoolMgr.setNamespaceAware(true);

		// Parse SAML token
		Reader in = new StringReader(acsToken); 
		Document document = null;
		try {
			document = parserPoolMgr.parse(in);
		} catch (XMLParserException e1) {
			e1.printStackTrace();
		}
		Element rstrRoot = document.getDocumentElement();

		// Get RequestSecurityTokenResponse
		UnmarshallerFactory unmarshallerFactory = Configuration.getUnmarshallerFactory();
		Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(rstrRoot);

		// Unmarshall using the document root element, an RequestSecurityTokenResponse i.e. rstrRoot
		try {
			RequestSecurityTokenResponseImpl samlResponse = 
				(RequestSecurityTokenResponseImpl) unmarshaller.unmarshall(rstrRoot);
			RequestedSecurityToken requestedSecurityToken = samlResponse.getRequestedSecurityToken().get(0);
			AssertionImpl assertion = (AssertionImpl) requestedSecurityToken.getSecurityTokens().get(0);
			List<AttributeStatement> attributeStatements = assertion.getAttributeStatements();
			List<Attribute> attributes = attributeStatements.get(0).getAttributes();
			String attrName = null;
			String attrValue = null;
			for (Attribute attribute : attributes) {
				attrName = attribute.getName();
				attrValue = attribute.getAttributeValues().get(0).getDOM().getTextContent();
				// retrieve the claims
				if (attrName.contains("emailaddress")) {
					tokenBean.setEmailAddress(attrValue);
				} else if (attrName.contains("name")) {
					tokenBean.setName(attrValue);
				} if (attrName.contains("identityprovider")) {
					tokenBean.setIdentityProvider(attrValue);
				} 
			}
		} catch (UnmarshallingException e) {
			e.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return tokenBean;
	}
}
