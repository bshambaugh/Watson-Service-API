/*
 * JenaUtils.java
 *
 * Created on April 19, 2007, 1:12 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package uk.ac.open.kmi.watson.services.utils;

import com.hp.hpl.jena.db.DBConnection;
import com.hp.hpl.jena.db.IDBConnection;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
;

/**
 *
 * @author lg3388
 */
public class JenaUtils {
   
    public static final String CACHE = "http://kmi-web05.open.ac.uk:81/cache/";
    
    public static Model getModel(String name) { 
    	System.out.println("HERE!!! "+name);
    	  String className = "com.mysql.jdbc.Driver";         // path of driver class
			try {
				Class.forName(className);
			} catch (ClassNotFoundException e) {e.printStackTrace(); return null;}
			String DB_URL =     "jdbc:mysql://kmi-web05/watson_jena_beta1";  // URL of database
			String DB_USER =   "ose";                          // database user id
			String DB_PASSWD = "owl";                          // database password
			String DB =        "MySQL";                         // database type
			IDBConnection conn = new DBConnection( DB_URL, DB_USER, DB_PASSWD, DB );                              
			ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
			try{
				System.out.println("maker: "+maker);
				Model result = maker.openModel(name, true);
				// Model result = maker.openModelIfPresent(name);
				System.out.println("return "+result);
			    return result;
			}
			catch(Exception e){
				System.out.println(e);
			    e.printStackTrace();
			    return null;
			}  
    }    
}
