package de.germeval2017.Evaluation.objectBindings;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Documents")

public class SentimentDocumentSet {
	List<SentimentDocument> docs;

	@XmlElement(name = "Document")
	public List<SentimentDocument> getDocs() {
		return docs;
	}

	public void setDocs(List<SentimentDocument> docs) {
		this.docs = docs;
	}
}
