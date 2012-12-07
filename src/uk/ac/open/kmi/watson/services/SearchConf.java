package uk.ac.open.kmi.watson.services;

import java.io.Serializable;

/**
 * This class is used to create configuration objects used to parametrize search functions.
 * @author mda99
 */
public class SearchConf implements Serializable {

	private int match = 1;
	private int scope = 255;
	private int entities = 255;
	private int entitiesInfo = 255;
	private int SCInfo = 255;
	
	private String[][] filters = new String[0][];	
	private int[]   sorts = new int[0];
	
	private int[] rankingWeights = new int[0];
	
	private int start = 0;
	private int inc = -1;

	/** returns the matching entities parameter **/
	public int getEntities() {
		return entities;
	}

	/** set the entities to be matched
	 * @param entities a number indicating which entities should be march (default CLASS+PROPERTY+INDIVIDUAL)
	 */
	public void setEntities(int entities) {
		this.entities = entities;
	}

	/** get the element that will be returned in the entity result **/
	public int getEntitiesInfo() {
		return entitiesInfo;
	}

	/** set the element that will be returned in the entity result **/
	public void setEntitiesInfo(int entitiesInfo) {
		this.entitiesInfo = entitiesInfo;
	}

	/** get the filters applied **/
	public String[][] getFilters() {
		return filters;
	}

	/** set the filters to apply
	 * @param filters [0] filter name (e.g. language, user) [1] filter value (e.g. OWL)
	 */
	public void setFilters(String[][] filters) {
		this.filters = filters;
	}

	/** get the matching function **/
	public int getMatch() {
		return match;
	}

	/** set the matching function (EXACT_MATCH or TOKEN_MATCH) **/
	public void setMatch(int match) {
		this.match = match;
	}

	/** undocumented **/
	public int[] getRankingWeights() {
		return rankingWeights;
	}

	/** undocumented **/
	public void setRankingWeights(int[] rankingWeights) {
		this.rankingWeights = rankingWeights;
	}

	/** get the elements to be included in the semantic content result **/
	public int getSCInfo() {
		return SCInfo;
	}

	/** set the elements to be included in the semantic content result **/
	public void setSCInfo(int info) {
		SCInfo = info;
	}

	/** get the scope parameter **/
	public int getScope() {
		return scope;
	}

	/** set the scope parameter
	 * @param scope where the keyword should be matched (default LOCAL_NAME+LABEL+COMMENT+ANY_LITERAL)
	 */
	public void setScope(int scope) {
		this.scope = scope;
	}

	/** undocumented **/
	public int[] getSorts() {
		return sorts;
	}

	/** undocumented **/	
	public void setSorts(int[] sorts) {
		this.sorts = sorts;
	}

	/** get the number of result that will be obtained **/
	public int getInc() {
		return inc;
	}

	/** set the number of results that should be returned
	 * @param inc -1 if un restricted
	 */
	public void setInc(int inc) {
		this.inc = inc;
	}

	/** get the indice of the first element to be returned **/
	public int getStart() {
		return start;
	}

	/** set the indice of the first element to be returned 
	 * @param start 0 for the first 
	 * **/
	public void setStart(int start) {
		this.start = start;
	}
	
	/** entity modifier for classes. **/
	public static final int CLASS = 1;
	
	/** entity modifier for properties. **/
	public static final int PROPERTY = 2;

	/** entity modifier for individuals. **/
	public static final int INDIVIDUAL = 4;

	/** scope modifier for namsespaces. **/
	public static final int NS = 1;
	
	/** scope modifier for local names. **/
	public static final int LOCAL_NAME = 2;

	/** scope modifier for labels. **/
	public static final int LABEL = 4;

	/** scope modifier for comments. **/
	public static final int COMMENT = 8;

	/** scope modifier for other related literals. **/
	public static final int LITERAL = 16;

    /** Matcher: match a token **/
	public static final int TOKEN_MATCH = 1;

	/** Matcher: exact match of a normalized form **/
	public static final int EXACT_MATCH = 2;
	
	/** the code used in the database to represent the class type of entities **/
	protected static final int DB_CLASS = 101;
	/** the code used in the database to represent the property type of entities **/
	protected static final int DB_PROPERTY = 102;
	/** the code used in the database to represent the individual type of entities **/
	protected static final int DB_INDIVIDUAL = 103;
	
	
	/** for scInfo: request the information about the languages used in the semantic document **/
	public final static int SC_LANGUAGES_INFO = 1;
	/** for scInfo: request the information about the languages used in the semantic document **/
	public final static int SC_SIZE_INFO = 2;
	/** for scInfo: request the information about the size of the semantic document **/
	public final static int SC_DLEXPR_INFO = 8;
	/** for scInfo: request the information about the Dl Expressiveness used in the semantic document **/
	public final static int SC_LOCATION_INFO = 16;
	/** for scInfo: request the information about the number of classes in the semantic document **/
	public final static int SC_NBCLASSES_INFO = 32;
	/** for scInfo: request the information about the number of properties in the semantic document **/
	public final static int SC_NBPROPS_INFO = 64;
	/** for scInfo: request the information about the number of individuals in the semantic document **/
	public final static int SC_NBINDIS_INFO = 128;

	/** for scInfo: request the information about the number of statements in the semantic document **/
	public final static int SC_NBSTATS_INFO = 256;
	/** for scInfo: request the information about the imported ontologies in the semantic document **/
	public final static int SC_IMPORTS_INFO = 512;
	/** for scInfo: request the information about the ontologies importing the semantic document **/
	public final static int SC_IMPORTEDBY_INFO = 1024;
	/** for scInfo: request the information about the labels of the ontology **/
	public final static int SC_LABELS_INFO = 2048;
	/** for scInfo: request the information about the comments of the ontology **/
	public final static int SC_COMMENTS_INFO = 4096;
	/** for scInfo: request the information about the entities... **/
	public final static int SC_ENTITIES_INFO = 8192;
	/** for scInfo: request the information about the domain **/
	public final static int SC_DOMAIN_INFO = 16384;

	
	/** for entInfo: request the information about the type of the entity **/
	public final static int ENT_TYPE_INFO = 1;
	/** for entInfo: request the information about the label of the entity **/	
	public final static int ENT_LABEL_INFO = 2;
	/** for entInfo: request the information about the comment of the entity **/
	public final static int ENT_COMMENT_INFO = 4;
	/** for entInfo: request the information about the relations from the entity **/
	public final static int ENT_ANYRELATIONFROM_INFO = 8;
	/** for entInfo: request the information about the relations to the entity **/
	public final static int ENT_ANYRELATIONTO_INFO = 16;
	/** for entInfo: request the information about the literal relations of the entity **/
	public final static int ENT_ANYLITERAL_INFO = 32;

	
	
}
