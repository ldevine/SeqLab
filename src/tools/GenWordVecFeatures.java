package tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import sequence.DataSet;
import sequence.NGram;
import sequence.Sentence;
import sequence.Token;
import core.DenseVector;
import core.VSpaceTerms;
import core.VecBuilder;
import core.Vocab;

public class GenWordVecFeatures {

	OptionSet options;
	OptionParser parser;
		
	OptionSpec<String> centroidFileSp;
	OptionSpec<String> labelledFileSp;	
	OptionSpec<String> wordVecFileSp;
	OptionSpec<String> outFileSp;
	
	OptionSpec<Integer> lexDimSp;
	
	OptionSpec<Integer> startIdxSp;
	OptionSpec<Integer> numWordsSp;
	
	String centroidFile;
	String labelledFile;
	String wordVecFile;
	String outFile;
	
	int startIdx;
	int numWords;
	
	int lexDim;
	int semDim;
	
	DataSet ds;
	
	
	VecBuilder vBuilder = null;
	//VSpaceTerms vst = null;
	
	
	void GenBiGramVecFeatures() {
		
		startIdx=-1000;
		numWords = 2;
		lexDim=0;
	}
	

	ArrayList<String> lines = new ArrayList<String>();
	
	ArrayList<DenseVector> centroids = new ArrayList<DenseVector>();
	ArrayList<Sentence> seqs;
	
	void init() {
		
		ds = new DataSet();
		ds.readLabelledData(labelledFile);
		seqs = ds.getSentences();
		
		vBuilder = new VecBuilder();

		vBuilder.setLexDim(lexDim);
		//vBuilder.prepareLexSpace();
		vBuilder.prepareTermSpace(wordVecFile);
		
	}
	
	
	void loadCentroids() {
			
		Vocab v;
		BufferedReader br = null;
		DenseVector vec;
		String strs[];
		
		int dim;
		int numVectors;
		int termCount = 0;
		
		try { 
			String line; 
			br = new BufferedReader(new FileReader(centroidFile));
 
			// Parse first line
			line = br.readLine();
			strs = line.split(" ");
			numVectors = Integer.parseInt(strs[0]);
			dim = Integer.parseInt(strs[1]);
			
			System.out.println(""+numVectors+"  "+dim);
			
			while ((line = br.readLine()) != null) {
				
				strs = line.split(" ");
				
				// Create new vector
				vec = new DenseVector(dim);
				
				for (int i=0; i<strs.length; i++) {
					vec.setVal(i, Float.parseFloat(strs[i]));					
				}	
				
				vec.normalize();
				
				// Add vector
				centroids.add(vec);				
			}
 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	void readSequences() {		

		BufferedReader br = null;
		 
		try { 
			String line;
			br = new BufferedReader(new FileReader(seqFile));
 			while ((line = br.readLine()) != null) {
				lines.add(line);
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	
	int getWordFeature(String s1) {
		
		NGram ng;
		DenseVector vec;
		
		VSpaceTerms vst = vBuilder.getTermSpace();
		
		if (!vst.hasTerm(s1)) {
			return 0;
		}
		
		vec = vst.getTermVector(s1);
		
		// Match against cluster centroid
		float dist;
		int idx = 0;
		int count = 0;
		float min = Float.MAX_VALUE;
		for (DenseVector v : centroids) {
			
			//dist = Math.abs(vec.cosine(cl.getCentroid()));
			
			dist = vec.euclideanDistanceSquared(v);
			if (dist<=min) {
				min = dist;
				idx = count;
			}
			count++;
		}
		
		return idx;
	}
		
	void processSeq(Sentence s) {

		int fVal;
		
		Token tok, toko1, toko2;
		NGram ng;
		DenseVector vec;
		
		int tIdx;

		ArrayList<Integer> feats = new ArrayList<Integer>();
		
		for (int i=0; i<s.size(); i++) {
			fVal = getWordFeature(s.getToken(i).text.toLowerCase());
			feats.add(fVal);
		}
		
		for (int i=0; i<s.size(); i++) {
			
			tok = s.getToken(i);
			
			for (int j=0; j<numWords; j++) {
				tIdx = i+startIdx+j;
				if ((tIdx)<0) {
					tok.addFeature("0");
				}
				else if ((tIdx)>=s.size()) {
					tok.addFeature("0");					
				}
				else {
					tok.addFeature(""+feats.get(tIdx));
				}
			}						
		}			
	}
	
	void process() {
		
		for (Sentence s : seqs) {
			processSeq(s);
		}
	}
	
	void writeFeatures() {	

		//char sep = '\t';
		
		Token tok;
		String feature;
		int numFeatures;
		
		try {
			FileWriter fw = new FileWriter(outFile);
			BufferedWriter bw = new BufferedWriter(fw);
			
			for (Sentence s : seqs) {
				
				for (int i=0; i<s.size(); i++) {
					
					tok = s.getToken(i);
					//bw.write(tok.text);
					
					// Write feature						
					numFeatures = tok.numFeatures();
					
					bw.write(tok.text);		

					for (int j=0; j<numFeatures; j++) {
						bw.write(" "+tok.getFeatureVal(j));						
						
					}
					
					// Write tag 	
					//bw.write(" " + tok.tag.label);
					
					bw.write("\n");
				}
				
				bw.write("\n");
			}
			
			bw.close();
 
			System.out.println("Done");
 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Prepare the options parser
	void prepareOptionsParser() {
		
        parser = new OptionParser();
        
        centroidFileSp = parser.accepts( "centroids" ).withRequiredArg().ofType( String.class )
            	.defaultsTo( "" );
        
        labelledFileSp = parser.accepts( "labelledfile" ).withRequiredArg().ofType( String.class )
        	.defaultsTo( "" );        
        
        wordVecFileSp = parser.accepts( "wordvecs" ).withRequiredArg().ofType( String.class )
            	.defaultsTo( "" );
        outFileSp = parser.accepts( "out" ).withRequiredArg().ofType( String.class )
            	.defaultsTo( "" );
        
        //lexDimSp = parser.accepts( "lexdim" ).withRequiredArg().ofType( Integer.class )
        //    	.defaultsTo( 0 );
        
        startIdxSp = parser.accepts( "startidx" ).withRequiredArg().ofType( Integer.class )
            	.defaultsTo( -1000 );
        numWordsSp = parser.accepts( "numwords" ).withRequiredArg().ofType( Integer.class )
            	.defaultsTo( 2 );        
	}

	
	void parseOptions(String[] args) {        
		options = parser.parse( args );
		
		outFile = outFileSp.value(options);
		centroidFile = centroidFileSp.value(options);
		labelledFile = labelledFileSp.value(options);
		wordVecFile = wordVecFileSp.value(options);
		//lexDim = lexDimSp.value(options);
		startIdx =  startIdxSp.value(options);
		numWords = numWordsSp.value(options);
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GenWordVecFeatures gbf = new GenWordVecFeatures();
		
		//String argss="-labelledfile i2b2_train.txt -centroids centroids.txt -startidx -1"
		//		+ " -numwords 3 -wordvecs vectors_PM_100_2.txt -out features.txt";
		
		//args = argss.split(" ");
		
		gbf.prepareOptionsParser();
		gbf.parseOptions(args);
		
		gbf.init();
		//gbf.readSequences();
		gbf.loadCentroids();
		gbf.process();
		gbf.writeFeatures();
	}

}


