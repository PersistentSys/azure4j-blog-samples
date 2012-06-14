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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import microsoft.cloud.accesscontrol.management.IdentityProvider;
import microsoft.cloud.accesscontrol.management.MicrosoftCloudAccessControlManagementService;
import microsoft.cloud.accesscontrol.management.RelyingParty;
import microsoft.cloud.accesscontrol.management.RelyingPartyAddress;
import microsoft.cloud.accesscontrol.management.RelyingPartyIdentityProvider;
import microsoft.cloud.accesscontrol.management.RelyingPartyKey;
import microsoft.cloud.accesscontrol.management.RelyingPartyRuleGroup;
import microsoft.cloud.accesscontrol.management.RuleGroup;

import org.restlet.ext.odata.Query;

/**
 * Standalone class demonstrating use of ACS Management Service
 * to create Relying Party Application in ACS. 
 *
 */
public class RelyingPartyCreator {
	
	// ACS Details
	private static String acsNameSpace = "namespaceforacs";

	private static String acsMgmtUserName = "ManagementClient";
	
	private static String acsMgmtPassword = "kAsp8gPTOaxGMGRyPtLBvnXoJ8d7DBZbo22BqlstZED=";
	
	private String ruleGroupName = "rulegroup";
	private String certPassword = "password";
	
	private static MicrosoftCloudAccessControlManagementService managementService;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			managementService = 
				new MicrosoftCloudAccessControlManagementService(acsNameSpace, acsMgmtUserName,
						acsMgmtPassword);
			new RelyingPartyCreator().createRelyingPartyApplication("Myapp");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates a relying party application.
	 * 
	 * @param name
	 * @throws Exception
	 */
	private void createRelyingPartyApplication(String name) throws Exception {
		createCertificate(name);
		
		RelyingParty relyingParty = createRelyingParty(name);
		assignIdentityProvider(relyingParty);
		assignRuleGroup(relyingParty);
		assignRelyingPartyKeyAndAddresses(relyingParty, name);
	}
	
	/**
	 * Creates a self signed certificate.
	 * 
	 * @param name
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void createCertificate(String name) throws IOException, InterruptedException {
		String certificateValidity = "365";
		String destination = "";
		
		StringBuffer keytoolPath = new StringBuffer();
		keytoolPath.append(System.getProperty("java.home"));
        if (keytoolPath.length() == 0) {
        	keytoolPath.append(System.getenv("JRE_HOME"));
        }

        destination = keytoolPath.toString();
        destination = destination + File.separator + "lib" + File.separator + "security" + File.separator;
        
        keytoolPath.append(File.separator).append("bin");
        keytoolPath.append(File.separator);
        
        Runtime runtime = Runtime.getRuntime();
        
        String genCertCommand = "cmd /c cd /d \"" + keytoolPath.toString() + "\" && keytool.exe -genkeypair -alias "+ name
			+ " -keystore \"" + destination + name + "_keystore.pfx\" -storepass "
			+ certPassword + " -validity " + certificateValidity 
			+ " -keyalg RSA -keysize 2048 -storetype pkcs12 -dname \"cn="+ name + "\"";
        // Creating a Self Signed Certificate
        Process p = runtime.exec(genCertCommand);
        p.waitFor();
	}
	
	/**
	 * Creates a relying party.
	 * 
	 * @param name
	 * @return the relying party.
	 * @throws Exception
	 */
	private RelyingParty createRelyingParty(String name) throws Exception {
		RelyingParty relyingParty = new RelyingParty();
		relyingParty.setName(name);
		relyingParty.setDisplayName("First Relying party application");
		relyingParty.setDescription("First Relying party application");
		relyingParty.setTokenType("SAML_2_0");
		relyingParty.setTokenLifetime(600); // in seconds
		relyingParty.setAsymmetricTokenEncryptionRequired(false);
		// This call will add a Relying Party
		managementService.addEntity(relyingParty);
		// Query for the added Relying Party to get the reference which can be 
		// used later. Along with all other information this Relying Party 
		// reference will also have an associated Id.
		Query<RelyingParty> queryRP =
		    managementService.createRelyingPartyQuery("/RelyingParties").filter(
		    "Name eq '" + name + "'");
		relyingParty = queryRP.iterator().next();
		return relyingParty;
	}
	
	/**
	 * Assigns the identity providers to the relying party.
	 * 
	 * @param relyingParty
	 * @throws Exception
	 */
	private void assignIdentityProvider(RelyingParty relyingParty) throws Exception {
		// Get IdentityProviders from the OData service
		Query<IdentityProvider> queryIP = 
		    managementService.createIdentityProviderQuery("/IdentityProviders");
		RelyingPartyIdentityProvider relyingPartyIdentityProvider = null;
		for (IdentityProvider identityProvider : queryIP) {
		    relyingPartyIdentityProvider = new RelyingPartyIdentityProvider();
		    relyingPartyIdentityProvider.setIdentityProviderId(
		        identityProvider.getId());
		    // Here the relyingParty is the reference returned by the code                             
		    // which created the Relying Party.
		    relyingPartyIdentityProvider.setRelyingPartyId(relyingParty.getId());
		    managementService.addEntity(relyingPartyIdentityProvider);
		}
	}
	
	/**
	 * Assigns the rule group to the relying party.
	 * 
	 * @param relyingParty
	 * @throws Exception
	 */
	private void assignRuleGroup(RelyingParty relyingParty) throws Exception {
		// Here <Rule_Group_Name> will be the name of rule group already
		// created in ACS.
		Query<RuleGroup> queryRG =
		    managementService.createRuleGroupQuery("/RuleGroups").filter(
		        "Name eq '" + ruleGroupName + "'");
		RuleGroup ruleGroup = queryRG.iterator().next();
				
		RelyingPartyRuleGroup relyingPartyRuleGroup = new RelyingPartyRuleGroup();
		relyingPartyRuleGroup.setRuleGroupId(ruleGroup.getId());
		// Here the relyingParty is the reference returned by the code which                              
		// created the Relying Party.
		relyingPartyRuleGroup.setRelyingPartyId(relyingParty.getId());
		managementService.addEntity(relyingPartyRuleGroup);
	}
	
	/**
	 * Assigns relying party key and addresses to the relying party.
	 * 
	 * @param relyingParty
	 * @param name
	 * @throws Exception
	 */
	private void assignRelyingPartyKeyAndAddresses(
			RelyingParty relyingParty, String name) throws Exception {
		// create relying party key
		RelyingPartyKey relyingPartyKey = new RelyingPartyKey();
		relyingPartyKey.setDisplayName(name);
		// set whether a symmetric key or an X.509 certificate
		// is being used
		relyingPartyKey.setType("X509Certificate");
		// set whether the usage is for Signing or Encrypting the token
		relyingPartyKey.setUsage("Signing");
		// set the byte contents of entity being used (symmetric key or an 
		// X.509 certificate)		
		relyingPartyKey.setValue(getCertificateBytes(name));
		// Here the relyingParty is the reference returned by the code which                              
		// created the Relying Party.
		relyingPartyKey.setRelyingPartyId(relyingParty.getId());
		
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S z");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		String startDate = dateFormat.format(calendar.getTime());
		// set start date from which the certificate will be valid
		relyingPartyKey.setStartDate(dateFormat.parse(startDate));

		calendar.add(Calendar.DAY_OF_YEAR, 364);
		String endDate = dateFormat.format(calendar.getTime());
		//set the expiry date
		relyingPartyKey.setEndDate(dateFormat.parse(endDate));
				
		relyingPartyKey.setPassword(certPassword.getBytes());
		//set whether the certificate is primary or secondary.
		relyingPartyKey.setIsPrimary(true);
		managementService.addEntity(relyingPartyKey);
		
		assignRealm(relyingParty);
		assignReturnURL(relyingParty);
	}
	
	/**
	 * Returns the byte array of the certificate file.
	 * 
	 * @param name
	 * @return byte array of certificate file.
	 * @throws IOException
	 */
	private byte[] getCertificateBytes(String name) throws IOException {
		StringBuffer keytoolPath = new StringBuffer();
		keytoolPath.append(System.getProperty("java.home"));
        if (keytoolPath.length() == 0) {
        	keytoolPath.append(System.getenv("JRE_HOME"));
        }

        String destination = keytoolPath.toString();
        destination = destination + File.separator + "lib" + File.separator + "security" + File.separator;
		
		File certificateFile = new File(destination + name + "_keystore.pfx");
		byte[] byteArr = new byte[(int) certificateFile.length()];
		FileInputStream ipStream = new FileInputStream(certificateFile);
		ipStream.read(byteArr);
		return byteArr;
	}
	
	/**
	 * Assigns realm to the relying party.
	 * 
	 * @param relyingParty
	 * @throws Exception
	 */
	private void assignRealm(RelyingParty relyingParty) throws Exception {
		RelyingPartyAddress realm = new RelyingPartyAddress();
		realm.setAddress("myapp.cloudapp.net");
		realm.setEndpointType("Realm");
		// Here the relyingParty is the reference returned by the code which                              
		// created the Relying Party.
		realm.setRelyingPartyId(relyingParty.getId());
		managementService.addEntity(realm);
	}
	
	/**
	 * Assigns return URL to the relying party.
	 * 
	 * @param relyingParty
	 * @throws Exception
	 */
	private void assignReturnURL(RelyingParty relyingParty) throws Exception {
		RelyingPartyAddress returnURL = new RelyingPartyAddress();
		returnURL.setAddress("http://myapp.cloudapp.net/home");
		returnURL.setEndpointType("Reply");
		// Here the relyingParty is the reference returned by the code which                              
		// created the Relying Party.
		returnURL.setRelyingPartyId(relyingParty.getId());
		managementService.addEntity(returnURL);
	}

}
