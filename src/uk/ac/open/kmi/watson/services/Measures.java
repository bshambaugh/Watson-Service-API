package uk.ac.open.kmi.watson.services;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TermQuery;

public class Measures extends WatsonService {

	
	//	 The cache for bracnhes...
	private static Cache brancheCache;

	private SemanticContentSearch scs = new SemanticContentSearch();
	private EntitySearch es = new EntitySearch();
	
	public Measures(){
		if (brancheCache == null) { 
	    	CacheManager cm = CacheManager.create();
	    	cm.addCache("brancheCache");
	    	brancheCache = cm.getCache("brancheCache");
	    }
	}
	
	// Overall because we can have internal (doc) and external
	public int entityOverallPopularity(String uri){
		Searcher search = getRelationIndexSearcher();
		// TODO: can probably be more efficient with 
		TermQuery tq = new TermQuery(new Term("obj", uri.trim()));
		try {
			Hits h = search.search(tq);
			return h.length();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public int entityExternalPopularity(String onto, String uri){
		return -1;
	}
	
	/** here we consider the statements that refer to uri in the ontology **/
	public int entityInternalPopularity(String onto, String uri){
		return es.getRelationsTo(onto, uri).length;
	}
	
	/** here we consider the statements that refer to uri in the ontology **/
	public double entityLocalInternalPopularity(String onto, String uri){
		int glob = entityInternalPopularity(onto, uri);
		double maxUp = getMaxWeightedPopUp(onto, uri, 0);
		double maxDown = getMaxWeightedPopDown(onto, uri, 0);		
		double maxR = (maxUp>maxDown?maxUp:maxDown);
		maxR = (maxR>glob?maxR:glob);
		return ((double)glob)/maxR;
	}
	
	private double LocalPopDistanceRatio = 0.3;
	private int LocalPopMaxDistance = 3;
	
	
	private double getMaxWeightedPopDown(String onto, String uri, int dist) {
		int lDist = dist + 1;
		double max = -1;
		if (lDist > LocalPopMaxDistance) return -1;
		String[][] rels = es.getRelationsTo(onto, uri);
		for (String[] rel : rels){
			int glob = entityInternalPopularity(onto, rel[2]);
			double wGlob = (1.-(((double)lDist)*LocalPopDistanceRatio))*glob;
			if (wGlob>max) max = wGlob;
			double lddown = getMaxWeightedPopDown(onto, rel[2], lDist);
			if (lddown>max) max = lddown;		
		}
		return max;
	}

	private double getMaxWeightedPopUp(String onto, String uri, int dist) {
		int lDist = dist + 1;
		double max = -1;
		if (lDist > LocalPopMaxDistance) return -1;
		String[][] rels = es.getRelationsFrom(onto, uri);
		for (String[] rel : rels){
			int glob = entityInternalPopularity(onto, rel[2]);
			double wGlob = (1.-(((double)lDist)*LocalPopDistanceRatio))*glob;
			if (wGlob>max) max = wGlob;
			double lddown = getMaxWeightedPopUp(onto, rel[2], lDist);
			if (lddown>max) max = lddown;		
		}
		return max;
	}
	
	/** here we use consider the number of statements that apply to uri **/
	public int entityDensity(String onto, String uri){
		return es.getRelationsFrom(onto, uri).length;
	}

	public double entityLocalDensity(String onto, String uri){
		int glob = entityDensity(onto, uri);
		double maxUp = getMaxWeightedGDUp(onto, uri, 0);
		double maxDown = getMaxWeightedGDDown(onto, uri, 0);		
		double maxR = (maxUp>maxDown?maxUp:maxDown);
		maxR = (maxR>glob?maxR:glob);
		return ((double)glob)/maxR;
	}

	private double LocalDensityDistanceRatio = 0.3;
	private int LocalDensityMaxDistance = 3;
	
	
	private double getMaxWeightedGDDown(String onto, String uri, int dist) {
		int lDist = dist + 1;
		double max = -1;
		if (lDist > LocalDensityMaxDistance) return -1;
		String[][] rels = es.getRelationsTo(onto, uri);
		for (String[] rel : rels){
			int glob = entityDensity(onto, rel[2]);
			double wGlob = (1.-(((double)lDist)*LocalDensityDistanceRatio))*glob;
			if (wGlob>max) max = wGlob;
			double lddown = getMaxWeightedGDDown(onto, rel[2], lDist);
			if (lddown>max) max = lddown;		
		}
		return max;
	}

	private double getMaxWeightedGDUp(String onto, String uri, int dist) {
		int lDist = dist + 1;
		double max = -1;
		if (lDist > LocalDensityMaxDistance) return -1;
		String[][] rels = es.getRelationsFrom(onto, uri);
		for (String[] rel : rels){
			int glob = entityDensity(onto, rel[2]);
			double wGlob = (1.-(((double)lDist)*LocalDensityDistanceRatio))*glob;
			if (wGlob>max) max = wGlob;
			double lddown = getMaxWeightedGDUp(onto, rel[2], lDist);
			if (lddown>max) max = lddown;		
		}
		return max;
	}

	public double entityCoverage(String onto, String uri){
		// propertion of the ontology reachable (connected to) the entity.
		// very related to density
		return -1.;
	}
	
	
	// Ontologies 
	
	public int ontologyNumberOfClasses(String onto){
		try {
			String SCID = getLuceneDocument(onto);
			if (SCID == null)
				return -1;
			Searcher indexSearcher = getEntityIndexSearcher();
			BooleanQuery q = new BooleanQuery();
			q.add(new TermQuery(new Term("sc", SCID)),
					BooleanClause.Occur.MUST);
			q.add(new TermQuery(new Term("type", new Integer(DB_CLASS)
					.toString())), BooleanClause.Occur.MUST);
			Hits results = indexSearcher.search(q);
			return results.length();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	

	public int ontologyNumberOfProperties(String onto){
		try {
			String SCID = getLuceneDocument(onto);
			if (SCID == null)
				return -1;
			Searcher indexSearcher = getEntityIndexSearcher();
			BooleanQuery q = new BooleanQuery();
			q.add(new TermQuery(new Term("sc", SCID)),
					BooleanClause.Occur.MUST);
			q.add(new TermQuery(new Term("type", new Integer(DB_PROPERTY)
					.toString())), BooleanClause.Occur.MUST);
			Hits results = indexSearcher.search(q);
			return results.length();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public int ontologyNumberOfIndividuals(String onto){
		try {
			String SCID = getLuceneDocument(onto);
			if (SCID == null)
				return -1;
			Searcher indexSearcher = getEntityIndexSearcher();
			BooleanQuery q = new BooleanQuery();
			q.add(new TermQuery(new Term("sc", SCID)),
					BooleanClause.Occur.MUST);
			q.add(new TermQuery(new Term("type", new Integer(DB_INDIVIDUAL)
					.toString())), BooleanClause.Occur.MUST);
			Hits results = indexSearcher.search(q);
			return results.length();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public int ontologyDirectPopularity(String onto){
		int importNum = scs.getImportedBy(onto).length;
		// FIXME use the name space
		return importNum;
	}

	// like ontogyDirectPopulatity, but count the indirect import/namespace as well.
	public int ontologyIndirectPopularity(String onto){
		return importTransClosure(onto, new Vector<String>()).size();
	}

	
	private Vector<String> importTransClosure(String onto, Vector<String> current) {
		String[] dimp = scs.getImportedBy(onto);
		Vector<String> res = new Vector<String>();
		for (String i : dimp){
			if (!current.contains(i)){
				current.add(i);
				res.add(i);
				res.addAll(importTransClosure(i, current));
			}
		}
		return res;
	}

	private String lastBranchesOnto = null;
	private Vector<Vector<String>> lastBranches = null;
	
	private Vector<Vector<String>> computeBranches(String onto){
		if (onto.equals(lastBranchesOnto)) {
			return lastBranches;
		}
		String[] listClasses = scs.listClasses(onto);
		// branches, indexed by their first element
		HashMap <String, Vector<Vector<String>>> branches = computeInitialBranches(onto, listClasses);
		System.out.println("Initialization:: "+listClasses.length+" classes ");

		for (int i =0; i < listClasses.length; ){
			String currentClass = listClasses[i];
			Vector<Vector<String>> cb = branches.get(currentClass);
			Vector<Vector<String>> nb = new Vector<Vector<String>>();
			// System.out.println("Looking at "+currentClass);
			if (cb==null) {
				// System.out.println("   Already treated, stepping");
				i++; 
			}
			else {
				// System.out.println("   "+cb.size()+" branches :: "+cb);
				branches.remove(currentClass);
				boolean found = false;
				for (Vector<String> br : cb){
					String lcl = br.elementAt(br.size()-1);
					Vector<Vector<String>> ob = branches.get(lcl);
					if (ob==null) {
						// System.out.println(br+" reached the root");
						nb.add(br); 
					}
					else {
						found = true;
						// branches.remove(lcl);
						// System.out.println("   found "+ob.size()+" branches to connect to");
						for (Vector<String> toAdd : ob){
							Vector<String> newB = addAllFirst(br,toAdd);
							nb.add(newB);
						}
					}
			    branches.put(currentClass, nb);
				if (!found) {
					//System.out.println("Done with "+currentClass); 
					i++;
					}
				}
			}
		
		}
		Vector<Vector<String>> res = createResults(branches);
		// Element el = new Element(onto, res);
		// brancheCache.put(el);
		lastBranchesOnto = onto;
		lastBranches = res;
		return res;
	}
	
	
	private Vector<Vector<String>> createResults(HashMap<String, Vector<Vector<String>>> branches) {
		Collection<Vector<Vector<String>>> resv = branches.values();
		Vector<Vector<String>> result = new Vector<Vector<String>>();
		for (Vector<Vector<String>> brs : resv){
			for (Vector<String> b : brs){
				Vector<String> nb = new Vector<String>();
				for (String s : b) nb.add(s);
				result.add(nb);
			}
		}
		return result;
	}

	// FIXME: this might be tough 
	private Vector<String> addAllFirst(Vector<String> br, Vector<String> ob) {
		Vector<String> result = (Vector<String>) br.clone();
		result.remove(result.size()-1);
		result.addAll(ob);
		return result;
	}

	private HashMap<String, Vector<Vector<String>>> computeInitialBranches(String onto, String[] listClasses) {
		HashMap<String, Vector<Vector<String>>> result = new HashMap<String, Vector<Vector<String>>>();
		for (String cl : listClasses){
			String[] scos = es.getSuperClasses(onto, cl);
			Vector<Vector<String>> clB = new Vector<Vector<String>>();
			for (String sco : scos){
				Vector<String> sbr = new Vector<String>(2);
				sbr.add(cl); sbr.add(sco);
				clB.add(sbr);
			}
			result.put(cl, clB);
		}
		return result;
	}

	private Vector<Vector<Integer>> computeBranches2(String onto){
		Element element = brancheCache.get(onto);
		if (element != null ) {
			Vector<Vector<Integer>> toReturn = (Vector<Vector<Integer>>)element.getObjectValue();
			return toReturn;
		}
		if (scs.getSizeInBytes(onto)>1000000) {System.out.println("Warning, onto too big ::"+onto); return new Vector<Vector<Integer>>();}
		System.out.println("Initialization");
		String[] listClasses = scs.listClasses(onto);
		HashMap <String, Integer> listClasses2 = computeLCMap(listClasses);
		Vector<Vector<Integer>> tmpbranches = new Vector<Vector<Integer>>(listClasses.length);
		Vector<Vector<Integer>> finalBranches = new Vector<Vector<Integer>>();
		HashMap<Integer, Integer[]> subclassofrels = computeSubClassOfRels(onto, listClasses, listClasses2);
		computeIntialBranches(listClasses, subclassofrels, tmpbranches, finalBranches);
		System.out.println("End Initialization ("+finalBranches.size()+"/"+listClasses.length+")");
		listClasses = null;
		System.gc();
		int k = 0;
		long time1 = System.currentTimeMillis();
		while (!tmpbranches.isEmpty()){
			if ((++k) % 100 == 0) {
				//System.out.println(k+"("+(System.currentTimeMillis()-time1)+", "+tmpbranches.size()+", "+finalBranches.size()+") ");
				time1 = System.currentTimeMillis();
			}
			Vector<Integer> current = tmpbranches.elementAt(0); // the top of the stack
			Integer lastClass = current.elementAt(current.size()-1); // the last class of the branch
			Integer[] sco = getSuperClasses(subclassofrels, lastClass);
			// System.out.println("Looking at unfinished branche "+current+" #SCO: "+(sco!=null?sco.length:-1));
			tmpbranches.remove(current);
			{
				for (Integer c : sco){
					Vector<Vector<Integer>> toExtendTMP = getBranchesContaining(tmpbranches, c);
					Vector<Vector<Integer>> toExtendFinal = getBranchesContaining(finalBranches, c);
					// System.out.println("    Found branches to extend : tmp="+toExtendTMP.size()+" final "+toExtendFinal.size());
					if (toExtendFinal.size() == 0 && toExtendTMP.size() == 0){
						finalBranches.add(current);
					}
					for (Vector<Integer> et : toExtendTMP) {
						// System.out.println("       extending tmp "+et);				
						if (et.elementAt(0).equals(c)) {
							for (int i = 0; i < current.size(); i++)
								et.insertElementAt(current.elementAt(i), i);
							// System.out.println("          result 1 "+et);													
						}
						else {
							Vector<Integer> et_c = (Vector<Integer>)current.clone();
							boolean finish = false;
							for (Integer e : et){
								if (finish) et_c.add(e);
								if (!finish && e.equals(c)) { et_c.add(c); finish = true; }
							}
							// System.out.println("          result 2 "+et_c);	
							// I should not have to do this test. Something is wrong!
							if (!tmpbranches.contains(et_c)) tmpbranches.add(et_c);
						}
					}
					for (Vector<Integer> ef : toExtendFinal) {
						// System.out.println("       extending final "+ef);				
						if (ef.elementAt(0).equals(c)) {
							for (int i = 0; i < current.size(); i++)
								ef.insertElementAt(current.elementAt(i), i);
							// System.out.println("          result 1 "+ef);
						}
						else {
							Vector<Integer> ef_c = (Vector<Integer>)current.clone();
							boolean finish = false;
							for (Integer e : ef){
								if (finish) ef_c.add(e);
								if (!finish && e.equals(c)) { ef_c.add(c); finish = true; }
							}
//							 I should not have to do this test. Something is wrong!
							if (!finalBranches.contains(ef_c)) finalBranches.add(ef_c);
							// System.out.println("          result 2 "+ef_c);
						}
					}
				}
			}
		}
		Element el = new Element(onto, finalBranches);
		brancheCache.put(el);
		return finalBranches;
	}
	
	private Integer[] getSuperClasses(HashMap<Integer, Integer[]> subclassofrels, Integer lastClass) {
		return subclassofrels.get(lastClass);
	}

	private HashMap<String, Integer> computeLCMap(String[] listClasses) {
		HashMap<String, Integer> res = new HashMap<String, Integer>(listClasses.length);
		for (int i = 0; i < listClasses.length; i++){
			res.put(listClasses[i],i);
		}
		return res;
	}


	private void computeIntialBranches(String[] listClasses, HashMap<Integer, Integer[]> subclassofrels, 
			Vector<Vector<Integer>> tmpbranches, Vector<Vector<Integer>> finalBranches) {
		for (int i =0; i < listClasses.length; i++){
			String c = listClasses[i];
			Integer[] scos = subclassofrels.get(i);
			Vector<Integer> nb = new Vector<Integer>();
			nb.add(i);
			if (scos==null || scos.length==0) finalBranches.add(nb);
			else tmpbranches.add(nb);
		}
	}

	private HashMap<Integer, Integer[]> computeSubClassOfRels(String onto, String[] listClasses, HashMap<String, Integer> listClasses2) {
		HashMap<Integer, Integer[]> res = new HashMap<Integer, Integer[]>(listClasses.length);
		for (int i = 0; i < listClasses.length; i++){
			String c = listClasses[i];
			String[] scos = es.getSuperClasses(onto, c);
			Integer[] scoi = new Integer[scos.length];
			for (int j=0; j < scos.length; j++){
				if (listClasses2.get(scos[j]) != null)
					scoi[j] = listClasses2.get(scos[j]);
			}
			res.put(i, scoi);
		}
		return res;
	}

	private Vector<Vector<Integer>> getBranchesContaining(Vector<Vector<Integer>> branches, Integer c) {
		Vector<Vector<Integer>> results = new Vector<Vector<Integer>>();
		for (Vector<Integer> v : branches)
			if (v.contains(c) && notKnownEnd(c, v, results)) results.add(v);
		return results;
	}

	private boolean notKnownEnd(Integer c, Vector<Integer> v, Vector<Vector<Integer>> results) {
		List<Integer> endv = v.subList(v.indexOf(c), v.size());
		for (Vector<Integer> r : results){
			List<Integer> endr = r.subList(r.indexOf(c), r.size());
			if (endv.equals(endr)) return false;
		}
		return true;
	}

	public int ontologyMaxDepth(String onto){
		int max = -1;
		Vector<Vector<String>> branches = computeBranches(onto);
		for (Vector<String> b : branches)
			if (b.size()>max) max = b.size();
		return max;
	}
	
	public int ontologyMinDepth(String onto){
		int min = Integer.MAX_VALUE;
		Vector<Vector<String>> branches = computeBranches(onto);
		for (Vector<String> b : branches)
			if (b.size()<min) min = b.size();
		return (min==Integer.MAX_VALUE?-1:min);
	}

	public double ontologyAvgDepth(String onto){
		int sum = 0;
		Vector<Vector<String>> branches = computeBranches(onto);
		for (Vector<String> b : branches)
			sum += b.size();
		return (sum==0?-1:((double)sum)/((double)branches.size()));
	}

	public double ontologyDepthVariance(String onto){
		double sum = 0;
		Vector<Vector<String>> branches = computeBranches(onto);
		double avg = ontologyAvgDepth(onto);
		for (Vector<String> br : branches){
			sum += Math.abs(((double)br.size())-avg);
		}
		return sum/((double)branches.size());
	}
	
	private Vector<Vector<Integer>> computeLevels(String onto){
			Vector<Vector<String>> branches = computeBranches(onto);
			Vector<Vector<Integer>> levels = new Vector<Vector<Integer>>();
			int maxLev = ontologyMaxDepth(onto);
			for (int i = maxLev; i > 0; i--){
				Vector<Integer> leveli = new Vector<Integer>();
				for (Vector<String> br : branches){
					if (br.size()>=i){
						Integer nb = br.elementAt(i-1).hashCode();
						if (!contains(levels, nb) && !leveli.contains(nb))
							leveli.add(nb);
					}
				}
				levels.add(leveli);
			}
			return levels;
	}
	
	private boolean contains(Vector<Vector<Integer>> levels, Integer nb) {
		for (Vector<Integer> lev : levels){
			if (lev.contains(nb)) return true;
		}
		return false;
	}

	public int ontologyMaxBreadth(String onto){
		int max = 0;
		Vector<Vector<Integer>> levels = computeLevels(onto);
		for (Vector<Integer> level : levels){
			if (level.size()>max) max = level.size();
		}
		return max;
	}
	
	public int ontologyMinBreadth(String onto){
		int min = Integer.MAX_VALUE;
		Vector<Vector<Integer>> levels = computeLevels(onto);
		for (Vector<Integer> level : levels){
			if (level.size()<min) min = level.size();
		}
		return min;
	}

	public double ontologyAvgBreadth(String onto){
		int sum = 0;
		Vector<Vector<Integer>> levels = computeLevels(onto);
		for (Vector<Integer> lev : levels)
			sum += lev.size();
		return (sum==0?-1:((double)sum)/((double)levels.size()));
	}

	public double ontologyBreadthVariance(String onto){
		double sum = 0;
		Vector<Vector<Integer>> levels = computeLevels(onto);
		double avg = ontologyAvgBreadth(onto);
		for (Vector<Integer> lev : levels){
			sum += Math.abs(((double)lev.size())-avg);
		}
		return sum/((double)levels.size());
	}
	
	public static void main (String[] args){
		Measures m = new Measures();
		Vector<Vector<String>> branches = m.computeBranches(args[0]);
		for (Vector<String> b : branches) System.out.println(b);
		System.out.println("****");
		Vector<Vector<Integer>> levels = m.computeLevels(args[0]);
		for (Vector<Integer> l : levels) System.out.println(l);
		System.out.println("Max Depth :: "+m.ontologyMaxDepth(args[0]));
		System.out.println("Min Depth :: "+m.ontologyMinDepth(args[0]));
		System.out.println("Avg Depth :: "+m.ontologyAvgDepth(args[0]));
		System.out.println("Var Depth :: "+m.ontologyDepthVariance(args[0]));
		System.out.println("Max Breadth :: "+m.ontologyMaxBreadth(args[0]));
		System.out.println("Min Breadth :: "+m.ontologyMinBreadth(args[0]));
		System.out.println("Avg Breadth :: "+m.ontologyAvgBreadth(args[0]));
		System.out.println("Var Breadth :: "+m.ontologyBreadthVariance(args[0]));
	//	System.out.println("Indirect Imported by : "+m.importTransClosure(args[0], new Vector<String>()));		
		System.out.println("Popularity :: "+m.ontologyDirectPopularity(args[0]));
		System.out.println("Coverage :: "+m.ontologyIndirectPopularity(args[0]));
		String[] cls = m.scs.listClasses(args[0]);
		String[] ind = m.scs.listIndividuals(args[0]);
		String[] prop = m.scs.listProperties(args[0]);
		for (int i = 0; i < 3; i ++){
			System.out.println("Density of "+cls[i]+" :: "+m.entityDensity(args[0], cls[i]));
			System.out.println("Density of "+ind[i]+" :: "+m.entityDensity(args[0], ind[i]));
			System.out.println("Density of "+prop[i]+" :: "+m.entityDensity(args[0], prop[i]));
			System.out.println("Local Density of "+cls[i]+" :: "+m.entityLocalDensity(args[0], cls[i]));
			System.out.println("Local Density of "+ind[i]+" :: "+m.entityLocalDensity(args[0], ind[i]));
			System.out.println("Local Density of "+prop[i]+" :: "+m.entityLocalDensity(args[0], prop[i]));
			System.out.println("Global Popularity of "+cls[i]+" :: "+m.entityOverallPopularity(cls[i]));
			System.out.println("Global Popularity of "+ind[i]+" :: "+m.entityOverallPopularity(ind[i]));
			System.out.println("Global Popularity of "+prop[i]+" :: "+m.entityOverallPopularity(prop[i]));
			System.out.println("Internal Popularity of "+cls[i]+" :: "+m.entityInternalPopularity(args[0], cls[i]));
			System.out.println("Internal Popularity of "+ind[i]+" :: "+m.entityInternalPopularity(args[0], ind[i]));
			System.out.println("Internal Popularity of "+prop[i]+" :: "+m.entityInternalPopularity(args[0], prop[i]));
			System.out.println("Local Popularity of "+cls[i]+" :: "+m.entityLocalInternalPopularity(args[0], cls[i]));
			System.out.println("Local Popularity of "+ind[i]+" :: "+m.entityLocalInternalPopularity(args[0], ind[i]));
			System.out.println("Local Popularity of "+prop[i]+" :: "+m.entityLocalInternalPopularity(args[0], prop[i]));
		}
	}
	
	
	
}
