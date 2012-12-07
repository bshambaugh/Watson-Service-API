package uk.ac.open.kmi.watson.services.revyu;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;


public class RevyuEndpoint {

	 private final String REVYU_ENDPOINT = "http://revyu.com/sparql";
	
	 private ResultSet executeOnRevyusEnpoint(String queryString){
	        try{
	        Query query = QueryFactory.create(queryString);
	        QueryExecution qe = QueryExecutionFactory.sparqlService(REVYU_ENDPOINT, query);
	        ResultSet results = qe.execSelect();
	        qe.close();
	        return results;
	        } catch(Exception e){
	            System.out.println("Querying revyu: "+e);
	            return null;
	        }
	    }

	 public int getAverageRating(String URI){
	        String query = "PREFIX owl:   <http://www.w3.org/2002/07/owl#>\n" +
	                   "PREFIX rev:   <http://purl.org/stuff/rev#>\n" +
	                               "SELECT ?z WHERE {?x owl:sameAs <"+URI+"> . " +
	            "?x rev:hasReview ?y . " +
	            "?y rev:rating ?z}";
	        ResultSet rs = executeOnRevyusEnpoint(query);
	        if (rs == null) return 0;
	        int sum = 0; int numb = 0;
	        while(rs.hasNext()){
	            numb ++;
	            String value = rs.nextSolution().get("??z").toString();
	            if (value.indexOf("^^") != -1)
	                value = value.substring(0, value.indexOf("^^"));
	            sum += Integer.parseInt(value);
	        }
	        double average = (double)sum/(double)numb;
	        return (int) Math.round(average);
	    }
	
	 public int getNumberOfReviews(String URI){
	        String query = "PREFIX owl:   <http://www.w3.org/2002/07/owl#>\n" +
	                   "PREFIX rev:   <http://purl.org/stuff/rev#>\n" +
	                               "SELECT ?z WHERE {?x owl:sameAs <"+URI+"> . " +
	            "?x rev:hasReview ?y . " +
	            "?y rev:rating ?z}";
	        ResultSet rs = executeOnRevyusEnpoint(query);
	        if (rs == null) return 0;
	        int nb=0;
	        while (rs.hasNext()){
	        	rs.next();
	        	nb++;
	        }
	        return nb;
	    }
	
	 public String getRevyuURI(String URI){
	        String query = "PREFIX owl:   <http://www.w3.org/2002/07/owl#>\n" +
	            "SELECT ?x WHERE {?x owl:sameAs <"+URI+">}";
	        ResultSet rs = executeOnRevyusEnpoint(query);
	        if (rs == null) return null;
	        //System.out.println(rs.nextSolution().get("??x"));
	        if (rs.hasNext()) return rs.nextSolution().get("??x").toString();
	        return null;
	    }

     public static void main(String [] args){
	        RevyuEndpoint app = new RevyuEndpoint();
	        String uri = "http://www.aktors.org/ontology/portal";
	        System.out.println("Revyu URI:: "+app.getRevyuURI(uri));
	        System.out.println("Revyu NB:: "+app.getNumberOfReviews(uri));
	        System.out.println("Revyu URI:: "+app.getAverageRating(uri));
	    }
}
