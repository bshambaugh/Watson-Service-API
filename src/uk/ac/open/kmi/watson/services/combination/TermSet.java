package uk.ac.open.kmi.watson.services.combination;

import java.util.Vector;

public class TermSet {
	
	private Vector<String> set = new Vector<String>(); // sorted for intersection and super/subset test
	private Vector<String> ontologies = new Vector<String>();
	
	public boolean equals(Object o){
			TermSet ts = (TermSet)o;
			if (ts.set.equals(set)) return true;
			return false;
	}
	
	// TODO: optimize with sort...
	public boolean subSet(TermSet ts){
			if (set.size()>=ts.set.size()) return false;
			for (String s : set) if (!ts.set.contains(s)) return false;
			return true;
	}
			
	public String toString(){
			return "TS::"+set+" ("+ontologies.size()+")";
	}

	public Vector<String> getOntologies() {
		return ontologies;
	}

	public void setOntologies(Vector<String> ontos) {
		this.ontologies = ontos;
	}

	public Vector<String> getSet() {
		return set;
	}

	public void setSet(Vector<String> set) {
		this.set = set;
	}

}
	
