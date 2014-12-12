package unl.cse.utils;

import java.io.File;
import java.util.Scanner;

public class PopulateDatabase {

	//TODO: develop a CSV parser to load student data
	public static void main(String args[]) {
		try {
			Scanner s = new Scanner(new File("WebContent/data/census.csv"));
			while(s.hasNext()) {
				String line = s.nextLine();
				String tokens[] = line.split(",");
				if(tokens.length != 3) {
					System.out.println("invalid tokens: " + line);
				}
				//TODO: update or create the user in the database
				//User u = new User(null, null, tokens[0], null, tokens[2], tokens[1], false);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
}
