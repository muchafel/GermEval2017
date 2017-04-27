package de.germeval2017.Evaluation.objectBindings;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class SentimentAspectSet {
	private List<SentimentAspect> aspects;

	public SentimentAspectSet() {
	}

	@XmlElement(name = "Opinion")
	public List<SentimentAspect> getAspects() {
		return aspects;
	}

	public void setAspects(List<SentimentAspect> aspects) {
		this.aspects = aspects;
	}
	
}
