package tools;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import sequence.DataSet;
import sequence.Sentence;

public class LabelledData2Sentences {
	
	OptionSet options;
	OptionParser parser;
	
	OptionSpec<String> labelledFileSp;
	OptionSpec<String> sentFileSp;	
	
	// Inputs	
	String labelledFile;
	
	// Outputs	
	String sentFile;
	
	
	DataSet ds;
	
	void init() {
		
		sentFile = "";
		labelledFile = "";
	}
	
	
	void loadSentences() {
		ds = new DataSet();
		ds.readLabelledData(labelledFile);
	}
	
	void writeSentences() {		
		
		ArrayList<Sentence> sents = ds.getSentences();
		
		BufferedWriter bw;
		
		try {
			FileWriter fw = new FileWriter(sentFile);
			bw = new BufferedWriter(fw); 
			
			Sentence s;
			StringBuffer buf;
			
			for (int i=0; i<sents.size(); i++) {
				buf = new StringBuffer();
				s = sents.get(i);
				for (int j=0; j<s.size(); j++) {
					buf.append(s.getToken(j).text+" ");
				}
				bw.write(buf.toString().trim()+"\n");				
			}
			
			bw.close();
		}		
		catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	// Prepare the options parser
	void prepareOptionsParser() {
		
        parser = new OptionParser();
        
        sentFileSp = parser.accepts( "sent" ).withRequiredArg().ofType( String.class )
        	.defaultsTo( "" ); 
        labelledFileSp = parser.accepts( "labelled" ).withRequiredArg().ofType( String.class )
            	.defaultsTo( "" ); 
	}

	
	void parseOptions(String[] args) {        
		options = parser.parse( args );
		
		sentFile = sentFileSp.value(options);
		labelledFile = labelledFileSp.value(options);

	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		LabelledData2Sentences lds = new LabelledData2Sentences();

		//String argss="-sent sents.txt -labelled i2b2_train.txt";
		
		//args = argss.split(" ");
		
		lds.prepareOptionsParser();
		lds.parseOptions(args);
		
		lds.loadSentences();
		lds.writeSentences();
	}

}
