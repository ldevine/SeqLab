package sequence;

import java.util.ArrayList;



public class NGram {

	public String text;
	
	public Tag tag;
	
	public ArrayList<Token> tokens = new ArrayList<Token>();
	
	public void makeTag() {
		if (tokens.size()==0) return;
		
		boolean singleTag = true;
		
		String t = tokens.get(0).tag.classLab;
		
		for (int i=1; i<tokens.size(); i++) {
			if (!tokens.get(i).tag.classLab.equals(t)) {
				singleTag = false;
				break;
			}
		}
		
		if (singleTag) tag = new Tag(t);
		else tag = new Tag("mixed");
	}
			
	public NGram(Token t1, Token t2) {
		tokens.add(t1);
		tokens.add(t2);		
	}
	
	public NGram(Token t1, Token t2, Token t3) {
		tokens.add(t1);
		tokens.add(t2);	
		tokens.add(t3);
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		
		if (tokens.size()==0) return "";
		buf.append(tokens.get(0).text);
		
		for (int i=1; i<tokens.size(); i++) {
			buf.append(" "+tokens.get(i).text);
		}
		
		return buf.toString();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}



