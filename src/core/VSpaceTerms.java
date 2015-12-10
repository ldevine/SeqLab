package core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;





public class VSpaceTerms {

	
	VocabStore vStore;
	ArrayList<DenseVector> vectors;	
	
	public VSpaceTerms() {
		vStore = new VocabStore();
		vectors = new ArrayList<DenseVector>();
	}

	public boolean hasTerm(String str) {
		return (vStore.getTermId(str)!=-1);
	}

	public int getVocabSize() {
		return vStore.getVocabSize();
	}
	
	public Vocab getVocabByID(int id) {
		return vStore.getTermByIndex(id);
	}

	public String getTermByID(int id) {
		return vStore.getTermByIndex(id).term;
	}
	
	public DenseVector getVectorByID(int id) {
		return vectors.get(id);
	}	
	
	public Vocab getVocab(String str) {
		return vStore.getTerm(str);
	}	
	
	public DenseVector getTermVector(String str) {
		int id = vStore.getTermId(str);
		if (id!=-1) return vectors.get(id);
		else return null;
	}
	
	public int getTermId(String str) {
		int id = vStore.getTermId(str);
		return id;
	}

	public void setEntropies(float defaultEntropyValue) {

		vStore.setEntropyValues(defaultEntropyValue);
	}

	
	public void query(String s) {
		
		//float score = Float.MIN_VALUE;
		String term;
		Vocab v;
		
		ScoreResultList srl = new ScoreResultList(20, 30);
		
		if (vStore.getTermId(s)==-1) return;
	
		int sz = vStore.getVocabSize();
		
		DenseVector vec;
		DenseVector qVec;

		v = vStore.getTerm(s);
		qVec = vectors.get(v.id);
			
		for (int i=0; i<sz; i++) {
			v = vStore.getTermByIndex(i);
			vec = vectors.get(v.id);
			srl.addScore(vStore.getTermByIndex(v.id).term, qVec.cosine(vec));
		}
		
		srl.printScores();
	}
	
	// Takes a tab delimited file containing with a word
	// and the word's entropy on each line
	public void setEntropies(String fileName, int entropyColumn, float defaultEntropyValue) {
		
		Vocab v;
		String term;
		String[] strs;
		float entropy;
		BufferedReader br = null;

		vStore.setEntropyValues(defaultEntropyValue);
		
		try { 
			String line;
			br = new BufferedReader(new FileReader(fileName));
 			while ((line = br.readLine()) != null) {
 				strs = line.split("\t");
 				term = strs[0];
 				entropy = Float.parseFloat(strs[entropyColumn]);
 				if (vStore.getTermId(term)!=-1) {
 					v = vStore.getTerm(term);
 					v.entropy = entropy;
 				} 				
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void normalizeVectors() {
		for (int i=0; i<vectors.size(); i++) {
			vectors.get(i).normalize();
		}
	}
	
	// Read vectors in w2v text file format
	public void readWord2VecFile(String fileName, HashSet<String> filterSet) {
		
		Vocab v;
		BufferedReader br = null;
		DenseVector vec;
		String strs[];
		
		int dim;
		int numVectors;
		int termCount = 0;
		
		try {
 
			String line;
 
			br = new BufferedReader(new FileReader(fileName));
 
			// Parse first line
			line = br.readLine();
			strs = line.split(" ");
			numVectors = Integer.parseInt(strs[0]);
			dim = Integer.parseInt(strs[1]);
			
			System.out.println(""+numVectors+"  "+dim);
			
			while ((line = br.readLine()) != null) {
				
				strs = line.split(" ");
				
				if (filterSet!=null) {
					if (!filterSet.contains(strs[0])) continue;
				}
				
				// Add new term				
				v = vStore.addTerm(strs[0]);
				v.id = termCount;
				
				// Create new vector
				vec = new DenseVector(dim);
				vec.id = termCount;
				
				for (int i=1; i<strs.length; i++) {
					vec.setVal(i-1, Float.parseFloat(strs[i]));					
				}			
				
				// Add vector
				vectors.add(vec);
				
				termCount++;
				
				//System.out.println(line);
			}
 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}



