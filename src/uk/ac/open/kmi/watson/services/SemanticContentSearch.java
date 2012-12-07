/*
 @prefix    :   <http://nwalsh.com/rdf/cvs#> .
 @prefix  dc:   <http://purl.org/dc/elements/1.1/> .
 @prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
 
 <>  dc:title    "Service.java";
 :Revision   "$Revision: 1.1 $";
 :Author     "$Author: mathieu $";
 :date       "$Date: 2010/01/12 16:17:25 $";
 :Log        """
 $Log: SemanticContentSearch.java,v $
 Revision 1.1  2010/01/12 16:17:25  mathieu
 *** empty log message ***

 Revision 1.9  2010/01/11 12:11:13  mathieu
 *** empty log message ***

 Revision 1.8  2009/08/25 16:50:27  mathieu
 *** empty log message ***

 Revision 1.7  2009/06/12 17:24:27  mathieu
 *** empty log message ***

 Revision 1.6  2009/04/28 09:01:18  mathieu
 first attempt at filters

 Revision 1.5  2009/04/24 16:31:04  mathieu
 *** empty log message ***

 Revision 1.4  2009/03/26 18:15:38  mathieu
 *** empty log message ***

 Revision 1.3  2009/01/12 14:57:50  mathieu
 *** empty log message ***

 Revision 1.2  2008/11/20 11:12:39  mathieu
 *** empty log message ***

 Revision 1.1  2008/11/12 11:45:11  mathieu
 *** empty log message ***

 Revision 1.1  2008/11/12 10:54:05  mathieu
 *** empty log message ***

 Revision 1.30  2008/10/27 14:10:55  mathieu
 *** empty log message ***

 Revision 1.29  2008/10/22 16:19:40  mathieu
 *** empty log message ***

 Revision 1.28  2008/10/03 16:12:26  mathieu
 *** empty log message ***

 Revision 1.27  2008/10/02 17:10:21  mathieu
 *** empty log message ***

 Revision 1.26  2008/10/02 16:19:53  mathieu
 *** empty log message ***

 Revision 1.25  2008/09/30 09:38:38  mathieu
 *** empty log message ***

 Revision 1.24  2008/06/10 08:04:44  mathieu
 *** empty log message ***

 Revision 1.23  2008/06/09 11:56:35  mathieu
 eliminate unused WildcardQueries

 Revision 1.22  2008/06/09 11:04:13  mathieu
 *** empty log message ***

 Revision 1.21  2008/06/09 08:45:36  mathieu
 *** empty log message ***

 Revision 1.20  2008/06/03 15:01:59  mathieu
 *** empty log message ***

 Revision 1.19  2008/06/03 11:12:13  mathieu
 *** empty log message ***

 Revision 1.18  2008/06/02 11:03:53  mathieu
 *** empty log message ***

 Revision 1.17  2008/06/02 11:02:58  mathieu
 *** empty log message ***

 Revision 1.16  2008/06/02 10:13:49  mathieu
 *** empty log message ***

 Revision 1.15  2008/06/02 10:07:29  mathieu
 *** empty log message ***

 Revision 1.14  2008/06/02 10:01:21  mathieu
 *** empty log message ***

 Revision 1.13  2008/05/30 15:28:20  mathieu
 *** empty log message ***

 Revision 1.12  2008/05/30 13:40:36  mathieu
 *** empty log message ***

 Revision 1.11  2008/05/29 14:20:23  mathieu
 *** empty log message ***

 Revision 1.10  2008/03/31 08:37:13  mathieu
 *** empty log message ***

 Revision 1.9  2008/03/06 17:12:36  mathieu
 add getImportedBy

 Revision 1.8  2008/02/20 11:46:35  mathieu
 reintroduction of the OMV stuff

 Revision 1.7  2008/02/14 10:12:24  mathieu
 Cleaned some of the TODOs

 Revision 1.6  2008/02/14 10:05:29  mathieu
 bit of cleaning... removed lowLevelAPI

 Revision 1.5  2008/02/14 09:54:29  mathieu
 added import and nbStatements in OntologySearch

 Revision 1.4  2008/02/08 17:57:13  mathieu
 *** empty log message ***

 Revision 1.3  2008/01/28 18:33:24  mathieu
 *** empty log message ***

 Revision 1.2  2008/01/28 18:22:59  mathieu
 *** empty log message ***

 Revision 1.1  2008/01/26 09:00:14  mathieu
 Lots of updates

 Revision 1.1  2007/01/16 12:22:05  lg3388
 Higher level API

 """;
 :Id         "$Id: SemanticContentSearch.java,v 1.1 2010/01/12 16:17:25 mathieu Exp $" .
 */

package uk.ac.open.kmi.watson.services;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.StringTokenizer;
import java.util.Vector;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TermQuery;

import uk.ac.open.kmi.watson.services.combination.BestCoverageQueryStrategy;
import uk.ac.open.kmi.watson.services.internal.EntitySearchInternal;
import uk.ac.open.kmi.watson.services.internal.OntologySearchInternal;
import uk.ac.open.kmi.watson.services.omv.OMVFileFactory;
import uk.ac.open.kmi.watson.services.revyu.RevyuEndpoint;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * Service for searching and inspecting semantic documents.
 * 
 * @author lg3388, md99
 */
public class SemanticContentSearch extends WatsonService {

	private OntologySearchInternal osi;
	private EntitySearchInternal esi;

	  private static Cache ScoreCache;

	
	
	/** create an instance of the service. * */
	public SemanticContentSearch() {this(true); }
	
	public SemanticContentSearch(boolean load) {
		super(load); 
		osi = new OntologySearchInternal(false);
		esi = new EntitySearchInternal(false);
		 if (ScoreCache == null) { 
		    	CacheManager cm = CacheManager.create();
		    	cm.addCache("ScoreCache");
		    	ScoreCache = cm.getCache("ScoreCache");
		    }
	}

	/** returns a list of semantic content results matching the parameters of the search 
	 * @param keywords set of keywords to match
	 * @param conf configuration object to restrict the search
	 * @return a list of semantic content results
	 */
	public SemanticContentResult[] getSemanticContentByKeywords(String[] keywords, SearchConf conf){
			Vector<SemanticContentResult> results = new Vector<SemanticContentResult>();
			try {
			Vector<String> ontos = osi.getSCIDSByKeywordsWithRestrictions(keywords, conf.getScope(), conf.getEntities(), conf.getMatch(), conf.getStart(), conf.getInc(), conf.getFilters());
			// System.out.println("Got "+ontos.size()+" ontos");
			for (int i = 0; i < ontos.size(); i++){
				Document sc_doc = null;
				String scid = ontos.elementAt(i);
				Searcher indexSearcher = getDocumentIndexSearcher();
				Searcher indexSearcherE = getEntityIndexSearcher();
				// can be obtimized, osi should return the Hits and we handle it here...
				org.apache.lucene.search.Query q;
				Hits theDoc = null;
				q = new TermQuery(new Term("id", scid));
				try{theDoc = indexSearcher.search(q);}
				catch(Exception e) {e.printStackTrace();}
				if (theDoc!= null && theDoc.length() >= 1) 
					sc_doc = theDoc.doc(0);
				SemanticContentResult scr = new SemanticContentResult(getValidID(scid));
				// System.out.print(".");
				osi.includeSCInfo(scr, sc_doc, conf.getSCInfo());
				//System.out.println(".");
				if ((conf.getSCInfo() & conf.SC_ENTITIES_INFO) == conf.SC_ENTITIES_INFO){
					System.out.print("x");
					Vector<EntityResult> Ents = new Vector<EntityResult>();
					for (String keyword : keywords){
						Hits entHits = null;
						if (conf.getMatch()==TOKEN_MATCH)
							entHits = esi.getLuceneEntitiesTokenMatch(indexSearcherE, scid, keyword, conf.getScope(), conf.getEntities());
						else if (conf.getMatch()==EXACT_MATCH)
							entHits = esi.getLuceneEntitiesExactMatch(indexSearcherE, scid, keyword, conf.getScope(), conf.getEntities());
						else System.out.println("ERROR:: unknown match technique");
						for (int j = 0; j < entHits.length(); j++){
							Document ent_doc = entHits.doc(j);
							String URI = ent_doc.get("ns")+ent_doc.get("locN");
							EntityResult er = new EntityResult(URI);
							Ents.add(er);
							esi.includeEntInfo(er, ent_doc, conf.getEntitiesInfo());
						}
					}
					scr.setEntityResultList(Ents);
				}
				results.add(scr);
			}
			}catch(Exception e){ e.printStackTrace(); return null;}
			SemanticContentResult[] scResults = new SemanticContentResult[results.size()];
			for (int i =0; i < results.size(); i++) scResults[i] = results.elementAt(i);
			return scResults; 
		}
	
	public double scoreOfSCforQuery(String scuri, String[] keywords, SearchConf conf){
		String kws = "";
		for (String s : keywords){
			kws += s;
		}
		Element element = ScoreCache.get(scuri+kws);
		if (element!=null){
			return (Double)element.getObjectValue();
		}
		try {
		String sc = getLuceneDocument(scuri);
		double score = osi.getScoreByKeywordsWithRestrictions(sc, keywords, conf.getScope(), conf.getEntities(), conf.getMatch(), conf.getStart(), conf.getInc(), conf.getFilters());
		Element el = new Element(scuri+kws, new Double(score));
		ScoreCache.put(el);
		return score;
		}catch(Exception e){
			e.printStackTrace();
		}
		return -1.0;
	}
	
	
	/** return the number of results mathcing the search parameters **/
	public int getNumberOfResults(String[] keywords, SearchConf conf){
			Vector<SemanticContentResult> results = new Vector<SemanticContentResult>();
			int res = osi.getNumberOfHits(keywords, conf.getScope(), conf.getEntities(), conf.getMatch(), conf.getStart(), conf.getInc(), conf.getFilters());
			return res;
		}

	/**
	 * Semantic document search function that returns documents that not
	 * necessaraly match all the keywords. Calculate the set of documents that
	 * best cover the set of keywords (match a maximal subset of the keywords.
	 * This function is relatively fast for less than 10 keywords, but can takes
	 * very long (several days!) above that treshold.
	 * 
	 * @param keywords
	 *            the list of keywords to search
	 * @param scopeModifier
	 *            where to search in entity, e.g.
	 *            WatsonService.LOCAL_NAME+WatsonService.LABEL
	 * @param entityTypeModifier
	 *            in which entity to search, e.g.
	 *            WatsonService.CLASS+WatsonService.INDIVIDUAL
	 * @param matchTechnique
	 *            the match technique, e.g. WatsonService.EXACT_MATCH
	 * @return a set of valid ontology indentifiers
	 */
	public SemanticContentResult[] getSemanticContentWithBestCoverage(String[] keywords,
			SearchConf conf) {
		// TODO: should also include info...
		BestCoverageQueryStrategy bcqs = new BestCoverageQueryStrategy(
				keywords, conf.getScope(), conf.getEntities(), conf.getMatch());
		Vector<String> SCIDs = bcqs.getSemanticContents();
		SemanticContentResult[] URIs = new SemanticContentResult[SCIDs.size()];
		for (int i = 0; i < SCIDs.size(); i++){
			URIs[i] = new SemanticContentResult(getValidID(SCIDs.elementAt(i)));
		}
		return URIs;
	}

	/**
	 * This function returns a list of URI corresponding to all the documents
	 * collected by Watson
	 * 
	 * @param start
	 *            the number of the first document to retrieve
	 * @param stop
	 *            the number of the last document to retrieve
	 * @return a list of URI of semantic content (semantic document, ontologies)
	 *         of start-stop if stop is smaller than the total number of
	 *         documents.
	 */
	public String[] listSemanticContents(int start, int stop) {
		String[] result = null;
		try {
			Searcher ir = getDocumentIndexSearcher();
			System.out.println("XXX found " + ir.maxDoc() + "docs, retrieving "
					+ start + "-" + stop);
			if (stop < ir.maxDoc())
				result = new String[stop - start + 1];
			else if (start < ir.maxDoc())
				result = new String[ir.maxDoc() - start];
			else
				result = new String[0];
			for (int i = start; i < ir.maxDoc() && i <= stop; i++) {
				String did = ir.doc(i).get("id");
				String uri = getValidID(did);
				result[i - start] = uri;
				System.out.print(".");
			}
			System.out.println();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Returns the set of URLs of the given semantic document
	 * 
	 * @param ontoURI
	 *            the URI identifying the considered semantic document
	 * @return a set of URLs where the ontology can be found
	 */
	public String[] getSemanticContentLocation(String ontoURI) {
		try {
			String SCID = getLuceneDocument(ontoURI);
			if (SCID == null)
				return null;
			Searcher indexSearcher = getDocumentIndexSearcher();
			org.apache.lucene.search.Query query;
			query = new TermQuery(new Term("id", SCID));
			Hits results = indexSearcher.search(query);
			if (results.length() >= 1) {
				String provs = results.doc(0).get("provs");
				StringTokenizer st = new StringTokenizer(provs);
				String[] prs = new String[st.countTokens()];
				int i = 0;
				while (st.hasMoreTokens()) {
					prs[i++] = st.nextToken();
				}
				return prs;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		/*
		 * Factory f = new Factory(); f.beginTransaction();
		 * uk.ac.open.kmi.watson.db.Document d = f.getDocument(SCID); Iterator
		 * it = d.provenance(); Vector<String> results = new Vector<String>();
		 * while (it.hasNext()) results.add((String) it.next());
		 * f.commitTransaction(); return toArray(results);
		 */
	}

	/**
	 * return the location of the cached file
	 * 
	 * @param ontoURI
	 *            the URI identifying the considered ontology
	 * @return the URLs where the ontology can be found in the Watson cache
	 */
	public String getCacheLocation(String ontoURI) {
		String SCID = getLuceneDocument(ontoURI);
		if (SCID == null)
			return null;
		try {
		Searcher indexSearcher = getDocumentIndexSearcher();
		org.apache.lucene.search.Query query;
		query = new TermQuery(new Term("id", SCID));
		Hits results = indexSearcher.search(query);
		if (results.length() >= 1) {
			String result = results.doc(0).get("cache");
			if (result != null) return result;
			}
		} catch(Exception e){ e.printStackTrace(); }
		// urn:sha1:fc75445068463f261e4da336a8b1c99d75e84ff0
		// 01234567890123456789012345678901234567890123456789
		// ---------^-^--^---^----^---------^
		// 9,11,14,18,23,33
		return "http://kmi-web05.open.ac.uk:81/cache/"
				+ SCID.substring(9, 10) + "/"
				+ SCID.substring(10, 13) + "/"
				+ SCID.substring(13, 17) + "/"
				+ SCID.substring(17, 22) + "/"
				+ SCID.substring(22, 32) + "/" + SCID.substring(32);
	}

	/**
	 * Returns the size in Bytes of the ontology file
	 * 
	 * @param ontoURI
	 *            the URI identifying the considered ontology
	 * @return the size in bytes of the file
	 */
	public long getSizeInBytes(String ontoURI) {
		try {
			String SCID = getLuceneDocument(ontoURI);
			if (SCID == null)
				return -1;
			Searcher indexSearcher = getDocumentIndexSearcher();
			org.apache.lucene.search.Query query;
			query = new TermQuery(new Term("id", SCID));
			Hits results = indexSearcher.search(query);
			if (results.length() >= 1) {
				long result = Long.parseLong(results.doc(0).get("size"));
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Returns the set of languages employed by the semantic document
	 * 
	 * @param ontoURI
	 *            the URI identifying the considered semantic document
	 * @return a set of String corresponding to the employed langauges
	 */
	public String[] getSemanticContentLanguages(String ontoURI) {
		/*
		 * String SCID = getLuceneDocument(ontoURI); if (SCID == null) return
		 * null; Factory f = new Factory(); f.beginTransaction();
		 * uk.ac.open.kmi.watson.db.SemanticContent d = f.getDocument(SCID)
		 * .semanticContent(); Iterator it = d.languages(); Vector<String>
		 * results = new Vector<String>(); while (it.hasNext())
		 * results.add((String) it.next()); f.commitTransaction();
		 */
		try {
			String SCID = getLuceneDocument(ontoURI);
			if (SCID == null)
				return null;
			Searcher indexSearcher = getDocumentIndexSearcher();
			org.apache.lucene.search.Query query;
			query = new TermQuery(new Term("id", SCID));
			Hits results = indexSearcher.search(query);
			if (results.length() >= 1) {
				String result = results.doc(0).get("lang");
				StringTokenizer st = new StringTokenizer(result);
				String[] langs = new String[st.countTokens()];
				int i = 0;
				while (st.hasMoreTokens()) {
					langs[i++] = st.nextToken();
				}
				return langs;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns the OWL Species of the ontology (removed)
	 * 
	 * @param ontoURI
	 *            the URI identifying the considered ontology
	 * @return a String for the OWL Specy
	 */
	/** private String getOWLSpecies(String ontoURI) {
		try {
			String SCID = getLuceneDocument(ontoURI);
			if (SCID == null)
				return null;
			Searcher indexSearcher = getDocumentIndexSearcher();
			org.apache.lucene.search.Query query;
			query = new WildcardQuery(new Term("id", SCID));
			Hits results = indexSearcher.search(query);
			if (results.length() == 1) {
				String result = results.doc(0).get("Owl");
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	} **/

	/**
	 * Returns the DL Expressivness of the ontology
	 * 
	 * @param ontoURI
	 *            the URI identifying the considered ontology
	 * @return a String for Dl expressivmess
	 */
	public String getDLExpressivness(String ontoURI) {
		try {
			String SCID = getLuceneDocument(ontoURI);
			if (SCID == null)
				return null;
			Searcher indexSearcher = getDocumentIndexSearcher();
			org.apache.lucene.search.Query query;
			query = new TermQuery(new Term("id", SCID));
			Hits results = indexSearcher.search(query);
			if (results.length() >= 1) {
				String result = results.doc(0).get("DL");
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns the list of class URIs of the given ontology
	 * 
	 * @param ontoURI
	 *            the identificator of the considered ontology
	 * @return a set of class URIs from ontoURI
	 */
	public String[] listClasses(String ontoURI) {
		return listEntitiesByType(ontoURI, DB_CLASS);
	}

	/**
	 * Returns the list of property URIs of the given ontology
	 * 
	 * @param ontoURI
	 *            the identificator of the considered ontology
	 * @return a set of property URIs from ontoURI
	 */
	public String[] listProperties(String ontoURI) {
		return listEntitiesByType(ontoURI, DB_PROPERTY);
	}

	/**
	 * Returns the list of individual URIs of the given semantic document
	 * 
	 * @param ontoURI
	 *            the identificator of the considered ontology
	 * @return a set of individual URIs from ontoURI
	 */
	public String[] listIndividuals(String ontoURI) {
		return listEntitiesByType(ontoURI, DB_INDIVIDUAL);
	}

	/** lists the entities of a particular type in ontoURI * */
	private String[] listEntitiesByType(String ontoURI, int type) {
		try {
			String SCID = getLuceneDocument(ontoURI);
			if (SCID == null)
				return null;
			Searcher indexSearcher = getEntityIndexSearcher();
			BooleanQuery q = new BooleanQuery();
			q.add(new TermQuery(new Term("sc", SCID)),
					BooleanClause.Occur.MUST);
			q.add(new TermQuery(new Term("type", new Integer(type)
					.toString())), BooleanClause.Occur.MUST);
			Hits results = indexSearcher.search(q);
			Vector<String> result = new Vector<String>();
			for (int i = 0; i < results.length(); i++) {
				String uri = results.doc(i).get("ns")
						+ results.doc(i).get("locN");
				result.add(uri);
			}
			return toArray(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		/*
		 * String DID = getLuceneDocument(ontoURI); if (DID == null) return
		 * null; Vector<String> results = new Vector<String>(); Factory f =
		 * new Factory(); f.beginTransaction(); Iterator it =
		 * f.getDocument(DID).semanticContent().listEntities();
		 * while(it.hasNext()){ Entity e = (Entity)it.next(); if
		 * (e.type()==type) results.add(e.namespace()+e.localName()); }
		 * f.commitTransaction(); return (String[]) toArray(results);
		 */
	}

	/**
	 * Returns the set of URIs of the imported semantic document
	 * 
	 * @param ontoURI
	 *            the URI identifying the considered semantic document
	 * @return a set of URIs of imported semantic documents
	 */
	public String[] getImports(String ontoUri) {
		try {
			String SCID = getLuceneDocument(ontoUri);
			if (SCID == null)
				return null;
			Searcher indexSearcher = getDocumentIndexSearcher();
			org.apache.lucene.search.Query query;
			query = new TermQuery(new Term("id", SCID));
			Hits results = indexSearcher.search(query);
			if (results.length() >= 1) {
				String provs = results.doc(0).get("imports");
				StringTokenizer st = new StringTokenizer(provs);
				String[] prs = new String[st.countTokens()];
				int i = 0;
				while (st.hasMoreTokens()) {
					prs[i++] = st.nextToken();
				}
				return prs;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns the set of URIs of the semantic document importing ontoURI
	 * 
	 * @param ontoURI
	 *            the URI identifying the considered semantic document
	 * @return a set of URIs of semantic documents importing ontoURI
	 */
	public String[] getImportedBy(String ontoUri) {
		// get possible URLs and URIs of ontoURI
		String[] URLs = getSemanticContentLocation(ontoUri);
		Searcher indexSearcher = getDocumentIndexSearcher();
		BooleanQuery bq = new BooleanQuery();
		TermQuery URIquery = new TermQuery(new Term("import", ontoUri));
		bq.add(URIquery, BooleanClause.Occur.SHOULD);
		if (URLs!= null) for (String url : URLs) {
			if (url !=null && !url.equals(ontoUri)) {
				TermQuery URLquery = new TermQuery(new Term("import",
						url));
				bq.add(URLquery, BooleanClause.Occur.SHOULD);
			}
		}
		try {
			Hits results = indexSearcher.search(bq);
			String[] result = new String[results.length()];
			for (int i = 0; i < results.length(); i++) {
				Document ri = results.doc(i);
				String SCID = ri.get("id");
				String value = getValidID(SCID);
				result[i] = value;
			}
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns the size in Bytes of the ontology file
	 * 
	 * @param ontoURI
	 *            the URI identifying the considered ontology
	 * @return the size in bytes of the file
	 */
	public long getNumberOfStatement(String ontoUri) {
		try {
			String SCID = getLuceneDocument(ontoUri);
			if (SCID == null)
				return -1;
			Searcher indexSearcher = getDocumentIndexSearcher();
			org.apache.lucene.search.Query query;
			query = new TermQuery(new Term("id", SCID));
			Hits results = indexSearcher.search(query);
			if (results.length() >= 1) {
				long result = Long.parseLong(results.doc(0).get("nbStat"));
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Executes and returns the results (a XML String) of a SPARQL query on the
	 * given semantic document.
	 * 
	 * @param ontoURI
	 *            the URI of the semantic document on which we want the query to
	 *            be executed
	 * @param queryString
	 *            the string containing the SPARQL query
	 * @return the XML serialization of the result
	 */
	public String executeSPARQLQuery(String ontoURI, String queryString) {
		System.out.println("Querying: " + ontoURI);
		// String SCID = getLuceneDocument(ontoURI);
		// if (SCID == null) return null;
		// System.out.println("Querying: "+SCID);
		// Model model = JenaUtils.getModel(SCID); // get the model
		Model model = ModelFactory.createDefaultModel();
		String cache = getCacheLocation(ontoURI);
		boolean cb = false;
		String cacheBase = "";
		if (cache.startsWith("http://cupboard")){
			 cacheBase = "http://cupboard.open.ac.uk:8081/cupboard/cache";
			 cb = true;
		}
		if (cache.startsWith("htt://kmi-web06")){
			 cacheBase = "http://kmi-web06.open.ac.uk:8081/cupboard/cache";
			cb = true;
		}
		if (cb){
			String path = cache.substring(cacheBase.length());
			cache = "file://"+path;
		}
		model.read(cache);
		System.out.println("Query: " + queryString);
		Query query = QueryFactory.create(queryString); // create the query
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		ResultSet results = qe.execSelect(); // execute the query and get the
												// results
		String toReturn = ResultSetFormatter.asXMLString(results); // format in
																	// an XML
																	// String
		qe.close(); // IMPORTANT : close the query
		System.out.println(toReturn);
		return toReturn;
	}

	/** the object of querying the revyu.com SPARQL endpoint * */
	private RevyuEndpoint revyuEndpoint = new RevyuEndpoint();

	/**
	 * Returns the number of reviews entered about this ontology in Revyu.com
	 * 
	 * @param ontoURI
	 *            the URI of the considered ontology
	 */
	public int getNumberOfReviews(String ontoURI) {
		return revyuEndpoint.getNumberOfReviews(ontoURI);
	}

	/**
	 * Returns the average rating of this ontology in Revyu.com
	 * 
	 * @param ontoURI
	 *            the URI of the considered ontology
	 * @return the rounded average rating from revyu [1-5], or 0 if no reviews
	 */
	public int getAverageRating(String ontoURI) {
		return revyuEndpoint.getAverageRating(ontoURI);
	}

	/**
	 * Returns the URL of the considered ontology on Revyu.com
	 * 
	 * @param ontoURI
	 *            the URI of the considered ontology
	 * @return the URL of the considered ontology on revyu.com, or null if it
	 *         does not exist
	 */
	public String getRevyuURL(String ontoURI) {
		return "http://revyu.com/" + revyuEndpoint.getRevyuURI(ontoURI);
	}

	/**
	 * Returns the location of the OMV file describing the semantic document
	 * 
	 * @param the
	 *            URI of the considered ontology
	 * @result the URL of the OMV file describing this ontology
	 */
	public String getOMVFileLocation(String ontoURI) {
		return new OMVFileFactory().getOMV(ontoURI);
	}

	/**
	 * Submit a URI to the crawler of Watson.
	  * TODO: re-implement with new crawler function in kmi-web11...
	 * @param uri the URI to be crawled by Watson.
	 */
	public void submitURI(String uri) {
		try {
			URL u = new URL(
					"http://paoli.open.ac.uk:8081/addUri/UserSubmittedUri.jsp?uri="
							+ uri.trim());
			URLConnection uc = u.openConnection();
			System.out.println(u.getPort());
			DataInputStream dis = new DataInputStream(uc.getInputStream());
			String inputLine;
			while ((inputLine = dis.readLine()) != null) {
				System.out.println(inputLine);
			}
			dis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the labels of the ontology element defined in this documents, if specified.
	 * @param ontoURI the URI of the ontology to consider
	 * @return a set of labels for the ontology or an empty array (or null if error).
	 */
	public String[] getLabels(String ontoURI){
		long time = System.currentTimeMillis();
    	String SCID = getLuceneDocument(ontoURI);
    	if (SCID == null) {System.out.println("Not found doc "+ontoURI); return null;}
    	try {
    		Searcher indexSearcher = getLiteralIndexSearcher();
			BooleanQuery q = new BooleanQuery();
			q.add(new TermQuery(new Term("scid", SCID)), BooleanClause.Occur.MUST);
			// TODO: can be different than the Watson given URI... should change 
			q.add(new TermQuery(new Term("subj", ontoURI.trim())), BooleanClause.Occur.MUST);
		    q.add(new TermQuery(new Term("pred", "http://www.w3.org/2000/01/rdf-schema#label")), BooleanClause.Occur.MUST);
			Hits results = indexSearcher.search(q);
			System.out.println("Lits Query time ("+results.length()+"):"+(System.currentTimeMillis()-time));
			String[] toReturn = new String[results.length()];
			for (int i = 0; i < results.length(); i++) {
				Document d = results.doc(i);
				toReturn[i] = d.get("obj");
			}
			System.out.println("getSCLabels took: "+(System.currentTimeMillis()-time));
			return toReturn;
    	} catch(Exception e){
				e.printStackTrace();
			}
     	return null;	
     }
	

	/**
	 * Returns the comments of the ontology element defined in this documents, if specified.
	 * @param ontoURI the URI of the ontology to consider
	 * @return a set of comments for the ontology or an empty array (or null if error).
	 */
	public String[] getComments(String ontoURI){
		long time = System.currentTimeMillis();
    	String SCID = getLuceneDocument(ontoURI);
    	if (SCID == null) {System.out.println("Not found doc "+ontoURI); return null;}
    	try {
    		Searcher indexSearcher = getLiteralIndexSearcher();
			BooleanQuery q = new BooleanQuery();
			q.add(new TermQuery(new Term("scid", SCID)), BooleanClause.Occur.MUST);
			// TODO: can be different than the Watson given URI... should change 
			q.add(new TermQuery(new Term("subj", ontoURI.trim())), BooleanClause.Occur.MUST);
		    q.add(new TermQuery(new Term("pred", "http://www.w3.org/2000/01/rdf-schema#comment")), BooleanClause.Occur.MUST);
			Hits results = indexSearcher.search(q);
			System.out.println("Lits Query time ("+results.length()+"):"+(System.currentTimeMillis()-time));
			String[] toReturn = new String[results.length()];
			for (int i = 0; i < results.length(); i++) {
				Document d = results.doc(i);
				toReturn[i] = d.get("obj");
			}
			System.out.println("getSCComments took: "+(System.currentTimeMillis()-time));
			return toReturn;
    	} catch(Exception e){
				e.printStackTrace();
			}
     	return null;
	}
	
	public static void main (String[] args){
		SemanticContentSearch scs = new SemanticContentSearch();
		SearchConf conf = new SearchConf();
		conf.setEntities(SearchConf.CLASS);
		conf.setScope(SearchConf.LOCAL_NAME);
		conf.setMatch(SearchConf.EXACT_MATCH);
		SemanticContentResult[] res = scs.getSemanticContentByKeywords(args, conf);
		for (SemanticContentResult s : res) System.out.println(s.getURI());
		System.out.println("total:: "+res.length);
	}
	
	
}
