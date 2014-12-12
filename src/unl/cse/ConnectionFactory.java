package unl.cse;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

public class ConnectionFactory {
	
	private static org.apache.log4j.Logger log = Logger.getLogger(ConnectionFactory.class);

	private static final String URL      = "jdbc:mysql://localhost/cse-courseplanner";
	private static final String USERNAME = "advising";
	private static final String PASSWORD = "";

	//Does not work: private static final String DATA_SOURCE = "cse-courseplanner-pool";
	//Untested: private static final String DATA_SOURCE = "jdbc/cse-courseplanner-pool";
	private static final String DATA_SOURCE = "java:comp/env/jdbc/cse-courseplanner-pool";
	private static DataSource dataSource = null;
	
	static {
		try {
			log.info("FYCP - Initializing ConnecitonFactory Context...");
			InitialContext ctx = new InitialContext();
			log.info("FYCP - Looking up data source " + DATA_SOURCE + "...");
			dataSource = (DataSource) ctx.lookup(DATA_SOURCE);
			log.info("FYCP - Data Source " + DATA_SOURCE + " successfully initialized.");
		} catch(Exception e) {
			log.error("not able to load initial context/data source ", e);
		}
	}

	public static Connection getConnection() {
		if(dataSource != null) {
			return getConnectionDataSource();
		} else {
			return getConnectionJDBC();
		}
	}
	
	private static Connection getConnectionDataSource() {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
		} catch (Exception e) {
			log.error("unable to get connection from data source: ", e);
		}
		return conn;
	}
	
	private static Connection getConnectionJDBC() {

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (InstantiationException e) {
			System.out.println("InstantiationException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			System.out.println("IllegalAccessException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			System.out.println("ClassNotFoundException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		return conn;

	}
	
	public static void closeConn(Connection c) {
		try {
			c.close();
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
