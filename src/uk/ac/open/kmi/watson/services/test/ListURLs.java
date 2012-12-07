package uk.ac.open.kmi.watson.services.test;

import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.Searcher;

import uk.ac.open.kmi.watson.services.*;

public class ListURLs extends SemanticContentSearch {

    public static void main(String[] args){
	ListURLs lu = new ListURLs();
	String[] res = lu.listSemanticContents(0,100);
	for (String r : res){
	    System.out.println(r);
	}
    }
	
	
}
