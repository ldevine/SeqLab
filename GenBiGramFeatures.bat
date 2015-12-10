java -cp ./libs/SeqLab.jar;./libs/jopt-simple-4.8-beta-1.jar;./libs/commons-math3-3.5.jar tools.GenBiGramVecFeatures -wordvecs vectors_PM_100_2.txt -lexdim 30 -labelledfile i2b2_train.txt -out features.txt -centroids centroids.txt -startIdx -1 -numbigrams 3

		
		
		
		