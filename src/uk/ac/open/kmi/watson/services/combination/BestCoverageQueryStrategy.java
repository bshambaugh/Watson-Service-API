package uk.ac.open.kmi.watson.services.combination;

import java.util.Vector;
import uk.ac.open.kmi.watson.services.SemanticContentSearch;
import uk.ac.open.kmi.watson.services.internal.OntologySearchInternal;

public class BestCoverageQueryStrategy {

	private int counter = 0;
	private Vector<TermSet> toTestSet = new Vector<TermSet>();
	private Vector<TermSet> OKSets = new Vector<TermSet>();
	private Vector<TermSet> NOKSets = new Vector<TermSet>();

	private OntologySearchInternal osi = new OntologySearchInternal();
	
	private TermSet initialTS;
	private int scopeModifier;
	private int entityModifier;
	private int matcher;
	
	public BestCoverageQueryStrategy(String[] keywords, int scopeModifier, int entityModifier, int matcher) {
		initialTS = new TermSet();
		Vector<String> is = new Vector<String>(keywords.length);
		for (String k : keywords) is.add(k);
		initialTS.setSet(is);
		this.scopeModifier = scopeModifier;
		this.entityModifier = entityModifier;
		this.matcher = matcher;
	}

	public static void main(String[] args){
		Vector<String> set = new Vector<String>();
		String[] testset = {"workshop",
			"unstructured",
			"objects",
			"mantel",
			"qin",
			"acl",
			"nlp",
			"ryu",
			"rubens",
			"networked",
			"semantics"};
		BestCoverageQueryStrategy bcs = new BestCoverageQueryStrategy(testset, 15, 7, 1);
	    Vector<String> results = bcs.getSemanticContents();
		bcs.displayResult();
		for (String r : results) System.out.println("Onto:: "+r);
	}

	private void displayResult() {
		for (TermSet ts : OKSets){
			System.out.println("RESULT:: "+ts);
		}
	}

	public Vector<String> getSemanticContents() {
		addToTestSet(initialTS);
		nextTest();
		while(true){
			if (toTestSet.isEmpty() && NOKSets.isEmpty()) break;
			if (toTestSet.isEmpty()) createToTestSet();
			nextTest();
		}
		Vector<String> result = new Vector<String>();
		for (TermSet ts : OKSets){
			result.addAll(ts.getOntologies());
		}
		return result;
	}

	private  void createToTestSet() {
		TermSet tg = NOKSets.elementAt(0);
		if (tg.getSet().size()==1) {NOKSets.remove(0); return;}
		NOKSets.remove(0);
		Vector<String> set = tg.getSet();
		for (String term : set){
			TermSet nt = new TermSet();
			Vector<String> nSet = (Vector<String>)set.clone();
			nSet.remove(nSet.indexOf(term));
			nt.setSet(nSet);
			if(!toTestSet.contains(nt) && !NOKSets.contains(nt) && !OKSets.contains(nt)){
				if (!hasSuperSetInOKSets(nt))
				toTestSet.add(nt);
			}
		}
	}

	private boolean hasSuperSetInOKSets(TermSet nt) {
		for (TermSet ts : OKSets){
			if (nt.subSet(ts)) return true;
		}
		return false;
	}

	private void nextTest() {
		if (toTestSet.isEmpty()) return;
		TermSet tt = toTestSet.elementAt(0);
		toTestSet.remove(0);
		System.out.print("TESTING("+(counter++)	+"):: "+tt+" :: ");
		Vector<String> ontos = queryTestSet(tt);
		if (ontos == null || ontos.size() == 0) 
			{ addToNOKSet(tt); System.out.println("NOK"); }
		else {
			tt.setOntologies(ontos);
			addToOKSet(tt);
			System.out.println("OK !!!"); 
		}
	}

	private Vector<String> queryTestSet(TermSet tt) {
		// Vector<String> result = osi.getSemanticContentIDsByKeywordsWithRestrictions(toArray(tt.getSet()), scopeModifier, entityModifier, matcher, 0, -1);
		// result.remove(result.size()-1);
		// return result;
		return null;
	}
	
	private String[] toArray(Vector<String> set) {
		String[] result = new String[set.size()];
		for (int i = 0; i < set.size(); i++) result[i] = set.elementAt(i);
		return result;
	}

	private void addToOKSet(TermSet ts){
		OKSets.add(ts);
	}
	
	private void addToNOKSet(TermSet ts){
		// TODO: can it be a subset of an existing NOK??
		NOKSets.add(ts);
	}
	
	private void addToTestSet(TermSet ts){
		toTestSet.add(ts);
	}
	
	// private void createTestTermSet(TermSet ts) {
//		ts.set.add("researcher");
//		ts.set.add("developer");
//		ts.set.add("programmer");
//		ts.set.add("design");
//		ts.set.add("kjhfjhflkjhdskh");
//		ts.set.add("modeling");
//		ts.set.add("software");
//		ts.set.add("research");       

		
		
//		ts.set.add("workshop");
//		ts.set.add("unstructured");
//		ts.set.add("objects");
//		ts.set.add("mantel");
//		ts.set.add("qin");
//		ts.set.add("acl");
////		ts.set.add("nlp");
//		ts.set.add("ryu");
//		ts.set.add("rubens");
//		ts.set.add("networked");
//		ts.set.add("semantics");
//		ts.set.add("automatic");
//		ts.set.add("genoa");
//		ts.set.add("wim");
//		ts.set.add("iso");
//		ts.set.add("rez");
//		ts.set.add("philipp");
//		ts.set.add("eduard");
//		ts.set.add("textual");
//		ts.set.add("domain");
//		ts.set.add("liang");
//		ts.set.add("linguistics");
//		ts.set.add("nathalie");
		
//		cayzer
//		spivack
//		scientist
//		swed
//		blogging
//		demonstrator
//		opinion
//		zillion
//		doubtless
//		entry
//		3031
//		nlp
//		aggregator
//		correlates
//		guessing
//		trackback
//		record
//		summary
//		interviewed
//		betting
//		metadata
//		people
//		startup
//		posted
//		modelling
//	}
	
	
}
