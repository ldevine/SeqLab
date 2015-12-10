package core;


public class RandomGen {

    int x = 7, y = 13, z = 2, w = 31;

    public RandomGen() {
    	
    }
    
    public RandomGen(int s) {
    	seed(s);
    }
    
	public int next() {
		return xor128();
	}
	
	public void seed(int s) {
		x = 7 + s;
		y = 13 + s;
		z = 2 + s;
		w = 31;
		warmUp();
	}
	
	// Private methods
    void warmUp() {
    	for (int i=0; i<20; i++) xor128();
    }	
	
    int xor128() {        
    	int t;     
        t = x ^ (x << 11);
        x = y; y = z; z = w;
        return w = w ^ (w >> 19) ^ t ^ (t >> 8);
    }
    
    float rndFloat() {
    	
    	return (float)xor128()/Integer.MAX_VALUE;
    }	
    
    
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}


