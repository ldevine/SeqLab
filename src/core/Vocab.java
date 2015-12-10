package core;




public class Vocab implements Comparable<Vocab> {

	public String term;    
	public int id;  // term id
	public int df;  // document frequency
	public int gf;  // global frequency
	public float entropy;
	
	// constructor.
	Vocab(String term) {
		this.term = term;
		df = 0;
		gf = 0;
		entropy = 0.0f;
	}


	public int compareTo(Vocab other) {
		return other.gf - this.gf; 
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
