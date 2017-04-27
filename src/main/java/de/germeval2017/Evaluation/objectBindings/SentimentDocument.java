package de.germeval2017.Evaluation.objectBindings;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class SentimentDocument {
	private String id;
	private String text;
	private boolean relevance;
	private String sentiment;
	private SentimentAspectSet aspects;
	
	public SentimentDocument(String id, String text, boolean relevance, String sentiment,SentimentAspectSet aspects) {
		this.id = id;
		this.text = text;
		this.relevance = relevance;
		this.sentiment = sentiment;
		this.aspects = aspects;
	}
	
	@XmlAttribute(name = "id")
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@XmlElement(name = "text")
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	@XmlElement(name = "relevance")
	public boolean isRelevance() {
		return relevance;
	}
	public void setRelevance(boolean relevance) {
		this.relevance = relevance;
	}
	
	@XmlElement(name = "sentiment")
	public String getSentiment() {
		return sentiment;
	}
	public void setSentiment(String sentiment) {
		this.sentiment = sentiment;
	}
	@XmlElement(name = "Opinions")
	public SentimentAspectSet getAspects() {
		return aspects;
	}
	public void setAspects(SentimentAspectSet aspects) {
		this.aspects = aspects;
	}

	public SentimentDocument() {
	}
	
}
