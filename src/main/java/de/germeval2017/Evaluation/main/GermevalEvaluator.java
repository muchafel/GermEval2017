package de.germeval2017.Evaluation.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.germeval2017.Evaluation.EvaluationUtil.EvaluationData;
import de.germeval2017.Evaluation.EvaluationUtil.Fscore;
import de.germeval2017.Evaluation.EvaluationUtil.OTEMatcher;
import de.germeval2017.Evaluation.objectBindings.SentimentAspect;
import de.germeval2017.Evaluation.objectBindings.SentimentDocument;
import de.germeval2017.Evaluation.objectBindings.SentimentDocumentSet;

/**
 * class that contains the evaluation logic call evaluate(SentimentDocumentSet
 * predicted, SentimentDocumentSet gold, String toEvaluate) to print an
 * evaluation
 * 
 * @author michael
 *
 */
public class GermevalEvaluator {

	/**
	 * @param predicted
	 *            (data)
	 * @param gold
	 *            (data)
	 * @param toEvaluate
	 *            specifies which attribute has to be evaluated
	 * @throws Exception
	 */
	public EvaluationData evaluate(SentimentDocumentSet predicted, SentimentDocumentSet gold, String toEvaluate)
			throws Exception {

		Map<String, SentimentDocument> id2Document_predicted = getid2DocumentMap(predicted.getDocs());
		Map<String, SentimentDocument> id2Document_gold = getid2DocumentMap(gold.getDocs());

		if (toEvaluate.equals("relevance")) {
			return evaluateRelevance(id2Document_predicted, id2Document_gold);
		} else if (toEvaluate.equals("sentiment")) {
			return evaluateSentiment(id2Document_predicted, id2Document_gold);
		} else if (toEvaluate.equals("category")) {
			return evaluateCategory(id2Document_predicted, id2Document_gold);
		} else if (toEvaluate.equals("OTE")) {
			return evaluateOTE(id2Document_predicted, id2Document_gold);
		} else {
			throw new Exception("use relevance, sentiment, category or OTE as an evaluation option");
		}

	}

	/**
	 * currently only exact matching is returned
	 * @param id2Document_predicted
	 * @param id2Document_gold
	 * @return
	 * @throws Exception
	 */
	private EvaluationData<String> evaluateOTE(Map<String, SentimentDocument> id2Document_predicted,
			Map<String, SentimentDocument> id2Document_gold) throws Exception {
		EvaluationData<String> ote_exact = new EvaluationData<>();
		EvaluationData<String> ote_overlap = new EvaluationData<>();

		OTEMatcher matcherExact = new OTEMatcher(ote_exact);
		OTEMatcher matcherOverlap = new OTEMatcher(ote_overlap);

		for (String id : id2Document_gold.keySet()) {
			SentimentDocument doc_gold = id2Document_gold.get(id);
			if (!id2Document_predicted.containsKey(id)) {
				if (doc_gold.getAspects() != null && doc_gold.getAspects().getAspects() != null) {
					for (SentimentAspect aspect : doc_gold.getAspects().getAspects()) {
						matcherExact.registerOTE_Missmatch(doc_gold.getAspects().getAspects(), "none");
						matcherOverlap.registerOTE_Missmatch(doc_gold.getAspects().getAspects(), "missmatch");
					}
				}
//				throw new Exception("Document " + id + " exists in gold data but not in prediction");
			}
			SentimentDocument doc_predicted = id2Document_predicted.get(id);

			if (doc_gold.getAspects() != null && doc_gold.getAspects().getAspects() != null) {
				if (doc_predicted.getAspects() != null && doc_predicted.getAspects().getAspects() != null) {
					// in case there a re bot predicted and gold cases
					matcherExact.registerExactOTEs(doc_gold.getAspects().getAspects(),
							doc_predicted.getAspects().getAspects());
					matcherOverlap.registerOverlaptOTEs(doc_gold.getAspects().getAspects(),
							doc_predicted.getAspects().getAspects(), doc_gold.getText());
				}
				// in case there are no predicted but gold aspects
				else {
					matcherExact.registerOTE_Missmatch(doc_gold.getAspects().getAspects(), "none");
					matcherOverlap.registerOTE_Missmatch(doc_gold.getAspects().getAspects(), "missmatch");
				}
				// in case there are no gold but predicted aspects
			}else if (doc_predicted.getAspects() != null && doc_predicted.getAspects().getAspects() != null & doc_gold.getAspects() != null && doc_gold.getAspects().getAspects() != null) {
				matcherExact.registerOTE_Missmatch("none", doc_gold.getAspects().getAspects());
				matcherOverlap.registerOTE_Missmatch("missmatch", doc_gold.getAspects().getAspects());
			}
		}
//		System.out.println(new ConfusionMatrix<>(matcherExact.getOte_pairs()));
		System.out.println("Evaluation Results for exact OTE matching (based on " + matcherExact.getOte_pairs().size()
				+ " instances):");
		printEvaluation(matcherExact.getOte_pairs());
		System.out.println("Evaluation Results for OTE with overlapping (based on "
				+ matcherOverlap.getOte_pairs().size() + " instances):");
		printEvaluation(matcherOverlap.getOte_pairs());

		return matcherExact.getOte_pairs();
	}

	/**
	 * currently only category is returned
	 * @param id2Document_predicted
	 * @param id2Document_gold
	 * @return
	 * @throws Exception
	 */
	private EvaluationData<String> evaluateCategory(Map<String, SentimentDocument> id2Document_predicted,
			Map<String, SentimentDocument> id2Document_gold) throws Exception {
		EvaluationData<String> aspectOccurrence = new EvaluationData<>();
		EvaluationData<String> aspectOccurrence_Sentiment = new EvaluationData<>();

		for (String id : id2Document_gold.keySet()) {
			SentimentDocument doc_gold = id2Document_gold.get(id);
			if (!id2Document_predicted.containsKey(id)) {
				if (doc_gold.getAspects() != null && doc_gold.getAspects().getAspects() != null) {
					for(SentimentAspect aspect:doc_gold.getAspects().getAspects()){
						aspectOccurrence = registerAspectOccurences(aspectOccurrence, doc_gold.getAspects().getAspects(),
								"none");
						aspectOccurrence_Sentiment = registerAspectSentimentOccurences(aspectOccurrence_Sentiment,
								doc_gold.getAspects().getAspects(), "none");
					}
				}
//				throw new Exception("Document " + id + " exists in gold data but not in prediction");
			}else{
				SentimentDocument doc_predicted = id2Document_predicted.get(id);
				if (doc_gold.getAspects() != null && doc_gold.getAspects().getAspects() != null) {
					if (doc_predicted.getAspects() != null && doc_predicted.getAspects().getAspects() != null) {
						aspectOccurrence = registerAspectOccurences(aspectOccurrence, doc_gold.getAspects().getAspects(),
								doc_predicted.getAspects().getAspects());
						aspectOccurrence_Sentiment = registerAspectSentimentOccurences(aspectOccurrence_Sentiment,
								doc_gold.getAspects().getAspects(), doc_predicted.getAspects().getAspects());
					} else {
						aspectOccurrence = registerAspectOccurences(aspectOccurrence, doc_gold.getAspects().getAspects(),
								"none");
						aspectOccurrence_Sentiment = registerAspectSentimentOccurences(aspectOccurrence_Sentiment,
								doc_gold.getAspects().getAspects(), "none");
					}
				}
			}
		}

		System.out.println("Evaluation Results for Categories (based on " + aspectOccurrence.size() + " instances):");
		printEvaluation(aspectOccurrence);

		System.out.println("Evaluation Results for Categories + Sentiment (based on "
				+ aspectOccurrence_Sentiment.size() + " instances):");
		printEvaluation(aspectOccurrence_Sentiment);
		return aspectOccurrence;
		//return aspectaspectOccurrence_Sentiment
	}

	private EvaluationData<String> registerAspectSentimentOccurences(EvaluationData<String> spectSentimentOccurrence,
			List<SentimentAspect> aspects, String string) {
		Set<String> aspectSet_gold = getAspectSentimentSet(aspects);
		for (String aspect : aspectSet_gold) {
			spectSentimentOccurrence.register(aspect, "none_sentiment");
		}
		return spectSentimentOccurrence;
	}

	private EvaluationData<String> registerAspectOccurences(EvaluationData<String> aspectOccurrence,
			List<SentimentAspect> aspects, String string) {
		Set<String> aspectSet_gold = getAspectSet(aspects);

		for (String aspect : aspectSet_gold) {
			aspectOccurrence.register(aspect, "none");
		}
		return aspectOccurrence;
	}

	private EvaluationData<String> registerAspectSentimentOccurences(EvaluationData<String> aspectSentimentOccurrence,
			List<SentimentAspect> aspects_gold, List<SentimentAspect> aspects_predicted) {
		Set<String> aspectSet_gold = getAspectSentimentSet(aspects_gold);
		Set<String> aspectSet_predicted = getAspectSentimentSet(aspects_predicted);
//		System.out.println(aspectSet_gold);
		for (String aspect : aspectSet_gold) {
			if (aspectSet_predicted.contains(aspect)) {
				aspectSentimentOccurrence.register(aspect, aspect);
			} else {
				aspectSentimentOccurrence.register(aspect, "none_sentiment");
			}
		}
		// Register false positives
		for (String aspect : aspectSet_predicted) {
			if (!aspectSet_gold.contains(aspect)) {
				aspectSentimentOccurrence.register("none_sentiment", aspect);
			}
		}
		return aspectSentimentOccurrence;
	}

	private Set<String> getAspectSentimentSet(List<SentimentAspect> aspects) {
		Set<String> result = new HashSet<>();
		for (SentimentAspect aspect : aspects) {
			result.add(aspect.getAspect().split("#")[0] + "_" + aspect.getSentiment());
		}
		return result;
	}

	private EvaluationData<String> registerAspectOccurences(EvaluationData<String> aspectOccurrence,
			List<SentimentAspect> aspects_gold, List<SentimentAspect> aspects_predicted) {
		List<SentimentAspect> processedPredictedAspects = new ArrayList<>();

		Set<String> aspectSet_gold = getAspectSet(aspects_gold);
		Set<String> aspectSet_predicted = getAspectSet(aspects_predicted);

		for (String aspect : aspectSet_gold) {
			if (aspectSet_predicted.contains(aspect)) {
				aspectOccurrence.register(aspect, aspect);
			} else {
				aspectOccurrence.register(aspect, "none");
			}
		}
		// Register false positives
		for (String aspect : aspectSet_predicted) {
			if (!aspectSet_gold.contains(aspect)) {
				aspectOccurrence.register("none", aspect);
			}
		}
		return aspectOccurrence;
	}

	private Set<String> getAspectSet(List<SentimentAspect> aspects) {
		Set<String> result = new HashSet<>();
		for (SentimentAspect aspect : aspects) {
			result.add(aspect.getAspect().split("#")[0]);
		}
		return result;
	}

	private void printEvaluation(EvaluationData<String> evalData) {
		Fscore<String> fscore = new Fscore<>(evalData);
		System.out.println("MICRO_F1 " + fscore.getMicroFscore());
	}

	private EvaluationData<String> evaluateSentiment(Map<String, SentimentDocument> id2Document_predicted,
			Map<String, SentimentDocument> id2Document_gold) throws Exception {
		EvaluationData<String> evaluationData = new EvaluationData<>();

		for (String id : id2Document_gold.keySet()) {
			SentimentDocument doc_gold = id2Document_gold.get(id);
			if (!id2Document_predicted.containsKey(id)) {
				evaluationData.register(doc_gold.getSentiment(), "nothing");
			}else{
				SentimentDocument doc_predicted = id2Document_predicted.get(id);
				evaluationData.register(doc_gold.getSentiment(), doc_predicted.getSentiment());
			}
		}

		System.out.println("Evaluation Results for Sentiment (based on " + evaluationData.size() + " instances):");
		printEvaluation(evaluationData);
		return evaluationData;
	}

	private EvaluationData evaluateRelevance(Map<String, SentimentDocument> id2Document_predicted,
			Map<String, SentimentDocument> id2Document_gold) throws Exception {
		EvaluationData<String> evaluationData = new EvaluationData<>();

		for (String id : id2Document_gold.keySet()) {
			SentimentDocument doc_gold = id2Document_gold.get(id);
			if (!id2Document_predicted.containsKey(id)) {
				evaluationData.register(String.valueOf(doc_gold.isRelevance()), "not there");
//				throw new Exception("Document " + id + " exists in gold data but not in prediction");
			}else{
				SentimentDocument doc_predicted = id2Document_predicted.get(id);
				evaluationData.register(String.valueOf(doc_gold.isRelevance()), String.valueOf(doc_predicted.isRelevance()));
			}
		}

		System.out.println("Evaluation Results for Relevance (based on " + evaluationData.size() + " instances):");
		Fscore<String> fscore = new Fscore<>(evaluationData);
		System.out.println("MICRO_F1 " + fscore.getMicroFscore());

		return evaluationData;
	}

	private Map<String, SentimentDocument> getid2DocumentMap(List<SentimentDocument> docs) {
		Map<String, SentimentDocument> result = new HashMap<>();
		for (SentimentDocument doc : docs) {
			result.put(doc.getId(), doc);
		}
		return result;
	}

}
