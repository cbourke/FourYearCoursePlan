package unl.cse.ldap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import unl.cse.ConnectionFactory;
import unl.cse.entities.User;

public class UserSyncher {

	private static org.apache.log4j.Logger log = Logger.getLogger(UserSyncher.class);

	/**
	 * Updates the local database's record for the given {@link User} using data
	 * pulled from UNL's LDAP.  Specifically, this method updates the last name, 
	 * first name, and NUID fields if they differ.
	 * @param u
	 */
	public static void SynchronizeUser(User u) {
		if(u == null) {
			log.warn("invalid user (null)");
			return;
		}
		Map<String, Set<String>> attributes = LdapData.getUserData(u.getMyUnlLogin());
		if(attributes == null || attributes.keySet().isEmpty()) {
			log.warn("unable to get user data from LDAP for "+u.getMyUnlLogin());
			return;
		}
		if(attributes.get(LdapData.MY_UNL_LOGIN_ID_ATTR) == null) {
			log.warn("LDAP data does not include data for "+LdapData.MY_UNL_LOGIN_ID_ATTR);
			return;
		}
		if(!attributes.get(LdapData.MY_UNL_LOGIN_ID_ATTR).contains(u.getMyUnlLogin())) {
			log.warn("requested LDAP data for " + u.getMyUnlLogin() + " but recieved data for "+attributes.get(LdapData.MY_UNL_LOGIN_ID_ATTR));
			return;
		}

		String firstName = null;
		String lastName = null;
		String nuid = null;
		
		Set<String> tmpSet = null;

		tmpSet = attributes.get(LdapData.FIRST_NAME_ATTR);
		if(tmpSet == null) {
			log.warn("LDAP("+u.getMyUnlLogin()+") - First Name attribute is missing");
		} else if(tmpSet.size() != 1) {
			log.warn("LDAP("+u.getMyUnlLogin()+") - First Name data contains unexpected number of values");
		} else {
			firstName = tmpSet.iterator().next();
		}
		
		tmpSet = attributes.get(LdapData.LAST_NAME_ATTR);
		if(tmpSet == null) {
			log.warn("LDAP("+u.getMyUnlLogin()+") - Last Name attribute is missing");
		} else if(tmpSet.size() != 1) {
			log.warn("LDAP("+u.getMyUnlLogin()+") - Last Name data contains unexpected number of values");
		} else {
			lastName = tmpSet.iterator().next();
		}

		tmpSet = attributes.get(LdapData.NUID_ATTR);
		if(tmpSet == null) {
			log.warn("LDAP("+u.getMyUnlLogin()+") - NUID attribute is missing");
		} else if(tmpSet.size() != 1) {
			log.warn("LDAP("+u.getMyUnlLogin()+") - NUID data contains unexpected number of values");
		} else {
			nuid = tmpSet.iterator().next();
		}

		if(firstName == null || lastName == null || nuid == null) {
			log.warn("Missing LDAP data for " + u.getMyUnlLogin() + ": firstName = " + firstName + ",  lastName = " + lastName + ", " + nuid + ", not performing update");
			return;
		}
		
		if(!firstName.equals(u.getFirstName()) || !lastName.equals(u.getLastName()) || !nuid.equals(u.getNuid())) {
			log.info("Updating User " + u.getMyUnlLogin() + ": " +
					 "firstName: " + u.getFirstName() + " -> " + firstName + ", " +
					 "lastName: " + u.getLastName() + " -> " + lastName + ", " +
					 "nuid: " + u.getNuid() + " -> " + nuid);
			Connection conn = ConnectionFactory.getConnection();
			try {
				PreparedStatement ps = conn.prepareStatement("UPDATE User SET last_name = ?, first_name = ?, nuid = ? WHERE user_id = ?");
				ps.setString(1, lastName);
				ps.setString(2, firstName);
				ps.setString(3, nuid);
				ps.setInt(4, u.getUserId());
				ps.executeUpdate();
				ps.close();
			} catch (Exception e) {
				log.error("Encountered exception", e);
			} finally {
				ConnectionFactory.closeConn(conn);
			}
		} else {
			log.info("User " + u.getMyUnlLogin() + " is up-to-date");
		}
	}
}
