java -cp ./libs/SeqLab.jar;./libs/jopt-simple-4.8-beta-1.jar;./libs/commons-math3-3.5.jar tools.ClusterWordVecs -vecs vectors_PM_100_2.txt -centroids centroids.txt -stats stats.txt -members members.txt -verbose clusters.txt -clusters 256 -maxiters 20