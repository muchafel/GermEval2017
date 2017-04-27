package de.germeval2017.Evaluation.objectBindings;

import javax.xml.bind.annotation.XmlAttribute;

public class SentimentAspect {
	
	private int begin;
	private int end;
	private String aspect;
	private String ote;
	private String sentiment;
	
	public SentimentAspect(int begin, int end, String aspect, String sentiment) {
		this.begin = begin;
		this.end = end;
		this.aspect = aspect;
		this.sentiment = sentiment;
	}
	
	@XmlAttribute(name = "from")
	public int getBegin() {
		return begin;
	}
	
	public void setBegin(int begin) {
		this.begin = begin;
	}
	
	@XmlAttribute(name = "to")
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	
	@XmlAttribute(name = "category")
	public String getAspect() {
		return aspect;
	}
	public void setAspect(String aspect) {
		this.aspect = aspect;
	}
	
	@XmlAttribute(name = "polarity")
	public String getSentiment() {
		return sentiment;
	}
	public void setSentiment(String sentiment) {
		this.sentiment = sentiment;
	}
	public SentimentAspect() {
	}

	@XmlAttribute(name = "target")
	public String getOte() {
		return ote;
	}

	public void setOte(String ote) {
		this.ote = ote;
	}
}
