package sequence;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;

import core.DenseVector;




public class NGrams {

	String fileName;
	int max;
	int order;
	
	ArrayList<DenseVector> termVecs = new ArrayList<DenseVector>();
	
	ArrayList<Sentence> sentences = new ArrayList<Sentence>();
	ArrayList<NGram> ngrams = new ArrayList<NGram>();
	
	HashSet<String> strings = new HashSet<String>();
	
	
	
	public NGrams(String fileName, int max, int order) {
		this.fileName = fileName;
		this.max = max;
		this.order = order;
	}
	
	public void process() {
		
		int nGramCount = 0;
		Token t1, t2, t3;
		NGram ng;
		
		readData(fileName);
				
		// No uni grams
		for (Sentence s : sentences) {
		
			// Bigrams
			if (order == 2) {
				for (int i=1; i<s.size(); i++) {
					t1 = s.getToken(i-1);
					t2 = s.getToken(i);					
					ng = new NGram(t1, t2);
					
					if (!strings.contains(ng.toString())) {
						ngrams.add(ng);	
						ng.makeTag();
						strings.add(ng.toString());
						if (ngrams.size()>max) break;
					}
				}
			}
			// Tri-grams
			if (order == 3) {
				for (int i=2; i<s.size(); i++) {
					
					t1 = s.getToken(i-2);
					t2 = s.getToken(i-1);
					t3 = s.getToken(i);					
					ng = new NGram(t1, t2, t3);					
					if (!strings.contains(ng.toString())) {
						ngrams.add(ng);
						ng.makeTag();
						strings.add(ng.toString());
						if (ngrams.size()>max) break;
					}
				}
			}
			
			if (ngrams.size()>max) break;
		}				
	}
	
	void readData(String fileName) {
		
		Token tok;
		String[] strs;
		BufferedReader br = null;
				
		Sentence s = new Sentence();
		
		try { 
			String line;
			br = new BufferedReader(new FileReader(fileName));
			
 			while ((line = br.readLine()) != null) {
 				
 				if (line.length()<2) {
 					sentences.add(s);
 					
 					//System.out.println(s.toString());
 					
 					s = new Sentence();
 					 					
 					continue;
 				} 				
 				strs = line.split("\t");
 				tok = s.addToken(strs[0]);
 				tok.tag = new Tag(strs[1]); 				
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}	
	
	public ArrayList<NGram> getNGrams() {
		
		
		return ngrams;		
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		NGrams gng = new NGrams("i2b2_train_data_labeled.txt", 1000, 3);

	}

}



