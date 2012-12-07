package uk.ac.open.kmi.watson.services;

import java.io.Serializable;
import java.util.Vector;

/**
 * Contains information about entities in the result of a search.
 * @author mda99
 */
public class SemanticContentResult implements Serializable {

	private String URI;
	private EntityResult[] entityResultList;
	private String[] languages;
	private String[] locations;
	private String DLExpressivness;
	private int NBClasses;
	private int NBProperties;
	private int NBIndividuals;
	private long size;
	private int nbStatements;
	private String[] imports;
	private String[] importedBy;
	private String[] labels;
	private String[] comments;
	private String[][] domains;
	
	/** returns the size of the semantic document in KB, or null if this information hasn't been requested. **/
	public long getSize() {
		return size;
	}

	/** set the size of the semantic document. Not usable from the client API. **/
	public void setSize(long size) {
		this.size = size;
	}

	/** build a SemanticContentResult from the URI of a semantic document **/
	public SemanticContentResult(String URI){
		this.URI = URI;
	}
	
	/** returns the size of the semantic document in KB. **/
	public String getURI(){
		return URI;
	}
	
	/** set the URI of the semantic document. Not usable from the client API. **/
	public void setURI(String URI){
		this.URI = URI;
	}
	
	/** returns the list of entities that matched the query in this semantic document. **/
	public EntityResult[] getEntityResultList(){
		return entityResultList;
	}
	
	/** set the list of entities of the semantic document. Not usable from the client API. **/
	public void setEntityResultList(EntityResult[] erl){
		entityResultList = erl;
	}
	
	/** set the list of entities of the semantic document. Not usable from the client API. **/
	public void setEntityResultList(Vector<EntityResult> erl){
		entityResultList = new EntityResult[erl.size()];
		for (int i = 0; i < erl.size(); i++) entityResultList[i] = erl.elementAt(i);
	}

	/** returns the DL Expressivness of the semantic document, or null if this information hasn't been requested. **/
	public String getDLExpressivness() {
		return DLExpressivness;
	}

	/** set the DL Expressivness of the semantic document. Not usable from the client API. **/
	public void setDLExpressivness(String expressivness) {
		DLExpressivness = expressivness;
	}

	/** returns the set of languages used in the semantic document, or null if this information hasn't been requested. **/
	public String[] getLanguages() {
		return languages;
	}

	/** set the languages of the semantic document. Not usable from the client API. **/
	public void setLanguages(String[] languages) {
		this.languages = languages;
	}

	/** returns the set of URLs of the semantic document, or null if this information hasn't been requested. **/
	public String[] getLocations() {
		return locations;
	}

	/** set the URLs of the semantic document. Not usable from the client API. **/
	public void setLocations(String[] locations) {
		this.locations = locations;
	}

	/** returns the number of classes declared in the semantic document, or 0 if this information hasn't been requested. **/
	public int getNBClasses() {
		return NBClasses;
	}
	
	/** set the number of classes of the semantic document. Not usable from the client API. **/
	public void setNBClasses(int classes) {
		NBClasses = classes;
	}

	/** returns the number of individuals declared in the semantic document, or 0 if this information hasn't been requested. **/
	public int getNBIndividuals() {
		return NBIndividuals;
	}

	/** set the number of individuals of the semantic document. Not usable from the client API. **/
	public void setNBIndividuals(int individuals) {
		NBIndividuals = individuals;
	}

	/** returns the number of properties declared in the semantic document, or 0 if this information hasn't been requested. **/
	public int getNBProperties() {
		return NBProperties;
	}

	/** set the number of properties of the semantic document. Not usable from the client API. **/
	public void setNBProperties(int props) {
		NBProperties = props;
	}

	public void setNumberOfStatements(int result) {
		nbStatements = result;
	}

	public void setImports(String[] prs) {
		imports = prs;
	}

	public void setImportedBy(String[] result) {
		importedBy = result;
	}

	public int getNbStatements() {
		return nbStatements;
	}

	public void setNbStatements(int nbStatements) {
		this.nbStatements = nbStatements;
	}

	public String[] getImportedBy() {
		return importedBy;
	}

	public String[] getImports() {
		return imports;
	}

	public void setLabels(String[] toReturn) {
		labels = toReturn;
	}

	public void setComments(String[] toReturn) {
		comments = toReturn;
	}

	public String[] getComments() {
		return comments;
	}

	public String[] getLabels() {
		return labels;
	}

	public String[][] getDomains() {
		// TODO: implements...
		return domains;
	}

	public void setDomains(String[][] domains) {
		this.domains = domains;
	}
	
}
