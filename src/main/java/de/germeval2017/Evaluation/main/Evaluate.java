package de.germeval2017.Evaluation.main;

import java.io.File;

import de.germeval2017.Evaluation.io.GermevalReader;
import de.germeval2017.Evaluation.objectBindings.SentimentDocumentSet;

/**
 * entry point for the evaluation, handles cmd line args, reads the data and configures evaluation
 * @author michael
 *
 */
public class Evaluate {

	public static void main(String[] args) throws Exception {
		
		File predictionFile = null;
		File goldFile = null;
		String toEvaluate = null;
		if(args.length!=3){
			System.out.println(args.length);
			throw new Exception("Please use the right number of args. 1. what you want to evaluate (relevance, sentiment, category or OTE) 2. prediction file 3. gold file");
		}
		
		if(args[0] != null && (args[0].equals("OTE")|| args[0].equals("relevance")|| args[0].equals("sentiment")|| args[0].equals("category"))){
			toEvaluate=args[0];
		}else{
			throw new Exception("Use the right args, 1. what you want to evaluate (relevance, sentiment, category or OTE) 2. prediction file 3. gold file");
		}
		
		if(args[1] != null){
			predictionFile=new File(args[1]);	
			System.out.println("prediction from "+predictionFile);
		}else{
			throw new Exception("Use the right args, 1. what you want to evaluate (relevance, sentiment, category or OTE) 2. prediction file 3. gold file");
		}
		
		if(args[2] != null){
			goldFile=new File(args[2]);	
			System.out.println("gold from "+goldFile);
		}else{
			throw new Exception("Use the right args, 1. what you want to evaluate (relevance, sentiment, category or OTE) 2. prediction file 3. gold file");
		}
		
		if((goldFile.getName().endsWith("tsv")||predictionFile.getName().endsWith("tsv"))&&toEvaluate.equals("OTE")){
			throw new Exception("If you want to evaluate OTEs please rely on xml for both files");
		}
		
		GermevalReader reader= new GermevalReader();
		GermevalEvaluator evaluator= new GermevalEvaluator();
		SentimentDocumentSet predicted =reader.read(predictionFile);
		SentimentDocumentSet gold =reader.read(goldFile);
		
		if(predicted.getDocs().size()!= gold.getDocs().size()){
			throw new Exception("Unequal number of instances. Predicted= "+predicted.getDocs().size()+" - Gold="+gold.getDocs().size());
		}
		System.out.println();
		
		evaluator.evaluate(predicted,gold,toEvaluate);
		
	}

}
