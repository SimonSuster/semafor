package edu.cmu.cs.lti.ark.fn.identification;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.common.io.CharStreams;
import com.google.common.io.InputSupplier;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import static edu.cmu.cs.lti.ark.util.IntRanges.xrange;

/**
 * @author sthomson@cs.cmu.edu
 */
public class Senna {
	//public static final int SENNA_VECTOR_DIM = 50;
    //public static final int SENNA_VECTOR_DIM = 128;
	static int sennaVectorDim;
	//public static final String DEFAULT_SENNA_WORDS_FILE = "senna/words.lst";
	public static final String DEFAULT_SENNA_WORDS_FILE = "sg_czeng.en_lH_lL2_lr0.1_e1e-06_mb1000_min20_max100000_ep3_neg1_s1e08_dim50_del0_downFalse_win5_sfac1e-03_lcrossentropy_oAdagrad/W_v.txt";
    //public static final String DEFAULT_SENNA_WORDS_FILE = "senna/posttype_cumul_plain.vocab";
    //public static final String DEFAULT_SENNA_WORDS_FILE = "wordreps/hmm_en_rel_miter2_N64_nsent1613710_alpha1_batchsize1000_NMOD-PMOD-SUB/posttype_cumul_plain.vocab";
    //public static final String DEFAULT_SENNA_WORDS_FILE = "wordreps/hmm_en_tree_miter2_N64_nsent1613710_alpha1_batchsize1000_/posttype_plain.vocab";
    //public static final String DEFAULT_SENNA_WORDS_FILE = "wordreps/hmm_en_rel_miter2_N64_nsent1613710_alpha1_batchsize1000_NMOD-PMOD-SUB/posttype_wordrel_plain.vocab";
	//public static final String DEFAULT_SENNA_WORDS_FILE = "wordreps/hmm_en__miter2_N64_nsent1613710_alpha1_batchsize1000_/posttype_plain.vocab";
    //    	public static final String DEFAULT_SENNA_WORDS_FILE = "wordreps/w2v128.vocab";

	//public static final String DEFAULT_SENNA_WORDS_FILE = "/home/p262594/projects/paper@HMTreeMwithrelations/code/wordreps/hmm_en_tree_miter2_N64_nsent1613710_alpha1_batchsize1000_/posttype_plain.vocab";

	//public static final String DEFAULT_SENNA_VECTORS_FILE = "senna/embeddings.txt";
	public static final String DEFAULT_SENNA_VECTORS_FILE = "sg_czeng.en_lH_lL2_lr0.1_e1e-06_mb1000_min20_max100000_ep3_neg1_s1e08_dim50_del0_downFalse_win5_sfac1e-03_lcrossentropy_oAdagrad/W_e.txt";
	//public static final String DEFAULT_SENNA_VECTORS_FILE = "senna/posttype_cumul_plain.txt";
    //public static final String DEFAULT_SENNA_VECTORS_FILE = "wordreps/hmm_en_rel_miter2_N64_nsent1613710_alpha1_batchsize1000_NMOD-PMOD-SUB/posttype_cumul_plain.txt";
    //public static final String DEFAULT_SENNA_VECTORS_FILE = "wordreps/hmm_en_rel_miter2_N64_nsent1613710_alpha1_batchsize1000_NMOD-PMOD-SUB/posttype_wordrel_plain.txt";
    	//public static final String DEFAULT_SENNA_VECTORS_FILE = "wordreps/hmm_en__miter2_N64_nsent1613710_alpha1_batchsize1000_/posttype_plain.txt";
    //public static final String DEFAULT_SENNA_VECTORS_FILE = "wordreps/hmm_en_tree_miter2_N64_nsent1613710_alpha1_batchsize1000_/posttype_plain.txt";
    //	public static final String DEFAULT_SENNA_WORDS_FILE = "wordreps/treebrown128onehot.vocab";
    // 	public static final String DEFAULT_SENNA_VECTORS_FILE = "wordreps/treebrown128onehot.txt";
    //public static final String DEFAULT_SENNA_WORDS_FILE = "wordreps/brown128onehot.vocab";
    //	public static final String DEFAULT_SENNA_VECTORS_FILE = "wordreps/brown128onehot.txt";
    // public static final String DEFAULT_SENNA_VECTORS_FILE = "wordreps/w2v128.txt";
	//public static final String DEFAULT_SENNA_VECTORS_FILE = "/home/p262594/projects/paper@HMTreeMwithrelations/code/wordreps/hmm_en_tree_miter2_N64_nsent1613710_alpha1_batchsize1000_/posttype_plain.txt";


	private static InputSupplier<InputStream> DEFAULT_WORDS_SUPPLIER = new InputSupplier<InputStream>() {
		@Override public InputStream getInput() throws IOException {
			System.out.println(DEFAULT_SENNA_WORDS_FILE);
			return getClass().getClassLoader().getResourceAsStream(DEFAULT_SENNA_WORDS_FILE);
		} };
	private static InputSupplier<InputStream> DEFAULT_VECTORS_SUPPLIER = new InputSupplier<InputStream>() {
		@Override public InputStream getInput() throws IOException {
			System.out.println(DEFAULT_SENNA_VECTORS_FILE);
            return getClass().getClassLoader().getResourceAsStream(DEFAULT_SENNA_VECTORS_FILE);
		} };

	private final Map<String, double[]> embeddings;

	public Senna(Map<String, double[]> embeddings, int sennaVectorDim) {
		this.embeddings = embeddings;
		this.sennaVectorDim = sennaVectorDim;
	}

	public static Senna load(int sennaVectorDim) throws IOException {
		return load(CharStreams.newReaderSupplier(DEFAULT_WORDS_SUPPLIER, Charsets.UTF_8),
				CharStreams.newReaderSupplier(DEFAULT_VECTORS_SUPPLIER, Charsets.UTF_8),
				sennaVectorDim);
	}

	public static Senna load(InputSupplier<InputStreamReader> wordsInput,
							 InputSupplier<InputStreamReader> vectorsInput,
							 int sennaVectorDim) throws IOException {
	    return new Senna(readFiles(wordsInput, vectorsInput, sennaVectorDim), sennaVectorDim);
	}

	private static Map<String, double[]> readFiles(InputSupplier<InputStreamReader> wordsInput,
						       InputSupplier<InputStreamReader> vectorsInput, int sennaVectorDim) throws IOException {
		final Map<String, double[]> embeddings = Maps.newHashMapWithExpectedSize(130000);
		final List<String> words = CharStreams.readLines(wordsInput);
		final List<String> vectorLines = CharStreams.readLines(vectorsInput);
		//System.err.println(sennaVectorDim);
		for (int i : xrange(words.size())) {
			final String[] fields = vectorLines.get(i).split(" ");
			final double[] vector = new double[sennaVectorDim];
			for (int j : xrange(sennaVectorDim)) {
				vector[j] = Float.parseFloat(fields[j]);
			}
			embeddings.put(words.get(i), vector);
		}
		return embeddings;
	}

	public Optional<double[]> getEmbedding(String word) {
		return Optional.fromNullable(embeddings.get(word));
	}
}
