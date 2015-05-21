package unl.cse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class CourseUtils {

	private static org.apache.log4j.Logger log = Logger.getLogger(Course.class);
	
	private static final Map<String, Course> coursesByCatalog = new HashMap<String, Course>();

	static {
		Set<Course> dbCourses = getCoursesFromDatabase();
		for(Course c : dbCourses) {
			coursesByCatalog.put(c.getSubject()+c.getNumber(), c);
		}
	}
	
	public static final Comparator<Course> byNumber = new Comparator<Course>() {
		@Override
		public int compare(Course c1, Course c2) {
			int n = c1.getSubject().compareTo(c2.getSubject());
			if(n == 0) {
				return c1.getNumber().compareTo(c2.getNumber());
			} else {
				return n;
			}
		}
	};
	
	/**
	 * Returns the honors version of the given course.  If no honors
	 * version exists (not in the bulletin), <code>null</code> is 
	 * returned.
	 * @param c
	 * @return
	 */
	public static Course getHonorsVersion(Course c) {
		if(c.getNumber().endsWith("H")) {
			return c;
		} else {
			return getCourse(c.getSubject() + c.getNumber() + "H");
		}
	}
	
	public static Course getCourse(String subjectNumber) { 
		Course c = coursesByCatalog.get(subjectNumber);
		if(c == null) {
			//attempt to split it and get it:
			String subject = subjectNumber.substring(0, 4);
			String number  = subjectNumber.substring(4);
			c = getCourse(subject, number);
		}
		return c;
	}
	
	private static Course getCourse(String subject, String number) {
		Course c = coursesByCatalog.get(subject+number);
		if(c == null) {
			//not in our database, so let's get it from the bulletin and cache it
			c = getCourseFromBulletin(subject, number);
			if(c != null) {
				coursesByCatalog.put(c.getSubject()+c.getNumber(), c);
			} else {
				log.warn("Unable to get course, "+subject+" "+number+" from bulletin");
			}
		}
		return c;
	}
	
	public static boolean isSummerTerm(int termCode) {
		int month = (termCode % 10);
		return (month == 6 || month == 7);
	}
	
	public static String termCodeToString(Integer termCode) {
		String result = "";
		if(termCode == null) {
			return "invalid";
		} else if (termCode == Schedule.TRANSFER_CREDIT_TERM_CODE) {
			return "Transfer Credits";
		}
		int month = (termCode % 10);
		if(month == 1) {
			result = "Spring 20";
		} else if(month == 6 || month == 7){
			result = "Summer 20";
		} else if(month == 8) {
			result = "Fall 20";
		} else {
			return "invalid";
		}
		result += ((termCode / 10) % 100);
		return result;
	}
	

	public static Set<Course> getCoursesFromDatabase() {
		Set<Course> tmp = new HashSet<Course>();
			
		Connection conn = ConnectionFactory.getConnection();

		String query = "SELECT * FROM Course";
			
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();
			while(rs.next()) {
				Course c = new Course(
						rs.getInt("course_id"), 
						rs.getString("subject"),
						rs.getString("number"),
						rs.getString("title"),
						rs.getString("description"),
						rs.getString("prerequisite_text"),
						rs.getString("adviser_comment"), //needs to be adviser unless DB is changed
						rs.getInt("credit_hours"), 
						rs.getString("schedule"),
						rs.getString("ui_group"));
				tmp.add(c);
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			log.error("SQLException: ", e);
			throw new RuntimeException(e);
		} finally {
			ConnectionFactory.closeConn(conn);
		}

		return tmp;
	}

	private static Course getCourseFromBulletin(String subject, String number) {

		String urlStr = "http://bulletin.unl.edu/undergraduate/courses/"+subject+"/"+number+"?format=xml";
		String rawXml = null;
		try {
	        URL url = new URL(urlStr);
	        URLConnection conn = url.openConnection();

	        conn.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            
            //write parameters
            //writer.write(data);
            writer.flush();
            
            // Get the response
            StringBuffer response = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
            	response.append(line);
            }
            writer.close();
            reader.close();
            
            //Output the response
            rawXml = response.toString();            
        	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        	InputSource is = new InputSource(new StringReader(rawXml));
        	Document doc = dBuilder.parse(is);
        	doc.getDocumentElement().normalize();
         
        	NodeList nList = doc.getElementsByTagName("course");
        	if(nList.getLength() != 1) {
        		log.warn("multiple courses returned");
        	}
        	Node n = nList.item(0);
        	Element e = (Element) n;
        	String title = e.getElementsByTagName("title").item(0).getTextContent().trim();
        	String description = e.getElementsByTagName("description").item(0).getTextContent().trim();
        	Element prereqElem = (Element) e.getElementsByTagName("prerequisite").item(0);
        	String prerequisiteText = "";
        	if(prereqElem != null) {
        		prerequisiteText = prereqElem.getElementsByTagName("div").item(0).getTextContent().trim();
        	}
        	Integer creditHours = null;
        	Element creditElem = (Element) e.getElementsByTagName("credits").item(0);
        	NodeList creditNodes = creditElem.getElementsByTagName("credit");
        	for(int i=0; i<creditNodes.getLength(); i++) {
        		Element cElem = (Element) creditNodes.item(i);
        		if(cElem.getAttribute("type").equals("Single Value")) {
        			creditHours = Integer.parseInt(cElem.getTextContent().trim());
        		}
        	}

        	Course c = new Course(null, subject, number, title, description, prerequisiteText, null, creditHours, "fall,spring,even,odd", null);
        	return c;
            
		} catch (Exception e) {
			log.error("Encountered Exception: (url = "+urlStr + ", rawXml = " + rawXml + ")", e);
			return null;
		}
	}
		
}
