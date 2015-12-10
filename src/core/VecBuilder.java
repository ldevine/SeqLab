package core;

import java.util.ArrayList;
import java.util.Random;

import sequence.NGram;
import sequence.Sentence;
import sequence.Token;





public class VecBuilder {

	int lexVecDim = 10;
	int semVecDim = 100;
	
	VSpaceNGramsChar lexSpace;	
	VSpaceTerms termSpace = new VSpaceTerms();
	
	Random rng = new Random();
	
	ArrayList<DenseVector> termVecs = new ArrayList<DenseVector>();	
	
	ArrayList<DenseVector> vecs = new ArrayList<DenseVector>();
	
	public VSpaceTerms getTermSpace() {
		return termSpace;
	}
	
	public void setLexDim(int ld) {
		lexVecDim = ld;
	}
	
	public void prepareLexSpace() {
		
		lexSpace = new VSpaceNGramsChar(lexVecDim);			
		for (int i=33; i<127; i++) lexSpace.addChar((char)i);		
		lexSpace.setCharVectors();		
	}
	
	public void prepareTermSpace(String fileName) {

		DenseVector v;		
		
		termSpace.readWord2VecFile(fileName, null);
		
		int numTerms = termSpace.getVocabSize();
		
		for (int i=0; i<numTerms; i++) {
			v = termSpace.getVectorByID(i);
			termVecs.add(v);
			v.normalize();
		}
	}

	public DenseVector vecForStringSeq(String seq, boolean concat) {
		
		//DenseVector
		DenseVector vec;
		
		//ArrayList<Token> tokens = n.tokens;
		
		String[] strs;
		strs = seq.split(" ");
		
		if (strs.length==0) return null;
		
		vec = vecForTokenString(strs[0], true);
		
		for (int i=1; i<strs.length; i++) {
			if (concat) {
				vec = VecUtils.concatenate(vec, vecForTokenString(strs[i], true));			
			}
			else {
				vec.addScale(vecForTokenString(strs[i], true), 1.0f);
			}
		}
		
		//System.out.println(vec.size());
		vec.normalize();
		
		return vec;		
	}
	
	public DenseVector vecForSentence(Sentence s, boolean concat) {
		
		//DenseVector
		DenseVector vec;
		
		ArrayList<Token> tokens = s.getTokens();
		
		if (tokens.size()==0) return null;
		vec = vecForToken(tokens.get(0), true);
		
		for (int i=1; i<tokens.size(); i++) {
			if (concat) {
				vec = VecUtils.concatenate(vec, vecForToken(tokens.get(i), true));			
			}
			else {
				vec.addScale(vecForToken(tokens.get(i), true), 1.0f);
			}
		}
		
		vec.normalize();
		
		return vec;		
	}
	
	public DenseVector vecForNGram(NGram n, boolean concat) {
		
		//DenseVector
		DenseVector vec;
		
		ArrayList<Token> tokens = n.tokens;
		
		if (tokens.size()==0) return null;
		vec = vecForToken(tokens.get(0), true);
		
		for (int i=1; i<tokens.size(); i++) {
			if (concat) {
				vec = VecUtils.concatenate(vec, vecForToken(tokens.get(i), true));			
			}
			else {
				vec.addScale(vecForToken(tokens.get(i), true), 1.0f);
			}
		}
		
		vec.normalize();
		
		return vec;		
	}
	
	public DenseVector vecForToken(Token t, boolean concat) {
		
		DenseVector semVec;
		DenseVector lexVec;
		DenseVector vec = null;
		
		vec = vecForTokenString(t.text.toLowerCase(), concat);
		
		return vec;		
	}	

	public DenseVector vecForTokenString(String s, boolean concat) {
		
		DenseVector semVec;
		DenseVector lexVec;
		DenseVector vec;
		
		if (termSpace.hasTerm(s.toLowerCase())) {
			semVec = termSpace.getTermVector(s.toLowerCase());
			semVec.normalize();
		}
		else {
			semVec = new DenseVector(semVecDim);
			semVec.setZero();
		}

		
		if (s.length()>1) {
			lexVec = lexSpace.genDenseRep(s);	
		}
		else {
			lexVec = lexSpace.genDenseRep("single.char");	
		}
		
		lexVec.normalize();
		
		if (concat) {
			vec = VecUtils.concatenate(lexVec, semVec);			
		}
		else {
			// Dimensions should be the same
			vec = new DenseVector(semVec);
			vec.addScale(lexVec, 0.1f); // 0.1 is a parameter - more work to do here.
		}
		
		vec.normalize();
		
		return vec;		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}



