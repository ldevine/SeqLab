package sequence;

import java.util.ArrayList;


public class Sentence {
	
	ArrayList<Token> tokens;
	
	public Sentence() {
		tokens = new ArrayList<Token>();
	}
		
	public Token addToken(String t) {
		Token tok = new Token(t);
		tokens.add(tok);
		return tok;
	}

	public int size() {
		return tokens.size();
	}
	
	public Token getToken(int index) {
		if (index>=tokens.size()) return null;		
		return tokens.get(index);
	}
	
	public ArrayList<Token> getTokens() {
		return tokens;
	}
	
	public Tag getTag(int index) {
		if (index>tokens.size()) return null;		
		return tokens.get(index).tag;
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (Token t : tokens) {
			buf.append(t.text+" ");
		}
		return buf.toString().trim();
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}



