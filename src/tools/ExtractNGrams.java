package tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import core.DenseVector;
import core.TokenizerNGram;



public class ExtractNGrams {

	OptionSet options;
	OptionParser parser;
	
	OptionSpec<String> ngramFileSp;
	OptionSpec<String> sentFileSp;
	
	OptionSpec<Integer> numGramsSp;
	OptionSpec<Integer> maxNGramsSp;
	OptionSpec<Integer> skipSp;	

	String sentFile;
	String ngramFile;
	
	int numGrams;
	int maxNGrams;
	int skip;
	
	
	ArrayList<String> lines = new ArrayList<String>();
	HashSet<String> ngrams = new HashSet<String>();
	
	void init() {
		
		sentFile = "";
		ngramFile = "";
	}
	
	
	void readInputFile() {
		
		//String fileName = sentFile;
		BufferedReader br = null;
		 
		try { 
			String line;
			br = new BufferedReader(new FileReader(sentFile));
 			while ((line = br.readLine()) != null) {
				lines.add(line);
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void extractNGrams() {	
	
		String s;
		String frag;
		String strs[];
		DenseVector v;
		DenseVector scoreVector; 

		TokenizerNGram tkn = new TokenizerNGram();
		
		// Loop over lines
		for (int i=0; i<lines.size(); i++) {
			s = lines.get(i);
			
			// Tokens
			tkn.tokenize(s);

			for (int j=0; j<(tkn.getNumTokens()-(numGrams-1)); j+=skip) {
				ngrams.add(tkn.getNGram(j, numGrams).toLowerCase());
				if (ngrams.size()>=maxNGrams) break;
			}
			
			if (ngrams.size()>=maxNGrams) break;
		}	
	}
	
	void writeNGrams() {		
		
		BufferedWriter bw;
		
		try {
			FileWriter fw = new FileWriter(ngramFile);
			bw = new BufferedWriter(fw); 
			
			//String s;
			String[] strs;
			StringBuffer buf;
						
			for (String s : ngrams) {
				/*buf = new StringBuffer();
				s = ngrams.get(i);
				strs = s.split(" ");
				for (int j=0; j<strs.length; j++) {
					buf.append(strs[j]+" ");
				}*/
				bw.write(s.trim()+"\n");				
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
        ngramFileSp = parser.accepts( "ngrams" ).withRequiredArg().ofType( String.class )
            	.defaultsTo( "" ); 
    	numGramsSp = parser.accepts( "numgrams" ).withRequiredArg().ofType( Integer.class )
            	.defaultsTo( 2 );
    	maxNGramsSp = parser.accepts( "maxngrams" ).withRequiredArg().ofType( Integer.class )
            	.defaultsTo( 100 );
    	skipSp = parser.accepts( "skip" ).withRequiredArg().ofType( Integer.class )
            	.defaultsTo( 0 );
	}

	
	void parseOptions(String[] args) {        
		options = parser.parse( args );
		
		sentFile = sentFileSp.value(options);
		ngramFile = ngramFileSp.value(options);
		numGrams = numGramsSp.value(options);
		maxNGrams = maxNGramsSp.value(options);		
		skip = skipSp.value(options);

	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		ExtractNGrams ngms = new ExtractNGrams();

		//String argss="-sent sents.txt -ngrams ngrams.txt -numgrams 2"+
		//" -maxngrams 10000 -skip 2";
		
		//args = argss.split(" ");
		
		ngms.prepareOptionsParser();
		ngms.parseOptions(args);
		
		ngms.readInputFile();
		ngms.extractNGrams();
		ngms.writeNGrams();		
	}

}
