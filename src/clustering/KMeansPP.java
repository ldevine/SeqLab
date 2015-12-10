package clustering;


import java.util.ArrayList;
import java.util.Random;

import org.apache.commons.math3.stat.descriptive.moment.Variance;

import core.DenseVector;


/**
 * This class is based on the KMeans clusterer from:
 * 
 * Apache Commons Math
 * 
 * K-tree - http://ktree.sourceforge.net/
 *
 */
public class KMeansPP {

    /** The number of clusters. */
    private int k;

    /** The maximum number of iterations. */
    private int maxIters;

    /** Random generator for choosing initial centers. */
    private Random random;

    
    public KMeansPP( int k, int maxIterations) {
    	random = new Random();
        this.k = k;
        this.maxIters = maxIterations;
        random.setSeed(System.currentTimeMillis());
    }

    public KMeansPP( int k, int maxIterations, Random random) {
        this.k = k;
        this.maxIters = maxIterations;
        this.random = random;
    }
    
    
    private ArrayList<Cluster> chooseInitialCenters(ArrayList<DenseVector> points) {

        // Convert to list for indexed access. Make it unmodifiable, since removal of items
        // would screw up the logic of this method.
        //ArrayList<T> pointList = Collections.unmodifiableList(new ArrayList<T> (points));

        // The number of points in the list.
        int numPoints = points.size();

        // Set the corresponding element in this array to indicate when
        // elements of pointList are no longer available.
        boolean[] taken = new boolean[numPoints];

        // The resulting list of initial centers.
        ArrayList<Cluster> resultSet = new ArrayList<Cluster>();

        // Choose one center uniformly at random from among the data points.
        int firstPointIndex = random.nextInt(numPoints);
        DenseVector firstPoint = new DenseVector(points.get(firstPointIndex));
        taken[firstPointIndex] = true;
        
        resultSet.add(new Cluster(firstPoint));

        // To keep track of the minimum distance squared of elements of
        // pointList to elements of resultSet.
        float[] minDistSquared = new float[numPoints];

        // Initialize the elements.  Since the only point in resultSet is firstPoint,
        // this is very easy.
        for (int i = 0; i < numPoints; i++) {
            if (i != firstPointIndex) { // That point isn't considered
            	minDistSquared[i] = firstPoint.euclideanDistanceSquared(points.get(i));
            }
        }

        while (resultSet.size() < k) {
            // Sum up the squared distances for the points in pointList not
            // already taken.
            float distSqSum = 0.0f;

            for (int i = 0; i < numPoints; i++) {
                if (!taken[i]) {
                    distSqSum += minDistSquared[i];
                }
            }

            // Add one new data point as a center. Each point x is chosen with
            // probability proportional to D(x)2
            float r = random.nextFloat() * distSqSum;

            // The index of the next point to be added to the resultSet.
            int nextPointIndex = -1;

            // Sum through the squared min distances again, stopping when
            // sum >= r.
            float sum = 0.0f;
            for (int i = 0; i < numPoints; i++) {
                if (!taken[i]) {
                    sum += minDistSquared[i];
                    if (sum >= r) {
                        nextPointIndex = i;
                        break;
                    }
                }
            }

            // If it's not set to >= 0, the point wasn't found in the previous
            // for loop, probably because distances are extremely small.  Just pick
            // the last available point.
            if (nextPointIndex == -1) {
                for (int i = numPoints - 1; i >= 0; i--) {
                    if (!taken[i]) {
                        nextPointIndex = i;
                        break;
                    }
                }
            }

            // We found one.
            if (nextPointIndex >= 0) {
                DenseVector p = new DenseVector(points.get(nextPointIndex));
                resultSet.add(new Cluster(p));

                // Mark it as taken.
                taken[nextPointIndex] = true;

                if (resultSet.size() < k) {
                    // Now update elements of minDistSquared.  We only have to compute
                    // the distance to the new center to do this.
                    for (int j = 0; j < numPoints; j++) {
                        // Only have to worry about the points still not taken.
                        if (!taken[j]) {
                            float d2 = p.euclideanDistanceSquared(points.get(j));
                            if (d2 < minDistSquared[j]) {
                                minDistSquared[j] = d2;
                            }
                        }
                    }
                }

            } else {
                // None found --
                // Break from the while loop to prevent
                // an infinite loop.
                break;
            }
        }

        return resultSet;
    }
    
    
    private boolean recalculateCentroids(ArrayList<Cluster> clusters) {
    	
    	boolean emptyCluster = false;
    	
        // Calculate the centroids
        for (Cluster cluster : clusters) {
            if (cluster.getPoints().isEmpty()) {
            	cluster.setCentroid(getPointFromLargestVarianceCluster(clusters));
            	emptyCluster = true;
            } else {
            	cluster.computeCentroid();
            }
        }
    	return emptyCluster;
    }
    
    
    
    public ArrayList<Cluster> cluster(ArrayList<DenseVector> points) {

        // create the initial clusters
        ArrayList<Cluster> clusters = chooseInitialCenters(points);

        // create an array containing the latest assignment of a point to a cluster
        // no need to initialize the array, as it will be filled with the first assignment
        int[] assignments = new int[points.size()];
        
        vectorsToCentroid(clusters, points, assignments);

    	boolean emptyCluster;
    	
        // iterate through updating the centers until we're done
    	int max = (maxIters < 0) ? Integer.MAX_VALUE : maxIters;
        for (int count = 0; count < max; count++) {
        	
        	System.out.println("Iteration "+count);

        	// Calculate Centroids
        	emptyCluster = recalculateCentroids(clusters);
            
            // Assign vectors to centroids
            int changes = vectorsToCentroid(clusters, points, assignments);
            
            // if there were no more changes in the point-to-cluster assignment
            // and there are no empty clusters left, return the current clusters
            if (changes == 0 && !emptyCluster) {
                return finalizeClusters(clusters);
            }
        }
        return finalizeClusters(clusters);
    }
    
    
    private int vectorsToCentroid(ArrayList<Cluster> clusters, ArrayList<DenseVector> vecs,
        int[] assignments) {
		int assignedDifferently = 0;
		int pointIndex = 0;
		int clusterIndex;
		
		// Get re-assignment cluster index
		for (DenseVector v : vecs) {
		
			clusterIndex = getNearestCluster(clusters, v);
			if (clusterIndex != assignments[pointIndex]) {
				assignedDifferently++;
			}
			assignments[pointIndex] = clusterIndex;
			
			Cluster cluster = clusters.get(clusterIndex);
			cluster.addPoint(v);
			//assignments[pointIndex++] = clusterIndex;
			pointIndex++;
		}
		
		// Clear points from centroids
		for (Cluster c : clusters) c.clearPoints();
		
		// Add points to clusters
		pointIndex = 0;
		for (DenseVector v : vecs) {
			Cluster cluster = clusters.get(assignments[pointIndex]);
			cluster.addPoint(v);
			pointIndex++;
		}
	
		return assignedDifferently;			
	}
    
    
    private DenseVector getPointFromLargestVarianceCluster(ArrayList<Cluster> clusters) {

        double maxVariance = Double.NEGATIVE_INFINITY;
        Cluster selected = null;
        
        for (Cluster c : clusters) {
            if (!c.getPoints().isEmpty()) {

                // compute the distance variance of the current cluster
                DenseVector center = c.getCentroid();
                
                Variance stat = new Variance();
                for (DenseVector point : c.getPoints()) {
                    stat.increment( point.euclideanDistanceSquared(center));
                }
                final double variance = stat.getResult();

                // select the cluster with the largest variance
                if (variance > maxVariance) {
                    maxVariance = variance;
                    selected = c;
                }

            }
        }

        // did we find at least one non-empty cluster ?
        if (selected == null) {
            //   ????
        }

        // extract a random point from the cluster
        ArrayList<DenseVector> selectedPoints = selected.getPoints();
        
        return selectedPoints.remove(random.nextInt(selectedPoints.size()));

    }
    
    private int getNearestCluster(ArrayList<Cluster> clusters, DenseVector vec) {
        float minDistance = Float.MAX_VALUE;
        
        int clusterIndex = 0;
        int minCluster = 0;
        
        for (Cluster c : clusters) {
        	
            float distance = vec.euclideanDistanceSquared(c.getCentroid());
            
            if (distance < minDistance) {
                minDistance = distance;
                minCluster = clusterIndex;
            }
            clusterIndex++;
        }
        return minCluster;
    }

    // Simply makes sure that centroids are properly computed
    // and MSE is calculated.
    public ArrayList<Cluster> finalizeClusters(ArrayList<Cluster> clusters) {
        for (Cluster c : clusters) {
        	c.computeCentroid();
        	c.computeMSE();
        }
    	return clusters;
    }
    
    
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}





