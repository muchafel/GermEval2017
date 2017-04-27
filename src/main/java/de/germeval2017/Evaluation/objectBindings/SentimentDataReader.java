package de.germeval2017.Evaluation.objectBindings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class SentimentDataReader {

	public static SentimentDocumentSet read(File input) throws JAXBException, FileNotFoundException {
		JAXBContext context = JAXBContext.newInstance(SentimentDocumentSet.class);
		Unmarshaller um = context.createUnmarshaller();
		SentimentDocumentSet result = (SentimentDocumentSet) um.unmarshal(new FileReader(input));
		return result;
	}

}
