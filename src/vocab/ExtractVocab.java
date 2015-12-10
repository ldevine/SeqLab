package vocab;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import sequence.DataSet;
import sequence.Sentence;
import sequence.Token;




public class ExtractVocab {

	DataSet trainDS;
	DataSet testDS;
	

	HashSet<String> vocab = new HashSet<String>();
	HashMap<String,Integer> vocabCount = new HashMap<String,Integer>();	
	
	public class VCount {
		
		public VCount(String t, int count) {
			this.text = t;
			this.count = count;
		}
		
		public String text;
		public int count;
				
	}	
	
	public class ValueResult {
		
		public ValueResult(int id, float val) {
			this.id = id;
			this.val = val;
		}
		
		public int id;
		public float val;
				
	}
	
	
	void loadData() {		
		trainDS = new DataSet();
		trainDS.readLabelledData("i2b2_train_data_labeled.txt");
		
		testDS = new DataSet();
		testDS.readLabelledData("i2b2_test_data_labeled.txt");
	}
	
	void collectConceptVocab1(DataSet ds) {
		ArrayList<Sentence> ss = new ArrayList<Sentence>();
		ss = ds.getSentences();
		
		Token tok;		
		Sentence s;
		String t;
		int count;
		
		boolean inConcept = false;
		
		StringBuffer buf = new StringBuffer();
		
		String concept;
		
		for (int i=0; i<ss.size(); i++) {
			s = ss.get(i);
			
			for (int j=0; j<s.size(); j++) {
				
				tok = s.getToken(j);
				
				if (!tok.getTag().label.equals("O")) {
					
						t = tok.text.toLowerCase().trim();
												
						if (vocabCount.containsKey(t)) {
							count = vocabCount.get(t)+1;
							vocabCount.put(t, count);
						}					
						else {
							vocabCount.put(t,1);
							//System.out.println(t);
						}
						
						vocab.add(t);
				}				
			}
		}
		
	}	
	
	
	void collectConceptVocab(DataSet ds) {
		ArrayList<Sentence> ss = new ArrayList<Sentence>();
		ss = ds.getSentences();
		
		Token tok;		
		Sentence s;
		String t;
		int count;
		
		boolean inConcept = false;
		
		StringBuffer buf = new StringBuffer();
		
		String concept;
		
		for (int i=0; i<ss.size(); i++) {
			s = ss.get(i);
			
			buf = new StringBuffer();
			inConcept=false;
			
			for (int j=0; j<s.size(); j++) {
				
				tok = s.getToken(j);
				
				if (tok.getTag().label.equals("O")) {
					
					if (inConcept) {
						
						//System.out.println("*");
						//t = tok.text.toLowerCase();
						//buf.append(" "+t);
						t = buf.toString().trim();
												
						if (vocabCount.containsKey(t)) {
							count = vocabCount.get(t)+1;
							vocabCount.put(t, count);
						}					
						else {
							vocabCount.put(t,1);
							//System.out.println(t);
						}
						
						vocab.add(t);
						
						buf = new StringBuffer();
						inConcept=false;
					}
					else {
						inConcept=false;
					}
				}
				else {
					if (tok.getTag().label.contains("treat")) {
						t = tok.text.toLowerCase();
						buf.append(" "+t);
						inConcept = true;
					}
					//System.out.println("_");
				}				
			}
			
			if (inConcept) {
				t = buf.toString().trim();
				
				if (vocabCount.containsKey(t)) {
					count = vocabCount.get(t)+1;
					vocabCount.put(t, count);
				}					
				else {
					vocabCount.put(t,1);
				}
				
				vocab.add(t);
			}
			
		}
		
	}	
	
	void process() {
		//collectConceptVocab1(trainDS);
		collectConceptVocab1(testDS);		
	}
	
	void sortAndWriteVocab(String fileName) {
		
		VCount vc;
		ArrayList<VCount> vcl = new ArrayList<VCount>();
		
		for (String s : vocab) {
			vc = new VCount(s, vocabCount.get(s));
			System.out.println(s);
			vcl.add(vc);
		}
		
		Comparator<VCount> cmp = new Comparator<VCount>() {
			public int compare(VCount r1, VCount r2) {
				return r2.count - r1.count;
		}};		
		
		ArrayList<ValueResult> res = new ArrayList<ValueResult>();
		
		Collections.sort(vcl, cmp);		
		
		try { 
			File file = new File(fileName);
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
						
			for (VCount v : vcl) {
				bw.write(v.text+"\t"+v.count+"\n");
			}			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ExtractVocab ev = new ExtractVocab();
		
		ev.loadData();
		ev.process();
		ev.sortAndWriteVocab("I2B2ConceptVocab_test_sorted.txt");
	}

}
