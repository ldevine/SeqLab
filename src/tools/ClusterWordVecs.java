package tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import tools.ClusterSeqVecs.ClusterStats;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import clustering.Cluster;
import clustering.KMeansPP;
import core.DenseVector;
import core.Utils;
import core.Vocab;


public class ClusterWordVecs {

	class ClusterStats {

		public int id;
		public int numVecs;
		
		public float minSE;
		public float maxSE;
		public float MSE;
		
		public int minSEVecID;
	}
	
	
	OptionSet options;
	OptionParser parser;
	
	OptionSpec<String> w2vFileSp;
	//OptionSpec<String> vecFileSp;	
	
	OptionSpec<String> centroidFileSp;	
	OptionSpec<String> clusterStatsFileSp;
	OptionSpec<String> membershipFileSp;	
	OptionSpec<String> verboseClustersFileSp;	
	
	OptionSpec<Integer> numClustersSp;
	OptionSpec<Integer> maxItersSp;	
	
	//String stringFile;
	String w2vFile;
	String centroidFile;
	String clusterStatsFile;
	String membershipFile;
	String verboseClustersFile;
	
	int numClusters;
	int maxIters;
	
	int vecDim;
	
	ArrayList<String> strings = new ArrayList<String>();	
	ArrayList<DenseVector> vecs = new ArrayList<DenseVector>();
	
	ArrayList<Cluster> clusterResults;
	ArrayList<ClusterStats> clusterStats;
	
	public ClusterWordVecs() {
		//stringFile="";
		w2vFile="";
		
		vecDim = 0;
		
		clusterStats = new ArrayList<ClusterStats>();
	}
	
	void sortClusterStats() {
		
		// Sort Clusters by MSE
		Comparator<ClusterStats> cmp = new Comparator<ClusterStats>() {
			public int compare(ClusterStats r1, ClusterStats r2) {
				return Utils.sign(r2.MSE - r1.MSE);
		}};	
		
		Collections.sort(clusterStats, cmp);		
	}
	
	
	ClusterStats getClusterStats(Cluster c) {
		
		ClusterStats stats = new ClusterStats();
		stats.numVecs = c.size();
		stats.MSE = c.MSE;
		
		float dist;
		DenseVector cen = c.getCentroid();
		
		float min = Float.MAX_VALUE;
		float max = Float.MIN_VALUE;
		
		for (DenseVector v : c.getPoints()) {			    	
	    	dist = cen.euclideanDistanceSquared(v);
	    	if (dist<=min) {
	    		min = dist;
	    		stats.minSEVecID = v.id;
	    	}
	    	if (dist>=max) max = dist;
	    }
		
		stats.minSE = min;
		stats.maxSE = max;
		
		return stats;
	}
	
	void writeClusterStats() {
		
		try { 
			File file = new File(clusterStatsFile);
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);			
			
			for (int i=0; i<clusterStats.size(); i++) {				
				ClusterStats stats = clusterStats.get(i);				
				
				String str = ""+stats.id+"\t"+stats.numVecs+"\t"+stats.minSE+"\t"
				+stats.maxSE+"\t"+stats.MSE+"\t"+stats.minSEVecID;
				
				bw.write(str+"\n");				
			}
			
			bw.close(); 		
			
			System.out.println("Done");
			 
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	void writeClustersVerbose() {
		
		Cluster cl;
		
		try { 
			File file = new File(verboseClustersFile);
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);

			for (int i=0; i<clusterResults.size(); i++) {	
				
				bw.write("\n------\n\n");
				
				ClusterStats stats = clusterStats.get(i);				
				String str = ""+stats.id+"\t"+stats.numVecs+"\t"+stats.minSE+"\t"
				+stats.maxSE+"\t"+stats.MSE;
				
				bw.write(i+"\n\n");
				
				cl = clusterResults.get(i);
				
				for (DenseVector v : cl.getPoints()) {			    	
			    	bw.write(strings.get(v.id)+"\n");
			    }
			}
			
			/*
			for (int i=0; i<clusterStats.size(); i++) {	
				
				bw.write("\n------\n\n");
				
				ClusterStats stats = clusterStats.get(i);				
				String str = ""+stats.id+"\t"+stats.numVecs+"\t"+stats.minSE+"\t"
				+stats.maxSE+"\t"+stats.MSE;
				
				bw.write(str+"\n\n");
				
				cl = clusterResults.get(stats.id);
				
				for (DenseVector v : cl.getPoints()) {			    	
			    	bw.write(strings.get(v.id)+"\n");
			    }
			}*/
			
			bw.close(); 		
			
			System.out.println("Done");
			 
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	void writeMembershipFile() {
		
		Cluster cl;
		
		// Create container for cluster ids
		ArrayList<Integer> ids = new ArrayList<Integer>();		
		for (int i=0; i<vecs.size(); i++) ids.add(-1);
		
		for (int i=0; i<clusterResults.size(); i++) {
			cl = clusterResults.get(i);
			
			for (DenseVector v : cl.getPoints()) {			    	
		    	ids.set(v.id, i);
		    }
		}
		
		try { 
			File file = new File(membershipFile);
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			for (int i=0; i<ids.size(); i++) bw.write(""+ids.get(i)+"\n");
			
			bw.close(); 		
			
			System.out.println("Done");
			 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	void writeCentroids() {
		
		Cluster cl;
		DenseVector cen;
		
		try { 
			File file = new File(centroidFile);
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			bw.write(""+clusterResults.size()+" "+vecDim+"\n");
			
			for (int i=0; i<clusterResults.size(); i++) {
				cl = clusterResults.get(i);
				cen = cl.getCentroid();				
				cen.writeText(bw);
			}
			
			bw.close(); 		
			
			System.out.println("Done");
			 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	void clusterVecs( int numClusters) {
		
		System.out.println("Number of vectors ... "+vecs.size());		
		System.out.println("Clustering ...");
		
		Cluster cl;
		KMeansPP clusterer = new KMeansPP(numClusters, maxIters);
		clusterResults = clusterer.cluster(vecs);
		
		// Make sure centroids are computed
		// And compute cluster stats;
		for ( int i=0; i<clusterResults.size(); i++) {
			Cluster c = clusterResults.get(i);
			c.computeCentroid();
			ClusterStats stats = getClusterStats(c);			
			clusterStats.add(stats);
			stats.id = i; // Make sure stats has the id of the cluster it represents
		}
		
		sortClusterStats();
		
		// Write stats
		writeClusterStats();
		
		// Write verbose
		writeClustersVerbose();
		
		// Membership File
		writeMembershipFile();
		
		// Write centroids
		writeCentroids();		
	}
	
	public void readW2VFile(String fileName) {
		
		Vocab v;
		BufferedReader br = null;
		DenseVector vec;
		String strs[];
		
		int dim;
		int numVectors;
		int termCount = 0;
		
		try {
 
			String line;
 
			br = new BufferedReader(new FileReader(fileName));
 
			// Parse first line
			line = br.readLine();
			strs = line.split(" ");
			numVectors = Integer.parseInt(strs[0]);
			vecDim = Integer.parseInt(strs[1]);
			
			System.out.println(""+numVectors+"  "+vecDim);
			
			while ((line = br.readLine()) != null) {
				
				strs = line.split(" ");
				
				// Add new term
				strings.add(strs[0]);
				
				// Create new vector
				vec = new DenseVector(vecDim);
				vec.id = termCount;
				
				for (int i=1; i<strs.length; i++) {
					vec.setVal(i-1, Float.parseFloat(strs[i]));					
				}			
				
				// Add vector
				vecs.add(vec);
				
				termCount++;
			}
 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	// Read strings
	void readStrings(String fileName) {	
		BufferedReader br = null;		 
		try { 
			String line;
			br = new BufferedReader(new FileReader(fileName));
 			while ((line = br.readLine()) != null) {
				strings.add(line);
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	// Read vecs
	public void readVecFile(String fileName) {
		
		BufferedReader br = null;
		DenseVector vec;
		String strs[];
		
		int dim;
		int numVectors;
		
		try { 
			String line; 
			br = new BufferedReader(new FileReader(fileName));
 
			// Parse first line
			line = br.readLine();
			strs = line.split(" ");
			numVectors = Integer.parseInt(strs[0]);
			dim = Integer.parseInt(strs[1]);
			
			System.out.println(""+numVectors+"  "+dim);
			
			while ((line = br.readLine()) != null) {
				
				strs = line.split(" ");
				
				// Create new vector
				vec = new DenseVector(dim);
				vec.id = vecs.size();
				
				for (int i=1; i<strs.length; i++) {
					vec.setVal(i-1, Float.parseFloat(strs[i]));					
				}			
				
				// Add vector
				vecs.add(vec);
			}
 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Prepare the options parser
	void prepareOptionsParser() {
		
        parser = new OptionParser();
        
        w2vFileSp = parser.accepts( "vecs" ).withRequiredArg().ofType( String.class )
        	.defaultsTo( "" );
        centroidFileSp = parser.accepts( "centroids" ).withOptionalArg().ofType( String.class )
            	.defaultsTo( "" );        
    	clusterStatsFileSp = parser.accepts( "stats" ).withOptionalArg().ofType( String.class )
            	.defaultsTo( "" );
        membershipFileSp = parser.accepts( "members" ).withOptionalArg().ofType( String.class )
            	.defaultsTo( "" );        
    	verboseClustersFileSp = parser.accepts( "verbose" ).withOptionalArg().ofType( String.class )
            	.defaultsTo( "" );    	

    	numClustersSp = parser.accepts( "clusters" ).withRequiredArg().ofType( Integer.class )
            	.defaultsTo( 10 );
    	maxItersSp = parser.accepts( "maxiters" ).withRequiredArg().ofType( Integer.class )
            	.defaultsTo( 20 );
	}

	
	void parseOptions(String[] args) {        
		options = parser.parse( args );
		
		w2vFile = w2vFileSp.value(options);
		centroidFile = centroidFileSp.value(options);
		clusterStatsFile = clusterStatsFileSp.value(options);
		membershipFile = membershipFileSp.value(options);
		verboseClustersFile = verboseClustersFileSp.value(options);
		
		numClusters = numClustersSp.value(options);
		maxIters = maxItersSp.value(options);		
	}
	
	void run() {
		
		readW2VFile(w2vFile);
		clusterVecs(numClusters);
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		////String argss="-vecs vectors_PM_100_2.txt -centroids centroids.txt "+
		//"-stats stats.txt -members members.txt -verbose clusters.txt -clusters 256 "+
		//		"-maxiters 30";
		
		//args = argss.split(" ");
		
		
		ClusterWordVecs cv = new ClusterWordVecs();
				
		cv.prepareOptionsParser();
		cv.parseOptions(args);
		
		cv.run();
	}

}
