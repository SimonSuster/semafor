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

import edu.cmu.cs.lti.ark.fn.data.prep.formats.Token;
import static edu.cmu.cs.lti.ark.util.IntRanges.xrange;
import static edu.cmu.cs.lti.ark.util.Math2.*;


/**
 * @author sthomson@cs.cmu.edu
 */
public class Senna {
	static int nSenses;
	static int sennaVectorDim;

    public static final String DEFAULT_SENNA_WORDS_FILE = "senses3_czeng.en_lH_lL2_lr0.1_e1e-06_mb1000_min20_max100000_ep3_neg1_s1e08_dim50_del0_downFalse_win5_sfac1e-03_lcrossentropy_oAdagrad/W_v.txt";
    public static final String DEFAULT_SENNA_VECTORS_FILE = "senses3_czeng.en_lH_lL2_lr0.1_e1e-06_mb1000_min20_max100000_ep3_neg1_s1e08_dim50_del0_downFalse_win5_sfac1e-03_lcrossentropy_oAdagrad/W_e.txt";
	public static final String DEFAULT_SENNA_CVECTORS_FILE = "senses3_czeng.en_lH_lL2_lr0.1_e1e-06_mb1000_min20_max100000_ep3_neg1_s1e08_dim50_del0_downFalse_win5_sfac1e-03_lcrossentropy_oAdagrad/W_c.txt";

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
	private static InputSupplier<InputStream> DEFAULT_CVECTORS_SUPPLIER = new InputSupplier<InputStream>() {
		@Override public InputStream getInput() throws IOException {
			System.out.println(DEFAULT_SENNA_CVECTORS_FILE);
			return getClass().getClassLoader().getResourceAsStream(DEFAULT_SENNA_CVECTORS_FILE);
		} };

	private final Map<String, double[][]> embeddings;  //
	private final Map<String, double[]> cembeddings; // context embeddings

	public Senna(Map<String, double[][]> embeddings, Map<String, double[]> cembeddings,
				 int sennaVectorDim, int nSenses) {
		this.embeddings = embeddings;
		this.cembeddings = cembeddings;
		Senna.sennaVectorDim = sennaVectorDim;
		Senna.nSenses = nSenses;
	}

	public static Senna load(int sennaVectorDim, int nSenses) throws IOException {
		return load(CharStreams.newReaderSupplier(DEFAULT_WORDS_SUPPLIER, Charsets.UTF_8),
				CharStreams.newReaderSupplier(DEFAULT_VECTORS_SUPPLIER, Charsets.UTF_8),
				CharStreams.newReaderSupplier(DEFAULT_CVECTORS_SUPPLIER, Charsets.UTF_8),
				sennaVectorDim, nSenses);
	}

	public static Senna load(InputSupplier<InputStreamReader> wordsInput,
							 InputSupplier<InputStreamReader> vectorsInput,
							 InputSupplier<InputStreamReader> cvectorsInput,
							 int sennaVectorDim, int nSenses) throws IOException {
	    return new Senna(readMultiEmbs(wordsInput, vectorsInput, sennaVectorDim, nSenses),
				readCembs(wordsInput, cvectorsInput, sennaVectorDim, nSenses), sennaVectorDim, nSenses);
	}

	private static Map<String, double[][]> readMultiEmbs(InputSupplier<InputStreamReader> wordsInput,
													   InputSupplier<InputStreamReader> vectorsInput, int sennaVectorDim,
													   int nSenses) throws IOException {
		System.err.println("Start readMultiEmbs");
		final Map<String, double[][]> embeddings = Maps.newHashMapWithExpectedSize(130000);
		final List<String> words = CharStreams.readLines(wordsInput);
		final List<String> vectorLines = CharStreams.readLines(vectorsInput);

		for (int i : xrange(words.size())) {
			final String[] fields = vectorLines.get(i).split(" ");
			final double[][] mat = new double[nSenses][sennaVectorDim];
			for (int k : xrange(nSenses)) {
				for (int j : xrange(sennaVectorDim)) {
					mat[k][j] = Float.parseFloat(fields[k*sennaVectorDim + j]);
				}
			}
			embeddings.put(words.get(i), mat);
		}
		return embeddings;
	}
	private static Map<String, double[]> readCembs(InputSupplier<InputStreamReader> wordsInput,
												   InputSupplier<InputStreamReader> cvectorsInput,
												   int sennaVectorDim, int nSenses) throws IOException {
		System.err.println("Start readCembs");
		final Map<String, double[]> cembeddings = Maps.newHashMapWithExpectedSize(130000);
		final List<String> words = CharStreams.readLines(wordsInput);
		final List<String> vectorLines = CharStreams.readLines(cvectorsInput);
		//System.err.println(sennaVectorDim);
		for (int i : xrange(words.size())) {
			final String[] fields = vectorLines.get(i).split(" ");
			final double[] vector = new double[sennaVectorDim];
			for (int j : xrange(sennaVectorDim)) {
				vector[j] = Float.parseFloat(fields[j]);
			}
			cembeddings.put(words.get(i), vector);
		}
		return cembeddings;
	}

	//public Optional<double[]> getEmbedding(String word) {
	//	return Optional.fromNullable(embeddings.get(word));
	//}

	public Optional<double[]> getEmbeddingFromContext(String word, int idx, List<Token> tokens) {
		final int winSize = 3; // to each side
		final int winStart = Math.max(0, idx-winSize);
		final int winEnd = Math.min(tokens.size(), idx+winSize+1);

		final Optional<double[][]> sembs = Optional.fromNullable(embeddings.get(word));
		if (sembs.isPresent()) {
			final double[][] sensembs = sembs.get();
			final double[][] cembs = new double[winSize * 2][Senna.sennaVectorDim];

			int cembs_idx = 0;
			// left contexts
			for (int i : xrange(winStart, idx)) {
				if (cembeddings.containsKey(tokens.get(i).getForm())) {
					cembs[cembs_idx] = cembeddings.get(tokens.get(i).getForm());
					cembs_idx += 1;
				}
			}
			// right contexts
			if (idx < winEnd) {
				for (int i : xrange(idx+1, winEnd)) {
					if (cembeddings.containsKey(tokens.get(i).getForm())) {
						cembs[cembs_idx] = cembeddings.get(tokens.get(i).getForm());
						cembs_idx += 1;
					}
				}
			}
			// average
			final double[] cemb = new double[Senna.sennaVectorDim];
			for (int j : xrange(cembs_idx)) {
				for (int i : xrange(Senna.sennaVectorDim)) {
					cemb[i] += cembs[j][i];
				}
			}
			for (int i : xrange(cemb.length)) {
				cemb[i] = cemb[i] / cembs_idx;
			}
			// dot product
			double[] acts = new double[sensembs.length];
			for (int i : xrange(sensembs.length)) {
				acts[i] = dotProduct(cemb, sensembs[i]);
			}
			// weighted average
			double[] weights = softMax(acts);
			double[] semb = new double[sensembs[0].length];
			for (int i : xrange(sensembs[0].length)) {
				for (int j : xrange(weights.length)) {
					semb[i] += sensembs[j][i] * weights[j];
				}
			}
			return Optional.of(semb);
		}
		return Optional.absent();
	}
}
