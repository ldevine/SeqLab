package core;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;



public class VecUtils {

	static Random rnd = new Random();
	static HashSet<Integer> intSet = new HashSet<Integer>();
	
	// Get normalized gaussian random densevector
	public static DenseVector getRndGausVector(int dim) {
		DenseVector vec = new DenseVector(dim);
		for (int i=0; i<dim; i++) {
			vec.setVal(i, (float)rnd.nextGaussian());
		}
		vec.normalize();
		
		return vec;
	}
	
	public static DenseVector concatenate(DenseVector v1, DenseVector v2) {
		
		DenseVector v = new DenseVector(v1.size()+v2.size());
		
		// Copy v1
		for (int i=0; i<v1.size(); i++) {
			v.setVal(i, v1.get(i));
		}
		// Copy v2
		for (int i=0; i<v2.size(); i++) {
			v.setVal(v1.size()+i, v2.get(i));
		}
		
		return v;
	}
	
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}



