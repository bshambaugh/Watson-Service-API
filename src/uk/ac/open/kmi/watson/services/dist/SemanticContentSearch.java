package uk.ac.open.kmi.watson.services.dist;

import uk.ac.open.kmi.watson.services.SearchConf;
import uk.ac.open.kmi.watson.services.SemanticContentResult;
import uk.ac.open.kmi.watson.services.revyu.RevyuEndpoint;

/**
 * Service for searching and inspecting semantic documents.
 * 
 * @author lg3388, md99
 */
public class SemanticContentSearch extends WatsonService {

	public SemanticContentSearch() {}
	
	public SemanticContentResult[] getSemanticContentByKeywords(String[] keywords, SearchConf conf){
		return null;
	}
	
	public int getNumberOfResults(String[] keywords, SearchConf conf){
		return -1;
	}

	public SemanticContentResult[] getSemanticContentWithBestCoverage(String[] keywords,
			SearchConf conf) {
		return null;
	}

	public String[] listSemanticContents(int start, int stop) {
		return null;
	}

	public String[] getSemanticContentLocation(String ontoURI) {
		return null;
	}

	public String getCacheLocation(String ontoURI) {
		return null;
	}

	public long getSizeInBytes(String ontoURI) {
		return -1;
	}

	public String[] getSemanticContentLanguages(String ontoURI) {
		return null;
	}

	
	public String getDLExpressivness(String ontoURI) {
		return null;
	}

	public String[] listClasses(String ontoURI) {
		return null;
	}

	public String[] listProperties(String ontoURI) {
		return null;
	}

	public String[] listIndividuals(String ontoURI) {
		return null;
	}

	public String[] getImports(String ontoUri) {
		return null;
	}

	public String[] getImportedBy(String ontoUri) {
		return null;
	}

	public long getNumberOfStatement(String ontoUri) {
		return -1;
	}

	public String executeSPARQLQuery(String ontoURI, String queryString) {
		return null;
	}

	private RevyuEndpoint revyuEndpoint = new RevyuEndpoint();

	public int getNumberOfReviews(String ontoURI) {
		return revyuEndpoint.getNumberOfReviews(ontoURI);
	}

	public int getAverageRating(String ontoURI) {
		return revyuEndpoint.getAverageRating(ontoURI);
	}

	public String getRevyuURL(String ontoURI) {
		return "http://revyu.com/" + revyuEndpoint.getRevyuURI(ontoURI);
	}

	public String getOMVFileLocation(String ontoURI) {
		return null;
	}

	public void submitURI(String uri) {
		// cupboard it??
	}
	public String[] getLabels(String ontoURI){
     	return null;	
     }

	public String[] getComments(String ontoURI){
	 	return null;
	}
		
}
