/*
 @prefix    :   <http://nwalsh.com/rdf/cvs#> .
 @prefix  dc:   <http://purl.org/dc/elements/1.1/> .
 @prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
 
 <>  dc:title    "Service.java";
 :Revision   "$Revision: 1.1 $";
 :Author     "$Author: mathieu $";
 :date       "$Date: 2010/01/12 16:17:27 $";
 :Log        """
 $Log: OntologySearchInternal.java,v $
 Revision 1.1  2010/01/12 16:17:27  mathieu
 *** empty log message ***

 Revision 1.8  2010/01/11 12:11:13  mathieu
 *** empty log message ***

 Revision 1.7  2009/08/25 16:50:27  mathieu
 *** empty log message ***

 Revision 1.6  2009/06/12 17:24:28  mathieu
 *** empty log message ***

 Revision 1.5  2009/04/28 09:01:18  mathieu
 first attempt at filters

 Revision 1.4  2009/04/24 16:31:04  mathieu
 *** empty log message ***

 Revision 1.3  2009/02/25 18:23:12  mathieu
 *** empty log message ***

 Revision 1.2  2009/01/12 14:57:50  mathieu
 *** empty log message ***

 Revision 1.1  2008/11/12 11:45:14  mathieu
 *** empty log message ***

 Revision 1.1  2008/11/12 10:54:06  mathieu
 *** empty log message ***

 Revision 1.44  2008/10/27 14:31:36  mathieu
 *** empty log message ***

 Revision 1.43  2008/10/22 16:19:40  mathieu
 *** empty log message ***

 Revision 1.42  2008/10/02 17:49:00  davide
 *** empty log message ***

 Revision 1.41  2008/10/02 15:02:49  mathieu
 *** empty log message ***

 Revision 1.40  2008/09/30 09:38:38  mathieu
 *** empty log message ***

 Revision 1.39  2008/07/03 17:43:08  mathieu
 *** empty log message ***

 Revision 1.38  2008/07/03 17:36:09  mathieu
 *** empty log message ***

 Revision 1.37  2008/06/03 17:25:14  mathieu
 *** empty log message ***

 Revision 1.36  2008/06/02 12:04:22  mathieu
 *** empty log message ***

 Revision 1.35  2008/06/02 10:19:38  mathieu
 *** empty log message ***

 Revision 1.34  2008/06/02 10:01:21  mathieu
 *** empty log message ***

 Revision 1.33  2008/05/30 13:40:36  mathieu
 *** empty log message ***

 Revision 1.32  2008/05/30 11:05:41  mathieu
 *** empty log message ***

 Revision 1.31  2008/05/30 10:44:39  mathieu
 *** empty log message ***

 Revision 1.30  2008/05/30 10:42:46  mathieu
 *** empty log message ***

 Revision 1.29  2008/05/30 10:33:42  mathieu
 *** empty log message ***

 Revision 1.28  2008/05/30 10:26:52  mathieu
 *** empty log message ***

 Revision 1.27  2008/05/30 10:00:16  mathieu
 *** empty log message ***

 Revision 1.26  2008/05/29 14:59:59  mathieu
 *** empty log message ***

 Revision 1.25  2008/05/29 14:20:23  mathieu
 *** empty log message ***

 Revision 1.24  2008/04/15 08:39:12  mathieu
 *** empty log message ***

 Revision 1.23  2008/03/31 08:37:14  mathieu
 *** empty log message ***

 Revision 1.22  2008/03/27 15:33:13  mathieu
 *** empty log message ***

 Revision 1.19  2008/03/27 15:25:36  mathieu
 *** empty log message ***

 Revision 1.18  2008/03/27 15:23:47  mathieu
 *** empty log message ***

 Revision 1.17  2008/03/27 15:21:54  mathieu
 *** empty log message ***

 Revision 1.16  2008/03/27 15:20:36  mathieu
 *** empty log message ***

 Revision 1.15  2008/03/27 15:17:18  mathieu
 *** empty log message ***

 Revision 1.14  2008/03/27 15:15:40  mathieu
 *** empty log message ***

 Revision 1.13  2008/03/27 15:14:31  mathieu
 *** empty log message ***

 Revision 1.12  2008/03/27 14:47:40  mathieu
 *** empty log message ***

 Revision 1.11  2008/03/27 09:11:24  mathieu
 *** empty log message ***

 Revision 1.10  2008/03/27 08:54:16  mathieu
 *** empty log message ***

 Revision 1.9  2008/03/27 08:48:23  mathieu
 *** empty log message ***

 Revision 1.8  2008/02/14 10:05:30  mathieu
 bit of cleaning... removed lowLevelAPI

 Revision 1.7  2008/02/14 09:54:30  mathieu
 added import and nbStatements in OntologySearch

 Revision 1.6  2008/02/12 15:00:40  mathieu
 *** empty log message ***

 Revision 1.5  2008/02/08 18:05:58  mathieu
 *** empty log message ***

 Revision 1.4  2008/02/08 18:03:14  mathieu
 *** empty log message ***

 Revision 1.3  2008/02/08 17:57:13  mathieu
 *** empty log message ***

 Revision 1.2  2008/01/31 11:17:02  mathieu
 *** empty log message ***

 Revision 1.1  2008/01/26 09:01:34  mathieu
 re-added to cvs...

 Revision 1.1  2007/01/16 12:22:05  lg3388
 Higher level API

 """;
 :Id         "$Id: OntologySearchInternal.java,v 1.1 2010/01/12 16:17:27 mathieu Exp $" .
 */

package uk.ac.open.kmi.watson.services.internal;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searchable;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;

import uk.ac.open.kmi.watson.services.utils.LabelSplitter;
import uk.ac.open.kmi.watson.services.utils.URN;
import uk.ac.open.kmi.watson.services.SearchConf;
import uk.ac.open.kmi.watson.services.SemanticContentResult;
import uk.ac.open.kmi.watson.services.WatsonService;
import uk.ac.open.kmi.watson.services.utils.WatsonAnalyzer;

/**
 * Ontology search service for Watson.
 * @author lg3388, md99
 */
public class OntologySearchInternal extends WatsonService {

	public OntologySearchInternal() { super(); }
	public OntologySearchInternal(boolean load) { super(load); }
	
    /** To be used only internally: 
	 * same as OntologySearch.getSemanticContentByKeywordsWithRestructions, but returns the SCIDs instead of the URIs **/
    public Hits getSemanticContentIDsByKeywordsWithRestrictions(String[] keywords,
				int scopeModifier, int entityTypeModifier, int matchTechnique, int start, int inc, String[][] filters) {
		Query query = null;
		if (matchTechnique==WatsonService.EXACT_MATCH)
			query = generateLuceneQueryExactMatch(keywords, scopeModifier, entityTypeModifier, filters);
		else if (matchTechnique==WatsonService.TOKEN_MATCH)
			query = generateLuceneQueryTokenMatch(keywords, scopeModifier, entityTypeModifier, filters);
		else
			System.err.println("ERROR:: unknown match technique");
		return getSemanticContentByQuery(query, start, inc);
	}
    
    public Hits getSemanticContentIDsByKeywordsWithRestrictions(String sc, String[] keywords,
			int scopeModifier, int entityTypeModifier, int matchTechnique, int start, int inc, String[][] filters) {
	Query query = null;
	if (matchTechnique==WatsonService.EXACT_MATCH)
		query = generateLuceneQueryExactMatch(sc, keywords, scopeModifier, entityTypeModifier, filters);
	else if (matchTechnique==WatsonService.TOKEN_MATCH)
		query = generateLuceneQueryTokenMatch(sc, keywords, scopeModifier, entityTypeModifier, filters);
	else
		System.err.println("ERROR:: unknown match technique");
	return getSemanticContentByQuery(query, start, inc);
}
    
	
    public Query generateLuceneQueryTokenMatch(String[] keywords,
			int scopeModifier, int entityTypeModifier, String[][] filters){
    	String query = "";
    	boolean start = true;
		for (String k : keywords) {
			String HCk = new URN(new LabelSplitter().splitLabel(k).toLowerCase()).toString().replaceAll(":", "?");
			if (!start)
				query += " AND ";
			else
				start = false;
			query += "(" + k;
			if ((scopeModifier & LOCAL_NAME) == LOCAL_NAME) {
				if ((entityTypeModifier & CLASS) == CLASS)
						query += " OR ClslocN:" + k;
				if ((entityTypeModifier & PROPERTY) == PROPERTY)
						query += " OR PrpLocN:" + k;
				if ((entityTypeModifier & INDIVIDUAL) == INDIVIDUAL)
						query += " OR IndLocN:" + k;
			}
			if ((scopeModifier & LITERAL) == LITERAL) {
				if ((entityTypeModifier & CLASS) == CLASS)
						query += " OR ClsLit:" + k;
				if ((entityTypeModifier & PROPERTY) == PROPERTY)
						query += " OR PrpLit:" + k;
				if ((entityTypeModifier & INDIVIDUAL) == INDIVIDUAL)
						query += " OR IndLit:" + k;
			}
			if ((scopeModifier & LABEL) == LABEL) {
				if ((entityTypeModifier & CLASS) == CLASS)
						query += " OR ClsL:" + k;
				if ((entityTypeModifier & PROPERTY) == PROPERTY)
						query += " OR PrpL:" + k;
				if ((entityTypeModifier & INDIVIDUAL) == INDIVIDUAL)
						query += " OR IndL:" + k;
			}
			if ((scopeModifier & COMMENT) == COMMENT) {
				if ((entityTypeModifier & CLASS) == CLASS)
						query += " OR ClsC:" + k;
				if ((entityTypeModifier & PROPERTY) == PROPERTY)
						query += " OR PrpC:" + k;
				if ((entityTypeModifier & INDIVIDUAL) == INDIVIDUAL)
						query += " OR IndC:" + k;
			}
			query += ")";
		}	
		for (String[] fil : filters){
			if (fil[0].equals("language")){
				query += " AND lang:" +fil[1];
			}
			// doesn't work very well with "Experiment1..."
			if (fil[0].equals("user")){
				query += " AND osname:" +fil[1];
			}
		}
		Query q = null;
		try {
			q = new QueryParser("ns", new WatsonAnalyzer()).parse(query);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return q;
    }
    
    public Query generateLuceneQueryTokenMatch(String sc, String[] keywords,
			int scopeModifier, int entityTypeModifier, String[][] filters){
    	String query = "id:"+sc.replaceAll(":", "?");;
    	boolean start = false;
		for (String k : keywords) {
			String HCk = new URN(new LabelSplitter().splitLabel(k).toLowerCase()).toString().replaceAll(":", "?");
			if (!start)
				query += " AND ";
			else
				start = false;
			query += "(" + k;
			if ((scopeModifier & LOCAL_NAME) == LOCAL_NAME) {
				if ((entityTypeModifier & CLASS) == CLASS)
						query += " OR ClslocN:" + k;
				if ((entityTypeModifier & PROPERTY) == PROPERTY)
						query += " OR PrpLocN:" + k;
				if ((entityTypeModifier & INDIVIDUAL) == INDIVIDUAL)
						query += " OR IndLocN:" + k;
			}
			if ((scopeModifier & LITERAL) == LITERAL) {
				if ((entityTypeModifier & CLASS) == CLASS)
						query += " OR ClsLit:" + k;
				if ((entityTypeModifier & PROPERTY) == PROPERTY)
						query += " OR PrpLit:" + k;
				if ((entityTypeModifier & INDIVIDUAL) == INDIVIDUAL)
						query += " OR IndLit:" + k;
			}
			if ((scopeModifier & LABEL) == LABEL) {
				if ((entityTypeModifier & CLASS) == CLASS)
						query += " OR ClsL:" + k;
				if ((entityTypeModifier & PROPERTY) == PROPERTY)
						query += " OR PrpL:" + k;
				if ((entityTypeModifier & INDIVIDUAL) == INDIVIDUAL)
						query += " OR IndL:" + k;
			}
			if ((scopeModifier & COMMENT) == COMMENT) {
				if ((entityTypeModifier & CLASS) == CLASS)
						query += " OR ClsC:" + k;
				if ((entityTypeModifier & PROPERTY) == PROPERTY)
						query += " OR PrpC:" + k;
				if ((entityTypeModifier & INDIVIDUAL) == INDIVIDUAL)
						query += " OR IndC:" + k;
			}
			query += ")";
		}	
		for (String[] fil : filters){
			if (fil[0].equals("language")){
				query += " AND lang:" +fil[1];
			}
			// doesn't work very well with "Experiment1..."
			if (fil[0].equals("user")){
				query += " AND osname:" +fil[1];
			}
		}
		Query q = null;
		try {
			q = new QueryParser("ns", new WatsonAnalyzer()).parse(query);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return q;
    }
    
    public Query generateLuceneQueryExactMatch(String[] keywords,
			int scopeModifier, int entityTypeModifier, String[][] filters){
    	int matchTechnique=WatsonService.EXACT_MATCH;
    	BooleanQuery topquery = new BooleanQuery();
 		for (String k2 : keywords) {
			String k = k2.toLowerCase(); // new LabelSplitter().splitLabel(k2);
			String HCk = new URN(new LabelSplitter().splitLabel(k2).toLowerCase()).toString();
	    	BooleanQuery query = new BooleanQuery();
			if ((scopeModifier & LOCAL_NAME) == LOCAL_NAME) {
				if ((entityTypeModifier & CLASS) == CLASS)
					if ((matchTechnique & TOKEN_MATCH) == TOKEN_MATCH)
						query.add(new BooleanClause(new WildcardQuery(new Term("ClslocN", k)), BooleanClause.Occur.SHOULD));
					else if ((matchTechnique & EXACT_MATCH) == EXACT_MATCH)
						query.add(new BooleanClause(new TermQuery(new Term("HCClslocN", HCk)), BooleanClause.Occur.SHOULD));
				if ((entityTypeModifier & PROPERTY) == PROPERTY)
					if ((matchTechnique & TOKEN_MATCH) == TOKEN_MATCH)
						query.add(new BooleanClause(new WildcardQuery(new Term("PrpLocN", k)), BooleanClause.Occur.SHOULD));
					else if ((matchTechnique & EXACT_MATCH) == EXACT_MATCH)
						query.add(new BooleanClause(new TermQuery(new Term("HCPrpLocN", HCk)), BooleanClause.Occur.SHOULD));
				if ((entityTypeModifier & INDIVIDUAL) == INDIVIDUAL)
					if ((matchTechnique & TOKEN_MATCH) == TOKEN_MATCH)
						query.add(new BooleanClause(new WildcardQuery(new Term("IndLocN", k)), BooleanClause.Occur.SHOULD));
					else if ((matchTechnique & EXACT_MATCH) == EXACT_MATCH)
						query.add(new BooleanClause(new TermQuery(new Term("HCIndLocN", HCk)), BooleanClause.Occur.SHOULD));
			}
			if ((scopeModifier & LITERAL) == LITERAL) {
				if ((entityTypeModifier & CLASS) == CLASS)
					if ((matchTechnique & TOKEN_MATCH) == TOKEN_MATCH)
						query.add(new BooleanClause(new WildcardQuery(new Term("ClsLit", k)), BooleanClause.Occur.SHOULD));
					else if ((matchTechnique & EXACT_MATCH) == EXACT_MATCH)
						query.add(new BooleanClause(new TermQuery(new Term("HCClsLit", HCk)), BooleanClause.Occur.SHOULD));
				if ((entityTypeModifier & PROPERTY) == PROPERTY)
					if ((matchTechnique & TOKEN_MATCH) == TOKEN_MATCH)
						query.add(new BooleanClause(new WildcardQuery(new Term("PrpLit", k)), BooleanClause.Occur.SHOULD));
					else if ((matchTechnique & EXACT_MATCH) == EXACT_MATCH)
						query.add(new BooleanClause(new TermQuery(new Term("HCPrpLit", HCk)), BooleanClause.Occur.SHOULD));
				if ((entityTypeModifier & INDIVIDUAL) == INDIVIDUAL)
					if ((matchTechnique & TOKEN_MATCH) == TOKEN_MATCH)
						query.add(new BooleanClause(new WildcardQuery(new Term("IndLit", k)), BooleanClause.Occur.SHOULD));
					else if ((matchTechnique & EXACT_MATCH) == EXACT_MATCH)
						query.add(new BooleanClause(new TermQuery(new Term("HCIndLit", HCk)), BooleanClause.Occur.SHOULD));
			}
			if ((scopeModifier & LABEL) == LABEL) {
				if ((entityTypeModifier & CLASS) == CLASS)
					if ((matchTechnique & TOKEN_MATCH) == TOKEN_MATCH)
						query.add(new BooleanClause(new WildcardQuery(new Term("ClsL", k)), BooleanClause.Occur.SHOULD));
					else if ((matchTechnique & EXACT_MATCH) == EXACT_MATCH)
						query.add(new BooleanClause(new TermQuery(new Term("HCClsL", HCk)), BooleanClause.Occur.SHOULD));
				if ((entityTypeModifier & PROPERTY) == PROPERTY)
					if ((matchTechnique & TOKEN_MATCH) == TOKEN_MATCH)
						query.add(new BooleanClause(new WildcardQuery(new Term("PrpL", k)), BooleanClause.Occur.SHOULD));
					else if ((matchTechnique & EXACT_MATCH) == EXACT_MATCH)
						query.add(new BooleanClause(new TermQuery(new Term("HCPrpL", HCk)), BooleanClause.Occur.SHOULD));
				if ((entityTypeModifier & INDIVIDUAL) == INDIVIDUAL)
					if ((matchTechnique & TOKEN_MATCH) == TOKEN_MATCH)
						query.add(new BooleanClause(new WildcardQuery(new Term("IndL", k)), BooleanClause.Occur.SHOULD));
					else if ((matchTechnique & EXACT_MATCH) == EXACT_MATCH)
						query.add(new BooleanClause(new TermQuery(new Term("HCIndL", HCk)), BooleanClause.Occur.SHOULD));
			}
			if ((scopeModifier & COMMENT) == COMMENT) {
				if ((entityTypeModifier & CLASS) == CLASS)
					if ((matchTechnique & TOKEN_MATCH) == TOKEN_MATCH)
						query.add(new BooleanClause(new WildcardQuery(new Term("ClsC", k)), BooleanClause.Occur.SHOULD));
					else if ((matchTechnique & EXACT_MATCH) == EXACT_MATCH)
						query.add(new BooleanClause(new TermQuery(new Term("HCClsC", HCk)), BooleanClause.Occur.SHOULD));
				if ((entityTypeModifier & PROPERTY) == PROPERTY)
					if ((matchTechnique & TOKEN_MATCH) == TOKEN_MATCH)
						query.add(new BooleanClause(new WildcardQuery(new Term("PrpC", k)), BooleanClause.Occur.SHOULD));
					else if ((matchTechnique & EXACT_MATCH) == EXACT_MATCH)
						query.add(new BooleanClause(new TermQuery(new Term("HCPrpC", HCk)), BooleanClause.Occur.SHOULD));
				if ((entityTypeModifier & INDIVIDUAL) == INDIVIDUAL)
					if ((matchTechnique & TOKEN_MATCH) == TOKEN_MATCH)
						query.add(new BooleanClause(new WildcardQuery(new Term("IndC", k)), BooleanClause.Occur.SHOULD));
					else if ((matchTechnique & EXACT_MATCH) == EXACT_MATCH)
						query.add(new BooleanClause(new TermQuery(new Term("HCIndC", HCk)), BooleanClause.Occur.SHOULD));
			}
			topquery.add(new BooleanClause(query, BooleanClause.Occur.MUST));
		}	
 		for (String[] fil : filters){
			if (fil[0].equals("language")){
				topquery.add(new BooleanClause(new TermQuery(new Term("lang", fil[1])), BooleanClause.Occur.MUST));
			}
			if (fil[0].equals("user")){
				topquery.add(new BooleanClause(new TermQuery(new Term("osname", fil[1])), BooleanClause.Occur.MUST));
			}
 		}
		return topquery;
    }
    
    public Query generateLuceneQueryExactMatch(String sc, String[] keywords,
			int scopeModifier, int entityTypeModifier, String[][] filters){
    	int matchTechnique=WatsonService.EXACT_MATCH;
    	BooleanQuery topquery = new BooleanQuery();
    	topquery.add(new WildcardQuery(new Term("id", sc)), BooleanClause.Occur.MUST);
 		for (String k2 : keywords) {
			String k = k2.toLowerCase(); // new LabelSplitter().splitLabel(k2);
			String HCk = new URN(new LabelSplitter().splitLabel(k2).toLowerCase()).toString();
	    	BooleanQuery query = new BooleanQuery();
			if ((scopeModifier & LOCAL_NAME) == LOCAL_NAME) {
				if ((entityTypeModifier & CLASS) == CLASS)
					if ((matchTechnique & TOKEN_MATCH) == TOKEN_MATCH)
						query.add(new BooleanClause(new WildcardQuery(new Term("ClslocN", k)), BooleanClause.Occur.SHOULD));
					else if ((matchTechnique & EXACT_MATCH) == EXACT_MATCH)
						query.add(new BooleanClause(new TermQuery(new Term("HCClslocN", HCk)), BooleanClause.Occur.SHOULD));
				if ((entityTypeModifier & PROPERTY) == PROPERTY)
					if ((matchTechnique & TOKEN_MATCH) == TOKEN_MATCH)
						query.add(new BooleanClause(new WildcardQuery(new Term("PrpLocN", k)), BooleanClause.Occur.SHOULD));
					else if ((matchTechnique & EXACT_MATCH) == EXACT_MATCH)
						query.add(new BooleanClause(new TermQuery(new Term("HCPrpLocN", HCk)), BooleanClause.Occur.SHOULD));
				if ((entityTypeModifier & INDIVIDUAL) == INDIVIDUAL)
					if ((matchTechnique & TOKEN_MATCH) == TOKEN_MATCH)
						query.add(new BooleanClause(new WildcardQuery(new Term("IndLocN", k)), BooleanClause.Occur.SHOULD));
					else if ((matchTechnique & EXACT_MATCH) == EXACT_MATCH)
						query.add(new BooleanClause(new TermQuery(new Term("HCIndLocN", HCk)), BooleanClause.Occur.SHOULD));
			}
			if ((scopeModifier & LITERAL) == LITERAL) {
				if ((entityTypeModifier & CLASS) == CLASS)
					if ((matchTechnique & TOKEN_MATCH) == TOKEN_MATCH)
						query.add(new BooleanClause(new WildcardQuery(new Term("ClsLit", k)), BooleanClause.Occur.SHOULD));
					else if ((matchTechnique & EXACT_MATCH) == EXACT_MATCH)
						query.add(new BooleanClause(new TermQuery(new Term("HCClsLit", HCk)), BooleanClause.Occur.SHOULD));
				if ((entityTypeModifier & PROPERTY) == PROPERTY)
					if ((matchTechnique & TOKEN_MATCH) == TOKEN_MATCH)
						query.add(new BooleanClause(new WildcardQuery(new Term("PrpLit", k)), BooleanClause.Occur.SHOULD));
					else if ((matchTechnique & EXACT_MATCH) == EXACT_MATCH)
						query.add(new BooleanClause(new TermQuery(new Term("HCPrpLit", HCk)), BooleanClause.Occur.SHOULD));
				if ((entityTypeModifier & INDIVIDUAL) == INDIVIDUAL)
					if ((matchTechnique & TOKEN_MATCH) == TOKEN_MATCH)
						query.add(new BooleanClause(new WildcardQuery(new Term("IndLit", k)), BooleanClause.Occur.SHOULD));
					else if ((matchTechnique & EXACT_MATCH) == EXACT_MATCH)
						query.add(new BooleanClause(new TermQuery(new Term("HCIndLit", HCk)), BooleanClause.Occur.SHOULD));
			}
			if ((scopeModifier & LABEL) == LABEL) {
				if ((entityTypeModifier & CLASS) == CLASS)
					if ((matchTechnique & TOKEN_MATCH) == TOKEN_MATCH)
						query.add(new BooleanClause(new WildcardQuery(new Term("ClsL", k)), BooleanClause.Occur.SHOULD));
					else if ((matchTechnique & EXACT_MATCH) == EXACT_MATCH)
						query.add(new BooleanClause(new TermQuery(new Term("HCClsL", HCk)), BooleanClause.Occur.SHOULD));
				if ((entityTypeModifier & PROPERTY) == PROPERTY)
					if ((matchTechnique & TOKEN_MATCH) == TOKEN_MATCH)
						query.add(new BooleanClause(new WildcardQuery(new Term("PrpL", k)), BooleanClause.Occur.SHOULD));
					else if ((matchTechnique & EXACT_MATCH) == EXACT_MATCH)
						query.add(new BooleanClause(new TermQuery(new Term("HCPrpL", HCk)), BooleanClause.Occur.SHOULD));
				if ((entityTypeModifier & INDIVIDUAL) == INDIVIDUAL)
					if ((matchTechnique & TOKEN_MATCH) == TOKEN_MATCH)
						query.add(new BooleanClause(new WildcardQuery(new Term("IndL", k)), BooleanClause.Occur.SHOULD));
					else if ((matchTechnique & EXACT_MATCH) == EXACT_MATCH)
						query.add(new BooleanClause(new TermQuery(new Term("HCIndL", HCk)), BooleanClause.Occur.SHOULD));
			}
			if ((scopeModifier & COMMENT) == COMMENT) {
				if ((entityTypeModifier & CLASS) == CLASS)
					if ((matchTechnique & TOKEN_MATCH) == TOKEN_MATCH)
						query.add(new BooleanClause(new WildcardQuery(new Term("ClsC", k)), BooleanClause.Occur.SHOULD));
					else if ((matchTechnique & EXACT_MATCH) == EXACT_MATCH)
						query.add(new BooleanClause(new TermQuery(new Term("HCClsC", HCk)), BooleanClause.Occur.SHOULD));
				if ((entityTypeModifier & PROPERTY) == PROPERTY)
					if ((matchTechnique & TOKEN_MATCH) == TOKEN_MATCH)
						query.add(new BooleanClause(new WildcardQuery(new Term("PrpC", k)), BooleanClause.Occur.SHOULD));
					else if ((matchTechnique & EXACT_MATCH) == EXACT_MATCH)
						query.add(new BooleanClause(new TermQuery(new Term("HCPrpC", HCk)), BooleanClause.Occur.SHOULD));
				if ((entityTypeModifier & INDIVIDUAL) == INDIVIDUAL)
					if ((matchTechnique & TOKEN_MATCH) == TOKEN_MATCH)
						query.add(new BooleanClause(new WildcardQuery(new Term("IndC", k)), BooleanClause.Occur.SHOULD));
					else if ((matchTechnique & EXACT_MATCH) == EXACT_MATCH)
						query.add(new BooleanClause(new TermQuery(new Term("HCIndC", HCk)), BooleanClause.Occur.SHOULD));
			}
			topquery.add(new BooleanClause(query, BooleanClause.Occur.MUST));
		}	
 		for (String[] fil : filters){
			if (fil[0].equals("language")){
				topquery.add(new BooleanClause(new TermQuery(new Term("lang", fil[1])), BooleanClause.Occur.MUST));
			}
			if (fil[0].equals("user")){
				topquery.add(new BooleanClause(new TermQuery(new Term("osname", fil[1])), BooleanClause.Occur.MUST));
			}
 		}
		return topquery;
    }
    
  
    
	/** not meant to be deployed - returns an array of SCIDs with the actual number of results at the end...**/
	private Hits getSemanticContentByQuery(Query query, int start, int inc) {
		long time1 = System.currentTimeMillis();
		try {
			Searcher indexSearcher = getDocumentIndexSearcher();
			Hits results = getSemanticContentByQuery(query, indexSearcher);
			return results;
		/*	long timep = System.currentTimeMillis();			
			System.out.println("Query time = "+(timep-time1));
			Vector<String> result = new Vector<String>();
			Vector<Double> scores = new Vector<Double>();
			for (int i = 0; i < results.length(); i++) {
				if (i >= start){
					if (inc > 0 && i-start >= inc) break;
					String SCID = results.doc(i).get("id");
					if (SCID != null){
						double score = calculateScore(results.doc(i));
						result.add(SCID);
						// System.out.println("SCID1:: "+SCID);
						scores.add(score);
						// System.out.println(results.score(i)+" - "+score);
					}
				}
			}
			// temporary ranking mechanism.
			long timer = System.currentTimeMillis();
			System.out.println("Process time = "+(timer-timep));
			result = rankSC(result, scores);
			System.out.println("Ranking time = "+(System.currentTimeMillis()-timer));
			System.out.println("Total time = "+(System.currentTimeMillis()-time1));
			result.add(""+results.length());
			return result; */
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null; 
	}
	
	

	private double calculateScore(Document document) {
		try {
		long size1 = 0;
		// For normalization... shouldn't be hard coded but this is temporary
		long maxsize = 65317258;
		int nbletters1 = 0;
		int maxletter = 10;
		double score1 = 0;
		size1 = Long.parseLong(document.get("size"));
		 // System.out.println("Size : "+size1);
		String dle1 = document.get("DL");
		if (dle1 != null) {
			dle1 = dle1.replace("(D)", "D");
			dle1 = dle1.replace("S", "ALCNR");
			// System.out.println("DLE : "+dle1);
			nbletters1 = dle1.length();
		}
		if (nbletters1 == 0) nbletters1 = 1; 
		// nb classes and props... // ratio instances relations... // ration classes hierar
		score1 = Math.log(((double)size1)/maxsize);
		score1 += Math.log(((double)nbletters1)/maxletter);	
		return score1;
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return 0;
	}


	private class SCComparator implements Comparator<String> {
		
		private Vector<Double> scores;
		private Vector<String> scids;
			
		public SCComparator(Vector<String> scids, Vector<Double> scores){
			this.scores = scores;
			this.scids = scids;
		}
		
		/** Objects are documentIDs (Strings...) **/
		public int compare(String scid1, String scid2) {
			// TODO: should do better, we should know the index...
			double score1 = scores.elementAt(scids.indexOf(scid1));
			double score2 = scores.elementAt(scids.indexOf(scid2));			
			// System.out.println(score1+" vs "+ score2);
			if (score1 == score2) return 0;
			if (score1 < score2) return 1;
			if (score2 < score1) return -1;
			return 0;
		}
	}
	
	public Vector<String> rankSC(Vector<String> ds, Vector<Double> scores) {
		SCComparator c = new SCComparator(ds, scores);
		Collections.sort(ds, c);
		return ds;
	}
	
	public Hits getSemanticContentByQuery(Query query, Searcher indexSearcher) {
		try {
		Analyzer language = new WatsonAnalyzer();
		// ClsLocl is the default field... need a big union field??
		//QueryParser qp = new QueryParser("uri", language);
		//org.apache.lucene.search.Query q = qp.parse(query);
		Hits results = indexSearcher.search(query);
		return results;
		}catch (Exception e){e.printStackTrace(); return null;}
	}


	public Vector<String> getSCIDSByKeywordsWithRestrictions(String[] keywords, int scopeModifier, int entityTypeModifier, int matchTechnique, int st, int limit, String[][] filters) {
		Hits res = getSemanticContentIDsByKeywordsWithRestrictions(keywords, scopeModifier, entityTypeModifier, matchTechnique, st, limit, filters);
		Vector<String> ret = new Vector<String>(res.length());
		if (limit == -1 || limit == 0) limit = res.length();
		for (int i = st; (i < st+limit) && i < res.length(); i++)
			try {
				ret.add(res.doc(i).get("id"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		return ret;
	}
	
	public double getScoreByKeywordsWithRestrictions(String sc, String[] keywords, int scopeModifier, int entityTypeModifier, int matchTechnique, int st, int limit, String[][] filters) {
		// maybe should check first if it exist in the results (add the sc in the query)... 
		Hits res = getSemanticContentIDsByKeywordsWithRestrictions(sc, keywords, scopeModifier, entityTypeModifier, matchTechnique, st, limit, filters);
		if (res.length()==0) return -1.0;
		try {
			return res.score(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1.0;
	}
	
	public int getNumberOfHits(String[] keywords, int scopeModifier, int entityTypeModifier, int matchTechnique, int st, int limit, String[][] filters) {
		Hits res = getSemanticContentIDsByKeywordsWithRestrictions(keywords, scopeModifier, entityTypeModifier, matchTechnique, st, limit, filters);
		return res.length();
	}

	public void includeSCInfo(SemanticContentResult scr, Document sc_doc, int scInfo) {
	//	System.out.println("WHAT???!! "+scr+" "+sc_doc+" "+scInfo);
		// BUG find out what happen here!
		if (sc_doc == null) return;
		if ((scInfo & SearchConf.SC_LANGUAGES_INFO) == SearchConf.SC_LANGUAGES_INFO) {
			String result = sc_doc.get("lang");
			StringTokenizer st = new StringTokenizer(result);
			String[] langs = new String[st.countTokens()];
			int i = 0;
			while (st.hasMoreTokens()){
				langs[i++] = st.nextToken();
			}
			scr.setLanguages(langs);
		}
		if ((scInfo & SearchConf.SC_SIZE_INFO ) == SearchConf.SC_SIZE_INFO) {
			try {
			long result = Long.parseLong(sc_doc.get("size"));
			scr.setSize(result);
			}catch(Exception e){ e.printStackTrace(); scr.setSize(-1);}
		}
		if ((scInfo & SearchConf.SC_DLEXPR_INFO) == SearchConf.SC_DLEXPR_INFO) {
			String result = sc_doc.get("DL");
			scr.setDLExpressivness(result);
		}
		if ((scInfo & SearchConf.SC_LOCATION_INFO) == SearchConf.SC_LOCATION_INFO) {
			String provs = sc_doc.get("provs");
			StringTokenizer st = new StringTokenizer(provs);
			String[] prs = new String[st.countTokens()];
			int i = 0;
			while (st.hasMoreTokens()){
				prs[i++] = st.nextToken();
			}
			scr.setLocations(prs);
		}
		if ((scInfo & SearchConf.SC_NBCLASSES_INFO) == SearchConf.SC_NBCLASSES_INFO) {
			// String uri = getValidID(sc_doc.get("id"));
			// scr.setNBClasses(os.listClasses(uri).length);
		}
		if ((scInfo & SearchConf.SC_NBPROPS_INFO) == SearchConf.SC_NBPROPS_INFO) {
			// String uri = getValidID(sc_doc.get("id"));
			// scr.setNBProperties(os.listProperties(uri).length);
		}
	    if ((scInfo & SearchConf.SC_NBINDIS_INFO) == SearchConf.SC_NBINDIS_INFO) {
			// String uri = getValidID(sc_doc.get("id"));
			// scr.setNBIndividuals(os.listIndividuals(uri).length);
		}	
	    if ((scInfo & SearchConf.SC_NBSTATS_INFO) == SearchConf.SC_NBSTATS_INFO) {
	    	try {
				int result = Integer.parseInt(sc_doc.get("nbStat"));
				scr.setNumberOfStatements(result);
	    	}catch(Exception e){ e.printStackTrace(); scr.setSize(-1);}
	    }
	    if ((scInfo & SearchConf.SC_LABELS_INFO) == SearchConf.SC_LABELS_INFO) {
	    	String SCID = sc_doc.get("id");
	    	String ontoURI = getValidID(SCID);
	    	try {
	    		Searcher indexSearcher = getLiteralIndexSearcher();
				BooleanQuery q = new BooleanQuery();
				q.add(new TermQuery(new Term("scid", SCID)), BooleanClause.Occur.MUST);
				// TODO: can be different than the Watson given URI... should change 
				q.add(new TermQuery(new Term("subj", ontoURI.trim())), BooleanClause.Occur.MUST);
			    q.add(new TermQuery(new Term("pred", "http://www.w3.org/2000/01/rdf-schema#label")), BooleanClause.Occur.MUST);
				Hits results = indexSearcher.search(q);
				String[] toReturn = new String[results.length()];
				for (int i = 0; i < results.length(); i++) {
					Document d = results.doc(i);
					toReturn[i] = d.get("obj");
				}
				scr.setLabels(toReturn);
	    	} catch(Exception e){
					e.printStackTrace();
				}
	    	
	    }
	    if ((scInfo & SearchConf.SC_COMMENTS_INFO) == SearchConf.SC_COMMENTS_INFO) {
	    	String SCID = sc_doc.get("id");
	    	String ontoURI = getValidID(SCID);
	    	try {
	    		Searcher indexSearcher = getLiteralIndexSearcher();
				BooleanQuery q = new BooleanQuery();
				q.add(new TermQuery(new Term("scid", SCID)), BooleanClause.Occur.MUST);
				// TODO: can be different than the Watson given URI... should change 
				q.add(new TermQuery(new Term("subj", ontoURI.trim())), BooleanClause.Occur.MUST);
			    q.add(new TermQuery(new Term("pred", "http://www.w3.org/2000/01/rdf-schema#comment")), BooleanClause.Occur.MUST);
				Hits results = indexSearcher.search(q);
				String[] toReturn = new String[results.length()];
				for (int i = 0; i < results.length(); i++) {
					Document d = results.doc(i);
					toReturn[i] = d.get("obj");
				}
				scr.setComments(toReturn);
	    	} catch(Exception e){
					e.printStackTrace();
				}
	    	
	    }
	    if ((scInfo & SearchConf.SC_IMPORTS_INFO) == SearchConf.SC_IMPORTS_INFO) {
	    	String provs = sc_doc.get("imports");
			StringTokenizer st = new StringTokenizer(provs);
			String[] prs = new String[st.countTokens()];
			int i = 0;
			while (st.hasMoreTokens()){
				prs[i++] = st.nextToken();
			}
			scr.setImports(prs);
	    }
	    if ((scInfo & SearchConf.SC_IMPORTEDBY_INFO) == SearchConf.SC_IMPORTEDBY_INFO) {
	    	// TODO: should be a better way to do that...
//	    	 get possible URLs and URIs of ontoURI
	    	String provs = sc_doc.get("provs");
			StringTokenizer st = new StringTokenizer(provs);
			String[] URLs = new String[st.countTokens()];
			int i = 0;
			while (st.hasMoreTokens()){
				URLs[i++] = st.nextToken();
			}			Searcher indexSearcher = getDocumentIndexSearcher();
			BooleanQuery bq = new BooleanQuery();
			String ontoUri = sc_doc.get("URI");
			if (ontoUri != null) {
				TermQuery URIquery = new TermQuery(new Term("import", ontoUri));
				bq.add(URIquery, BooleanClause.Occur.SHOULD);
			}
			for (String url : URLs) {
				if (!url.equals(ontoUri)) {
					TermQuery URLquery = new TermQuery(new Term("import",
							url));
					bq.add(URLquery, BooleanClause.Occur.SHOULD);
				}
			}
			try {
				Hits results = indexSearcher.search(bq);
				String[] result = new String[results.length()];
				for (int j = 0; j < results.length(); j++) {
					Document ri = results.doc(j);
					String SCID = ri.get("id");
					String value = getValidID(SCID);
					result[j] = value;
				}
				scr.setImportedBy(result);
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }	    
	}
	
	public static void main(String[] args){
		String k2 = "VOYAGE";
		String sk2 = new LabelSplitter().splitLabel(k2).toLowerCase();
		String HCk = new URN(new LabelSplitter().splitLabel(k2).toLowerCase()).toString();
		System.out.println(k2);
		System.out.println(sk2);
		System.out.println(HCk);

	}
	
	
}
