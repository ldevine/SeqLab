package core;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Random;



public class DenseVector {

	float[] data;
	public int id;

	public DenseVector(int dim) {
		data = new float[dim];
		for (int i=0; i<data.length; i++) {
			data[i] = 0.0f;;
		}
	}
	
	public DenseVector(DenseVector other) {
		data = new float[other.data.length];
		for (int i=0; i<data.length; i++) {
			data[i] = other.data[i];
		}
	}	
	
	public DenseVector(int dim, int seed) {
		
		Random rnd = new Random(seed);
		
		data = new float[dim];
		for (int i=0; i<data.length; i++) {
			data[i] = (float) rnd.nextGaussian();
		}
	}
	
    public float[] getPoint() {
        return data;
    }
	
	public int getDimensions() {
		return data.length;
	}

	public void setVal(int i, float val) {		
		data[i] = val;
	}
	
	public void setZero() {		
		for (int i=0; i<data.length; i++) data[i] = 0.0f;
	}
	
	public void copy(DenseVector other) {		
		for (int i=0; i<data.length; i++) data[i] = other.data[i];
	}	
	
	public float get(int idx) {
		return data[idx];
	}
	
	public int size() {
		return data.length;
	}	

	public void scale(float sc) {		
		for (int i=0; i<data.length; i++) data[i] = sc * data[i];
	}	
	
	public void add(DenseVector other) {
		if (other.size()!=this.size()) {
			System.out.println("Sizes don't match ... '''");
			return;
		}
		for (int i=0; i<data.length; i++) {
			data[i] += other.get(i);
		}		
	}
	
	public void addScale(DenseVector other, float scale) {
		if (other.size()!=this.size()) {
			System.out.println("Sizes don't match ... ___");
			return;
		}
		for (int i=0; i<data.length; i++) {
			data[i] += other.get(i) * scale;
		}		
	}	

	// Adds a column of matrix other
	public void addColumn(DenseMatrix other, int col) {
		int start = col * data.length;
		for (int i=0; i<data.length; i++) {
			data[i] += other.data[start+i];
		}		
	}	
	
	// Adds a scaled column of matrix other
	public void addScaleColumn(DenseMatrix other, int col, float scale) {
		//if (other.size()!=this.size()) {
		//	System.out.println("Sizes don't match ... ___");
		//	return;
		//}
		int start = col * data.length;
		for (int i=0; i<data.length; i++) {
			data[i] += other.data[start+i] * scale;
		}		
	}	
	
	public void multiply(DenseVector other) {
		if (other.size()!=this.size()) {
			System.out.println("Sizes don't match ... ///");
			return;
		}
		for (int i=0; i<data.length; i++) {
			data[i] *= other.get(i);
		}			
	}	

	public void multiplyPermute(DenseVector other, int perm) {
		if (other.size()!=this.size()) {
			System.out.println("Sizes don't match ... ;;;;");
			return;
		}
		int idx=data.length-perm;
		for (int i=0; i<idx; i++) {
			data[i] *= other.get(i+perm);
		}		
		for (int i=0; i<perm; i++) {
			data[idx+i] *= other.get(i);
		}		
	}
	
	public float norm() {
		float norm;
		float sum = 0.0f;
		for (int i=0; i<data.length; i++) {
			sum += data[i] * data[i];
		}	
		norm = (float)Math.sqrt(sum);
		return norm;
	}
	
	public void normalize() {
		float norm;
		float sum = 0.0f;
		for (int i=0; i<data.length; i++) {
			sum += data[i] * data[i];
		}	
		norm = (float)Math.sqrt(sum);
		if (norm==0.00000f) {
			System.out.println("Norm is 0.0 !!");
		}
		else for (int i=0; i<data.length; i++) data[i] = data[i]/norm;
	}

	public float cosine(DenseVector other) {
		float dist = 0.0f;
		if (other.size()!=this.size()) {
			System.out.println("Sizes don't match ... zzz");
			return 0.0f;
		}
		float norm;
		float sum = 0.0f;
		for (int i=0; i<data.length; i++) {
			dist += data[i] * other.data[i];
		}	
		return dist;
	}

	public float euclideanDistanceSquared(DenseVector other) {
		float dist = 0.0f;
		if (other.size()!=this.size()) {
			System.out.println(this.size());
			System.out.println("Sizes don't match ... yyy");
			return 0.0f;
		}
		float dif;
		for (int i=0; i<data.length; i++) {
			dif = data[i] - other.data[i];
			dist += dif * dif;
		}	
		return dist;
	}
	
	
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<data.length; i++) {
			buf.append(""+data[i]+" ");
		}
		return buf.toString();
	}	
	
	public void write(DataOutputStream dos) throws Exception {
		
		for (int i=0; i<data.length; i++) {
			dos.writeFloat(data[i]);
		}
	}

	public void writeText(BufferedWriter bw) throws Exception {
		
		if (data.length==0) return;
		
		bw.write(""+data[0]);
		
		for (int i=1; i<data.length; i++) {
			bw.write(" "+data[i]);
		}
		
		bw.write("\n");
	}
	
	public void read(DataInputStream dis) throws Exception {
		
		for (int i=0; i<data.length; i++) {
			data[i] = dis.readFloat();
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DenseVector v1 = new DenseVector(10, 3);
		DenseVector v2 = new DenseVector(10, 3);
		
		System.out.println(v1.toString());
		System.out.println(v2.toString());

	}

}
