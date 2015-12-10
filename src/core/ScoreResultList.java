package core;



import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ScoreResultList {
	
	class CompareByScore implements Comparator<ScoreResult> {
	    @Override
	    public int compare(ScoreResult a, ScoreResult b) {
	    	return b.score < a.score ? -1 : b.score == a.score ? 0 : 1;	        
	    }
	}
	
	public ScoreResultList(int sz, int maxSz) {
		size = sz;
		maxSize = maxSz;
		scoreThreshold = -1.0f;
		curs = 0;
		scores = new ArrayList<ScoreResult>();
		
		init();
	}

	void init() {
		for (int i = 0; i < maxSize + 1; i++) {
			scores.add(new ScoreResult("", -1.0f));
		}
	}

	void adjust() {
		//System.out.println("Adjusting ...");
		//System.out.println(scores.size());
		// Sort
		Collections.sort(scores, new CompareByScore());
		// Prune
		curs = size;
		scoreThreshold = scores.get(curs).score;
		
		//System.out.println(scores.size());
	}

	public void addScore(String term, float score) {
		ScoreResult sr;
		if (score > scoreThreshold) {	
			sr = new ScoreResult(term, score);
			scores.set(curs, sr);
			curs++;
			//System.out.println(curs);			
			if (curs >= maxSize) {
				adjust();
			}			
		}

	}
	
	public void addScore(String term, int id, float score) {
		ScoreResult sr;
		if (score > scoreThreshold) {
			sr = new ScoreResult(term, id, score);
			scores.set(curs, sr);
			curs++;
			if (curs >= maxSize) {
				adjust();
			}			
		}
	}

	public void addScore(int id, float score) {
		ScoreResult sr;
		if (score > scoreThreshold) {
			sr = new ScoreResult(id, score);
			scores.set(curs, sr);
			curs++;
			if (curs >= maxSize) {
				adjust();
			}			
		}
	}
	
	public void printScores() {
		adjust();
		//System.out.println(scores.size());
		for (int i = 0; (i < size && i < scores.size()); i++) {
			sr = scores.get(i);
			System.out.println(""+i+"  "+sr.term + "\t\t\t" + sr.score);
		}
	}
	
	public String getScoreResultString(int idx) {
		return scores.get(idx).term;
	}
	
	public ScoreResult getScoreResultFromIndex(int idx) {
		return scores.get(idx);
	}	
	
	float getCurrentThreshold() {
		return scoreThreshold;
	}

	void reset() {
		scores.clear();
		scoreThreshold = -1.0f;
		init();
	}

	static void test() {
		ScoreResultList l = new ScoreResultList(3, 5);
		ScoreResult sr;
		l.addScore("1", 0.1f);
		l.addScore("2", 0.01f);
		l.addScore("2", 0.1f);
		l.addScore("5", 0.5f);
		l.addScore("2", 0.2f);
		l.addScore("2", 0.4f);
		l.addScore("2", 0.25f);
		l.printScores();
	}

	ScoreResult sr;
	int size;
	int maxSize;
	int curs;
	float scoreThreshold = -1.0f;
	ArrayList<ScoreResult> scores;
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		test();

	}

}


