package core;




public class ScoreResult {
	
	public int termId;
	public String term;
	public float score;	

	public ScoreResult(String _term, float _score) { 
		term = _term;
		score = _score;
	}
	
	public ScoreResult(String _term, int _termId, float _score) { 
		term = _term;
		termId = _termId;
		score = _score;
	}
	
	public ScoreResult(int _termId, float _score) { 
		termId = _termId;
		score = _score;
	}
		
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}


