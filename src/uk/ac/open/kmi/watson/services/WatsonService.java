package uk.ac.open.kmi.watson.services;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ParallelMultiSearcher;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TermQuery;

import uk.ac.open.kmi.watson.services.utils.ConfFileReader;

/**
 * Super-class of the classes of the Watson services (server side).
 * Contains common methods for the management of URIs and constants used for defining the search functions.<br>
 * @author mda99
 */
public class WatsonService {
	
    // protected ConfFileReader cfr = new ConfFileReader("/opt/tomcat/webapps/ODP-Search/watson.conf");
	protected ConfFileReader cfr = new ConfFileReader("watson.conf");
	
    protected String[] luceneDocumentIndexes = cfr.getStringArrayParameter("document-indexes");
    protected String[] luceneEntityIndexes = cfr.getStringArrayParameter("entity-indexes");
 	protected String[] luceneRelationIndexes = cfr.getStringArrayParameter("relation-indexes");
 	protected String[] luceneLiteralIndexes = cfr.getStringArrayParameter("literal-indexes");
	
  protected IndexSearcher[] documentIndexSearchers;
  protected IndexSearcher[] entityIndexSearchers;
  protected IndexSearcher[] relationIndexSearchers;
  protected IndexSearcher[] literalIndexSearchers;

  protected static ParallelMultiSearcher documentSearcher;
  protected static ParallelMultiSearcher entitySearcher;
  protected static ParallelMultiSearcher relationSearcher;
  protected static ParallelMultiSearcher literalSearcher;

  private boolean reloadDocumentIndex;
  private boolean reloadEntityIndex;
  private boolean reloadRelationIndex;
  private boolean reloadLiteralIndex;

  
   // The SC cache... TODO: may not be useful any more...
   private static Hashtable<String,String> SCHashtable = new Hashtable<String,String>();

   // The cache for getValidID...
   private static Cache URICache;

   // The cache for getValidID...
   private static Cache DocCache;
   
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
	
	private static int TIME_DEBUG_WARNING = 20;
	private static int SPACE_DEBUG_WARNING = 1000000000;
	

	/** create an instance of Watson service **/
	protected WatsonService() {
		this(true);
	}

	private boolean load = true;
	
	protected WatsonService(boolean load) {
		System.out.println("Creating service "+load);
		this.load = load;
		try {
	    if (URICache == null) { 
	    	CacheManager cm = CacheManager.create();
	    	cm.addCache("URICache");
	    	URICache = cm.getCache("URICache");
	    }
	    if (DocCache == null) { 
	    	CacheManager cm = CacheManager.create();
	    	cm.addCache("DocCache");
	    	DocCache = cm.getCache("DocCache");
	    }
		}
		catch(Exception e){
			e.printStackTrace();
		}
		// INITIALIZE LUCENE INDEXes
		ConfFileReader cfr2 = new ConfFileReader("watson_dir.conf");
		String ind_dir = cfr2.getStringParameter("WATSON_INDEX_DIR");
		if (ind_dir==null){
			Map map = System.getenv();
			ind_dir = (String) map.get("WATSON_INDEX_DIR");
		}
		if (ind_dir != null){
			 System.out.println("Using WATSON_INDEX_DIR = "+ind_dir);
			    File homedir = new File(ind_dir);
			    String[] allfiles = homedir.list();
			    Vector<String> doFiles = new Vector<String>();
			    Vector<String> entFiles = new Vector<String>();
			    Vector<String> relFiles = new Vector<String>();
			    Vector<String> litFiles = new Vector<String>();	
			    for (String s : allfiles)
				if (s.startsWith("documents")) {
				    doFiles.add(ind_dir+"/"+s);
				    System.out.println("Found index "+s);
				}
				else if (s.startsWith("entities")) {
				    entFiles.add(ind_dir+"/"+s);
				    System.out.println("Found index "+s);
				}
				else if (s.startsWith("relations")) {
				    relFiles.add(ind_dir+"/"+s);
				    System.out.println("Found index "+s);
				}
				else if (s.startsWith("literals")) {
				    litFiles.add(ind_dir+"/"+s);
				    System.out.println("Found index "+s);
				}
			    luceneDocumentIndexes = toArray(doFiles);
			    luceneEntityIndexes = toArray(entFiles);
			    luceneRelationIndexes = toArray(relFiles);
			    luceneLiteralIndexes = toArray(litFiles);
		}
			getDocumentIndexSearcher();
			getEntityIndexSearcher();
			getRelationIndexSearcher();
			getLiteralIndexSearcher();
	}
	
	
	protected String getLuceneDocument(String ontoURI) {
		long time1 = System.currentTimeMillis();
		long mem1 = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
		Element element = DocCache.get(ontoURI);
		String toReturn = null;
		if (element != null ) {
			toReturn = (String)element.getObjectValue();
			long time2 = System.currentTimeMillis();
			long mem2 = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
			if (time2-time1 >= TIME_DEBUG_WARNING)
				System.out.println("WARNING:: getLuceneDocument1 "+ontoURI+" took "+(time2-time1)+" ms");
			if (mem2-mem1 >= SPACE_DEBUG_WARNING)
				System.out.println("WARNING:: getLuceneDocument "+ontoURI+" took "+(mem2-mem1)+" bytes "+Runtime.getRuntime().freeMemory());			
			return toReturn;
		}
		try{
		  Searcher indexSearcher = getDocumentIndexSearcher();
		  org.apache.lucene.search.Query query;
		  query = new TermQuery(new Term("URI",ontoURI));
		  Hits results = indexSearcher.search(query);
		  if (results.length() >= 1){
			  String docID = results.doc(0).get("id");
			  Element el = new Element(ontoURI, docID);
			  DocCache.put(el);
			  long time2 = System.currentTimeMillis();
				long mem2 = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
				if (time2-time1 >= TIME_DEBUG_WARNING)
					System.out.println("WARNING:: getLuceneDocument2 "+ontoURI+" took "+(time2-time1)+" ms");
				if (mem2-mem1 >= SPACE_DEBUG_WARNING)
					System.out.println("WARNING:: getLuceneDocument "+ontoURI+" took "+(mem2-mem1)+" bytes "+Runtime.getRuntime().freeMemory());
			  return docID;
		  }
		  else {
			  query = new TermQuery(new Term("prov",ontoURI));
			  results = indexSearcher.search(query);
			  if (results.length() >= 1){
				  String docID = results.doc(0).get("id");
				  Element el = new Element(ontoURI, docID);
				  DocCache.put(el);
				  long time2 = System.currentTimeMillis();
					long mem2 = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
					if (time2-time1 >= TIME_DEBUG_WARNING)
						System.out.println("WARNING:: getLuceneDocument3 "+ontoURI+" took "+(time2-time1)+" ms");
					if (mem2-mem1 >= SPACE_DEBUG_WARNING)
						System.out.println("WARNING:: getLuceneDocument "+ontoURI+" took "+(mem2-mem1)+" bytes "+Runtime.getRuntime().freeMemory());
				  return docID;
			  }
			  else {
				  if (SCHashtable.containsKey(ontoURI)){
					  String docID = SCHashtable.get(ontoURI);
					  System.out.println("KO Lucene on "+ontoURI);	
					  Element el = new Element(ontoURI, docID);
					  DocCache.put(el);
					  long time2 = System.currentTimeMillis();
						long mem2 = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
						if (time2-time1 >= TIME_DEBUG_WARNING)
							System.out.println("WARNING:: getLuceneDocument4 "+ontoURI+" took "+(time2-time1)+" ms");
						if (mem2-mem1 >= SPACE_DEBUG_WARNING)
							System.out.println("WARNING:: getLuceneDocument "+ontoURI+" took "+(mem2-mem1)+" bytes "+Runtime.getRuntime().freeMemory());
						return docID;
					}
				  else {
					  System.out.println("PROBLEM: I can't retrieve this ontology, even if it may exist..."+ontoURI);
				  }
			  }
				  
		  }
		}
		catch(Exception e){ 
			e.printStackTrace();
		}
		return null;
	}
	
	// TODO: put a cache??
	protected int getLuceneDocumentID(String ontoURI) {
		long time1 = System.currentTimeMillis();
		long mem1 = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
		// Element element = DocCache.get(ontoURI);
		String toReturn = null;
		// if (element != null ) {
		//	toReturn = (String)element.getObjectValue();
		//	long time2 = System.currentTimeMillis();
		//	long mem2 = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
		//	if (time2-time1 >= TIME_DEBUG_WARNING)
		//		System.out.println("WARNING:: getLuceneDocument "+ontoURI+" took "+(time2-time1)+" ms");
	//		if (mem2-mem1 >= SPACE_DEBUG_WARNING)
//				System.out.println("WARNING:: getLuceneDocument "+ontoURI+" took "+(mem2-mem1)+" bytes "+Runtime.getRuntime().freeMemory());			
	//		return toReturn;
		// }
		try{
		  Searcher indexSearcher = getDocumentIndexSearcher();
		  org.apache.lucene.search.Query query;
		  query = new TermQuery(new Term("URI",ontoURI));
		  Hits results = indexSearcher.search(query);
		  if (results.length() == 1){
			  // String docID = results.doc(0).get("id");
			  int docID = results.id(0);
			  //Element el = new Element(ontoURI, docID);
			  // DocCache.put(el);
			  // long time2 = System.currentTimeMillis();
				// long mem2 = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
				// if (time2-time1 >= TIME_DEBUG_WARNING)
					// System.out.println("WARNING:: getLuceneDocument "+ontoURI+" took "+(time2-time1)+" ms");
				// if (mem2-mem1 >= SPACE_DEBUG_WARNING)
					// System.out.println("WARNING:: getLuceneDocument "+ontoURI+" took "+(mem2-mem1)+" bytes "+Runtime.getRuntime().freeMemory());
			  return docID;
		  }
		  else {
			  query = new TermQuery(new Term("prov",ontoURI));
			  results = indexSearcher.search(query);
			  if (results.length() >= 1){
				  // String docID = results.doc(0).get("id");
				  int docID = results.id(0);
				  // Element el = new Element(ontoURI, docID);
				  // DocCache.put(el);
				  // long time2 = System.currentTimeMillis();
				//	long mem2 = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
					//if (time2-time1 >= TIME_DEBUG_WARNING)
				//		System.out.println("WARNING:: getLuceneDocument "+ontoURI+" took "+(time2-time1)+" ms");
				//	if (mem2-mem1 >= SPACE_DEBUG_WARNING)
				//		System.out.println("WARNING:: getLuceneDocument "+ontoURI+" took "+(mem2-mem1)+" bytes "+Runtime.getRuntime().freeMemory());
				  return docID;
			  }
			  else {
				  if (SCHashtable.containsKey(ontoURI)){
					  String docID = SCHashtable.get(ontoURI);
					  System.out.println("KO Lucene on "+ontoURI);	
					  Element el = new Element(ontoURI, docID);
					  DocCache.put(el);
					  long time2 = System.currentTimeMillis();
						long mem2 = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
						if (time2-time1 >= TIME_DEBUG_WARNING)
							System.out.println("WARNING:: getLuceneDocument "+ontoURI+" took "+(time2-time1)+" ms");
						if (mem2-mem1 >= SPACE_DEBUG_WARNING)
							System.out.println("WARNING:: getLuceneDocument "+ontoURI+" took "+(mem2-mem1)+" bytes "+Runtime.getRuntime().freeMemory());
						// return docID;
						return 0;
					}
				  else {
					  System.out.println("PROBLEM: I can't retrieve this ontology, even if it may exist..."+ontoURI);
				  }
			  }
				  
		  }
		}
		catch(Exception e){ 
			e.printStackTrace();
		}
		return 0;
	}
	
	/** 
	 * Indicates if a URI is ambigous (used more than once) 
	 * @param ontoURI the URI to check
	 * @return true if ontoURI is used more than once
	 **/
	protected boolean isAmbigous(String ontoURI) {
		// TODO: may get wrong if the URI is the URL of another doc...
		try{
			  Searcher indexSearcher = getDocumentIndexSearcher();
			  int f = indexSearcher.docFreq(new Term("URI", ontoURI));
			  /* org.apache.lucene.search.Query query;
			  query = new WildcardQuery(new Term("URI", ontoURI));
			  Hits results = indexSearcher.search(query);
			  boolean result = results.length() > 1;*/
			  boolean result = f > 1;
			  return result;
		}
		catch(Exception e){ 
			e.printStackTrace();
		}
		return false;
	}

	/** 
	 * returns the first found URL of a semantic content. 
	 * To be used when URIs are ambigous or undeclared
	 * @param sc the semantic content for which a URL is required
	 * @return the first found URL of sc
	 */
/*I	private String getFirstURL(String ID) {
		try {
		IndexSearcher indexSearcher = getDocumentIndexSearcher();
		  org.apache.lucene.search.Query query;
		  query = new WildcardQuery(new Term("id",ID));
		  Hits results = indexSearcher.search(query);
		  if (results.length()==1){
			  org.apache.lucene.document.Document doc = results.doc(0);
			  return doc.get("prov");
		  }
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
		/* String result = null;
		Factory f = new Factory();
		f.beginTransaction();
		Document d = f.getDocument(ID);
		Iterator it = d.provenance();
		if (it.hasNext()) result = (String)it.next();
		f.commitTransaction();
		return result; 
	} */

	/** 
	 * Returns the valid identificator for a semantic content.
	 * If a URI is declared for sc, and if it is not ambigous, return the URI.
	 * Otherwise, return a URL.
	 * @param sc the semantic content for which an identificator is required
	 * @return a unambigous URI or a URL for sc
	 */
	protected String getValidID(String scID){
		long time1 = System.currentTimeMillis();
		long mem1 = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
		Element element = URICache.get(scID);
		String toReturn = null;
		if (element != null ) {
			toReturn = (String)element.getObjectValue();
			long time2 = System.currentTimeMillis();
			long mem2 = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
			if (time2-time1 >= TIME_DEBUG_WARNING)
				System.out.println("WARNING:: getValidID-1 "+scID+" took "+(time2-time1)+" ms");
			if (mem2-mem1 >= SPACE_DEBUG_WARNING)
				System.out.println("WARNING:: getValidID-1 "+scID+" took "+(mem2-mem1)+" bytes "+Runtime.getRuntime().freeMemory());
			return toReturn;
		}
		try{
			long time11 = System.currentTimeMillis();
			Searcher indexSearcher = getDocumentIndexSearcher();
			long time12 = System.currentTimeMillis();
			org.apache.lucene.search.Query query;
			query = new TermQuery(new Term("id",scID));
			long time13 = System.currentTimeMillis();
			Hits results = indexSearcher.search(query);
			long time14 = System.currentTimeMillis();
			// System.out.println("search:: "+(time14-time13));
			if (results.length()>=1){
				String result;
				long time15 = System.currentTimeMillis();
				Document d = results.doc(0);
				result = getValidID(d);	
				long time2 = System.currentTimeMillis();
				long mem2 = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
				if (time2-time1 >= TIME_DEBUG_WARNING)
					System.out.println("WARNING:: getValidID "+scID+" took "+(time2-time1)+" ms");
				return result;	
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	protected String getValidID(Document d) {  
		String scID = d.get("id");
	  long time1 = System.currentTimeMillis();
	  String result = d.get("URI");
	  long time17 = System.currentTimeMillis();
	  if (result != null && !isBuggy(result) && !isAmbigous(result)){
		  long time18 = System.currentTimeMillis();
		  Element el = new Element(scID, result);
		  URICache.put(el);
		  Element el2 = new Element(result, scID);
		  DocCache.put(el2);
		  long time2 = System.currentTimeMillis();
			long mem2 = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
		  if (time2-time1 >= TIME_DEBUG_WARNING)
				System.out.println("WARNING:: getValidID-2 "+scID+" took "+(time2-time1)+" ms");
		  return  result;
	   }
	   else {
		   long time18 = System.currentTimeMillis();	
		   result = d.get("prov");
		   long time19 = System.currentTimeMillis();	
		   // Runtime.getRuntime().gc();
		   Element el = new Element(scID, result);
		   URICache.put(el);
		   Element el2 = new Element(result, scID);
		   DocCache.put(el2);
		   long time110 = System.currentTimeMillis();	
		   long time2 = System.currentTimeMillis();
		   long mem2 = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
		   if (time2-time1 >= TIME_DEBUG_WARNING)
				System.out.println("WARNING:: getValidID-3 "+scID+" took "+(time2-time1)+" ms");
			// System.out.println("get index:: "+(time12-time11));
			// System.out.println("create query:: "+(time13-time12));
			// System.out.println("test:: "+(time15-time14));
			// System.out.println("get doc:: "+(time16-time15));
			// System.out.println("get URI:: "+(time17-time16));
			// System.out.println("test:: "+(time18-time17));
			// System.out.println("get prov:: "+(time19-time18));
			// System.out.println("put in cache:: "+(time110-time19));
			return result;
	   }
	}
	
	// TODO: should be changed at validation level... 
	private boolean isBuggy(String string) {
		boolean res =  string.startsWith("http://kmi-web05") || string.startsWith("http://paoli");
		return res;
	}

	/** there was a problem with Vector.toArray in listEntitiesByType **/
    protected String[] toArray(Vector<String> v){
    	String[] results = new String[v.size()];
    	for (int i = 0; i < v.size(); i++){
    		results[i] = v.elementAt(i);
    	}
    	return results;
    }

    private Object dlock = new Object();
    private Object elock = new Object();
    private Object rlock = new Object();
    private Object llock = new Object();
      
    public void setDocumentIndexSearcher(ParallelMultiSearcher pms){
    	documentSearcher = pms;
    }
    
	protected Searcher getDocumentIndexSearcher() {
		// System.out.println("getting..."+load);
		considerReloading();
		// System.out.println("doci:: "+documentSearcher+" re:: "+reloadDocumentIndex);
		if (load && (documentSearcher == null || reloadDocumentIndex)){
			System.out.println("loading..."+load);
			synchronized(dlock) {	
				System.out.println("Getting document index...");
				documentIndexSearchers = new IndexSearcher[luceneDocumentIndexes.length];
		for (int i = 0 ; i < luceneDocumentIndexes.length; i++)
			try {
				System.out.println("Loading "+luceneDocumentIndexes[i]);
				documentIndexSearchers[i] = new IndexSearcher(luceneDocumentIndexes[i]);
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				// TODO: this is over dangerous... doesn't free mem if not close, but may 
				// cause crash of things using this index.
				if (documentSearcher!=null)
					documentSearcher.close();
				System.out.println("Loading searcher...");
				documentSearcher = new ParallelMultiSearcher(documentIndexSearchers);
			} catch (IOException e) {
				e.printStackTrace();
			}
			reloadDocumentIndex = false;
			}
		}
		return documentSearcher;
	}
	
	private long lastDateReload = 0;

    // will most probably have issues with synchronisation...
	private void considerReloading() {
		File f = new File("/data/reload.indexes");
		if (f.exists()) {
			long nd = f.lastModified();
			if (nd > lastDateReload){
				lastDateReload = nd;
				System.out.println("Detected file /data/reload.indexes... reloading");
				reloadDocumentIndex = true;
				reloadEntityIndex = true;
				reloadRelationIndex = true;
				reloadLiteralIndex = true;
			}
		}
		// long freemem = Runtime.getRuntime().maxMemory()-(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory());
		// System.out.println("FREE MEM ::"+freemem);
	}

	public void setEntityIndexSearcher(ParallelMultiSearcher s){
		entitySearcher = s;
	}
	
	protected Searcher getEntityIndexSearcher() {
		considerReloading();
		// System.out.println("entitySearcher = "+entitySearcher+" reload= "+reloadEntityIndex);
		if (load && (entitySearcher == null || reloadEntityIndex)){
			synchronized(elock) {	
				System.out.println("Getting entity index...");
		entityIndexSearchers = new IndexSearcher[luceneEntityIndexes.length];
		for (int i = 0 ; i < luceneEntityIndexes.length; i++){
			try {
				System.out.println("Loading "+luceneEntityIndexes[i]+" ("+i+"/"+luceneEntityIndexes.length+")");
				entityIndexSearchers[i] = new IndexSearcher(luceneEntityIndexes[i]);
			} catch (Exception e) {
				System.out.println("Prob....");
				e.printStackTrace();
			}
		}
		try {
			System.out.println("Loading searcher");
			entitySearcher = new ParallelMultiSearcher(entityIndexSearchers);
			System.out.println("done...");	
		} catch (IOException e) {
			e.printStackTrace();
		}
		reloadEntityIndex = false;
		}	
	}
	return entitySearcher;
	}
    
	public void setRelationIndexSearcher(ParallelMultiSearcher s){
		relationSearcher = s;
	}
	
	protected Searcher getRelationIndexSearcher() {
		considerReloading();
		if (load && (relationSearcher == null || reloadRelationIndex)){
			synchronized(rlock) {	
				System.out.println("Getting relation index...");
		relationIndexSearchers = new IndexSearcher[luceneRelationIndexes.length];
		for (int i = 0 ; i < luceneRelationIndexes.length; i++)
			try {
				System.out.println("Loading "+luceneRelationIndexes[i]);
				relationIndexSearchers[i] = new IndexSearcher(luceneRelationIndexes[i]);
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				relationSearcher = new ParallelMultiSearcher(relationIndexSearchers);
			} catch (IOException e) {
				e.printStackTrace();
			}
			reloadRelationIndex = false;
		}	
			}
		return relationSearcher;
	}
    
	public void getLiteralIndexSearcher(ParallelMultiSearcher s){
		literalSearcher = s;
	}
	
	protected Searcher getLiteralIndexSearcher() {
		considerReloading();
		if (load & (literalSearcher == null || reloadLiteralIndex)){
			synchronized(llock) {	
				
				System.out.println("Getting literal index...");
		literalIndexSearchers = new IndexSearcher[luceneLiteralIndexes.length];
		for (int i = 0 ; i < luceneLiteralIndexes.length; i++)
			try {
				System.out.println("Loading "+luceneLiteralIndexes[i]);
				literalIndexSearchers[i] = new IndexSearcher(luceneLiteralIndexes[i]);
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				literalSearcher = new ParallelMultiSearcher(literalIndexSearchers);
			} catch (IOException e) {
				e.printStackTrace();
			}
			reloadLiteralIndex = false;
		}
		}
		return literalSearcher;
	}
}
