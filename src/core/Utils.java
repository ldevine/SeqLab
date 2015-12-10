package core;


import java.io.File;
import java.util.ArrayList;

public class Utils {

	public static int sign(final float x) {
		if (Float.isNaN(x)) {
			return 0;
		}
		return (x == 0.0F) ? 0 : (x > 0.0F) ? 1 : -1;
	} 
	
	public static String formString(String[] strs, int start, int end) {
		StringBuffer buf = new StringBuffer();
		for (int i=start; i<end; i++) {
			buf.append(strs[i]+" ");			
		}		
		return buf.toString().trim();
	}
	
	public static ArrayList<String> getFiles(String dir) {
		ArrayList<String> fileList = new ArrayList<String>();
		File folder = new File(dir);
		File[] files = folder.listFiles();
		
		for (File f : files) fileList.add(f.getAbsolutePath());
		return fileList;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
