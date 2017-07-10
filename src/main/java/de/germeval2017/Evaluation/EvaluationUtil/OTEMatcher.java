package de.germeval2017.Evaluation.EvaluationUtil;

import java.util.ArrayList;
import java.util.List;

import de.germeval2017.Evaluation.objectBindings.SentimentAspect;

public class OTEMatcher {

	private EvaluationData<String> ote_pairs;
	
	public OTEMatcher(EvaluationData<String> ote_pairs) {
		this.ote_pairs=ote_pairs;
	}

	

	/**
	 * foreach doc foreach aspects in gold and predicted adds pairs to the EvaluationData obkect
	 * counts only exact matches (see getExactMatch())
	 * @param aspects_gold
	 * @param aspects_predicted
	 */
	public void registerExactOTEs(List<SentimentAspect> aspects_gold, List<SentimentAspect> aspects_predicted) {
		List<SentimentAspect> processedPredictedAspects = new ArrayList<>();
		for (SentimentAspect aspect : aspects_gold) {
			SentimentAspect match = getExactMatch(aspect, aspects_predicted,processedPredictedAspects);
			if (match != null) {
				ote_pairs.register(aspect.getAspect(), aspect.getAspect());
				processedPredictedAspects.add(match);
			} else {
				ote_pairs.register(aspect.getAspect(), "missmatch");
			}
		}
		for (SentimentAspect aspect : getRemainingList(aspects_predicted, processedPredictedAspects)) {
			ote_pairs.register("missmatch", aspect.getAspect());
		}
	}

	/**
	 * returns an predicted aspect if it has:
	 * a) the same begin and end (thus OTE)
	 * b) the same category label
	 * c) is not already matched to another aspect
	 * @param aspect
	 * @param aspects_predicted
	 * @param processedPredictedAspects
	 * @return
	 */
	private SentimentAspect getExactMatch(SentimentAspect aspect, List<SentimentAspect> aspects_predicted, List<SentimentAspect> processedPredictedAspects) {
		for (SentimentAspect aspect_predcited : aspects_predicted) {
			if (aspect.getEnd() == aspect_predcited.getEnd() && aspect.getBegin() == aspect_predcited.getBegin()&& aspect.getAspect().equals(aspect_predcited.getAspect()) && !processedPredictedAspects.contains(aspect_predcited) ) {
				return aspect_predcited;
			}
		}
		return null;
	}



	/**
	 * returns a copy of a list of aspect minus another list of aspects
	 * @param aspects_predicted
	 * @param processedPredictedAspects
	 * @return
	 */
	private List<SentimentAspect> getRemainingList(List<SentimentAspect> aspects_predicted,
			List<SentimentAspect> processedPredictedAspects) {
		List<SentimentAspect> copy = new ArrayList<SentimentAspect>(aspects_predicted);
		copy.removeAll(processedPredictedAspects);
		return copy;
	}


	public void registerOverlaptOTEs(List<SentimentAspect> aspects_gold, List<SentimentAspect> aspects_predicted, String docText) {
		List<SentimentAspect> processedPredictedAspects = new ArrayList<>();
		for (SentimentAspect aspect : aspects_gold) {
			SentimentAspect matching = overlapMatch(aspect, aspects_predicted, docText,processedPredictedAspects);
			if (matching != null) {
				ote_pairs.register(aspect.getAspect(), aspect.getAspect());
				processedPredictedAspects.add(matching);
			} else {
				ote_pairs.register(aspect.getAspect(), "none");
			}
		}
		for (SentimentAspect aspect : getRemainingList(aspects_predicted, processedPredictedAspects)) {
			ote_pairs.register("none", aspect.getAspect());
		}

	}
	
	private SentimentAspect overlapMatch(SentimentAspect aspect, List<SentimentAspect> aspects_predicted,
			String docText, List<SentimentAspect> processedPredictedAspects) {
		
		//calculate the ranges for the lower and upper bound
		int lowerBound_min=getMin(aspect.getBegin(), docText);
		int lowerBound_max=getMax(aspect.getBegin(), docText);
		int upperBound_min = getMin(aspect.getEnd(), docText);
		int upperBound_max = getMax(aspect.getEnd(), docText);
		
		for (SentimentAspect aspect_predcited : aspects_predicted) {
			if (betweenBounds(aspect_predcited, aspect,lowerBound_min,lowerBound_max,upperBound_min,upperBound_max)
					&& aspect.getAspect().equals(aspect_predcited.getAspect())
					&&! processedPredictedAspects.contains(aspect_predcited)) {
				return aspect_predcited;
			}
		}
		return null;
	}
	
	//return true if the begin of the prediction is in the +/+ 1 token range of gold.end() and the end of the prediction is in the +/- token range of gold.end()
	private boolean betweenBounds(SentimentAspect predcited, SentimentAspect gold, int lowerBound_min,int lowerBound_max, int upperBound_min, int upperBound_max) {
		if(predcited.getBegin() >= gold.getBegin()-lowerBound_min
				&& predcited.getBegin() <= gold.getBegin()+lowerBound_max
				&& predcited.getEnd() >= gold.getEnd()-upperBound_min
				&& predcited.getEnd() <= gold.getEnd()+upperBound_max){
			return true;
		}
		return false;
	}


	/**
	 * add+1 for whitespace
	 * 
	 * @param end
	 * @param docText
	 * @return
	 */
	private int getMax(int end, String docText) {
		String upperText = docText.substring(end, docText.length());
		upperText = upperText.trim();

		if (upperText.contains(" ")) {
			String[] tokens = upperText.split(" ");
			String nextToken = tokens[0];
			return nextToken.length() + 1;
		}
		return docText.length();
	}

	private int getMin(int begin, String docText) {
		String lowerText = docText.substring(0, begin);

		if (lowerText.contains(" ")) {
			String[] tokens = lowerText.split(" ");
			String preceddingToken = tokens[tokens.length - 1];
			return preceddingToken.length() + 1;
		}
		return 0;
	}

	public void registerOTE_Missmatch(String notThere, List<SentimentAspect> aspects) {
		for (SentimentAspect aspect : aspects) {
			ote_pairs.register(notThere,aspect.getAspect());
		}
	}
	
	public void registerOTE_Missmatch(List<SentimentAspect> aspects,
			String notThere) {
		for (SentimentAspect aspect : aspects) {
			ote_pairs.register(aspect.getAspect(), notThere);
		}
	}



	public EvaluationData<String> getOte_pairs() {
		return ote_pairs;
	}



	public void setOte_pairs(EvaluationData<String> ote_pairs) {
		this.ote_pairs = ote_pairs;
	}
	
}
