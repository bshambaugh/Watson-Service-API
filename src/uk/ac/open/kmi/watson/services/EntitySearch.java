package uk.ac.open.kmi.watson.services;

import java.io.IOException;
import java.util.Vector;

import net.sf.ehcache.Element;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TermQuery;

import uk.ac.open.kmi.watson.services.internal.EntitySearchInternal;
import uk.ac.open.kmi.watson.services.utils.Array;
import uk.ac.open.kmi.watson.services.utils.URN;
import uk.ac.open.kmi.watson.services.utils.UriUtil;
import uk.ac.open.kmi.watson.services.utils.WatsonAnalyzer;

/**
 * Service for searching and inspecting entities.
 * @author mda99
 */
public class EntitySearch extends WatsonService {
	
	private static final long TIME_LIMIT_WARNING = 20;

	private EntitySearchInternal esi = new EntitySearchInternal();
		  
	public EntitySearch(){}
        
    /**
     * Returns a list of entities martching the search criteria.
     * @param onto the ontology to search in
     * @param kw the keyword to match
     * @param conf the configuration object to restrict the search
     * @return a list of entity results
     */
    public EntityResult[] getEntitiesByKeyword(String onto, String kw, SearchConf conf){
		  try {
			  long time = System.currentTimeMillis();
			  // System.out.println("Starting ent search ...");
			  String SCID = getLuceneDocument(onto);
			  long time2 = System.currentTimeMillis();
			  	Vector<EntityResult> toReturn = new Vector<EntityResult>();
			  	Searcher indexSearcher = getEntityIndexSearcher();
			  	long time3 = System.currentTimeMillis();
				Hits results = null;
				if (conf.getMatch()==SearchConf.TOKEN_MATCH)
					 results = esi.getLuceneEntitiesTokenMatch(indexSearcher, SCID, kw, conf.getScope(), conf.getEntities());
				else if (conf.getMatch()==SearchConf.EXACT_MATCH)
					 results = esi.getLuceneEntitiesExactMatch(indexSearcher, SCID, kw, conf.getScope(), conf.getEntities());	
				else { System.out.println("ERROR:: unknown match technique"); return new EntityResult[0];}
				long time4 = System.currentTimeMillis();
				Searcher relationSearch = getRelationIndexSearcher();
				for (int i = 0; i < results.length(); i++) {
					if (i >= conf.getStart()){
						if (conf.getInc() > 0 && i-conf.getStart() >= conf.getInc()) break;
						if (results.doc(i).get("locN")!=null){ 
							Document d = results.doc(i);
							String ent = d.get("ns")+d.get("locN");
							EntityResult res = new EntityResult(ent);
							String uri = onto;
							res.setSCURI(uri);
							float score = results.score(i);
							res.setScore(score);
							if ((conf.getEntitiesInfo() & SearchConf.ENT_LABEL_INFO) == SearchConf.ENT_LABEL_INFO){
								String[] labels = esi.getLabels(SCID, ent, relationSearch);
								res.setLabels(labels);
							}
							if ((conf.getEntitiesInfo() & SearchConf.ENT_TYPE_INFO) == SearchConf.ENT_TYPE_INFO){
								String type = d.get("type");
								if (type.equals("101") ) type = "Class";
								else if (type.equals("102") ) type = "Property";
								else if (type.equals("103") ) type = "Individual";
								res.setType(type);
							}
							toReturn.add(res);
						}
					}
				}	
				long time5 = System.currentTimeMillis();
				// System.out.println("  Get SCID = "+(time2-time));
				// System.out.println("  Get Indexer = "+(time3-time2));
				// System.out.println("  Search = "+(time4-time3));			
				// System.out.println("  Process = "+(time5-time4));			
				return Array.toArray(toReturn);
		  } catch (IOException e) {
				e.printStackTrace();
			}
		return null;	
	  }	
    
    /** undocumented function... forget about it **/
    public EntityResult[] getEntitiesByStructuredQuery(String s, String p, String o, SearchConf conf){
      	// TODO add infor according to conf.entitiesInfo
    	try {
    		boolean vars = s.trim().equals("?");
    		boolean varp = p.trim().equals("?");
    		boolean varo = o.trim().equals("?");
    		Hits res = esi.searchStructuredQuery(s, p, o, conf);
    		if (res==null) return new EntityResult[0];
    		EntityResult[] result = new EntityResult[res.length()];
    		for (int i = 0; i < res.length(); i++){
    			Document d = res.doc(i);
    			String uri = null; 
    			if (vars) uri = d.get("subj");
    			else if (varp) uri = d.get("pred");
    			else if (varo) uri = d.get("obj");	
    			String onto = getValidID(d.get("scid"));
    			EntityResult r = new EntityResult(uri);
    			r.setSCURI(onto);
    			result[i]=r;
    	}
    		return result;
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return new EntityResult[0];
    }
    
/**
 * return a list of entities in any ontology
 * @param kw the keyword to match
 * @param conf the configuration object to restrict the search
 * @return a list of entity results
 */
    public EntityResult[] getAnyEntityByKeyword(String kw, SearchConf conf){
    	String[][] res = esi.getAnyEntityByKeyword(kw, conf.getScope(), conf.getEntities(), conf.getMatch(), conf.getStart(), conf.getInc());
    	EntityResult[] toReturn = new EntityResult[res.length];
    	int i = 0;
    	for (String[] r : res){
    		EntityResult er = new EntityResult(r[0]);
    		er.setSCURI(getValidID(r[1]));
    		er.setScore(Double.parseDouble(r[2]));
    		if ((conf.getEntitiesInfo() & conf.ENT_TYPE_INFO) == conf.ENT_TYPE_INFO)
    			er.setType(r[3]);
    		if ((conf.getEntitiesInfo() & conf.ENT_LABEL_INFO) == conf.ENT_LABEL_INFO){
    			String[] labels = esi.getLabels(r[1], r[0], getRelationIndexSearcher());
				er.setLabels(labels);
    		}
    		toReturn[i++] = er;
    	}
    	return toReturn;
    }
    
    /**
     * Returns literals matching the given keyword in the given document.
     * @param ontoURI the URI of the considered document
     * @param keyword the keyword to match
     * @return an array of rows of the form [literal][language][entity][property], where literal match keyword and there is in ontoURI a statement of the form {entity property literal@language}
     */
    public String[][] getLiteralsByKeyword(String ontoURI, String keyword){
    	long time = System.currentTimeMillis();
    	Searcher is = getLiteralIndexSearcher();
    	String SCID = getLuceneDocument(ontoURI);
    	keyword = keyword.trim().replaceAll(" ", "-");
    	keyword = keyword.toLowerCase();
    	
		BooleanQuery q = new BooleanQuery();
		q.add(new TermQuery(new Term("scid", SCID)), BooleanClause.Occur.MUST);
		try {
			q.add(new QueryParser("obj", new WatsonAnalyzer()).parse("obj:"+keyword), BooleanClause.Occur.MUST);
		} catch (ParseException e1) {
			e1.printStackTrace();
			return null;
		}
		String[][] toReturn = null;
		try {
			Hits results = is.search(q);
			toReturn = new String[results.length()][];
			for (int i = 0; i < results.length(); i++) {
				Document result = results.doc(i);
				String [] na = new String[4];
				na[0] = result.get("obj");
				na[1] = result.get("lang");
				na[2] = result.get("subj");
				na[3] = result.get("pred");
				toReturn[i] = na;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		// System.out.println("get literals:: "+(System.currentTimeMillis()-time));
		return toReturn;
    }
    
    /** 
     * returns the type of the Entity.
     * @param ontoURI the ontologogy URI the entity belongs to
     * @param entityURI the URI of the entity
     * @return "Class", "Property", or "Individual"
     */
    public String getType(String ontoURI, String entityURI) {
    	long time = System.currentTimeMillis();
    	String result = "";	
    	String SCID = getLuceneDocument(ontoURI);
    	if (SCID == null) {
    		System.out.println("didn't find the document"); return null;
    	}
    	String ns = UriUtil.splitNamespace(entityURI)[0].trim();
    	String ln = UriUtil.splitNamespace(entityURI)[1].trim();
    	ln = new URN(ln).toString();
    	try {
			Searcher indexSearcher = getEntityIndexSearcher();
			BooleanQuery q = new BooleanQuery();
			q.add(new TermQuery(new Term("sc", SCID)), BooleanClause.Occur.MUST);
			q.add(new TermQuery(new Term("ns", ns)), BooleanClause.Occur.MUST);
			q.add(new TermQuery(new Term("HlocN", ln)), BooleanClause.Occur.MUST);
			Hits results = indexSearcher.search(q);
			for (int i = 0; i < results.length(); i++) {
				String type = results.doc(i).get("type");
				if (type.equals("101") ) result = "Class";
				else if (type.equals("102") ) result = "Property";
				else if (type.equals("103") ) result = "Individual";
				else {System.out.println("Incorrect type"); result = null;}
				long time2 = System.currentTimeMillis() - time;
				if (time2 >= TIME_LIMIT_WARNING)
					System.out.println("WARNING:: getType took: "+time2+" for "+ontoURI+" "+entityURI);
				return result;
			}
		} catch(Exception e){
				e.printStackTrace();
			}
     	return null;
    }

    /**
     * Returns the labels of the considered entity in the considered document
     * @param ontoURI the URI of the document
     * @param entityURI the URI of the entity 
     * @return a set of labels
     */
    public String[] getLabels(String ontoURI, String entityURI){
    	long time = System.currentTimeMillis();
    	String SCID = getLuceneDocument(ontoURI);
    	if (SCID == null) {System.out.println("Not found doc "+ontoURI); return null;}
		Searcher indexSearcher = getLiteralIndexSearcher();    	
    	String[] tr = esi.getLabels(SCID, entityURI, indexSearcher);
    		long time2 = (System.currentTimeMillis()-time);
		if (time2>=TIME_LIMIT_WARNING) System.out.println("Warning:: getLabels took: "+time2+" on "+entityURI);
		return tr;
    }
    
    /**
     * Returns the comments of the considered entity in the considered document
     * @param ontoURI the URI of the document
     * @param entityURI the URI of the entity 
     * @return a set of comments
     */
    public String[] getComments(String ontoURI, String entityURI){
    	long time = System.currentTimeMillis();
    	String SCID = getLuceneDocument(ontoURI);
    	if (SCID == null) return null;
    	try {
    		Searcher indexSearcher = getLiteralIndexSearcher();
			BooleanQuery q = new BooleanQuery();
			q.add(new TermQuery(new Term("scid", SCID)), BooleanClause.Occur.MUST);
			q.add(new TermQuery(new Term("subj", entityURI.trim())), BooleanClause.Occur.MUST);
			q.add(new TermQuery(new Term("pred", "http://www.w3.org/2000/01/rdf-schema#comment")), BooleanClause.Occur.MUST);
			Hits results = indexSearcher.search(q);
			// System.out.println("Lits Query time :"+(System.currentTimeMillis()-time));
			String[] toReturn = new String[results.length()];
			for (int i = 0; i < results.length(); i++) {
				Document d = results.doc(i);
				toReturn[i] = d.get("obj");
			}
			long time2 = (System.currentTimeMillis()-time);
			if (time2>=TIME_LIMIT_WARNING) System.out.println("Warning:: getComments took: "+time2+" on "+entityURI);
    	} catch(Exception e){
				e.printStackTrace();
			}
     	return null;
    }
    
    /** 
     * Returns the uri of the semantic contents in which the considered entity is described 
     * @param entityURI the URI of the entity to look for
     * @return the list of URIs of documents in which entityURI is declared
     **/
    public String[] getBelongsTo(String entityURI){
    	Vector<String> result = new Vector<String>();
    	String ns = UriUtil.splitNamespace(entityURI)[0].trim();
    	String ln = UriUtil.splitNamespace(entityURI)[1].trim();
    	ln = new URN(ln).toString();
    	try {
			Searcher indexSearcher = getEntityIndexSearcher();
			BooleanQuery q = new BooleanQuery();
			q.add(new TermQuery(new Term("ns", ns)), BooleanClause.Occur.MUST);
			q.add(new TermQuery(new Term("HlocN", ln)), BooleanClause.Occur.MUST);
			Hits results = indexSearcher.search(q);
			for (int i = 0; i < results.length(); i++) {
				String did = results.doc(i).get("sc");
				String uri = getValidID(did);
				result.add(uri);
			}
			return toArray(result);
    	} catch(Exception e){
				e.printStackTrace();
			}
     	return null;
    }
    
    public String[] getMentionedIn(String entityURI){
    	Vector<String> result = new Vector<String>();
    	try {
			Searcher indexSearcher = getRelationIndexSearcher();
			BooleanQuery q = new BooleanQuery();
			q.add(new TermQuery(new Term("subj", entityURI)), BooleanClause.Occur.SHOULD);
			q.add(new TermQuery(new Term("obj", entityURI)), BooleanClause.Occur.SHOULD);
			Hits results = indexSearcher.search(q);
			for (int i = 0; i < results.length(); i++) {
				String did = results.doc(i).get("scid");
				String uri = getValidID(did);
				if (!result.contains(uri)) result.add(uri);
			}
			indexSearcher = getLiteralIndexSearcher();
			q = new BooleanQuery();
			q.add(new TermQuery(new Term("subj", entityURI)), BooleanClause.Occur.MUST);
			results = indexSearcher.search(q);
			for (int i = 0; i < results.length(); i++) {
				String did = results.doc(i).get("scid");
				String uri = getValidID(did);
				if (!result.contains(uri)) result.add(uri);
			}
			return toArray(result);
    	} catch(Exception e){
				e.printStackTrace();
			}
     	return null;
    }
    
    
    /** 
     * Returns the set of entity relations for entityURI in ontoURI 
     * @return result[0] the property URI, result[1] the property local name, result[2] the related entity uri
     **/
    public String[][] getRelationsFrom(String ontoURI, String entityURI){
    	String SCID = getLuceneDocument(ontoURI);
    	if (SCID == null) return null;
    	return Array.toArray(esi.getRelationsFrom(SCID, entityURI));
    }
    
    /** 
     * Returns the set of entity relations that point to entityURI in ontoURI 
     * @return result[0] the property URI, result[1] the property local name, result[2] the relating entity uri
     **/
    public String[][] getRelationsTo(String ontoURI, String entityURI){
    	String SCID = getLuceneDocument(ontoURI);
    	if (SCID == null) return null;
    	return Array.toArray(esi.getRelationsTo(SCID, entityURI, getRelationIndexSearcher()));
    }
    
    /** 
     * Returns the set of literal relations for entityURI in ontoURI 
     * @return result[0] the property URI, result[1] the property local name, result[2] the literal, result[3] the language of the literal if specified
     **/
    public String[][] getLiteralsFor(String ontoURI, String entityURI){
    	long time = System.currentTimeMillis();
    	String SCID = getLuceneDocument(ontoURI);
    	if (SCID == null) return null;
    	String[][] toReturn = esi.getLiteralsFor(SCID, entityURI, getLiteralIndexSearcher());
    	long time2 = (System.currentTimeMillis()-time);
		if (time2>=TIME_LIMIT_WARNING) System.out.println("Warning:: getLiteralsFor took: "+time2+" on "+entityURI);
		return toReturn;
    }
    
    /** 
     * Returns the list of sub-classes of the given entity in the given document
     * @param ontoURI the URI of the semantic document to consider
     * @param entityURI the URI of the entity to consider
     * @return the list of URIs of sub-classes of entityURI in ontoURI
     */
    public String[] getSubClasses(String ontoURI, String entityURI){
    	long time = System.currentTimeMillis();
		String[] toReturn = null;
    	Vector<String> results = new Vector<String>();
    	// TODO ask directly for the subclasses!!!
    	String[][] relations = getRelationsTo(ontoURI, entityURI);
    	for (String[] rel: relations){
    		if (rel[0].equals("http://www.w3.org/2000/01/rdf-schema#subClassOf")) results.add(rel[2]);
    	}
    	toReturn = toArray(results);
		Element el = new Element(ontoURI+entityURI, toReturn);
		long time2 = System.currentTimeMillis()-time;
		if (time2 >= TIME_LIMIT_WARNING) System.out.println("Warning:: getSubClasses took: "+(time2)+" for"+ontoURI+" "+entityURI);
		return toReturn;
    }
    
    /** 
     * Returns the list of all the direct and indirect sub-classes of the given entity in the given document
     * @param ontoURI the URI of the semantic document to consider
     * @param entityURI the URI of the entity to consider
     * @return the list of URIs of all the sub-classes of entityURI in ontoURI
     */
    public String[] getAllSubClasses(String ontoURI, String entityURI){
       	long time = System.currentTimeMillis();
    	String[] result = toArray(getAllSubClasses(ontoURI, entityURI, new Vector<String>()));
    	long time2 = System.currentTimeMillis()-time;
    	if (time2>=TIME_LIMIT_WARNING)
    		System.out.println("Warning:: getAllSCO took("+result.length+"): "+time2);
    	return result;
    }
        
    /** recursive version with cycle detection **/
    private Vector<String> getAllSubClasses(String ontoURI, String entityURI, Vector<String> name) {
    	String[] r = getSubClasses(ontoURI, entityURI);
    	for (String e : r) {
    		if (!name.contains(e)){
    			name.add(e);
    			getAllSubClasses(ontoURI, e, name);
    		}
    	}
    	return name;
	}
    
    /** 
     * Returns the list of super-classes of the given entity in the given document
     * @param ontoURI the URI of the semantic document to consider
     * @param entityURI the URI of the entity to consider
     * @return the list of URIs of super-classes of entityURI in ontoURI
     */   
    public String[] getSuperClasses(String ontoURI, String entityURI){
    	long time = System.currentTimeMillis();
		String[] toReturn = null;
    	Vector<String> results = new Vector<String>();
    	String[][] relations = getRelationsFrom(ontoURI, entityURI);
    	for (String[] rel: relations){
    		if (rel[0].equals("http://www.w3.org/2000/01/rdf-schema#subClassOf")) results.add(rel[2]);
    	}
       	toReturn = toArray(results);
		Element el = new Element(ontoURI+entityURI, toReturn);
		long time2 = System.currentTimeMillis()-time;
		if (time2 >= TIME_LIMIT_WARNING) System.out.println("Warning:: getSuperClasses took: "+(time2)+" for"+ontoURI+" "+entityURI);
    	return toReturn;
    }
    
    /** 
     * Returns the list of all the direct and indirect super-classes of the given entity in the given document
     * @param ontoURI the URI of the semantic document to consider
     * @param entityURI the URI of the entity to consider
     * @return the list of URIs of all the super-classes of entityURI in ontoURI
     */
    public String[] getAllSuperClasses(String ontoURI, String entityURI){
       	long time = System.currentTimeMillis();
    	String[] result = toArray(getAllSuperClasses(ontoURI, entityURI, new Vector<String>()));
    	long time2 = System.currentTimeMillis()-time;
		if (time2 >= TIME_LIMIT_WARNING) 
		System.out.println("Warning:: getALlSup took ("+result.length+"): "+time2);   
    	return result;
    }
    
    /** recursive version with cycle detection **/
    private Vector<String> getAllSuperClasses(String ontoURI, String entityURI, Vector<String> name) {
    	String[] r = getSuperClasses(ontoURI, entityURI);
    	for (String e : r) {
    		if (!name.contains(e)){
    			name.add(e);
    			getAllSuperClasses(ontoURI, e, name);
    		}
    	}
    	return name;
	}
    
    /** 
     * Returns the list of sub-properties of the given entity in the given document
     * @param ontoURI the URI of the semantic document to consider
     * @param entityURI the URI of the entity to consider
     * @return the list of URIs of sub-properties of entityURI in ontoURI
     */
    public String[] getSubProperties(String ontoURI, String entityURI){
    	long time = System.currentTimeMillis();
		String[] toReturn = null;
    	Vector<String> results = new Vector<String>();
    	String[][] relations = getRelationsTo(ontoURI, entityURI);
    	for (String[] rel: relations){
    		if (rel[0].equals("http://www.w3.org/2000/01/rdf-schema#subPropertyOf")) results.add(rel[2]);
    	}
    	toReturn = toArray(results);
		Element el = new Element(ontoURI+entityURI, toReturn);
		long time2 = System.currentTimeMillis()-time;
		if (time2 >= TIME_LIMIT_WARNING) System.out.println("Warning:: getSubProperties took: "+(time2)+" for"+ontoURI+" "+entityURI);
		return toReturn;
    }
    
    
    /** 
     * Returns the list of all the direct and indirect sub-properties of the given entity in the given document
     * @param ontoURI the URI of the semantic document to consider
     * @param entityURI the URI of the entity to consider
     * @return the list of URIs of all the sub-properties of entityURI in ontoURI
     */
    public String[] getAllSubProperties(String ontoURI, String entityURI){
       	long time = System.currentTimeMillis();
    	String[] result = toArray(getAllSubProperties(ontoURI, entityURI, new Vector<String>()));
    	long time2 = System.currentTimeMillis()-time;
    	if (time2>=TIME_LIMIT_WARNING)
    		System.out.println("Warning:: getAllSPO took("+result.length+"): "+time2);
    	return result;
    }
        
    /** recursive version with cycle detection **/
    private Vector<String> getAllSubProperties(String ontoURI, String entityURI, Vector<String> name) {
    	String[] r = getSubProperties(ontoURI, entityURI);
    	for (String e : r) {
    		if (!name.contains(e)){
    			name.add(e);
    			getAllSubProperties(ontoURI, e, name);
    		}
    	}
    	return name;
	}
    
    /** 
     * Returns the list of super-properties of the given entity in the given document
     * @param ontoURI the URI of the semantic document to consider
     * @param entityURI the URI of the entity to consider
     * @return the list of URIs of super-properties of entityURI in ontoURI
     */   
    public String[] getSuperProperties(String ontoURI, String entityURI){
    	long time = System.currentTimeMillis();
		String[] toReturn = null;
    	Vector<String> results = new Vector<String>();
    	String[][] relations = getRelationsFrom(ontoURI, entityURI);
    	for (String[] rel: relations){
    		if (rel[0].equals("http://www.w3.org/2000/01/rdf-schema#subPropertyOf")) results.add(rel[2]);
    	}
       	toReturn = toArray(results);
		Element el = new Element(ontoURI+entityURI, toReturn);
		long time2 = System.currentTimeMillis()-time;
		if (time2 >= TIME_LIMIT_WARNING) System.out.println("Warning:: getSuperProperties took: "+(time2)+" for"+ontoURI+" "+entityURI);
    	return toReturn;
    }
    
    /** 
     * Returns the list of all the direct and indirect super-properties of the given entity in the given document
     * @param ontoURI the URI of the semantic document to consider
     * @param entityURI the URI of the entity to consider
     * @return the list of URIs of all the super-properties of entityURI in ontoURI
     */
    public String[] getAllSuperProperties(String ontoURI, String entityURI){
       	long time = System.currentTimeMillis();
    	String[] result = toArray(getAllSuperProperties(ontoURI, entityURI, new Vector<String>()));
    	long time2 = System.currentTimeMillis()-time;
		if (time2 >= TIME_LIMIT_WARNING) 
		System.out.println("Warning:: getALlSupP took ("+result.length+"): "+time2);   
    	return result;
    }
    
    /** recursive version with cycle detection **/
    private Vector<String> getAllSuperProperties(String ontoURI, String entityURI, Vector<String> name) {
    	String[] r = getSuperProperties(ontoURI, entityURI);
    	for (String e : r) {
    		if (!name.contains(e)){
    			name.add(e);
    			getAllSuperProperties(ontoURI, e, name);
    		}
    	}
    	return name;
	}
    
    
    /** 
     * Returns the list of equivalent classes of the given entity in the given document
     * @param ontoURI the URI of the semantic document to consider
     * @param entityURI the URI of the entity to consider
     * @return the list of URIs of equivalent classes of entityURI in ontoURI
     */   
    public String[] getEquivalentClasses(String ontoURI, String entityURI){
    	Vector<String> results = new Vector<String>();
    	String[][] relations = getRelationsFrom(ontoURI, entityURI);
    	for (String[] rel: relations){
    		if (rel[0].equals("http://www.w3.org/2002/07/owl#equivalentClass") || 
    				rel[0].equals("http://www.daml.org/2001/03/daml+oil#sameClassAs")) results.add(rel[2]);
    	}
    	return toArray(results);
    }
    
    /** 
     * Returns the list of instances of the given class in the given document
     * @param ontoURI the URI of the semantic document to consider
     * @param entityURI the URI of the class to consider
     * @return the list of URIs of instances of entityURI in ontoURI
     */
    public String[] getInstances(String ontoURI, String entityURI){
    	Vector<String> results = new Vector<String>();
    	String[][] relations = getRelationsTo(ontoURI, entityURI);
    	for (String[] rel: relations){
    		if (rel[0].equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")) {
    			results.add(rel[2]);
    		}
    	}
    	return toArray(results);
    }
    
    
    /** 
     * Returns the list of instances of the given class in the given document
     * @param ontoURI the URI of the semantic document to consider
     * @param entityURI the URI of the class to consider
     * @return the list of URIs of instances of entityURI in ontoURI
     */
    public String[] getAllInstances(String ontoURI, String entityURI){
    	String[] inst1 = getInstances(ontoURI, entityURI);
    	Vector<String> results = new Vector<String>();
    	for (String i :inst1) results.add(i);
    	String[] scs = getAllSubClasses(ontoURI, entityURI);
    	for (String sc : scs){
    		String[] inst2 = getInstances(ontoURI, sc);
    	 	for (String i :inst2) results.add(i);
    	}
    	return toArray(results);
    }
    
    /** 
     * Returns the list of classes (type) of the given individual in the given document
     * @param ontoURI the URI of the semantic document to consider
     * @param entityURI the URI of the individual to consider
     * @return the list of URIs of classes of entityURI in ontoURI
     */
    public String[] getClasses(String ontoURI, String entityURI){
    	Vector<String> results = new Vector<String>();
    	String[][] relations = getRelationsFrom(ontoURI, entityURI);
    	for (String[] rel: relations){
    		if (rel[0].equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")) results.add(rel[2]);
    	}
    	return toArray(results);
    }
    
    /** 
     * Returns the list of classes (type) of the given individual in the given document, considering also the transitive super-classes
     * @param ontoURI the URI of the semantic document to consider
     * @param entityURI the URI of the individual to consider
     * @return the list of URIs of classes of entityURI in ontoURI
     */
    public String[] getAllClasses(String ontoURI, String entityURI){
    	String[] classes = getClasses(ontoURI, entityURI);
    	Vector<String> results = new Vector<String>();
    	for (String c : classes){
    		results.add(c);
    		String[] scs = getAllSuperClasses(ontoURI,c);
    		for (String sc : scs) results.add(sc);
    	}
    	return toArray(results);
    }
    
    
    /** 
     * Returns the list of domain classes of the given property in the given document
     * @param ontoURI the URI of the semantic document to consider
     * @param entityURI the URI of the property to consider
     * @return the list of URIs of domain of entityURI in ontoURI
     */
    public String[] getDomain(String ontoURI, String entityURI){
    	long time = System.currentTimeMillis();
    	Vector<String> results = new Vector<String>();
    	String[][] relations = getRelationsFrom(ontoURI, entityURI);
    	for (String[] rel: relations){
    		if (rel[0].equals("http://www.w3.org/2000/01/rdf-schema#domain")) results.add(rel[2]);
    	}
		System.out.println("getDomain took: "+(System.currentTimeMillis()-time));
    	return toArray(results);
    }
    
    /** 
     * Returns the list of domain classes of the given property in the given document
     * @param ontoURI the URI of the semantic document to consider
     * @param entityURI the URI of the property to consider
     * @return the list of URIs of domain of entityURI in ontoURI
     */
    public String[] getAllDomain(String ontoURI, String entityURI){
    	String[] cl1 = getDomain(ontoURI, entityURI);
    	Vector<String> results = new Vector<String>();
    	for (String c : cl1){
    		results.add(c);
    		String[] cl2 = getAllSubClasses(ontoURI, c);
    		for (String c2 : cl2) results.add(c2);
    	}
    	return toArray(results);
    }
    
    /** 
     * Returns the list of range classes of the given property in the given document
     * @param ontoURI the URI of the semantic document to consider
     * @param entityURI the URI of the property to consider
     * @return the list of URIs of range of entityURI in ontoURI
     */
    public String[] getRange(String ontoURI, String entityURI){
    	// TODO: also take into account the subproperties...
    	long time = System.currentTimeMillis();
    	Vector<String> results = new Vector<String>();
    	String[][] relations = getRelationsFrom(ontoURI, entityURI);
    	for (String[] rel: relations){
    		if (rel[0].equals("http://www.w3.org/2000/01/rdf-schema#range")) results.add(rel[2]);
    	}
		System.out.println("getRange took: "+(System.currentTimeMillis()-time));
    	return toArray(results);
    }
    
    /** 
     * Returns the list of range classes of the given property in the given document
     * @param ontoURI the URI of the semantic document to consider
     * @param entityURI the URI of the property to consider
     * @return the list of URIs of range of entityURI in ontoURI
     */
    public String[] getAllRange(String ontoURI, String entityURI){
    	// TODO: also take into account the subproperties...
    	String[] cl1 = getRange(ontoURI, entityURI);
    	Vector<String> results = new Vector<String>();
    	for (String c : cl1){
    		results.add(c);
    		String[] cl2 = getAllSubClasses(ontoURI, c);
    		for (String c2 : cl2) results.add(c2);
    	}
    	return toArray(results);
    }
    
    /** 
     * Returns the list of property having the given class as domain in the given document
     * @param ontoURI the URI of the semantic document to consider
     * @param entityURI the URI of the class to consider
     * @return the list of URIs of property having entityURI as domain in ontoURI
     */
    public String[] getAllDomainOf(String ontoURI, String entityURI){
    	// TODO: also take into account the subproperties...
    	String[] prp1 = getDomainOf(ontoURI, entityURI);
    	Vector<String> results = new Vector<String>();
    	for (String p : prp1) results.add(p);
    	String[] cls = getAllSuperClasses(ontoURI, entityURI);
    	for (String c : cls){
    		String[] prp2 = getDomainOf(ontoURI, c);
    		for (String p : prp2) results.add(p);
    	}
    	return toArray(results);
    }
    
    /** 
     * Returns the list of property having the given class as domain in the given document
     * @param ontoURI the URI of the semantic document to consider
     * @param entityURI the URI of the class to consider
     * @return the list of URIs of property having entityURI as domain in ontoURI
     */
    public String[] getDomainOf(String ontoURI, String entityURI){
    	Vector<String> results = new Vector<String>();
    	String[][] relations = getRelationsTo(ontoURI, entityURI);
    	for (String[] rel: relations){
    		if (rel[0].equals("http://www.w3.org/2000/01/rdf-schema#domain")) results.add(rel[2]);
    	}
    	return toArray(results);
    }
    
    /** 
     * Returns the list of property having the given class as range in the given document
     * @param ontoURI the URI of the semantic document to consider
     * @param entityURI the URI of the class to consider
     * @return the list of URIs of property having entityURI as range in ontoURI
     */
    public String[] getRangeOf(String ontoURI, String entityURI){
    	Vector<String> results = new Vector<String>();
    	String[][] relations = getRelationsTo(ontoURI, entityURI);
    	for (String[] rel: relations){
    		if (rel[0].equals("http://www.w3.org/2000/01/rdf-schema#range")) results.add(rel[2]);
    	}
    	return toArray(results);
    }
    
    /** 
     * Returns the list of property having the given class as range in the given document
     * @param ontoURI the URI of the semantic document to consider
     * @param entityURI the URI of the class to consider
     * @return the list of URIs of property having entityURI as range in ontoURI
     */
    public String[] getAllRangeOf(String ontoURI, String entityURI){
    	// TODO: also take into account the subproperties...
    	String[] prp1 = getRangeOf(ontoURI, entityURI);
    	Vector<String> results = new Vector<String>();
    	for (String p : prp1) results.add(p);
    	String[] cls = getAllSuperClasses(ontoURI, entityURI);
    	for (String c : cls){
    		String[] prp2 = getRangeOf(ontoURI, c);
    		for (String p : prp2) results.add(p);
    	}
    	return toArray(results);
    }
    
    /** 
     * Returns the list of individuals that are the same as  the given individuals in the given document
     * @param ontoURI the URI of the semantic document to consider
     * @param entityURI the URI of the individual to consider
     * @return the list of URIs of individuals equivalent to entityURI in ontoURI
     */
    public String[] getSameIndividuals(String ontoURI, String entityURI){
    	Vector<String> results = new Vector<String>();
    	String[][] relations = getRelationsFrom(ontoURI, entityURI);
    	for (String[] rel: relations){
    		if (rel[0].equals("http://www.w3.org/2002/07/owl#sameAs") || 
    				rel[0].equals("http://www.daml.org/2001/03/daml+oil#sameIndividualAs")) results.add(rel[2]);
    	}
    	return toArray(results);
    }
    
    /** 
     * Returns the list of individuals that are the different from the given individuals in the given document
     * @param ontoURI the URI of the semantic document to consider
     * @param entityURI the URI of the individual to consider
     * @return the list of URIs of individuals declared as different from entityURI in ontoURI
     */
    public String[] getDifferentFrom(String ontoURI, String entityURI){
    	Vector<String> results = new Vector<String>();
    	String[][] relations = getRelationsFrom(ontoURI, entityURI);
    	for (String[] rel: relations){
    		if (rel[0].equals("http://www.w3.org/2002/07/owl#differentFrom") || 
    				rel[0].equals("http://www.daml.org/2001/03/daml+oil#differentIndividualFrom")) results.add(rel[2]);
    	}
    	return toArray(results);
    }
    
    /** 
     * Returns the list of classes that are disjoint with the given class in the given document
     * @param ontoURI the URI of the semantic document to consider
     * @param entityURI the URI of the class to consider
     * @return the list of URIs of classes disjoint with entityURI in ontoURI
     */
    public String[] getDisjointWith(String ontoURI, String entityURI){
    	Vector<String> results = new Vector<String>();
    	String[][] relations = getRelationsFrom(ontoURI, entityURI);
    	for (String[] rel: relations){
    		if (rel[0].equals("http://www.w3.org/2002/07/owl#disjointWith") || 
    				rel[0].equals("http://www.daml.org/2001/03/daml+oil#disjointWith")) results.add(rel[2]);
    	}
    	return toArray(results);
    }
    
    /** 
     * Returns the list of classes that are declared to be disjoint with the given class in the given document
     * @param ontoURI the URI of the semantic document to consider
     * @param entityURI the URI of the class to consider
     * @return the list of URIs of classes declared to be disjoint with entityURI in ontoURI
     */
    public String[] getIsDisjointWith(String ontoURI, String entityURI){
    	Vector<String> results = new Vector<String>();
    	String[][] relations = getRelationsTo(ontoURI, entityURI);
    	for (String[] rel: relations){
    		if (rel[0].equals("http://www.w3.org/2002/07/owl#disjointWith") || 
    				rel[0].equals("http://www.daml.org/2001/03/daml+oil#disjointWith")) results.add(rel[2]);
    	}
    	return toArray(results);
    }
    
    /** 
     * Returns the list of classes that are disjoint with the given class in the given document
     * @param ontoURI the URI of the semantic document to consider
     * @param entityURI the URI of the class to consider
     * @return the list of URIs of classes disjoint with entityURI in ontoURI
     */
    public String[] getAllDisjointWith(String ontoURI, String entityURI){
    	Vector<String> results = getDWI(ontoURI, entityURI, new Vector<String>());
    	String[] supc = getAllSuperClasses(ontoURI, entityURI);
    	for (String cl : supc)
    		results.addAll(getDWI(ontoURI, entityURI, results));
    	Vector<String> results2 = (Vector<String>) results.clone();
    	for (String cl : results){
    		String[] subc = getAllSubClasses(ontoURI, cl);
    		for (String scl: subc)
    			if (!results2.contains(scl)) results2.add(scl);
    	}
    	return toArray(results2);
    }
    
    private Vector<String> getDWI(String ontoURI, String entityURI, Vector<String> known){
    	Vector<String> results = new Vector<String>();
    	String[] res1 = getDisjointWith(ontoURI, entityURI);
     	String[] res2 = getIsDisjointWith(ontoURI, entityURI);
        for(String res : res1) if (!known.contains(res)) results.add(res);
        for(String res : res2) if (!known.contains(res)) results.add(res);
        return results;
    }
    
    /** Get in onto all the pairs entity-entity/entity-literal (e1, l2/e2) that are linked through a property p
     *  (i.e. such that there is a triple <e1,p,e2/l2>)
     * @param onto the ontology to look at
     * @param p the property to consider
     * @return pairs of entities, with result[0] = e1 and result[1] = e2/l2
     */
    public String[][] getRelatedBy(String onto, String p){
    	Searcher indexSearcher = getRelationIndexSearcher();
    	String SCID = getLuceneDocument(onto);
		Vector<String[]> res = new Vector<String[]>();
    	if (SCID==null) return null;
    	try{
			long time1 = System.currentTimeMillis();
		  BooleanQuery q = new BooleanQuery();
		  q.add(new TermQuery(new Term("scid", SCID)), BooleanClause.Occur.MUST);
		  q.add(new TermQuery(new Term("pred", p)), BooleanClause.Occur.MUST);
		  Hits results = indexSearcher.search(q);	
		  // System.out.println("R::"+results.length());
		  long time2 = System.currentTimeMillis();
		  for (int i = 0; i < results.length(); i++){
			  Document doc = results.doc(i);
			  String obj = doc.get("obj");
			  String subj  = doc.get("subj");
			  String[] rel = new String[2];
			  rel[0] = subj;
			  rel[1] = obj;
			  res.add(rel);
		  }
			long time3 = System.currentTimeMillis();
	  		// System.out.print("RB1 "+res.size()+" Searching "+(time2-time1));
    		// System.out.println("  processing "+(time3-time2));
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
    	indexSearcher = getLiteralIndexSearcher();
    	try{
			long time1 = System.currentTimeMillis();
		  BooleanQuery q = new BooleanQuery();
		  q.add(new TermQuery(new Term("scid", SCID)), BooleanClause.Occur.MUST);
		  q.add(new TermQuery(new Term("pred", p)), BooleanClause.Occur.MUST);
		  Hits results = indexSearcher.search(q);	
		  // System.out.println("L::"+results.length());
		  long time2 = System.currentTimeMillis();
		  for (int i = 0; i < results.length(); i++){
			  Document doc = results.doc(i);
			  String obj = doc.get("obj");
			  String subj  = doc.get("subj");
			  String[] rel = new String[2];
			  rel[0] = subj;
			  rel[1] = obj;
			  res.add(rel);
		  }
			long time3 = System.currentTimeMillis();
	  		// System.out.print("RB2 "+res.size()+" Searching "+(time2-time1));
    		// System.out.println("  processing "+(time3-time2));
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
    	return Array.toArray(res);
    }
  
           	
}
