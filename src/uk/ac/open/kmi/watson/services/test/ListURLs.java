package uk.ac.open.kmi.watson.services.test;

import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.Searcher;

import uk.ac.open.kmi.watson.services.WatsonService;

public class ListURLs extends WatsonService {

	public void listURLs(){
		String[] result = null;
		try {
			Searcher ir = getDocumentIndexSearcher();
			for (int i = 0; i < ir.maxDoc(); i++) {
				Document d = ir.doc(i);
				String urls = d.get("provs");
				StringTokenizer t = new StringTokenizer(urls);
				while (t.hasMoreTokens()) System.out.println(t.nextToken());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public static void main(String[] args){
		ListURLs lu = new ListURLs();
		lu.listURLs();
	}
	
	
}
