package clustering;

import java.util.ArrayList;

import core.DenseVector;



public class Cluster {

    DenseVector centroid;
    
    public float MSE;
    public int id;
        
    /** The points contained in this cluster. */
    private final ArrayList<DenseVector> points;

    /**
     * Build a cluster centered at a specified point.
     */
    public Cluster() {
        points = new ArrayList<DenseVector>();
        centroid = null;
    }

    public Cluster(DenseVector v) {
        points = new ArrayList<DenseVector>();
        centroid = v;
    }
    
    public void clearPoints() {
    	points.clear();
    }
    
    /**
     * Add a point to this cluster.
     * @param point point to add
     */
    public void addPoint(DenseVector point) {
        points.add(point);
    }

    /**
     * Get the points contained in the cluster.
     * @return points contained in the cluster
     */
    public ArrayList<DenseVector> getPoints() {
        return points;
    }
    
    public int size() {
        return points.size();
    }
    
    public DenseVector getCentroid() {
    	return centroid;
    }
    
    public void setCentroid(DenseVector v) {
    	if (centroid==null) {
    		centroid = new DenseVector(v.getDimensions());
    	}
    	centroid.copy(v);
    	centroid.normalize();
    }
    
    public void computeCentroid() {
    	
    	if (points.size()==0) {
    		System.out.println("Centroid is empty ...");
    		return;
    	}
    	
    	int dim = points.get(0).getDimensions();
    	centroid = new DenseVector(dim);
    	centroid.setZero();
    	
        for (DenseVector p : points) {
            centroid.add(p);
        }
        
        centroid.normalize();
    }
    
    public void computeMSE() {
    	float sum = 0.0f;
        
    	for (DenseVector p : points) {
            sum += p.euclideanDistanceSquared(centroid);
        }
        
        sum /= points.size();
        
        MSE = sum;
    }

}


