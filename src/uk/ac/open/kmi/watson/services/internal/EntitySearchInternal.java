package uk.ac.open.kmi.watson.services.internal;

import java.io.IOException;
import java.util.Vector;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ParallelMultiSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;

import uk.ac.open.kmi.watson.services.EntityResult;
import uk.ac.open.kmi.watson.services.SearchConf;
import uk.ac.open.kmi.watson.services.WatsonService;
import uk.ac.open.kmi.watson.services.utils.LabelSplitter;
import uk.ac.open.kmi.watson.services.utils.URN;
import uk.ac.open.kmi.watson.services.utils.UriUtil;
import uk.ac.open.kmi.watson.services.utils.WatsonAnalyzer;

public class EntitySearchInternal extends WatsonService {
	
    private static final long TIME_LIMIT_WARNING = 20;

    public EntitySearchInternal() { super(); }
	public EntitySearchInternal(boolean load) { super(load); }
    
	public String[] getEntitiesByKeywordWithRestriction(String SCID, String keyword, int scopeModifier, int entityModifier, int matchTechnique) {
		try {
			Searcher indexSearcher = getEntityIndexSearcher();
			Hits results = null;
			if (matchTechnique==TOKEN_MATCH)
				 results = getLuceneEntitiesTokenMatch(indexSearcher, SCID, keyword, scopeModifier, entityModifier);
			else if (matchTechnique==EXACT_MATCH)
				 results = getLuceneEntitiesExactMatch(indexSearcher, SCID, keyword, scopeModifier, entityModifier);	
			else System.out.println("ERROR:: unknown match technique");
			long time = System.currentTimeMillis();
			Vector<String> result = new Vector<String>();
			for (int i = 0; i < results.length(); i++) {
				if (results.doc(i).get("locN")!=null){ // problem with disapearing locN...
					String uri = results.doc(i).get("ns")+results.doc(i).get("locN");
					result.add(uri); 
				}
			}
			long time2 = (System.currentTimeMillis()-time);
			if (time2>=TIME_LIMIT_WARNING)
				System.out.println("Warning:: Ents Process Time "+SCID+" :: "+time2);			
			return toArray(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
    } 
    
	public String[][] getAnyEntityByKeyword(String kw, int scopeModifier, int entityModifier, int matchTechnique, int start, int increment) {
		try {
			Searcher indexSearcher = getEntityIndexSearcher();
			Hits results = null;
			if (matchTechnique==TOKEN_MATCH)
				 results = getLuceneEntitiesTokenMatch(indexSearcher, null, kw, scopeModifier, entityModifier);
			else if (matchTechnique==EXACT_MATCH)
				 results = getLuceneEntitiesExactMatch(indexSearcher, null, kw, scopeModifier, entityModifier);	
			else System.out.println("ERROR:: unknown match technique");
			long time = System.currentTimeMillis();
			Vector<String[]> result = new Vector<String[]>();
			for (int i = 0; i < results.length(); i++) {
				if (i >= start){
					if (increment > 0 && i-start >= increment) break;
					if (results.doc(i).get("locN")!=null){ 
						String[] toAdd = new String[5];
						toAdd[0] = results.doc(i).get("ns")+results.doc(i).get("locN");
						String SCID = results.doc(i).get("sc");
						if (SCID != null){
							toAdd[1] = SCID;
							result.add(toAdd); 
						}
						toAdd[2] = ""+results.score(i);
						toAdd[3] = results.doc(i).get("type");
						if (toAdd[3].equals("101")) toAdd[3]="Class";
						if (toAdd[3].equals("102")) toAdd[3]="Property";
						if (toAdd[3].equals("103")) toAdd[3]="Individual";
					}
				}
			}
			long time2 = (System.currentTimeMillis()-time);
			if (time2>=TIME_LIMIT_WARNING)
				System.out.println("Warning:: Ents Process Time "+kw+" :: "+time2);			
			return toArray(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String[][] toArray(Vector<String[]> v) {
    	String[][] res = new String[v.size()][];
    	for (int i = 0; i < v.size(); i++)
    		res[i] = v.elementAt(i);
    	return res;
	}

	public Hits getLuceneEntitiesExactMatch(Searcher indexSearcher, String SCID, String keyword, int scopeModifier, int entityModifier) {
		// System.out.println("   Start Ent Exact Search");
		long time = System.currentTimeMillis();
		int matchTechnique = WatsonService.EXACT_MATCH;
		BooleanQuery q = new BooleanQuery();
		if (SCID!=null){
			q.add(new TermQuery(new Term("sc", SCID)), BooleanClause.Occur.MUST);
		}
		BooleanQuery qscope = new BooleanQuery();
		String k = keyword.toLowerCase();
		String HCk = new URN(new LabelSplitter().splitLabel(keyword).toLowerCase()).toString();
		if ((scopeModifier & LOCAL_NAME) == LOCAL_NAME) {
			if ((matchTechnique & EXACT_MATCH) == EXACT_MATCH)
				qscope.add(new TermQuery(new Term("HClocN", HCk)), BooleanClause.Occur.SHOULD);
			else qscope.add(new WildcardQuery(new Term("locN", k)), BooleanClause.Occur.SHOULD);	
		}
		if ((scopeModifier & LITERAL) == LITERAL) {
			if ((matchTechnique & EXACT_MATCH) == EXACT_MATCH)
				qscope.add(new TermQuery(new Term("HClit", HCk)), BooleanClause.Occur.SHOULD);
			else qscope.add(new WildcardQuery(new Term("lit", k)), BooleanClause.Occur.SHOULD);
		}
		if ((scopeModifier & LABEL) == LABEL) {
			if ((matchTechnique & EXACT_MATCH) == EXACT_MATCH)
				qscope.add(new TermQuery(new Term("HChttp://www.w3.org/2000/01/rdf-schema#label", HCk)), BooleanClause.Occur.SHOULD);
			else qscope.add(new WildcardQuery(new Term("http://www.w3.org/2000/01/rdf-schema#label", k)), BooleanClause.Occur.SHOULD);
		}
		if ((scopeModifier & COMMENT) == COMMENT) {
			if ((matchTechnique & EXACT_MATCH) == EXACT_MATCH)
				qscope.add(new TermQuery(new Term("HChttp://www.w3.org/2000/01/rdf-schema#comment", HCk)), BooleanClause.Occur.SHOULD);
			else qscope.add(new WildcardQuery(new Term("http://www.w3.org/2000/01/rdf-schema#comment", k)), BooleanClause.Occur.SHOULD);
		} 
		q.add(qscope, BooleanClause.Occur.MUST);
		if ((entityModifier & CLASS) == CLASS || (entityModifier & PROPERTY) == PROPERTY ||
				(entityModifier & INDIVIDUAL) == INDIVIDUAL){
		   BooleanQuery qent = new BooleanQuery();
			if ((entityModifier & CLASS) == CLASS){
				qent.add(new WildcardQuery(new Term("type", "101")), BooleanClause.Occur.SHOULD);
			}
			if ((entityModifier & PROPERTY) == PROPERTY){
				qent.add(new WildcardQuery(new Term("type", "102")), BooleanClause.Occur.SHOULD);
			}
			if ((entityModifier & INDIVIDUAL) == INDIVIDUAL){
				qent.add(new WildcardQuery(new Term("type", "103")), BooleanClause.Occur.SHOULD);
			}
		    q.add(qent, BooleanClause.Occur.MUST);
		}
		try {
			long time2 = System.currentTimeMillis();
			Hits results = indexSearcher.search(q);
			long time3 = System.currentTimeMillis();
			// System.out.println("      Create Query: "+(time2-time));
			// System.out.println("      Search: "+(time3-time2));			
			return results;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
    } 
	
	public Hits getLuceneEntitiesTokenMatch(Searcher indexSearcher, String SCID, String keyword, int scopeModifier, int entityModifier) {
		// System.out.println("   Start Ent Token Search");
		long time = System.currentTimeMillis();
		keyword = keyword.trim().replaceAll(" ", "-");
		BooleanQuery q = new BooleanQuery();
		if (SCID!=null){
			q.add(new TermQuery(new Term("sc", SCID)), BooleanClause.Occur.MUST);
		}
		String qscope = "";
		boolean already = false;
		String k = keyword.toLowerCase();
		if ((scopeModifier & LOCAL_NAME) == LOCAL_NAME) {
			qscope += "locN:"+k;
			already = true;
		}
		if ((scopeModifier & LITERAL) == LITERAL) {
			if (already) qscope += " OR ";
			qscope += "lit:"+k;
			already = true;
		}
		if ((scopeModifier & LABEL) == LABEL) {
			if (already) qscope += " OR ";
			qscope += "http\\://www.w3.org/2000/01/rdf-schema#label:"+k;
			already = true;
		}
		if ((scopeModifier & COMMENT) == COMMENT) {
			if (already) qscope += " OR ";
			qscope += "http\\://www.w3.org/2000/01/rdf-schema#comment:"+k;
			already = true;
		} 
		Query qscope_q = null;
		try {
			qscope_q = new QueryParser("ns", new WatsonAnalyzer()).parse(qscope);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		q.add(qscope_q, BooleanClause.Occur.MUST);
		if ((entityModifier & CLASS) == CLASS || (entityModifier & PROPERTY) == PROPERTY ||
				(entityModifier & INDIVIDUAL) == INDIVIDUAL){
		   BooleanQuery qent = new BooleanQuery();
			if ((entityModifier & CLASS) == CLASS){
				qent.add(new WildcardQuery(new Term("type", "101")), BooleanClause.Occur.SHOULD);
			}
			if ((entityModifier & PROPERTY) == PROPERTY){
				qent.add(new WildcardQuery(new Term("type", "102")), BooleanClause.Occur.SHOULD);
			}
			if ((entityModifier & INDIVIDUAL) == INDIVIDUAL){
				qent.add(new WildcardQuery(new Term("type", "103")), BooleanClause.Occur.SHOULD);
			}
		    q.add(qent, BooleanClause.Occur.MUST);
		}
		try {
			long time2 = System.currentTimeMillis();
			Hits results = indexSearcher.search(q);
			/* for (int i = 0; i < results.length(); i++){
				System.out.println(results.doc(i).get("ns")+results.doc(i).get("locN")+" :: "+results.score(i));
				Explanation exp = indexSearcher.explain(q, results.id(i));
				System.out.println(exp);
			} for test purposes only!!! */
			long time3 = System.currentTimeMillis();
			// System.out.println("      Create Query: "+(time2-time));
			// System.out.println("      Search: "+(time3-time2));	
			return results;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
    } 
    
    /** Not meant to be deployed 
     * return a list of entity URIs mathcing the lucene query **/
    private String[] getEntitiesByQuery(String query, int limit){
    	try {
			Searcher indexSearcher = getEntityIndexSearcher();
			Analyzer language = new WatsonAnalyzer();
			QueryParser qp = new QueryParser("sc", language);
			org.apache.lucene.search.Query q = qp.parse(query);
			Hits results = indexSearcher.search(q);
			Vector<String> result = new Vector<String>();
			for (int i = 0; i < results.length(); i++) {
				String uri = results.doc(i).get("ns")+results.doc(i).get("locN");
				// String uri = results.doc(i).get("sc");
				result.add(uri);
			}
			return toArray(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
    }
    
    public String[][] getLiteralsFor(String SCID, String entityURI, Searcher indexSearcher){
    	try {
    		long time1 = System.currentTimeMillis();
    		BooleanQuery q = new BooleanQuery();
			// q.add(new TermQuery(new Term("scid", SCID)), BooleanClause.Occur.MUST);
			q.add(new TermQuery(new Term("subj", entityURI)), BooleanClause.Occur.MUST);
			Hits results = indexSearcher.search(q);
    		long time2 = System.currentTimeMillis();
			// System.out.println("Lits Query time :"+(System.currentTimeMillis()-time));
			String[][] toReturn = new String[results.length()][];
			Vector<String[]> res = new Vector<String[]>(results.length());
			for (int i = 0; i < results.length(); i++) {
				Document d = results.doc(i);
				if (d.get("scid").equals(SCID)){
				String[] lit = new String[4];
				lit[0] = d.get("pred");
				lit[1] = UriUtil.splitNamespace(d.get("pred"))[1];
				lit[2] = d.get("obj");
				lit[3] = d.get("lang");
				res.add(lit);
				}
				toReturn = toArray(res);
			}
    		long time3 = System.currentTimeMillis();
    		// System.out.print("GLF Searching "+(time2-time1));
    		// System.out.println("  Processing "+(time3-time2));
			return toReturn;
    	} catch(Exception e){
				e.printStackTrace();
			}
     	return null;
 
    }
    
    
    public Vector<String[]> getRelationsFrom(String SCID, String entityURI){
    	try{
    //     System.out.println("Get relation from "+SCID+" "+entityURI);
    		long time1 = System.currentTimeMillis();	
    	  Vector<String[]> res = new Vector<String[]>();
    	  Searcher indexSearcher = getRelationIndexSearcher();
    	  BooleanQuery q = new BooleanQuery();
		  // q.add(new TermQuery(new Term("scid", SCID)), BooleanClause.Occur.MUST);
		  q.add(new TermQuery(new Term("subj", entityURI)), BooleanClause.Occur.MUST);
		  Hits results = indexSearcher.search(q);
          long time2 = System.currentTimeMillis();	
		  for (int i = 0; i < results.length(); i++){
			  Document doc = results.doc(i);
				if (doc.get("scid").equals(SCID)){
			  String pred = doc.get("pred");
			  String obj  = doc.get("obj");
			  String[] rel = new String[3];
			  rel[0] = pred;
			  rel[1] = UriUtil.splitNamespace(pred)[1].trim();
			  rel[2] = obj;
			  res.add(rel);
				}
		  }
		  long time3 = System.currentTimeMillis();
	  		// System.out.print("GRF Searching "+(time2-time1));
    		// System.out.println("  processing "+(time3-time2));
		  return res;
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
    	return null;
    	/* Vector<String[]> result = new Vector<String[]>();
    	Factory f = new Factory();
    	f.beginTransaction();
    	SemanticContent sc = f.getDocument(SCID).semanticContent();
    	Iterator it = sc.listEntityRelations();
    	while (it.hasNext()){
    		EntityRelation er = (EntityRelation)it.next();
    		String subj = er.subject();
    		if (subj.equals(entityURI)){
    			String predicate = er.predicate();
    			String[] res = new String[3];
    			res[0] = predicate;
    			res[1] = UriUtil.splitNamespace(predicate)[1].trim();
    			res[2] = er.object();
    			result.add(res);
    		}
    	}
    	f.commitTransaction();
    	return result; */
    }
    
    
    public String[] getLabels(String SCID, String entityURI, Searcher indexSearcher){
    	long time = System.currentTimeMillis();
    	String[][] lits = getLiteralsFor(SCID, entityURI, indexSearcher);
    	Vector<String> res = new Vector<String>();
    	for (String[] l : lits){
    		if (l[0].equals("http://www.w3.org/2000/01/rdf-schema#label")){
    			res.add(l[2]);
    		}
    	}
    	// System.out.println("get label:: "+(System.currentTimeMillis()-time));
    	return toArray(res);
    	/* try {
    		BooleanQuery q = new BooleanQuery();
			q.add(new TermQuery(new Term("scid", SCID)), BooleanClause.Occur.MUST);
			q.add(new TermQuery(new Term("subj", entityURI.trim())), BooleanClause.Occur.MUST);
		    q.add(new TermQuery(new Term("pred", "http://www.w3.org/2000/01/rdf-schema#label")), BooleanClause.Occur.MUST);
			Hits results = indexSearcher.search(q);
			// System.out.println("Lits Query time ("+results.length()+"):"+(System.currentTimeMillis()-time));
			String[] toReturn = new String[results.length()];
			for (int i = 0; i < results.length(); i++) {
				Document d = results.doc(i);
				toReturn[i] = d.get("obj");
			}
			return toReturn;
    	} catch(Exception e){
				e.printStackTrace();
			}
     	return null; */
    }
    
    public Hits searchStructuredQuery(String s, String p, String o, SearchConf c){
    	// Testing illegal queries
    	boolean vars = s.trim().equals("?");
    	boolean varp = p.trim().equals("?");
    	boolean varo = o.trim().equals("?");
    	int count =0;
    	if (vars) count++;
    	if (varp) count++;
    	if (varo) count++;
    	if (count!=1) return null;
    	String[][] matchs;
    	String[][] matchp;
    	String[][] matcho;
    	BooleanQuery bq = new BooleanQuery();
    	if (!vars) {
    		matchs = getAnyEntityByKeyword(s, c.getScope(), c.getScope(), c.getMatch(), c.getStart(), c.getInc());
    		// System.out.println("Found "+matchs.length+" subj for "+s);
    		BooleanQuery bq2 = new BooleanQuery();
    		TermQuery tq = new TermQuery(new Term("subj", s));
    		bq2.add(new BooleanClause(tq, BooleanClause.Occur.SHOULD));
    		for (String[] ent : matchs){
    	 		TermQuery tq2 = new TermQuery(new Term("subj", ent[0]));
        		bq2.add(new BooleanClause(tq2, BooleanClause.Occur.SHOULD)); 			
    		}
    		bq.add(bq2, BooleanClause.Occur.MUST);
    	}
    	if (!varp) {
    		matchp = getAnyEntityByKeyword(p, c.getScope(), c.PROPERTY, c.getMatch(), c.getStart(), c.getInc());
    		// System.out.println("Found "+matchp.length+" pred for "+p);
    		BooleanQuery bq2 = new BooleanQuery();
    		TermQuery tq = new TermQuery(new Term("pred", p));
    		bq2.add(new BooleanClause(tq, BooleanClause.Occur.SHOULD));
    		for (String[] ent : matchp){
    	 		TermQuery tq2 = new TermQuery(new Term("pred", ent[0]));
        		bq2.add(new BooleanClause(tq2, BooleanClause.Occur.SHOULD)); 			
    		}
    		bq.add(bq2, BooleanClause.Occur.MUST);
    	}
    	if (!varo) {
    		// can also be a literal...
    		matcho = getAnyEntityByKeyword(o, c.getScope(), c.getScope(), c.getMatch(), c.getStart(), c.getInc());
    		// System.out.println("Found "+matcho.length+" obj for "+o);
    		BooleanQuery bq2 = new BooleanQuery();
    		TermQuery tq = new TermQuery(new Term("obj", o));
    		bq2.add(new BooleanClause(tq, BooleanClause.Occur.SHOULD));
    		for (String[] ent : matcho){
    	 		TermQuery tq2 = new TermQuery(new Term("obj", ent[0]));
        		bq2.add(new BooleanClause(tq2, BooleanClause.Occur.SHOULD)); 			
    		}
    		bq.add(bq2, BooleanClause.Occur.MUST);
    	}
    	Searcher is = getRelationIndexSearcher();
    	try {
    		// System.out.println("Running "+bq);
			Hits result = is.search(bq);
			// System.out.println("found "+result.length()+" triples");
			return result;
    	} catch (IOException e) {
			e.printStackTrace();
		}
    	return null;
    }
    
    
    /* TODO: A cache would make sense here... could reuse things when asked for several relations to...
     */
	public Vector<String[]> getRelationsTo(String SCID, String entityURI, Searcher indexSearcher){
		try{
			// System.out.println("Get relationsTo "+SCID+" "+entityURI);
			long time1 = System.currentTimeMillis();
			Vector<String[]> res = new Vector<String[]>();
    	  // Searcher indexSearcher = getRelationIndexSearcher();
    	  BooleanQuery q = new BooleanQuery();
		  q.add(new TermQuery(new Term("scid", SCID)), BooleanClause.Occur.MUST);
		  q.add(new TermQuery(new Term("obj", entityURI)), BooleanClause.Occur.MUST);
		  Hits results = indexSearcher.search(q);	
		  long time2 = System.currentTimeMillis();
		  for (int i = 0; i < results.length(); i++){
			  Document doc = results.doc(i);
				if (doc.get("scid").equals(SCID)){
			  String pred = doc.get("pred");
			  String obj  = doc.get("subj");
			  String[] rel = new String[3];
			  rel[0] = pred;
			  rel[1] = UriUtil.splitNamespace(pred)[1].trim();
			  rel[2] = obj;
			  res.add(rel);
				}
		  }
			long time3 = System.currentTimeMillis();
	  		// System.out.print("GRT Searching "+(time2-time1));
    		// System.out.println("  processing "+(time3-time2));
			return res;
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
    	return null;
    	/*
		Vector<String[]> result = new Vector<String[]>();
    	Factory f = new Factory();
    	f.beginTransaction();
    	SemanticContent sc = f.getDocument(SCID).semanticContent();
    	Iterator it = sc.listEntityRelations();
    	while (it.hasNext()){
    		EntityRelation er = (EntityRelation)it.next();
    		String obj = er.object();
    		if (obj.equals(entityURI)){
    			String predicate = er.predicate();
    			String[] res = new String[3];
    			res[0] = predicate;
    			res[1] = UriUtil.splitNamespace(predicate)[1].trim();
    			res[2] = er.subject();
    			result.add(res);
    		}
    	}
    	f.commitTransaction();
    	return result;
    	*/
    }

    
//    public Vector<String[]> getLiteralsFor(Document ent_doc) {
//		long time = System.currentTimeMillis();
//    	Vector<String[]> result = new Vector<String[]>();
//    	List l = ent_doc.getFields();
//		for (Object o : l) {
//			Field f = (Field) o;
//			String name = f.name();
//			if (!name.equals("sc") && !name.equals("type")
//					&& !name.equals("ns") && !name.equals("Hns")
//					&& !name.equals("locN") && !name.equals("id")
//					&& !name.equals("splitN") && !name.equals("HlocN")
//					&& !name.equals("lit") && !name.equals("cbd")
//					&& !name.equals("HClocN")
//					) {
//				String[] res = new String[3];
//				res[0] = name;
//				res[1] = UriUtil.splitNamespace(name)[1].trim();
//				res[2] = ent_doc.get(name);
//				result.add(res);
//			}
//		}
//		System.out.println("getLitsF took: "+(System.currentTimeMillis()-time));
//		return result;
//	}
    	
	public void includeEntInfo(EntityResult er, Document ent_doc, int entInfo) {
		if((entInfo & SearchConf.ENT_TYPE_INFO) == SearchConf.ENT_TYPE_INFO){
			String type = ent_doc.get("type");
			if (type.equals("101") ) er.setType("Class");
			else if (type.equals("102") ) er.setType("Property");
			else if (type.equals("103") ) er.setType("Individual");
		}
		if((entInfo & SearchConf.ENT_LABEL_INFO) == SearchConf.ENT_LABEL_INFO) {
			try {
				String SCID = ent_doc.get("sc");
				String entityURI = ent_doc.get("ns")+ent_doc.get("locN");
	    		Searcher indexSearcher = getLiteralIndexSearcher();
				BooleanQuery q = new BooleanQuery();
				q.add(new TermQuery(new Term("scid", SCID)), BooleanClause.Occur.MUST);
				q.add(new TermQuery(new Term("subj", entityURI)), BooleanClause.Occur.MUST);
				q.add(new TermQuery(new Term("pred", "http://www.w3.org/2000/01/rdf-schema#label")), BooleanClause.Occur.MUST);
				Hits results = indexSearcher.search(q);
				if (results != null && results.length() != 0) 
					er.setLabels(new String[] {results.doc(0).get("obj")});
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
		if((entInfo & SearchConf.ENT_COMMENT_INFO) == SearchConf.ENT_COMMENT_INFO){
			try {
				String SCID = ent_doc.get("sc");
				String entityURI = ent_doc.get("ns")+ent_doc.get("locN");
	    		Searcher indexSearcher = getLiteralIndexSearcher();
				BooleanQuery q = new BooleanQuery();
				q.add(new TermQuery(new Term("scid", SCID)), BooleanClause.Occur.MUST);
				q.add(new TermQuery(new Term("subj", entityURI)), BooleanClause.Occur.MUST);
				q.add(new TermQuery(new Term("pred", "http://www.w3.org/2000/01/rdf-schema#label")), BooleanClause.Occur.MUST);
				Hits results = indexSearcher.search(q);
				if (results != null && results.length() != 0) 
					er.setLabels(new String[]{results.doc(0).get("obj")});
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
		if((entInfo & SearchConf.ENT_ANYRELATIONFROM_INFO)  == SearchConf.ENT_ANYRELATIONFROM_INFO) {
			String scid = ent_doc.get("sc");
			String uri  = ent_doc.get("ns")+ent_doc.get("locN");
			Vector<String[]> relFrom = getRelationsFrom(scid, uri);
			er.setRelationFrom(toArray(relFrom));
		}
        if((entInfo & SearchConf.ENT_ANYRELATIONTO_INFO)  == SearchConf.ENT_ANYRELATIONTO_INFO) {
        	String scid = ent_doc.get("sc");
			String uri  = ent_doc.get("ns")+ent_doc.get("locN");
			Vector<String[]> relTo = getRelationsTo(scid, uri, getRelationIndexSearcher());
			er.setRelationTo(toArray(relTo));
		}
		if((entInfo & SearchConf.ENT_ANYLITERAL_INFO)  == SearchConf.ENT_ANYLITERAL_INFO) {
			String SCID = ent_doc.get("sc");
			String entityURI  = ent_doc.get("ns")+ent_doc.get("locN");
	    	if (SCID != null) 
	    	try {
	    		Searcher indexSearcher = getLiteralIndexSearcher();
				BooleanQuery q = new BooleanQuery();
				q.add(new TermQuery(new Term("scid", SCID)), BooleanClause.Occur.MUST);
				q.add(new TermQuery(new Term("subj", entityURI)), BooleanClause.Occur.MUST);
				Hits results = indexSearcher.search(q);
				String[][] toReturn = new String[results.length()][];
				for (int i = 0; i < results.length(); i++) {
					Document d = results.doc(i);
					String[] lit = new String[4];
					lit[0] = d.get("pred");
					lit[1] = UriUtil.splitNamespace(d.get("pred"))[1];
					lit[2] = d.get("obj");
					lit[3] = d.get("lang");
					toReturn[i] = lit;
				}
				er.setLiterals(toReturn);
	    	} catch(Exception e){
					e.printStackTrace();
				}
		}
	}
	
	
}
