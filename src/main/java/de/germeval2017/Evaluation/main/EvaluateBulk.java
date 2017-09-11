package de.germeval2017.Evaluation.main;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.SynchronousQueue;

import de.germeval2017.Evaluation.EvaluationUtil.EvaluationData;
import de.germeval2017.Evaluation.EvaluationUtil.Fscore;
import de.germeval2017.Evaluation.io.GermevalReader;
import de.germeval2017.Evaluation.objectBindings.Run;
import de.germeval2017.Evaluation.objectBindings.SentimentDocumentSet;

public class EvaluateBulk {
	public static void main(String[] args) throws Exception {
		
		ArrayList<String> tasks = new ArrayList<String>( Arrays.asList(
				"relevance", 
				"sentiment",
				"category",
				"OTE"
				));
		
		for(String task : tasks){
			System.out.println("+++ TIMESTAMP 1 +++");
			File time1 = new File("/Users/michael/Desktop/germeval results/timestamp1");
			evaluateTaskOnTimeStamp(time1,task);
			
			System.out.println("+++ TIMESTAMP 2 +++");
			File time2 = new File("/Users/michael/Desktop/germeval results/timestamp2");
			evaluateTaskOnTimeStamp(time2,task);
		}
		
	}

	private static void evaluateTaskOnTimeStamp(File time1, String task) throws Exception {
		System.out.println("+++ "+task+" +++");
		List<Run> runs= new ArrayList<Run>();
		for(File file: time1.listFiles()){
			if(file.getName().startsWith(".")) continue;
			Run run= new  Run(file);
			runs.add(run);
			if(run.getSubtasks().contains(task)){
//				System.out.println(run.getSubtasks());
//				System.out.println("\t"+run.getTeam()+"_"+run.getRunName()+ "\t"+run.getFileName()); 
				System.out.println(run.getTeam()+"\t"+run.getRunName()+"\t"+evaluate(run,time1,task).getMicroFscore()); 
			}
		}
		
	}

	private static Fscore<String> evaluate(Run run, File time1, String task) throws Exception {

		GermevalReader reader= new GermevalReader();
		GermevalEvaluator evaluator= new GermevalEvaluator();
		SentimentDocumentSet predicted =reader.read(run.getOrigin());
		SentimentDocumentSet gold =reader.read(getTestFile(time1));
		
		if(predicted.getDocs().size()!= gold.getDocs().size()){
			throw new Exception("Unequal number of instances. Predicted= "+predicted.getDocs().size()+" - Gold="+gold.getDocs().size());
		}
		
		EvaluationData<String> evaluationData= evaluator.evaluate(predicted,gold,task);
		Fscore<String> fscore = new Fscore<>(evaluationData);
		return fscore;
	}

	private static File getTestFile(File time) throws Exception {
		if(time.getName().equals("timestamp1")){
			return new File("/Users/michael/Desktop/germeval results/test_TIMESTAMP1.xml");
		}else if(time.getName().equals("timestamp2")){
			return new File("/Users/michael/Desktop/germeval results/test_TIMESTAMP2.xml");
		}
		throw new Exception("unknown time");

	}
}
