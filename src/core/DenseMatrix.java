package core;



public class DenseMatrix {

	int numElems;
	int rows;
	int cols;
	float[] data;
	
	public DenseMatrix(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
		this.numElems = rows*cols;
		
		data = new float[rows*cols];
		for (int i=0; i<numElems; i++) {
			data[i] = 0.0f;;
		}
	}
	
	public void normalizeColumns() {	

		int start;
		int idx;
		float sum, normInv;
		
		for (int i=0; i<cols; i++) {
			sum = 0.0f;
			start = i*rows;
			for (int j=start; j<(start+rows); j++) {				
				sum += data[j]*data[j];
			}
			if (sum==0.0f) {
				//System.out.println("*");
				continue;
			}
			normInv = 1.0f/(float)Math.sqrt(sum);
			for (int j=start; j<(start+rows); j++) {				
				data[j] = data[j]*normInv;
			}
		}
	}
	
	public void addVec(DenseVector v, int col) {
		
		// Need to check that number of rows of 2 matrices are equal
		int start = col * rows;
		//int start2 = col2 * rows;
		
		//float sum = 0.0f;
		
		for (int i=0; i<rows; i++) data[start+i] += v.data[i];		
	}
	
	public void addScaleVec(DenseVector v, int col, float s) {
		
		// Need to check that number of rows of 2 matrices are equal
		int start = col * rows;
		//int start2 = col2 * rows;
		
		//float sum = 0.0f;
		
		for (int i=0; i<rows; i++) data[start+i] += v.data[i] * s;		
	}
	
	public float columnDot(int col1, int col2) {
		
		// Need to check that number of rows of 2 matrices are equal
		int start1 = col1 * rows;
		int start2 = col2 * rows;
		
		float sum = 0.0f;
		for (int i=0; i<rows; i++) sum += (data[start1+i] * data[start2+i]);
		
		return sum;
	}


	public float columnVecDot(DenseVector other, int col) {
		
		// Need to check that number of rows of 2 matrices are equal
		int start = col * rows;		
		float sum = 0.0f;
		
		for (int i=0; i<rows; i++) sum += (data[start+i] * other.data[i]);
		
		return sum;
	}
	
	public float columnDot(DenseMatrix other, int col1, int col2) {
	
		// Need to check that number of rows of 2 matrices are equal
		int start1 = col1 * rows;
		int start2 = col2 * rows;
		
		float sum = 0.0f;
		for (int i=0; i<rows; i++) sum += (data[start1+i] * other.data[start2+i]);
		
		return sum;
	}
	
	// Adds a scaled column of the other matrix to a column of this matrix
	public void columnScaleAdd(DenseMatrix other, int col1, int col2, float c) {
		
		// Need to check that number of rows of 2 matrices are equal
		int start1 = col1 * rows;
		int start2 = col2 * rows;
		
		float sum = 0.0f;
		for (int i=0; i<rows; i++) data[start1+i] += (other.data[start2+i] * c);		
	}	
	
	public void set(int r, int c, float val) {
		int idx = c*rows+r;
		data[idx] = val;
	}
	
	public void setZero() {
		for (int i=0; i<numElems; i++) data[i]=0;
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<rows; i++) {
			for (int j=0; j<cols;j++) {
				buf.append(""+data[j*rows+i]+" ");
			}
			buf.append("\n");
		}
		return buf.toString();
	}
	
	static void test() {		
		
		DenseVector dv = new DenseVector(3);
		dv.setVal(0, 1.0f);
		dv.setVal(1, 2.0f);
		dv.setVal(2, 3.0f);
		
		DenseMatrix mat1 = new DenseMatrix(3, 3);
		mat1.setZero();
		
		DenseMatrix mat2 = new DenseMatrix(3, 3);
		mat2.setZero();
		
		mat2.set(0, 1, 2);
		//System.out.println(mat2.toString());
		
		mat1.columnScaleAdd(mat2, 0, 1, 2);
		//System.out.println(mat1.toString());
		
		mat1.columnScaleAdd(mat2, 1, 1, 2);
		//System.out.println(mat1.toString());
		
		mat1.normalizeColumns();
		//System.out.println(mat1.toString());
		
		System.out.println(mat1.toString());
		System.out.println(mat2.toString());
		
		//System.out.println(mat1.columnDot(2, 2));
		
		System.out.println(mat1.columnDot(mat2, 1, 1));
		
		mat1.addVec(dv, 2);
		System.out.println(mat1.toString());
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DenseMatrix.test();

	}

}



