package uk.ac.open.kmi.watson.services.terms;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;

import uk.ac.open.kmi.watson.services.WatsonService;

public class TermUtils extends WatsonService {

	private IndexSearcher mapset;
	
	public TermUtils() {
		try {
			mapset = new	 IndexSearcher("/opt/watson/var/lucene/cmerged-Map-label-Map-locN-0");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String[][] suggestSynonym(String term){
		TermQuery q = new TermQuery(new Term("label", term));
		try {
			Hits result = mapset.search(q);
			if (result.length() == 0) System.out.println("No result... sorry :-(");
			else {
				String[][] results = new String[result.length()][];
				for (int j = 0; j < result.length(); j ++){
					StringTokenizer st = new StringTokenizer(result.doc(j).get("label"));
					String[] toReturn = new String[st.countTokens()]; 
					int i = 0;
					while (st.hasMoreTokens()){
						toReturn[i++] = st.nextToken();
					}
					results[j] = toReturn;
				}
				return results;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new String[0][];
	}
	
	public int countEntitiesForMap(String term){
		TermQuery q = new TermQuery(new Term("label", term));
		try {
			Hits result = mapset.search(q);
			if (result.length() > 1) System.out.println("Warning:: more than one map...");
			else if (result.length() == 0) System.out.println("No result... sorry :-(");
			else {
				StringTokenizer st = new StringTokenizer(result.doc(0).get("ents"));
				return st.countTokens();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/** return an array of <ent-uri>, <ont-uri> **/
	public String[][] getEntitiesInMap(String term){
		TermQuery q = new TermQuery(new Term("label", term));
		try {
			Hits result = mapset.search(q);
			if (result.length() > 1) System.out.println("Warning:: more than one map...");
			else if (result.length() == 0) System.out.println("No result... sorry :-(");
			else {
				StringTokenizer st = new StringTokenizer(result.doc(0).get("ents"));
				String[][] toReturn = new String[st.countTokens()][]; 
				int i = 0;
				while (st.hasMoreTokens()){
					toReturn[i] = new String[2];
					int num = Integer.parseInt(st.nextToken());
					Document d = getEntityIndexSearcher().doc(num);
					toReturn[i][0] = d.get("ns") + d.get("locN");
					toReturn[i][1] = getValidID(d.get("sc"));
					i++;
				}
				return toReturn;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new String[0][];	
	}
	
	public static void main(String[] args){
		TermUtils app = new TermUtils();
		if (args.length!=2) usage();
		String term = args[1];
		if (args[0].contains("s")){
			String[][] result = app.suggestSynonym(term);
			for (String[] s : result) System.out.println(s);
		}
		if (args[0].contains("c")){
			int result = app.countEntitiesForMap(term);
			System.out.println("Associated with "+result+" entities");
		}
		if (args[0].contains("e")){
			String[][] result = app.getEntitiesInMap(term);
			for (String[] s : result) System.out.println(s[0]+" in "+s[1]);
		}
	}

	private static void usage() {
		System.out.println("Usage:: TermUtils <action:[s|c|e]> <term>");
		System.exit(-1);
	}
	
	
}
