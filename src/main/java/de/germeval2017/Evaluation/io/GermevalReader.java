package de.germeval2017.Evaluation.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FileUtils;

import de.germeval2017.Evaluation.objectBindings.SentimentAspect;
import de.germeval2017.Evaluation.objectBindings.SentimentAspectSet;
import de.germeval2017.Evaluation.objectBindings.SentimentDocument;
import de.germeval2017.Evaluation.objectBindings.SentimentDocumentSet;

/**
 * reader class for both xml and tsv files
 * @author michael
 *
 */
public class GermevalReader {

	public SentimentDocumentSet read(File file) throws Exception {
		if(file.getName().endsWith("tsv")){
			return readTSV(file);
		}else if(file.getName().endsWith("xml")){
			return readXML(file);
		}
		else{
			throw new Exception(file.getName()+" cannot be read. use xml or tsv format and file endings");
		}
		
	}

	/**
	 * reads xmls using java object bindings (the corresponding objects are in the object bindings package)
	 * @param file
	 * @return SentimentDocumentSet
	 * @throws JAXBException
	 */
	private SentimentDocumentSet readXML(File file) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(SentimentDocumentSet.class);

        Unmarshaller unmarshaller = jc.createUnmarshaller();
        SentimentDocumentSet documentSet = (SentimentDocumentSet) unmarshaller.unmarshal(file);
		return documentSet;
	}

	/**
	 * reads TSV using FileUtils
	 * @param file
	 * @return SentimentDocumentSet
	 * @throws JAXBException
	 */
	private SentimentDocumentSet readTSV(File file) throws IOException {
		SentimentDocumentSet result = new SentimentDocumentSet();
		List<SentimentDocument> docs = new ArrayList<SentimentDocument>();
		result.setDocs(docs);
		int i=0;
		for(String line: FileUtils.readLines(file)){
			if(line.equals("SOURCE"+"\t"+"TEXT"+"\t"+"RELEVANCE"+"\t"+"SENTIMENT"+"\t"+"CATEGORY:SENTIMENT"+"\t")){
				continue;
			}
			SentimentDocument doc= new SentimentDocument();
			SentimentAspectSet aspectSet= new SentimentAspectSet();
			List<SentimentAspect> aspects = new ArrayList<SentimentAspect>();
			aspectSet.setAspects(aspects);
			doc.setAspects(aspectSet);
			String[] lineParts= line.split("\t");
//			System.out.println(line);
			doc=setUpDoc(doc, lineParts[0],lineParts[1],lineParts[2],lineParts[3]);
			if(lineParts.length>4){
				aspects.addAll(getAspects(lineParts[4]));
			}
			docs.add(doc);
			i++;
		}
		return result;
	}

	private Collection<? extends SentimentAspect> getAspects(String aspectString) {
		List<SentimentAspect> result= new ArrayList<SentimentAspect>();
		for(String part: aspectString.split(" ")){
			SentimentAspect aspect= new SentimentAspect();
			aspect.setAspect(part.split(":")[0]);
			if(part.split(":").length > 1){
				aspect.setSentiment(part.split(":")[1]);
			}
			result.add(aspect);
		}
		return result;
	}

	private SentimentDocument setUpDoc(SentimentDocument doc, String source, String text, String relevance,
			String sentiment) {
		doc.setId(source);
		doc.setText(text);
		doc.setRelevance(Boolean.valueOf(relevance));
		doc.setSentiment(sentiment);
		return doc;
	}

}
