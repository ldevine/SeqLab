package vocab;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;


public class IntersectVocab {

	
	
	
	HashSet<String> vocab1 = new HashSet<String>();
	HashSet<String> vocab2 = new HashSet<String>();
	
	
	void readInputFile(String fileName, HashSet<String> s) {
		
		BufferedReader br = null;
		String strs[];
		
		try { 
			String line;
			br = new BufferedReader(new FileReader(fileName));
 			while ((line = br.readLine()) != null) {
 				strs = line.split("\t");
 				s.add(strs[0].trim());
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	boolean containsDigits(String s) {
		boolean result = false;
		for (int i=0; i<s.length(); i++) {
			if (Character.isDigit(s.charAt(i))) {
				result = true;
				break;
			}
		}
		return result;
	}
	
	void process() {
		//readInputFile("./analysis/I2B2ConceptVocab_sorted.txt", vocab1);
		readInputFile("./analysis/I2B2ConceptVocab_train_sorted.txt", vocab1);		
		//readInputFile("./analysis/I2B2ConceptVocab_test_sorted.txt", vocab1);		
		
		readInputFile("./analysis/vocab_C1.txt", vocab2);
		
		
		System.out.println(vocab1.size());
		System.out.println(vocab2.size());
		
		int count = 0;
		for (String s : vocab1) {
			
			if (!vocab2.contains(s)) {
				
				count++;
				System.out.println(s);
			}
			
		}
		
		System.out.println(count);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		IntersectVocab iv = new IntersectVocab();
		iv.process();

	}

}


