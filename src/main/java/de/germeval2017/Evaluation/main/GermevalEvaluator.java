package de.germeval2017.Evaluation.main;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.germeval2017.Evaluation.EvaluationUtil.EvaluationData;
import de.germeval2017.Evaluation.EvaluationUtil.Fscore;
import de.germeval2017.Evaluation.objectBindings.SentimentAspect;
import de.germeval2017.Evaluation.objectBindings.SentimentDocument;
import de.germeval2017.Evaluation.objectBindings.SentimentDocumentSet;

/**
 * class that contains the evaluation logic
 * call evaluate(SentimentDocumentSet predicted, SentimentDocumentSet gold, String toEvaluate) to print an evaluation
 * @author michael
 *
 */
public class GermevalEvaluator {

	/**
	 * @param predicted (data)
	 * @param gold (data)
	 * @param toEvaluate  specifies which attribute has to be evaluated
	 * @throws Exception
	 */
	public void evaluate(SentimentDocumentSet predicted, SentimentDocumentSet gold, String toEvaluate) throws Exception {
		
		Map<String,SentimentDocument> id2Document_predicted= getid2DocumentMap(predicted.getDocs());
		Map<String,SentimentDocument> id2Document_gold= getid2DocumentMap(gold.getDocs());
		
		if(toEvaluate.equals("relevance")){
			evaluateRelevance(id2Document_predicted,id2Document_gold);
		}else if(toEvaluate.equals("sentiment")){
			evaluateSentiment(id2Document_predicted,id2Document_gold);
		}else if(toEvaluate.equals("category")){
			evaluateCategory(id2Document_predicted,id2Document_gold);
		}else if(toEvaluate.equals("OTE")){
			evaluateOTE(id2Document_predicted,id2Document_gold);
		}else{
			throw new Exception("use relevance, sentiment, category or OTE as an evaluation option");
		}
		
	}

	private void evaluateOTE(Map<String, SentimentDocument> id2Document_predicted,
			Map<String, SentimentDocument> id2Document_gold) throws Exception {
		EvaluationData<String> ote_exact= new EvaluationData<>();
		EvaluationData<String> ote_overlap= new EvaluationData<>();
		
		for(String id: id2Document_gold.keySet()){
			SentimentDocument doc_gold=id2Document_gold.get(id);
			if(!id2Document_predicted.containsKey(id)){
				throw new Exception("Document "+id+" exists in gold data but not in prediction");
			}
			SentimentDocument doc_predicted=id2Document_predicted.get(id);
			if(doc_gold.getAspects() != null && doc_gold.getAspects().getAspects()!= null){
				ote_exact=registerExactOTEs(ote_exact, doc_gold.getAspects().getAspects(),doc_predicted.getAspects().getAspects());
				ote_overlap=registerOverlaptOTEs(ote_overlap, doc_gold.getAspects().getAspects(),doc_predicted.getAspects().getAspects(),doc_gold.getText());
			}
		}
		
		System.out.println("Evaluation Results for exact OTE matching (based on "+ote_exact.size()+" instances):");
		printEvaluation(ote_exact);
		System.out.println("Evaluation Results for OTE with overlapping (based on "+ote_exact.size()+" instances):");
		printEvaluation(ote_overlap);
		
	}

	private EvaluationData<String> registerOverlaptOTEs(EvaluationData<String> ote_overlap, List<SentimentAspect> aspects_gold,
			List<SentimentAspect> aspects_predicted, String docText) {
		for(SentimentAspect aspect: aspects_gold){
			if(overlapMatch(aspect,aspects_predicted,docText)){
				ote_overlap.register(aspect.getAspect(), aspect.getAspect());
			}else{
				ote_overlap.register(aspect.getAspect(), "none");
			}
		}
		return ote_overlap;
	}

	private boolean overlapMatch(SentimentAspect aspect, List<SentimentAspect> aspects_predicted, String docText) {
		int lowerBound= getLowerBound(aspect.getBegin(),docText);
		int upperBound= getUpperBound(aspect.getEnd(),docText);
		for(SentimentAspect aspect_predcited: aspects_predicted){
			if(betweenBounds(aspect_predcited,aspect.getBegin()-lowerBound,aspect.getEnd()+upperBound)){
				return true;
			}
		}
		return false;
	}

	private boolean betweenBounds(SentimentAspect aspect_predcited, int lowerBound, int upperBound) {
		if(aspect_predcited.getBegin()>=lowerBound && aspect_predcited.getEnd()<=upperBound){
			return true;
		}
		return false;
	}

	/**
	 * add+1 for whitespace
	 * @param end
	 * @param docText
	 * @return
	 */
	private int getUpperBound(int end, String docText) {
		String upperText=docText.substring(end, docText.length());
		upperText=upperText.trim();
		
		if(upperText.contains(" ")){
			String[] tokens = upperText.split(" ");
			String nextToken= tokens[0];
			return nextToken.length()+1;
		}
		return docText.length();
	}

	private int getLowerBound(int begin, String docText) {
		String lowerText=docText.substring(0, begin);
		
		if(lowerText.contains(" ")){
			String[] tokens = lowerText.split(" ");
			String preceddingToken= tokens[tokens.length-1];
			return preceddingToken.length()+1;
		}
		return 0;
	}

	private EvaluationData<String> registerExactOTEs(EvaluationData<String> ote_exact, List<SentimentAspect> aspects_gold,
			List<SentimentAspect> aspects_predicted) {
		for(SentimentAspect aspect: aspects_gold){
			if(containsExactMatch(aspect,aspects_predicted)){
				ote_exact.register(aspect.getAspect(), aspect.getAspect());
			}else{
				ote_exact.register(aspect.getAspect(), "missmatch");
			}
		}
		return ote_exact;
	}

	private boolean containsExactMatch(SentimentAspect aspect, List<SentimentAspect> aspects_predicted) {
		for(SentimentAspect aspect_predcited: aspects_predicted){
			if(aspect.getEnd()==aspect_predcited.getEnd() && aspect.getBegin()==aspect_predcited.getBegin()){
				return true;
			}
		}
		return false;
	}

	private void evaluateCategory(Map<String, SentimentDocument> id2Document_predicted,
			Map<String, SentimentDocument> id2Document_gold) throws Exception {
		EvaluationData<String> aspectOccurrence= new EvaluationData<>();
		EvaluationData<String> aspectOccurrence_Sentiment= new EvaluationData<>();
		
		
		for(String id: id2Document_gold.keySet()){
			SentimentDocument doc_gold=id2Document_gold.get(id);
			if(!id2Document_predicted.containsKey(id)){
				throw new Exception("Document "+id+" exists in gold data but not in prediction");
			}
			SentimentDocument doc_predicted=id2Document_predicted.get(id);
			if(doc_gold.getAspects() != null && doc_gold.getAspects().getAspects()!= null){
				aspectOccurrence=registerAspectOccurences(aspectOccurrence, doc_gold.getAspects().getAspects(),doc_predicted.getAspects().getAspects());
				aspectOccurrence_Sentiment=registerAspectSentimentOccurences(aspectOccurrence_Sentiment, doc_gold.getAspects().getAspects(),doc_predicted.getAspects().getAspects());
			}
		}
		
		System.out.println("Evaluation Results for Categories (based on "+aspectOccurrence.size()+" instances):");
		printEvaluation(aspectOccurrence);
		
		System.out.println("Evaluation Results for Categories + Sentiment (based on "+aspectOccurrence_Sentiment.size()+" instances):");
		printEvaluation(aspectOccurrence_Sentiment);
		
		
	}

	private EvaluationData<String> registerAspectSentimentOccurences(EvaluationData<String> aspectSentimentOccurrence,
			List<SentimentAspect> aspects_gold, List<SentimentAspect> aspects_predicted) {
		Set<String> aspectSet_gold=getAspectSentimentSet(aspects_gold);
		Set<String> aspectSet_predicted=getAspectSentimentSet(aspects_predicted);
//		System.out.println(aspectSet_gold);
//		System.out.println(aspectSet_predicted);
		for(String aspect: aspectSet_gold){
			if(aspectSet_predicted.contains(aspect)){
				aspectSentimentOccurrence.register(aspect, aspect);
			}else{
				System.out.println("none_sentiment");
				aspectSentimentOccurrence.register(aspect,"none_sentiment");
			}
		}
		return aspectSentimentOccurrence;
	}

	private Set<String> getAspectSentimentSet(List<SentimentAspect> aspects) {
		Set<String> result= new HashSet<>();
		for(SentimentAspect aspect: aspects){
			result.add(aspect.getAspect()+"_"+aspect.getSentiment());
		}
		return result;
	}

	private EvaluationData<String> registerAspectOccurences(EvaluationData<String> aspectOccurrence,
			List<SentimentAspect> aspects_gold, List<SentimentAspect> aspects_predicted) {
		Set<String> aspectSet_gold=getAspectSet(aspects_gold);
		Set<String> aspectSet_predicted=getAspectSet(aspects_predicted);

		for(String aspect: aspectSet_gold){
			if(aspectSet_predicted.contains(aspect)){
				aspectOccurrence.register(aspect, aspect);
			}else{
				System.out.println("wrong");
				aspectOccurrence.register(aspect,"none");
			}
		}
		return aspectOccurrence;
	}

	private Set<String> getAspectSet(List<SentimentAspect> aspects) {
		Set<String> result= new HashSet<>();
		for(SentimentAspect aspect: aspects){
			result.add(aspect.getAspect().split("#")[0]);
		}
		return result;
	}

	private void printEvaluation(EvaluationData<String> evalData) {
		Fscore<String> fscore= new Fscore<>(evalData);
		System.out.println("MICRO_F1 "+fscore.getMicroFscore());
	}

	private void evaluateSentiment(Map<String, SentimentDocument> id2Document_predicted,
			Map<String, SentimentDocument> id2Document_gold) throws Exception {
			EvaluationData<String> evaluationData= new EvaluationData<>();
		
		
		for(String id: id2Document_gold.keySet()){
			SentimentDocument doc_gold=id2Document_gold.get(id);
			if(!id2Document_predicted.containsKey(id)){
				throw new Exception("Document "+id+" exists in gold data but not in prediction");
			}
			SentimentDocument doc_predicted=id2Document_predicted.get(id);
			evaluationData.register(doc_gold.getSentiment(), doc_predicted.getSentiment());
		}
		
		System.out.println("Evaluation Results for Sentiment (based on "+evaluationData.size()+" instances):");
		printEvaluation(evaluationData);
		
	}

	private EvaluationData evaluateRelevance(Map<String, SentimentDocument> id2Document_predicted,
			Map<String, SentimentDocument> id2Document_gold) throws Exception {
		EvaluationData<Boolean> evaluationData= new EvaluationData<>();
		
		
		for(String id: id2Document_gold.keySet()){
			SentimentDocument doc_gold=id2Document_gold.get(id);
			if(!id2Document_predicted.containsKey(id)){
				throw new Exception("Document "+id+" exists in gold data but not in prediction");
			}
			SentimentDocument doc_predicted=id2Document_predicted.get(id);
			evaluationData.register(doc_gold.isRelevance(), doc_predicted.isRelevance());
		}
		
		System.out.println("Evaluation Results for Relevance (based on "+evaluationData.size()+" instances):");
		Fscore<Boolean> fscore= new Fscore<>(evaluationData);
		System.out.println("MICRO_F1 "+fscore.getMicroFscore());
		
		return evaluationData;
	}

	private Map<String, SentimentDocument> getid2DocumentMap(List<SentimentDocument> docs) {
		Map<String,SentimentDocument> result= new HashMap<>();
		for(SentimentDocument doc: docs){
			result.put(doc.getId(), doc);
		}
		return result;
	}


}
