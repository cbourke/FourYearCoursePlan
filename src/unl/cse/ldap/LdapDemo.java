package unl.cse.ldap;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.StartTlsRequest;
import javax.naming.ldap.StartTlsResponse;
import javax.net.ssl.SSLSession;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

public class LdapDemo {

	public static void main(String args[]) {
		
		String nuidAttributeName = "unlUNCWID";
		String username = "cbourke3";
		String passwd   = "";
		Hashtable<String, String> ldapEnv = new Hashtable<String, String>(10);
		ldapEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		ldapEnv.put(Context.PROVIDER_URL, "ldap://ldap.unl.edu");

		String ldapSearchBase = "ou=people,dc=unl,dc=edu";
		LdapContext ctx = null;
		 
		try {
			ctx = new InitialLdapContext(ldapEnv, null);
		 
			//initialize TLS
			StartTlsResponse tls = (StartTlsResponse) ctx.extendedOperation(new StartTlsRequest());
			SSLSession sess = tls.negotiate();
		 
			ctx.addToEnvironment(Context.SECURITY_AUTHENTICATION, "simple");
			ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, "uid="+username+","+ldapSearchBase);
			ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, passwd);
		         
			SearchResult r = findAccountByAccountName(ctx, ldapSearchBase, "s-kchiang2");
			Attributes a = r.getAttributes();
		 
//		         Attributes a = ctx.getAttributes("uid="+username);
		         if(a == null) {
		        	 System.out.println("no attributes");
		         } else {
		        	 for (NamingEnumeration ae = a.getAll(); ae.hasMore();) {
		        		 Attribute attr = (Attribute) ae.next();
		        		 System.out.println("attribute: " + attr.getID());

		        		 /* print each value */
		        		 for (NamingEnumeration e = attr.getAll(); e.hasMore(); System.out.println("\tvalue: " + e.next()));
		        	 }
		         }
		 }
		 catch(Exception e){
		         System.err.println("LDAP Client init error:");
		         e.printStackTrace();
		 }
		 
		 System.out.println("DONE");
	}
	
	public static SearchResult findAccountByAccountName(DirContext ctx, String ldapSearchBase, String accountName) throws NamingException {

        String searchFilter = "(&(objectClass=person)(uid=" + accountName + "))";

        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        NamingEnumeration<SearchResult> results = ctx.search(ldapSearchBase, searchFilter, searchControls);

        SearchResult searchResult = null;
        if(results.hasMoreElements()) {
             searchResult = (SearchResult) results.nextElement();

            //make sure there is not another item available, there should be only 1 match
            if(results.hasMoreElements()) {
                System.err.println("Matched multiple users for the accountName: " + accountName);
                return null;
            }
        }
        
        return searchResult;
    }
    
    
}
