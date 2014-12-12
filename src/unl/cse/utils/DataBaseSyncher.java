package unl.cse.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import unl.cse.ConnectionFactory;
import unl.cse.ScheduleDatabase;
import unl.cse.entities.User;
import unl.cse.ldap.UserSyncher;

public class DataBaseSyncher {

	private static org.apache.log4j.Logger log = Logger.getLogger(DataBaseSyncher.class);
	
	private static final int REPORT = 0;
	private static final int UPDATE = 1;
	private static int MODE = REPORT;

	public static void synchronizeName(User u) {
		String fullName = getFullName(u.getMyUnlLogin());
		if(fullName == null) {
			log.warn("Unknown User: " + u);
			return;
		}
		String nameTokens[] = fullName.split("\\s+");
		String lastName = null;
		String firstName = null;
		if(nameTokens.length == 2) {
			lastName = nameTokens[1];
			firstName = nameTokens[0];
		} else if(nameTokens.length == 3) {
			lastName = nameTokens[2];
			firstName = nameTokens[0];
		} else {
			log.warn("Unexpected number of tokens (fullName = " + fullName + " for user "+u);
			return;
		}

		if(!lastName.equals(u.getLastName()) || 
		   !firstName.equals(u.getFirstName())) {
			if(MODE == REPORT) {
				log.info("User ("+u+"): Names do not match: db: "+u.getLastName()+", "+u.getFirstName()+", directory: " + lastName +", "+ firstName);
			} else if(MODE == UPDATE) {
				log.info("User ("+u+"): Updating Name: "+u.getLastName()+", "+u.getFirstName()+" --> " + lastName +", "+ firstName);
				Connection conn = ConnectionFactory.getConnection();
				try {
					PreparedStatement ps = conn.prepareStatement("UPDATE User SET last_name = ?, first_name = ? WHERE user_id = ?");
					ps.setString(1, lastName);
					ps.setString(2, firstName);
					ps.setInt(3, u.getUserId());
					ps.executeUpdate();
					ps.close();
				} catch (Exception e) {
					log.error("Encountered exception", e);
				} finally {
					ConnectionFactory.closeConn(conn);
				}
			}
		}
	}
	
	/**
	 * Returns a String containing the full name of the user with the given 
	 * My.UNL user id.  Returns null upon failure.  Uses UNL's directory service
	 * at <a href="http://directory.unl.edu/">http://directory.unl.edu/</a>
	 * @param myUnlLogin
	 * @return
	 */
	private static String getFullName(String myUnlLogin) {
		String urlStr = "http://directory.unl.edu/?uid="+myUnlLogin+"&format=json";
		String rawJson = null;
		String name = null;
		try {
	        URL url = new URL(urlStr);
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        if(conn.getResponseCode() == 404) {
	        	log.debug("returned 404, no such user "+myUnlLogin);
	        	return null;
	        }
	        //URLConnection conn = url.openConnection();

	        //conn.setDoOutput(true);
            //OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            
            //write parameters
            //writer.write(data);
            //writer.flush();
            
            // Get the response
            StringBuffer response = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
            	response.append(line);
            }
            //writer.close();
            reader.close();

            rawJson = response.toString();
            //System.out.println(rawJson);
            
    		JsonParser jp = new JsonParser();
    		JsonObject root = jp.parse(rawJson).getAsJsonObject();
    		JsonElement cn = root.get("cn");
    		if(cn != null) {
    			JsonElement zero = cn.getAsJsonObject().get("0");
    			if(zero != null) {
    				name = zero.getAsString();
    			}
    		}
		} catch (Exception e) {
			log.error("Encountered Exception: (url = "+urlStr + ", rawJson = " + rawJson + ")", e);
			return null;
		}
		return name;
	}		
	
	public static void main(String args[]) {
		
		List<User> users = ScheduleDatabase.getAllUsers();
		for(User u : users) {
			UserSyncher.SynchronizeUser(u);
		}
		
	}
	
}
