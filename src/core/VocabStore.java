package core;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;





public class VocabStore {

	ArrayList<Vocab> vocab;
	HashMap<String, Vocab> vocabMap;
	
	public VocabStore() {
		vocab = new ArrayList<Vocab>();
		vocabMap = new HashMap<String,Vocab>();		
	}
	
	public void setEntropyValues(float value) {
		
		for (Vocab v : vocab) {
			v.entropy = value;
		}
	}
	
	public HashSet<String> getTermSet() {
		HashSet<String> hs = new HashSet<String>();
		for (String s : vocabMap.keySet()) {
			hs.add(s);
		}
		return hs;
	}

	public void incrementDocCount(String str) {	
		Vocab w;
		if (!vocabMap.containsKey(str)) {
			w = new Vocab(str);
			w.df = 1;
			vocab.add(w);
			vocabMap.put(str, w);
		}
		else {
			w = vocabMap.get(str);
			w.df++;
		}		
	}

	public void incrementTermCount(String str) {	
		Vocab w;
		if (!vocabMap.containsKey(str)) {
			w = new Vocab(str);
			w.gf = 1;
			vocab.add(w);
			vocabMap.put(str, w);
		}
		else {
			w = vocabMap.get(str);
			w.gf++;
		}		
	}
	
	public Vocab getTerm(String str) {
		if (!vocabMap.containsKey(str)) return null;
		else return vocabMap.get(str);
	}
	
	public int getTermId(String str) {
		if (!vocabMap.containsKey(str)) return -1;
		else return vocabMap.get(str).id;
	}

	public Vocab getTermByIndex(int idx) {
		// Need to put checks in here !!!
		return vocab.get(idx);
	}

	public String getTermStringByIndex(int idx) {
		// Need to put checks in here !!!
		return vocab.get(idx).term;
	}
	
	public Vocab addTerm(String str, int count) {
		Vocab w;
		if (!vocabMap.containsKey(str)) {
			w = new Vocab(str);
			w.gf = count;
			vocab.add(w);
			vocabMap.put(str, w);
		}
		else {
			w = vocabMap.get(str);
			w.gf += count;
		}
		return w;
	}

	public Vocab addTerm(String str) {
		Vocab w;
		if (!vocabMap.containsKey(str)) {
			w = new Vocab(str);
			w.gf = 1;
			vocab.add(w);
			vocabMap.put(str, w);
		}
		else {
			w = vocabMap.get(str);
			w.gf++;
		}
		return w;
	}


	public void sortVocab() {
		Collections.sort(vocab);
	}

	public void reidentifyVocab() {
		for (int i = 0; i < vocab.size(); i++) {
			vocab.get(i).id = i;
		}
	}

	public void sortReduce(int minCount, int maxTerms) {

		sortVocab();

		// Reduce
		int i;
		int toRemove = 0;
		ArrayList<Vocab> temps = new ArrayList<Vocab>();
		for (i = 0; i < vocab.size() && i < maxTerms; i++) {
			if (vocab.get(i).gf < minCount) {
				vocabMap.remove(vocab.get(i).term);
			}
			else temps.add(vocab.get(i));
		}
		vocab.clear();
		for (i = 0; i < temps.size(); i++) vocab.add(temps.get(i));

		reidentifyVocab();
	}


	public int getVocabSize() {
		return vocab.size();
	}


	public void printStats() {
		System.out.println( "Size of vocab: " + vocab.size());
		System.out.println( "First 5 terms ..." );
		for (int i = 0; i < 5; i++) {
			System.out.println( vocab.get(i).term + "  " + vocab.get(i).gf);
		}
	}

	public void writeVocab(String fileName, boolean termCount, boolean docCount, boolean entropies) {

		try {
			FileWriter fw = new FileWriter(fileName);
			BufferedWriter bw = new BufferedWriter(fw);
			
			for (int i = 0; i < vocab.size(); i++) {
				bw.write(vocab.get(i).term);
				if (termCount) bw.write("\t"+vocab.get(i).gf);
				if (docCount) bw.write("\t"+vocab.get(i).df);
				if (entropies) bw.write("\t"+vocab.get(i).entropy);
				bw.write("\n");
			}
			bw.close();
 
			System.out.println("Done");
 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
}



