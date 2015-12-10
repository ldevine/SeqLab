# SeqLab

The code and scripts in this repository accompanies the paper:

De Vine, L., Kholghi, M., Zuccon, G., Sitbon, L., Nguyen, A., "Analysis ofWord Embeddings and Sequence Features for Clinical Information Extraction", 13th Annual Workshop of the Australasian Language Technology Association, 2015.
Proceedings here: http://www.alta.asn.au/events/alta2015/proceedings.pdf

The code is somewhat specific to the i2b2 2010 concept extraction task, but could, with some minor modifications, be used for other concept extraction tasks.

The code assumes access to an implementation of the Skip-gram language model from Mikolov et al. 2013, such as the freely available word2vec:
https://code.google.com/p/word2vec/

Vectors created with word2vec should be saved in text file format, not binary.

[Note: actually any real valued vector-space representation for tokens could be used.] 

The java code has been exported as a jar file and contains a number of command line applications which can be accessed via the provided scripts.


The following scripts are provided:


*Labels2Sentences.bat:*
Converts tokens from single token/line to single sentence per line.


*ExtractNGrams.bat:*
Extracts distinct ngrams from some corpus of text and saves the result in a file with one ngram per line.

*ClusterWordVecs.bat:*
Clusters word vectors and saves the results to disk.

*MakeSeqVecs.bat*
Creates vectors for sequences of tokens contained on each line of an input file. The created vectors are built using a concatenation of the provided word vectors and generated "lexical" vectors, which encode orthographic information of tokens.

*ClusterSeqVecs.bat*
Clusters vectors associated with token sequences.

*ClusterSeqVecs.bat*
Clusters vectors associated with token sequences.

*GenWordFeatures.bat*
This script takes a clustering of word vectors and applies the clustering features to some given text data which is going to be labelled.

*GenBiGramFeatures.bat*
This script takes a clustering of bigram vectors and applies the clustering features to some given text data which is going to be labelled.

*GenSentFeatures.bat*
This script takes a clustering of sentence vectors and applies the clustering features to some given text data which is going to be labelled.

*CSVExtract.bat, CSVInsert.bat, CSVMerge.bat*
These scripts can be used to manipulate feature files in preparation for use with a classification software such as Mallet's CRF implementation.



A typical execution workflow would be:

1) create word embeddings with word2vec, saving vectors in text file format.
2) cluster vectors using ClusterWordVecs.bat
3) create features for a text dataset using the word vector clusters using GenWordFeatures.bat
4) prepare feature files for classification algorithm, eg. Mallet CRF




This code is not meant to be a polished output for end user applications.



