package unl.cse.ldap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.StartTlsRequest;
import javax.naming.ldap.StartTlsResponse;
import javax.net.ssl.SSLSession;

import org.apache.log4j.Logger;

public class LdapData {

	private static org.apache.log4j.Logger log = Logger.getLogger(LdapData.class);

	private static final String LDAP_ACCOUNT_NAME = "";
	private static final String LDAP_ACCOUNT_PASSWORD = "";
	private static final String LDAP_URL = "ldap://ldap.unl.edu";
	private static final String LDAP_SEARCH_BASE = "ou=service,dc=unl,dc=edu";
	
	/*
	 * Full list of fields:
	 * http://www1.unl.edu/wdn/wiki/Identity_Management
	 */
	public static final String MY_UNL_LOGIN_ID_ATTR = "uid";
	public static final String FIRST_NAME_ATTR      = "givenName";
	public static final String LAST_NAME_ATTR       = "sn";
	public static final String NUID_ATTR            = "unlUNCWID";
	//public static final String EMAIL_ATTR           = "mail";
	public static final String MAJOR_ATTR           = "unlSISMajor";
	public static final String CLASS_LEVEL_ATTR     = "unlSISClassLevel";
	public static final String COLLEGE_ATTR         = "unlSISCollege";
	public static final String DEGREE_ATTR          = "unlSISDegree";
	
	@SuppressWarnings({ "rawtypes", "unused" })
	public static Map<String, Set<String>> getUserData(String myUnlLogin) {
		Map<String, Set<String>> attributes = new HashMap<String, Set<String>>();
		if(myUnlLogin == null) {
			log.warn("attempted to get LDAP data for null login");
			return attributes;
		}
		
		try {
			Hashtable<String, String> ldapEnv = new Hashtable<String, String>(10);
			ldapEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
			ldapEnv.put(Context.PROVIDER_URL, LDAP_URL);
			LdapContext ctx = new InitialLdapContext(ldapEnv, null);
			//initialize TLS, not necessary if we use ldaps?
			StartTlsResponse tls = (StartTlsResponse) ctx.extendedOperation(new StartTlsRequest());
			SSLSession sess = tls.negotiate();
	 
			ctx.addToEnvironment(Context.SECURITY_AUTHENTICATION, "simple");
			ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, "uid="+LDAP_ACCOUNT_NAME+","+LDAP_SEARCH_BASE);
			ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, LDAP_ACCOUNT_PASSWORD);
	         
	         //String searchFilter = "(&(objectClass=person)(uid=" + myUnlLogin + "))";
	         String searchFilter = "(uid=" + myUnlLogin + ")";
	         SearchControls searchControls = new SearchControls();
	         searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
	         NamingEnumeration<SearchResult> results = ctx.search("dc=unl,dc=edu", searchFilter, searchControls);

	         SearchResult searchResult = null;
	         if(results.hasMoreElements()) {
	              searchResult = (SearchResult) results.nextElement();
	             //make sure there is not another item available, there should be only 1 match
	             if(results.hasMoreElements()) {
	                 log.warn("Matched multiple users for the accountName: " + myUnlLogin);
	             }
	         }
	         	         
	         Attributes a = searchResult.getAttributes();
	 
	         if(a == null) {
	        	 log.info("no attributes found for user " + myUnlLogin);
	         } else {
	        	 for (NamingEnumeration ae = a.getAll(); ae.hasMore();) {
	        		 Attribute attr = (Attribute) ae.next();
	        		 Set<String> values = attributes.get(attr.getID());
	        		 if(values == null) {
	        			 values = new HashSet<String>();
	        			 attributes.put(attr.getID(), values);
	        		 }
	        		 for (NamingEnumeration e = attr.getAll(); e.hasMore(); values.add(e.next().toString()));
	        	 }
	         }
			
		} catch (Exception e) {
			log.error("Encountered Exception attempting to get LDAP data", e);
			return attributes;
		}
		return attributes;
	}
	
	public static void main(String args[]) {
		
		Map<String, Set<String>> attributes = LdapData.getUserData("s-kchiang2");
		for(String s : attributes.keySet()) {
			System.out.println(s);
			for(String a : attributes.get(s)) {
				System.out.println("  " + a);
			}
		}
		
		System.out.println("MY_UNL_LOGIN_ID_ATTR = " + attributes.get(LdapData.MY_UNL_LOGIN_ID_ATTR));
		System.out.println("FIRST_NAME_ATTR = " + attributes.get(LdapData.FIRST_NAME_ATTR));
		System.out.println("LAST_NAME_ATTR = " + attributes.get(LdapData.LAST_NAME_ATTR));
        System.out.println("NUID_ATTR = " + attributes.get(LdapData.NUID_ATTR));
		
	}
}
