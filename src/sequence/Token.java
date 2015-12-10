package sequence;

import java.util.ArrayList;
import java.util.HashMap;

public class Token {

	public String text;
	
	public Tag tag;
	
	int featureIdx = 0;
	
	//HashMap<String,String> features = new HashMap<String,String>();
	ArrayList<String> featureList = new ArrayList<String>();
	
	public Token(String t) {
		text = t;
		tag = null;
	}
	
	public void setTag(String t) {
		tag = new Tag(t);
	}
	
	public Tag getTag() {
		return tag;
	}	
	
	public void clearFeatures() {
		//features.clear();
		featureList.clear();
		featureIdx = 0;
	}
	
	public void addFeature(String value) {
		//features.put(key,value);
		featureList.add(value);
	}
	
	public int numFeatures() {
		return featureList.size();
	}
	
	public void rewindFeatures() {
		featureIdx = 0;
	}
	
	public String nextFeature() {
		if (!hasNextFeature()) return null;		
		String feat = featureList.get(featureIdx);
		featureIdx++;
		return feat;
	}
	
	public boolean hasNextFeature() {
		return featureIdx < featureList.size();
	}
	
	public String getFeatureVal(int idx) {
		if (idx>featureList.size()) return null;
		else return featureList.get(idx);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
