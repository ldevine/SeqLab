package tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import tools.ClusterWordVecs.ClusterStats;
import clustering.Cluster;
import clustering.KMeansPP;
import core.DenseVector;
import core.Utils;

public class ClusterSeqVecs {

	
	class ClusterStats {

		public int id;
		public int numVecs;
		
		public int minSentLength;
		public int maxSentLength;		
		public float avSentLength;
		
		public float stdDevSentLength;		
		public float median;
		
		public float minSE;
		public float maxSE;
		public float MSE;
		
		public int minSEVecID;
	}
	
	
	OptionSet options;
	OptionParser parser;
	
	OptionSpec<String> stringFileSp;
	OptionSpec<String> vecFileSp;	
	
	OptionSpec<String> centroidFileSp;	
	OptionSpec<String> clusterStatsFileSp;
	OptionSpec<String> membershipFileSp;	
	OptionSpec<String> verboseClustersFileSp;	
	
	OptionSpec<Integer> numClustersSp;
	OptionSpec<Integer> maxItersSp;	
	
	String stringFile;
	String vecFile;
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
	
	public ClusterSeqVecs() {
		stringFile="";
		vecFile="";
		
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
	
	int numTokens(String str) {
		String[] strs = str.split(" ");
		return strs.length;
	}
	
	ClusterStats getClusterStats(Cluster c) {
		
		ClusterStats stats = new ClusterStats();
		stats.numVecs = c.size();
		stats.MSE = c.MSE;
		
		String s;
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
		
		// Get sentence lengths
		int minSL = Integer.MAX_VALUE;
		int maxSL = Integer.MIN_VALUE;
		int sumSL = 0;
		int len;
		float sum = 0;
		int count = 0;
		
		for (DenseVector v : c.getPoints()) {
			s = strings.get(v.id);
			len = numTokens(s);			
	    	if (len<=minSL) minSL = len;
	    	if (len>=maxSL) maxSL = len;
	    	
	    	count++;
	    	sum += len;
	    }		
		
		stats.maxSentLength = maxSL;
		stats.minSentLength = minSL;
		stats.avSentLength = sum / (float)count;
		
		// Compue std dev and median
		sum = 0.0f;
		count = 0;
		
		ArrayList<Integer> lengths = new ArrayList<Integer>();
		
		for (DenseVector v : c.getPoints()) {
			count++;
			s = strings.get(v.id);
			len = numTokens(s);
			lengths.add(len);
            sum += (len - stats.avSentLength) * (len - stats.avSentLength);
        }
		stats.stdDevSentLength = (float)Math.sqrt( sum / (float)count );
		
		// Get Median
		Collections.sort(lengths);
		
		if (lengths.size() % 2 == 0)
		    stats.median = (float)((float)lengths.get(lengths.size()/2) + 
		    		(float)lengths.get(lengths.size()/2-1)) / 2.0f;
		else
			stats.median = (float)lengths.get(lengths.size()/2);
		
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
				+stats.maxSE+"\t"+stats.MSE+"\t"+stats.minSentLength+"\t"+
						+stats.maxSentLength+"\t"+stats.avSentLength+"\t"+
						stats.stdDevSentLength+"\t"+stats.median+"\t"+
						stats.minSEVecID;	
				
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
		
		//int dim;
		int numVectors;
		
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
				
				// Create new vector
				vec = new DenseVector(vecDim);
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
	
	/*
	void writeCentroids(ArrayList<Cluster> clusterResults) {				
		
		System.out.println("Writing centroids ...");
		
		try {			
			
			File file = new File("centroids.txt");
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			bw.write(""+clusterResults.size()+" "+vecDim+"\n");
			
			System.out.println(vecDim);
			
			for (int i=0; i<clusterResults.size(); i++) {
				Cluster c = clusterResults.get(i);
				System.out.println(""+i+"  :  " + c.getPoints().size());
				if (i==0) {
				    for (DenseVector v : clusterResults.get(i).getPoints()) {
				    	//System.out.println(lines.get(v.id));
				    	//System.out.println(v);
				    }
				}
				if (c.getPoints().size()==0) {
					System.out.println("***");
				}
				c.computeCentroid();
				c.getCentroid().writeText(bw);
			}
			
			bw.close(); 
			
			System.out.println("Done");
 
		} catch (Exception e) {
			e.printStackTrace();
		}			
	}
	*/
	
	// Prepare the options parser
	void prepareOptionsParser() {
		
        parser = new OptionParser();
    	
    	stringFileSp = parser.accepts( "strings" ).withRequiredArg().ofType( String.class )
        	.defaultsTo( "" );        
        vecFileSp = parser.accepts( "vecs" ).withRequiredArg().ofType( String.class )
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
		
		stringFile = stringFileSp.value(options);
		vecFile = vecFileSp.value(options);
		centroidFile = centroidFileSp.value(options);
		clusterStatsFile = clusterStatsFileSp.value(options);
		membershipFile = membershipFileSp.value(options);
		verboseClustersFile = verboseClustersFileSp.value(options);
		
		numClusters = numClustersSp.value(options);
		maxIters = maxItersSp.value(options);
		
		//System.out.println("\n"+maxIters+"\n---");
	}	
	
	void run() {
		
		readStrings(stringFile);
		readVecFile(vecFile);
		clusterVecs(numClusters);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		//String argss="-strings ngrams.txt -vecs seqvecs.txt -centroids centroids.txt "+
		//"-stats stats.txt -members members.txt -verbose clusters.txt -clusters 256 "+
		//		"-maxiters 20";
		//args = argss.split(" ");
		
		ClusterSeqVecs cs = new ClusterSeqVecs();
		
		cs.prepareOptionsParser();
		cs.parseOptions(args);
		
		cs.run();
	}

}



