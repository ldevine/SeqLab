package vocab;

import core.VSpaceTerms;
import core.VecBuilder;



public class Nearest {

	
	void process() {
		
		String path = "E:\\ALTA_2015\\Vectors\\";
		
		VecBuilder vBuilder = new VecBuilder();
		
		vBuilder.prepareLexSpace();
		vBuilder.prepareTermSpace(path+"vectors_WK_100_2.txt");
		
		VSpaceTerms terms = vBuilder.getTermSpace();
		
		
		terms.query("instructed"); 
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Nearest n = new Nearest();
		n.process();

	}

}
