package unl.cse.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;

public class DataFileProcessor {

	private final boolean containsHeader;
	private final String fileName;
	private final String delimiter;
	private final List<String> columns;
	private final List<LinkedHashMap<String, String>> records;
	
	public DataFileProcessor(String fileName, String delimiter, boolean containsHeader) {
		this.fileName = fileName;
		this.delimiter = delimiter;
		this.containsHeader = containsHeader;
		this.columns = new ArrayList<String>();
		this.records = new ArrayList<LinkedHashMap<String, String>>();
		this.process();
	}

	public DataFileProcessor(String fileName) {
		this(fileName, ",", true);
	}
	
	private void process() {
		try {
			Scanner s = new Scanner(new File(this.fileName));
			String line;
			String tokens[];
			
			if(this.containsHeader) {
				line = s.nextLine();
				tokens = line.split(this.delimiter);
				for(String tok : tokens) {
					this.columns.add(tok);
				}
			}

			while(s.hasNext()) {
				line = s.nextLine();
				if(!line.trim().isEmpty()) {
					tokens = line.split(this.delimiter);
					if(this.columns.size() == 0) {
						for(int i=0; i<tokens.length; i++) {
							this.columns.add(String.valueOf(i));
						}
					}
					LinkedHashMap<String, String> values = new LinkedHashMap<String, String>();
					if(tokens.length != this.columns.size()) {
						//log.warn"line contains invalid number of tokens (expected " + this.columns.size() + ", encountered "+tokens.length +": " + line);
					} else {
						for(int i=0; i<tokens.length; i++) {
							values.put(this.columns.get(i), tokens[i].trim());
						}
						this.records.add(values);
					}
				} else {
					//log.warn("line is empty: " + line);
				}
			}
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}	
	}
	
	public List<String> getColumns() {
		return columns;
	}

	public List<LinkedHashMap<String, String>> getRecords() {
		return records;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		int i=1;
		for(LinkedHashMap<String, String> values : this.records) {
			sb.append(i).append(": \n");
			for(String col : this.columns) {
				sb.append("   ").append(col).append(": ").append(values.get(col)).append("\n");
			}
			i++;
		}
		return sb.toString();
	}
	
	public static void main(String args[]) {
		DataFileProcessor p = new DataFileProcessor("sql/test.csv", ",", false);
		System.out.println(p);
	}
}
