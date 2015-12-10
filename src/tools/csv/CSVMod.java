package tools.csv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;


/*
 * This class provides methods for working with CSV-like files containing
 * features.
 * 
 * It is far from optimized and far from safe and requires more work.
 * 
 */

public class CSVMod {
	
	OptionSet options;
	OptionParser parser;
	
	OptionSpec<String> opSp;
	
	OptionSpec<String> in1Sp;
	OptionSpec<String> in2Sp;
	OptionSpec<String> outSp;
	
	OptionSpec<Integer> colNumSp;
	
	String op;
	String in1;
	String in2;
	String out;
	
	int colNum;
	//int colNum2;

	String delim = " ";
	
	ArrayList<ArrayList<String>> tab1 = new ArrayList<ArrayList<String>>();
	ArrayList<ArrayList<String>> tab2 = new ArrayList<ArrayList<String>>();	
	
	ArrayList<String> col = new ArrayList<String>();	
	
	void writeCol(ArrayList<String> col, String fileName) {
		
        try {

            FileWriter fw = new FileWriter(fileName);
            BufferedWriter bw = new BufferedWriter(fw);
            
            for (String s : col) {
            	bw.write(s+"\n");
            }            
            
            bw.close();            
        }
        catch(Exception e) {
        	e.printStackTrace();
        }
	}

	void writeTable(ArrayList<ArrayList<String>> tab, String fileName) {
		
        try {

            FileWriter fw = new FileWriter(fileName);
            BufferedWriter bw = new BufferedWriter(fw);
            
    		for (ArrayList<String> l : tab) {
    			
    			if (l.size()==0) {
    				bw.write("\n");
    			}
    			else {
    				
    				for (int i=0; i<l.size(); i++) {
    					
    					if (i<(l.size()-1)) {
    						bw.write(l.get(i)+delim);
    					}
    					else bw.write(l.get(i));
    				}
    			
    				bw.write("\n");
    			}
            }            
            
            bw.close();            
        }
        catch(Exception e) {
        	e.printStackTrace();
        }
	}
	
	// Insert Column
	void insertColumn(ArrayList<ArrayList<String>> from, ArrayList<ArrayList<String>> to,
			ArrayList<String> col, int c) {
		
		int rowCount = 0;
		
		for (ArrayList<String> l : from) {
			
			int count = 0;
			
			if (l.size()==0) { // Empty row
				to.add(new ArrayList<String>());				
			}
			else {
				
				ArrayList<String> row = new ArrayList<String>();
				
				for (int i=0; i<l.size(); i++) {
					
					if (i==c) {
						row.add(col.get(rowCount));
					}
					
					row.add(l.get(i));
				}
				
				if (c==l.size()) {
					row.add(col.get(rowCount));
				}
				
				to.add(row);
			}
			
			rowCount++;
		}			
	}
	
	
	// Extract Column
	ArrayList<String> extractColumn(ArrayList<ArrayList<String>> table, int c) {
	
		ArrayList<String> col = new ArrayList<String>();
		
		for (ArrayList<String> l : table) {		
			if (l.size()==0) {
				col.add("");
			}
			else {
				col.add(l.get(c));
			}
		}		
		
		return col;
	}
	
	
	// Read Table
	void readTable(String fileName, ArrayList<ArrayList<String>> table) {
		
		int rowSize = 0;
		
		BufferedReader br = null;

		try {

			String line;
			String[] strs;
			
			br = new BufferedReader(new FileReader(fileName));

			// Get row size			
			line = br.readLine();
			line = line.trim();
			if (line.length()<1) return;
			
			strs = line.split(delim);
			rowSize = strs.length;
			
			br.close(); // Don't like doing this
			br = new BufferedReader(new FileReader(fileName));
			
			// Read file
			while ((line = br.readLine()) != null) {
				line = line.trim();
				
				if (line.length()<1) { // Empty row
					table.add(new ArrayList<String>());
					continue;
				}
				
				strs = line.split(delim);
				ArrayList<String> row = new ArrayList<String>();
				for (String s : strs) {
					row.add(s);
				}
				
				table.add(row);				
			}

			br.close();
			
			System.out.println(table.size());
			
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	// Read Column
	void readColumn(String fileName, ArrayList<String> column) {
		
		int rowSize = 0;
		
		BufferedReader br = null;

		try {

			String line;
			String[] strs;
			
			br = new BufferedReader(new FileReader(fileName));
			
			// Read file
			while ((line = br.readLine()) != null) {
				line = line.trim();
				
				if (line.length()<1) { // Empty row
					column.add("");
					continue;
				}
				column.add(line);				
			}

			br.close();
			
			System.out.println(column.size());
			
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	// Add table
	void addTable(String fileName, ArrayList<ArrayList<String>> table) {
		
		int rowSize = 0;
		
		BufferedReader br = null;

		try {

			String line;
			String[] strs;
			
			br = new BufferedReader(new FileReader(fileName));

			// Get row size			
			line = br.readLine();
			line = line.trim();
			if (line.length()<1) return;
			
			strs = line.split(delim);
			rowSize = strs.length;
			
			br.close(); // Don't like doing this
			br = new BufferedReader(new FileReader(fileName));
			
			int rowCount = 0;
			
			// Read file
			while ((line = br.readLine()) != null) {
				line = line.trim();
				
				if (line.length()<1) { // Empty row
					//table.add(new ArrayList<String>());
					//continue;
				}
				else {				
					strs = line.split(delim);
					ArrayList<String> row = table.get(rowCount);
					for (String s : strs) {
						row.add(s);
					}
					
					//table.add(row);
				}
				
				rowCount++;
			}

			br.close();
			
			System.out.println(table.size());
			
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	// Prepare the options parser
	void prepareOptionsParser() {
		
        parser = new OptionParser();
        
    	opSp = parser.accepts( "op" ).withRequiredArg().ofType( String.class )
        	.defaultsTo( "" );        
    	in1Sp = parser.accepts( "in1" ).withRequiredArg().ofType( String.class )
        	.defaultsTo( "" );
    	in2Sp = parser.accepts( "in2" ).withRequiredArg().ofType( String.class )
            	.defaultsTo( "" );
    	outSp = parser.accepts( "out" ).withRequiredArg().ofType( String.class )
            	.defaultsTo( "" );
        colNumSp = parser.accepts( "col" ).withRequiredArg().ofType( Integer.class )
            	.defaultsTo( -1 );
	}

	
	void parseOptions(String[] args) {        
		options = parser.parse( args );
		
		op = opSp.value(options);
		in1 = in1Sp.value(options);
		in2 = in2Sp.value(options);
		out = outSp.value(options);
		colNum = colNumSp.value(options);
	}
	
	void run() {
		
		if (op.equals("insert")) {
			
			readTable(in1, tab1);			
			readColumn(in2, col);
			insertColumn(tab1, tab2, col, colNum);
			writeTable(tab2, out);
		}

		else if (op.equals("merge")) {
			
			readTable(in1, tab1);
			addTable(in2, tab1);
			writeTable(tab1, out);
		}		
		
		else if (op.equals("extract")) {
			
			readTable(in1, tab1);
			col = extractColumn(tab1, colNum);
			writeCol(col, out);
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		//String argss="-op insert -in1 i2b2_train.txt -in2 col.txt -col 1 -out out.txt";
		
		//args = argss.split(" ");
		
		CSVMod mod = new CSVMod();
		
		mod.prepareOptionsParser();
		mod.parseOptions(args);
		
		mod.run();
		
	}

}



