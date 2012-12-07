/*
 * OMVdescriptionExtractor.java
 *
 * Created on 22 May 2007, 16:56
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package uk.ac.open.kmi.watson.services.omv;

import java.util.Vector;

import org.neon_toolkit.omv.api.core.OMVOntology;
import org.neon_toolkit.omv.api.core.OMVOntologyLanguage;
import org.neon_toolkit.registry.api.Oyster2Connection;
import org.neon_toolkit.registry.api.Oyster2Manager;

import uk.ac.open.kmi.watson.services.SemanticContentSearch;
import uk.ac.open.kmi.watson.services.WatsonService;


/**
 * TODO mda change to use high-level api functions and to work better
 * TODO have to look first into the peer if it already exist before creating it
 * TODO have to create all the ontologies in the peer, and just retrieve it... so need  to be in validation, so somme high-level api have to move...
 * @author cb7224
 */
public class OMVFileFactory extends WatsonService {
	    
   private OMVOntology buildOMVdescription(String URI) {
        OMVOntology oo = new OMVOntology();
   
        SemanticContentSearch os = new SemanticContentSearch();
        
        System.out.println("Building OMV desc for "+URI);
        
        //set number of entity on the OMV object OK
        oo.setNumberOfClasses(os.listClasses(URI).length);
        oo.setNumberOfProperties(os.listProperties(URI).length);
        oo.setNumberOfIndividuals(os.listIndividuals(URI).length);
        
        oo.setURI(URI);
        
        //set Creation Date (TODO) 
        //oo.setCreationDate(Calendar.getInstance(TimeZone.getTimeZone(Locale.UK.toString()) , Locale.UK ).getTime().toString());
        // System.out.print("."); 
        
        // set provenance
        String [] provs = os.getSemanticContentLocation(URI);
        for (String location : provs){
        	oo.setResourceLocator(location); // TODO: Strange isn't it???
        	break; // TODO: koi??
        }
        
        //set Ontology language 
        OMVOntologyLanguage ol = getOntologyLanguage(URI, os);
        oo.setHasOntologyLanguage(ol); // TODO: only one?? 
        
        // set type of semantic content (IDEM) TODO: fix that
        //OMVOntologyType ot = getOntologyType(sc);
        //oo.setIsOfType(ot); // ????
        //System.out.print("."); 
        
        // TODO: set natural language
        //Vector<String> naturalLanguage = listNaturalLanguage(sc);
        //for (String nl : naturalLanguage) oo.addNaturalLanguage(nl);
        //System.out.print("."); 
        
        // TODO: set syntax
        //OMVOntologySyntax os = getOntologySyntax(sc);
        //oo.setHasOntologySyntax(os); // ???
        //System.out.print("."); 
        
        // TODO: set description
        //String description = listComments(sc);
        //if (description!=null && description!="") oo.setDescription(description); // only one??
        //System.out.print("."); 
        
        // set name cannot be null
        oo.addName(URI); 
        
        // TODO set imports difficult to supply the OMV version of the imported ontology according to our architecture
        Vector<OMVOntology> imports = listImports(URI, os);
        for (OMVOntology ooImport : imports) {
             oo.addUseImports(ooImport);
        }
                
        //set number of axioms ?(RDF)? TODO check that
        oo.setNumberOfAxioms((int) os.getNumberOfStatement(URI));
        
        //TODO set keywords from sofia topic study
        //Topic t = sc.listTopic();
        //oo.setKeywords(t.toString());
        
        //TODO set prior version from duplication study (M: can do simpler)
        //Iterator<OMVontology> pvIt = sc.listPriorVersion();
        //while (pvIt.hasNext()) {
        //OMVOntology ooPv = (OMVOntology) pvIt.next();
        //oo.setHasPriorVersion(ooPv);
        //}
    
        return oo;
    }
    
   	
    private OMVOntologyLanguage getOntologyLanguage(String URI, SemanticContentSearch os) {
        String description = "";
        String language = "";
        Vector<String> languages = new Vector<String>();
        OMVOntologyLanguage ol = new OMVOntologyLanguage();
        String[] langs = os.getSemanticContentLanguages(URI);
        boolean OWL = false, DAML = false, RDFS = false;
       for (String lang : langs){
            if(!languages.contains(lang)){
                languages.add(lang);
                if(lang.toLowerCase().matches("owl")){
                    description+= "- This ontology contains instantiation of either OWL:Class or OWL:Property or both \n";
                    OWL = true;
                }
                if(lang.toLowerCase().matches("daml+oil")){
                    description+= "- This ontology contains instantiation of either DAML:Class or DAML:Property or both \n";
                    DAML = true;
                }
                if(lang.toLowerCase().matches("rdf-s")){
                    description+= "- This ontology contains instantiation of RDFS:Class \n";
                    RDFS = true;
                }
            }
        }
        boolean already = false;
        if (RDFS) {language += "RDFS"; already = true;}
        if (OWL)  {language += (already?"_OWL":"OWL"); already = true;}
        if (DAML)  language += (already?"_DAML-OIL":"DAML-OIL");
        
        ol.setName(language);
        ol.setDescription(description);
        return ol;
    }
    
//    private OMVOntologyType getOntologyType(SemanticContent sc) {
//        
//        OMVOntologyType ot = new OMVOntologyType();
//        
//        boolean hasClasses = containsClasses(sc.listEntities());
//        boolean hasProperties = containsProperties(sc.listEntities());
//        boolean hasIndividuals = containsIndividuals(sc.listEntities());
//        
//        boolean hasSubClass = containsSubClassOf(sc.listEntityRelations());
//        boolean hasSubProperty = containsSubPropertyOf(sc.listEntityRelations());
//        // TODO : add domain and range...
//        
//        String description = "";
//        String name = "";
//        boolean tbox = false, taxo = false, rbox = false, abox = false;
//        
//        if(hasClasses){
//            tbox = true;
//            description+= "- This ontology contains declaration of classes\n";
//        }
//        
//        if(hasProperties){
//            tbox = true;
//            description+= "- This ontology contains declaration of properties\n";
//        }
//        
//        if ( hasSubClass ){
//           taxo = true;   
//            description+= "- This ontology contains hirarchy of classes\n" +
//                    "either belonging to its schema or imported from remote schema\n";
//        }
//        if( hasSubProperty ){
//        	rbox = true;
//        	description+= "- This ontology contatins hirarchy of properties\n "+
//                    "either belonging to its schema or imported from remote schema\n";
//        }
//        if (hasIndividuals){
//        	abox = true;
//        	description+= "- This ontology contains individual instatiating classe\n" +
//                    "either belonging to its schema or imported from remote schema\n";
//        }
//        
//        ot.setDescription(description);
//        boolean already = false;
//        if (tbox) {name = "TBox"; already = true;}
//        if (taxo) {name += (already?"_Taxonomy":"Taxonomy"); already = true;}
//        if (rbox) {name += (already?"_RBox":"RBox"); already = true;}
//        if (abox) {name += (already?"_ABox":"ABox"); already = true;}
//        ot.setName(name);
//        
//        return ot;
//    }
    
  
    public Vector<OMVOntology> listImports(String URI, SemanticContentSearch os){
    	Vector<OMVOntology> result = new Vector<OMVOntology>();
    	String[] imports = os.getImports(URI);
    	if (imports != null)
    	for (String imp : imports){
    		OMVOntology o = new OMVOntology();
    		o.setURI(imp);
    		o.setResourceLocator(imp);
    		o.addName(URI);
    		result.add(o);
    	}
    	return result;
    }
    
    
    // TODO : now assume only one peer, nothing from outside... need to change...
    private void generateOMVfile(String documentID, String fileName){
    	System.out.println("Generating OMV file for "+documentID);
    	OMVOntology oo;
    	//	OMVOntology oo = retrieveOMVDescription(documentID);
    	// if (oo == null) {
    	   Oyster2Connection oyster2Conn = Oyster2Manager.newConnection(false, "/opt/tomcat/webapps/WatsonWUI/OysterStuff/new store");
    	   oo = buildOMVdescription(documentID);
           oyster2Conn.register(oo);
           System.out.println(documentID+" "+fileName);
           oo.generateOMV2RDFFile(fileName);
           Oyster2Manager.closeConnection();	
    	// }
    }
    
	// need kaon to work in background 
	public String getOMV(String ontoURI){	
		System.out.println("building OMV file for: "+ontoURI);
		String DID = getLuceneDocument(ontoURI);
		if (DID==null) return null;
		String rep = "/data/watson/var/cache/";
		String name = DID.substring(9, 10)
        + "/"
        + DID.substring(10, 13)
        + "/"
        + DID.substring(13, 17)
        + "/"
        + DID.substring(17, 22)
        + "/"
        + DID.substring(22, 32)
        + "/"
        + DID.substring(32);
		String httppath = "http://kmi-web05.open.ac.uk:81/cache/";
		String ext = ".omv";
		generateOMVfile(ontoURI, rep+name+ext);
		System.out.println("Done: "+httppath+name+ext);
		return httppath+name+ext;
	}
	
}
