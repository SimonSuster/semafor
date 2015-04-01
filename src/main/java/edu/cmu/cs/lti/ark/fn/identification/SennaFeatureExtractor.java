package edu.cmu.cs.lti.ark.fn.identification;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import edu.cmu.cs.lti.ark.fn.data.prep.formats.Sentence;
import edu.cmu.cs.lti.ark.fn.data.prep.formats.Token;
import edu.cmu.cs.lti.ark.util.nlp.parse.DependencyParse;
import gnu.trove.THashSet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static edu.cmu.cs.lti.ark.util.IntRanges.xrange;
import static edu.cmu.cs.lti.ark.util.nlp.parse.DependencyParse.getHeuristicHead;

/**
 * @author sthomson@cs.cmu.edu
 * @author s.suster@rug.nl
 */
public class SennaFeatureExtractor {
	public static final String[] FIVE_WORD_WINDOW_NAMES = {"[-2]", "[-1]", "[0]", "[1]", "[2]"};

	private final boolean useSyntactic;
	// use special wordreps for listed syn. func.
	public static final THashSet<String> ACTIVE_SYN_FUN = getActiveSynFun();
	public static final String DUMMY_SYN_FUN = "OTHER";

	private static final Map<String, String> synFunMap = getSynFunMap();

	private final Senna senna;

	public SennaFeatureExtractor(Senna senna) {
		this(senna, false);
	}

	public SennaFeatureExtractor(Senna senna, boolean useSyntactic) {
	    this.senna = senna;
	    this.useSyntactic = useSyntactic;
	}

	public static SennaFeatureExtractor load(int sennaVectorDim, boolean useSyntactic) throws IOException {
		return new SennaFeatureExtractor(Senna.load(sennaVectorDim), useSyntactic);
	}

	private static THashSet<String> getActiveSynFun() {
		final THashSet<String> activeSynFun = new THashSet<String>();
		activeSynFun.add("NMOD");
		activeSynFun.add("PMOD");
		activeSynFun.add("SBJ");
		//activeSynFun.add("OBJ");
		//activeSynFun.add("ADV");

		return activeSynFun;
	}

	private static Map<String, String> getSynFunMap() {
		/**
		 * A map of syntactic functions for lookup of wordreps.
		 * Only the functions listed in ACTIVE_SYN_FUN will be used.
		 */
		final Map<String, String> synFunMap = Maps.newHashMap();
		{
			final String nmod = "NMOD";
			final String pmod = "PMOD";
			final String sbj = "SBJ";
			final String obj = "OBJ";
			final String adv = "ADV";
			if (ACTIVE_SYN_FUN.contains(nmod)) {
				synFunMap.put("number", nmod);
				synFunMap.put("predet", nmod);
				synFunMap.put("det", nmod);
				synFunMap.put("num", nmod);
				synFunMap.put("nn", nmod);
				synFunMap.put("amod", nmod);
				synFunMap.put("poss", nmod);
			}
			if (ACTIVE_SYN_FUN.contains(pmod)) {
				synFunMap.put("prep", pmod);
				synFunMap.put("pobj", pmod);
			}
			if (ACTIVE_SYN_FUN.contains(sbj)) {
				synFunMap.put("nsubj", sbj);
				synFunMap.put("nsubjpass", sbj);
			}
			if (ACTIVE_SYN_FUN.contains(obj)) {
				synFunMap.put("mark", obj);
				synFunMap.put("complm", obj);
				synFunMap.put("iobj", obj);
				synFunMap.put("dobj", obj);
			}
			if (ACTIVE_SYN_FUN.contains(adv)) {
				synFunMap.put("advmod", adv);
				synFunMap.put("neg", adv);
			}
		}
		return synFunMap;
	}

	public Map<String, Double> getSennaFeatures(int[] targetTokenIdxs, Sentence sentence) {
		final Map<String, Double> features = Maps.newHashMap();

		final DependencyParse parse = DependencyParse.processFN(sentence.toAllLemmaTagsArray(), 0.0);
		final DependencyParse[] nodes = parse.getIndexSortedListOfNodes();
		final int headIdx = getHeuristicHead(nodes, targetTokenIdxs).getIndex() - 1;
		final List<Token> tokens = sentence.getTokens();
		// add senna features for five-word window around target head
		for (int i : xrange(FIVE_WORD_WINDOW_NAMES.length)) {
			final int idx = headIdx - 2 + i;
			if (idx >= 0 && idx < sentence.size()) {
				final String form = tokens.get(idx).getForm();
				final Map<String, Double> sennaFeaturesForWord;
				if (useSyntactic) {
					final String synFun;
					//use rather some default map
					if (synFunMap.get(nodes[idx + 1].getLabelType()) == null) synFun = DUMMY_SYN_FUN;
					else synFun = synFunMap.get(nodes[idx + 1].getLabelType());
					sennaFeaturesForWord = getSennaFeaturesForWord(form + synFun);
				}
				else sennaFeaturesForWord = getSennaFeaturesForWord(form);
				//0
				features.putAll(FrameFeatureExtractor.conjoin(FIVE_WORD_WINDOW_NAMES[i], sennaFeaturesForWord));
			}
		}
		//System.err.println("Features size: ");
		//System.err.println(features.size());
		return features;
	}

	public Map<String, Double> getSennaFeaturesForWord(String word) {
		final Map<String, Double> features = Maps.newHashMap();
		final Optional<double[]> oEmbedding = senna.getEmbedding(word);

		if (oEmbedding.isPresent()) {
		    //		    System.err.println("present");
			final double[] embedding = oEmbedding.get();
			for (int i : xrange(embedding.length))  {
			    features.put(String.format("senna%02d", i), embedding[i]);
			}
		}
		//0
		return features;
	}
}
