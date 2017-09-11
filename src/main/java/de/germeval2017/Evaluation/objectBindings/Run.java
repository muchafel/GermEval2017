package de.germeval2017.Evaluation.objectBindings;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Run {

	private File origin;
	private String team;
	private String timestamp;
	private String runName;
	private String fileName;
	private List<String> subtasks; 
	

	public Run(File file) {
		
		this.origin = file;
		this.fileName=file.getName();
//		System.out.println(fileName);
		this.team = fileName.split("\\.")[0];
		this.timestamp = fileName.split("\\.")[2];
		this.runName = fileName.split("\\.")[3];
		this.subtasks = parseSubtasks(fileName.split("\\.")[1]);
	}


	private List<String> parseSubtasks(String task) {
		List<String> tasks= new ArrayList<>();
		//relevance, sentiment, category or OTE 
		switch (task) {
	         case "A":
	             tasks.add("relevance");
	             break;
	         case "B":
	        	 tasks.add("sentiment");
	        	 break;
	         case "C":
	        	 tasks.add("category");
	        	 break;
	         case "D":
	        	 tasks.add("OTE");
	        	 break;
	         case "ALL":
	        	 tasks.add("relevance");
	        	 tasks.add("sentiment");
	        	 tasks.add("category");
	        	 tasks.add("OTE");
	             break;
	         case "1":
	        	 tasks.add("relevance");
	             break;
	         case "2":
	        	 tasks.add("sentiment");
	        	 break;
	         case "3":
	        	 tasks.add("category");
	        	 break;
	         case "4":
	        	 tasks.add("OTE");
	        	 break;
	         case "task1":
	        	 tasks.add("relevance");
	             break;
	         case "task2":
	        	 tasks.add("sentiment");
	             break;
	         case "task3":
	        	 tasks.add("category");
	             break;
	         case "AB":
	             tasks.add("relevance");
	             tasks.add("sentiment");
	             break;
	         case "CD":
	             tasks.add("category");
	             tasks.add("OTE");
	             break;
	         default:
	             throw new IllegalArgumentException("task: " + task);
	     }
	     return tasks;
	}


	public File getOrigin() {
		return origin;
	}


	public String getTeam() {
		return team;
	}


	public String getTimestamp() {
		return timestamp;
	}


	public String getRunName() {
		return runName;
	}


	public String getFileName() {
		return fileName;
	}


	public List<String> getSubtasks() {
		return subtasks;
	}

}
