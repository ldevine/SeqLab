package core;

import java.util.ArrayList;


public class TokenizerNGram {

	final int maxTokens = 1000;
	char chars[];
	String delimiter;
	int numToks = 0;
	
	ArrayList<Span> spans;
	
	String normStr;
	
	
	public class Span {

		public int begin;
		public int end;
		public int len;
	};
	
	
	public TokenizerNGram() {
		
		// Max 10000 chars per line
		chars = new char[10000];		
		spans = new ArrayList<Span>();
		
		// Initialise spans
		for (int i = 0; i < maxTokens; i++) {
			spans.add(new Span());
		}

		delimiter = " ";
	}	
	
	public void setDelimiter(String delimit) {
		delimiter = delimit;
	}
	
	public int tokenize(String str) {

		int begin;
		int end;
		int len;
		int curs = 0;

		Span sp;	
		numToks = 0;

		if (str.length() == 0) return 0;

		begin = 0;
		
		// First split string
		String[] strs = str.split(delimiter);

		// Put back together
		StringBuffer buf = new StringBuffer();
		for (String s : strs) {
			if (!(s.length()==0)) {
				buf.append(s+" ");
			}
		}
		normStr = buf.toString().trim();
		
		// Now make spans
		for (int i = 0; i < normStr.length(); i++) {
			
			if (normStr.charAt(i) == ' ') {
				// Set a new span
				end = i;
				sp = spans.get(numToks);
				sp.begin = begin;
				sp.end = end;					
				begin = end+1;
				
				numToks++;				
				if (numToks >= maxTokens) break;
			}
		}
		sp = spans.get(numToks);
		sp.begin = begin;
		sp.end = normStr.length();
		
		return numToks+1;
	}

	
	public String getNGram(int i, int order) {
		int begin = spans.get(i).begin;
		int end = spans.get(i+(order-1)).end;
		return normStr.substring(begin, end);
	}

	// Pre: has been tokenised already
	void print() {
		for (int i = 0; i < numToks - 3; i++) {
			//System.out.println( get4Gram(i) );
		}
	}

	public int getNumTokens() {
		return numToks;
	}
	
	public static void main(String[] args) {
		
		String str = "He included the   step to explain a principle of achievement";
		TokenizerNGram tkn = new TokenizerNGram();
		tkn.tokenize(str);
		
		
		System.out.println(tkn.getNGram(0, 1));
		System.out.println(tkn.getNGram(2, 3));		
		System.out.println(tkn.getNGram(7, 3));

	}
}


