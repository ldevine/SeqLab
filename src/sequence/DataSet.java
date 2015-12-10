package sequence;


import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class DataSet {
	
	String delim =  " ";

	ArrayList<Sentence> sentences;
	
	public DataSet() {
		sentences = new ArrayList<Sentence>();		
	}
	
	public void setDelimiter(String delim) {
		this.delim = delim;
	}
	
	public ArrayList<Sentence> getSentences() {
		return sentences;
	}
	
	public void readLabelledData(String fileName) {
		
		String[] strs;
		BufferedReader br = null;
				
		Token tok;
		Sentence s = new Sentence();
		
		try { 
			String line;
			br = new BufferedReader(new FileReader(fileName));
			
 			while ((line = br.readLine()) != null) {
 				
 				if (line.length()<2) {
 					sentences.add(s);
 					s = new Sentence();
 					continue;
 				} 				
 				strs = line.split(delim);
 				tok = s.addToken(strs[0]);
 				tok.setTag(strs[1]);
			}
 			if (s.size()>0) {
 				sentences.add(s);
 			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DataSet ds = new DataSet();
		ds.readLabelledData("i2b2_train_data_labeled.txt");
		//ds.readLabelledData("i2b2_train_data_labeled.txt");		
	}

}
