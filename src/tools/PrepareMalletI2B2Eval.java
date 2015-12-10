package tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import sequence.DataSet;
import sequence.Sentence;
import sequence.Tag;



public class PrepareMalletI2B2Eval {

	
	OptionSet options;
	OptionParser parser;
	
	OptionSpec<String> testDataSp;
	OptionSpec<String> taggedDirSp;	
	
	String testData;
	String taggedDir;

	
	DataSet testDS;
	DataSet taggedDS;
	
	int fileCount = 0;
	String filePrefix = "";
	
	BufferedWriter bw;//1;//, bw2;
	FileWriter fw;
	
	void init() {
		
		try {			
			fw = new FileWriter(getFileName());			
			bw = new BufferedWriter(fw);		
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	void cleanUp() {
		try {
			bw.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	String getFileName() {
		return filePrefix+"tagged"+fileCount+".con";
	}
		
	void commitToFile() {
		try {
			bw.close();
			
			fileCount++;
			
			fw = new FileWriter(getFileName());			
			bw = new BufferedWriter(fw);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	void writeStringToFile(String str) {
		
		try {
			bw.write(str);
		}
		catch(Exception e) {
			e.printStackTrace();
		}		
	}
	
	void processSentences(ArrayList<Sentence> sent) {
		
		int start = 0;
		String label = "";
		Sentence s;
		Tag tag;
		boolean span;
		
		StringBuffer buf = new StringBuffer();
		
		for (int i=0; i<sent.size(); i++) {
			
			if (i%5000==0) {
				commitToFile();
				//buf = new StringBuffer();
			}
			
			s = sent.get(i);
			span = false;
			//System.out.println(i);
			
			buf = new StringBuffer();
			
			for (int j=0; j<s.size(); j++) {
				tag = s.getTag(j);
				if (!tag.label.equals("O")) {
					
					// Unknown tag
					if (tag.label.length()<3) {
						continue;
					}
					
					if (!span) {
						span = true;
						start = j;						
						//buf = new StringBuffer();
						buf.append("c=\" \"");
						
						label = tag.label.substring(2);
						
						if (j==s.size()-1) {						
							buf.append(" "+(i+1)+":"+(start+1));
							buf.append(" "+(i+1)+":"+(j+1));
							buf.append("||t=\""+label+"\"");
							buf.append("\n");
						}
					}
					else if (j==s.size()-1) {						
						buf.append(" "+(i+1)+":"+(start+1));
						buf.append(" "+(i+1)+":"+(j+1));
						buf.append("||t=\""+label+"\"");
						buf.append("\n");
					}
				}
				else if (span) {
					span = false;
					buf.append(" "+(i+1)+":"+(start+1));
					buf.append(" "+(i+1)+":"+(j));
					buf.append("||t=\""+label+"\"");
					buf.append("\n");
				}
			}
			
			writeStringToFile(buf.toString());
		}		
	}
	
	
	void loadData() {		
		testDS = new DataSet();
		testDS.setDelimiter("\t");
		testDS.readLabelledData(testData);
		
		taggedDS = new DataSet();
		taggedDS.setDelimiter("\t");
		loadDataDirectory(taggedDir, taggedDS);
		
		System.out.println(testDS.getSentences().size());
		System.out.println(taggedDS.getSentences().size());		
		
		//testDS.readLabelledData("i2b2_test_data_labeled.txt");
	}
	
	void loadDataDirectory(String dir, DataSet ds) {
	
		File[] files = new File(dir).listFiles();
		
		// Sort files
		String path;
		//HashMap<String, File> fileMap = new HashMap<String, File>();
		ArrayList<String> paths = new ArrayList<String>();
		for (File file : files) {
			path = file.getAbsolutePath();
			//fileMap.put(path, file);
			paths.add(path);
		}
		
		Collections.sort(paths);
		
	    
		for (String p : paths) {
			//System.out.println(p);
			ds.readLabelledData(p);
	    }		
	}
	
	
	void process() {
		fileCount = 0;
		filePrefix = "./ref/";
		processSentences(testDS.getSentences());
		
		fileCount = 0;
		filePrefix = "./sys/";
		processSentences(taggedDS.getSentences());
	}
	
	
	// Prepare the options parser
	void prepareOptionsParser() {
		
        parser = new OptionParser();
        
        testDataSp = parser.accepts( "testdata" ).withRequiredArg().ofType( String.class )
        	.defaultsTo( "" );        
        taggedDirSp = parser.accepts( "taggeddir" ).withRequiredArg().ofType( String.class )
        	.defaultsTo( "" );
	}

	
	void parseOptions(String[] args) {        
		options = parser.parse( args );
		
		testData = testDataSp.value(options);
		taggedDir = taggedDirSp.value(options);
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		PrepareMalletI2B2Eval pm = new PrepareMalletI2B2Eval();
		
		//String argss="-testdata i2b2_test_data_labeled.txt -taggeddir ./TaggedFiles";
		
		//args = argss.split(" ");
		
		pm.prepareOptionsParser();
		pm.parseOptions(args);
		
		pm.loadData();
		pm.init();
		pm.process();
		pm.cleanUp();

	}

}
