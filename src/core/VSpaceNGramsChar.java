package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;




public class VSpaceNGramsChar {


	int vecDim = 0;
	int maxCacheSize = 10000;
	
	// Cache
	HashMap<String,DenseVector> vecMap = new HashMap<String,DenseVector>();
	ArrayList<String> strs = new ArrayList<String>();
	ArrayList<DenseVector> vecs = new ArrayList<DenseVector>();	
	
	// Set of chars to use
	HashSet<Character> chars = new HashSet<Character>();
	
	// Map of chars to integer index
	//HashMap<Integer,Integer> charIdx = new HashMap<Integer,Integer>();	
	
	HashMap<Character, DenseVector> charVecs = new HashMap<Character, DenseVector>();
	
	char skipChar = 255;	
	Random rng = new Random();	
	
	
	public VSpaceNGramsChar(int dim) {
		vecDim = dim;
	}
	

	public void setVecDim(int dim) {
		vecDim = dim;		
	}
	
	public void addChar(char c) {
		chars.add(c);		
	}
	
	public void setCharVectors() {
		int idx = 0;
		DenseVector vec;
		
		// Add char for marking the skip in skip-grams
		chars.add(skipChar);
		chars.add('ï');
		chars.add('»');
		chars.add('¿');
		
		for (Character c : chars) {			
			vec = new DenseVector(vecDim, c.charValue());
			vec.normalize();
			charVecs.put(c, vec);			
			// charIdx.put((int)c.charValue(), idx);
			// System.out.println((int)c.charValue());
			idx++;
		}		
	}
	
	// Print the recorded chars
	public void printChars() {
		
		for (Character c : chars) {
			System.out.print(c);
			System.out.print("  ");
		}
	}
	
	// Random replacement cache
	void addVector(String str, DenseVector vec) {
		int idx;
		String s;
		if (strs.size()>=maxCacheSize) {
			// Get random index
			//idx = rng.nextInt()%maxCacheSize;
			idx = rng.nextInt(maxCacheSize);
			
			//System.out.println(idx);
			//System.out.println(maxCacheSize);
			
			s = strs.get(idx);
			strs.set(idx, str);
			vecs.set(idx, vec);
			vecMap.remove(s);
		}	
		else {
			strs.add(str);
			vecs.add(vec);
			
			//if (strs.size()%100==0) System.out.println(strs.size());
		}
		vecMap.put(str, vec);
	}
	
	public DenseVector genDenseRep(String str) {
		
		String ngram;
		DenseVector dv = new DenseVector(vecDim);
		DenseVector d;
		
		//DenseVector v = VecUtils.getRndGausVector(vecDim);		
		//v.normalize();
		//v.scale(0.02f);
		
		// For string length
		//float scale = (float)(Math.log((double)str.length()/2)/Math.log(2));
		//v.scale(scale);
		//v.multiply(v);
		//System.out.println(scale);
			
		
		// Do 1-grams
		for (int i=0; i<str.length(); i++) {
			if (str.charAt(i)==' ') continue;
			d = getNGramVector(str.substring(i,i+1));
			dv.add(d);
		}	
		// Do 2-grams
		for (int i=0; i<str.length()-1; i++) {
			d = getNGramVector(str.substring(i,i+2));
			dv.addScale(d,1.2f);
		}
		// Do 2-skip-grams
		for (int i=0; i<str.length()-2; i++) {
			ngram = ""+str.charAt(i)+skipChar+str.charAt(i+2);
			d = getNGramVector(ngram);
			dv.addScale(d,1.2f);
		}
		// Do 3-grams
		for (int i=0; i<str.length()-2; i++) {
			d = getNGramVector(str.substring(i,i+3));
			dv.addScale(d,1.4f);
		}	
		// Do 3-skip-grams
		for (int i=0; i<str.length()-3; i++) {
			ngram = ""+str.charAt(i)+skipChar+str.charAt(i+2)+str.charAt(i+3);
			d = getNGramVector(ngram);
			dv.addScale(d,1.4f);
		}
		// Do 3-skip-grams
		for (int i=0; i<str.length()-3; i++) {
			ngram = ""+str.charAt(i)+str.charAt(i+1)+skipChar+str.charAt(i+3);
			d = getNGramVector(ngram);
			dv.addScale(d,1.4f);
		}		
		// Do 4-grams
		for (int i=0; i<str.length()-3; i++) {
			d = getNGramVector(str.substring(i,i+4));
			dv.addScale(d,1.6f);
		}	

		//dv.add(v);
		dv.normalize();
		
		return dv;
	}
	
	DenseVector getNGramVector(String str) {
	
		//System.out.println(str);
		
		DenseVector v;
		if (!vecMap.containsKey(str)) {
			// generate vector here
			v = getVector(str);
			// put vector in cache
			addVector(str, v);
		}
		
		return vecMap.get(str);
	}	
		
	DenseVector getVector(String str) {
	
		// Get vector for first character
		if (!charVecs.containsKey(str.charAt(0))) {			
			System.out.println("-- "+str.charAt(0));
		}
		
		DenseVector vec = new DenseVector(charVecs.get(str.charAt(0)));
				
		if (str.length()>1) {
			DenseVector dv2 = charVecs.get(str.charAt(1));
			vec.multiplyPermute(dv2, 1);
		}

		if (str.length()>2) {
			DenseVector dv3 = charVecs.get(str.charAt(2));
			vec.multiplyPermute(dv3, 2);
		}
		
		vec.normalize();
		
		return vec;
	}
	
	public static void main(String[] args) {
		
		VSpaceNGramsChar v = new VSpaceNGramsChar(10);
		
		v.addChar('a');
		v.addChar('b');
		v.addChar('c');
		v.addChar('d');
		v.setCharVectors();
		
		DenseVector vec = v.genDenseRep("abbcccbaaa");
		System.out.println(vec.toString());
	}
	
}
	


