package tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Random;

import clustering.Cluster;

import core.DenseVector;
import core.VSpaceNGramsChar;
import core.VSpaceTerms;
import core.VecBuilder;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;


public class MakeSeqVecs {

	
	OptionSet options;
	OptionParser parser;
	
	OptionSpec<String> seqFileSp;
	OptionSpec<String> seqVecFileSp;	
	OptionSpec<String> wordVecFileSp;
	OptionSpec<Integer> lexDimSp;
	
	
	String seqVecFile;
	String seqFile;
	String wordVecFile;
	int lexDim;
	int semDim;

	
	Random rng = new Random();
	
	ArrayList<String> lines = new ArrayList<String>();

	VecBuilder vBuilder;
	
	ArrayList<DenseVector> seqVecs = new ArrayList<DenseVector>();
	
	
	public MakeSeqVecs() {
		
		seqFile = "";
		wordVecFile = "";
		lexDim = 0;
		semDim = 0;
		seqVecFile = "";
	}	
	
	void init() {
		
		vBuilder = new VecBuilder();

		vBuilder.setLexDim(lexDim);
		vBuilder.prepareLexSpace();
		vBuilder.prepareTermSpace(wordVecFile);
		
	}
	
	
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
	}
	
	void genSeqVecs() {
		
		String sl;
		DenseVector v;
		
		String[] strs;
		
		for (int i=0; i<lines.size(); i++) {
			sl = lines.get(i).toLowerCase().trim();
			v = vBuilder.vecForStringSeq(sl, false);
			v.id = i;
			seqVecs.add(v);
		}
		
	}
	
	void writeVectors() {				
		
		if (!(seqVecs.size()>0)) return;
		
		try { 
			File file = new File(seqVecFile);
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			bw.write(""+seqVecs.size()+" "+seqVecs.get(0).size()+"\n");
			
			for (DenseVector v : seqVecs) {
				//System.out.println(v.norm());
				v.writeText(bw);
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
        
        seqFileSp = parser.accepts( "seqfile" ).withRequiredArg().ofType( String.class )
        	.defaultsTo( "" );        
        seqVecFileSp = parser.accepts( "seqvecs" ).withRequiredArg().ofType( String.class )
        	.defaultsTo( "" );
        wordVecFileSp = parser.accepts( "wordvecs" ).withRequiredArg().ofType( String.class )
            	.defaultsTo( "" );
        lexDimSp = parser.accepts( "lexdim" ).withRequiredArg().ofType( Integer.class )
            	.defaultsTo( 0 );
	}

	
	void parseOptions(String[] args) {        
		options = parser.parse( args );
		
		seqFile = seqFileSp.value(options);
		seqVecFile = seqVecFileSp.value(options);
		wordVecFile = wordVecFileSp.value(options);
		lexDim = lexDimSp.value(options);
	}
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MakeSeqVecs msv = new MakeSeqVecs();
		
		//String argss="-seqfile ngrams.txt -seqvecs seqvecs.txt"
		//		+ " -wordvecs vectors_PM_100_2.txt -lexdim 30";
		
		//args = argss.split(" ");
		
		msv.prepareOptionsParser();
		msv.parseOptions(args);
		
		msv.init();
		msv.readSequences();
		msv.genSeqVecs();
		msv.writeVectors();

	}

}
