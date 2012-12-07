package uk.ac.open.kmi.watson.services.dist;

import java.rmi.RemoteException;
import java.util.Vector;

import javax.xml.rpc.ServiceException;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import uk.ac.open.kmi.watson.clientapi.EntitySearchServiceLocator;
import uk.ac.open.kmi.watson.services.EntityResult;
import uk.ac.open.kmi.watson.services.SearchConf;


/**
 * Service for searching and inspecting entities.
 * @author mda99
 */
public class EntitySearch extends WatsonService {
	
	protected static String[] serviceLocations = new String[] {
		"http://cupboard.kmi.open.ac.uk:8081/watson-ws-v2/services/urn:EntitySearch",
		"http://watson.kmi.open.ac.uk/watson-ws-lod1/services/urn:EntitySearch",
		"http://smartproducts1.kmi.open.ac.uk:8080/watson-ws-v2/services/urn:EntitySearch",
	};
	
	static uk.ac.open.kmi.watson.clientapi.EntitySearch[] ess = new uk.ac.open.kmi.watson.clientapi.EntitySearch[serviceLocations.length];
	
	private static Cache isIn;
	
	public EntitySearch(){
		 if (isIn == null) { 
		    	CacheManager cm = CacheManager.create();
		    	cm.addCache("URICache");
		    	isIn = cm.getCache("isIn");
		    }
		 for (int i = 0; i < serviceLocations.length; i++){
			 EntitySearchServiceLocator locator = new EntitySearchServiceLocator();
			 locator.setUrnEntitySearchEndpointAddress(serviceLocations[i]);
			 try {
				ess[i] =  locator.getUrnEntitySearch();
			} catch (ServiceException e) {
				e.printStackTrace();
			}
		 }
	}
        
    public EntityResult[] getEntitiesByKeyword(String onto, String kw, SearchConf conf){
    	Element el = isIn.get(onto);
    	uk.ac.open.kmi.watson.clientapi.EntitySearch service = null;
    	uk.ac.open.kmi.watson.clientapi.SearchConf conf2 = tranformConfFromServerToClient(conf);
    	if (el != null) {
    		service = (uk.ac.open.kmi.watson.clientapi.EntitySearch)el.getValue();
    		try {
				return transformEntityResultArrayFromClientToServer(service.getEntitiesByKeyword(onto, kw, conf2));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    	}
    	else {
    		for (uk.ac.open.kmi.watson.clientapi.EntitySearch s : ess){
    			EntityResult[] toRet = null;
    			try {
					toRet = transformEntityResultArrayFromClientToServer(service.getEntitiesByKeyword(onto, kw, conf2));
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				if (toRet!=null){
					isIn.put(new Element(onto, s));
					return toRet;
				}
    		}
    	}
     	return null;
	  }	
    
    public EntityResult[] getEntitiesByStructuredQuery(String s, String p, String o, SearchConf conf){
     	return null;
    }
  
    public EntityResult[] getAnyEntityByKeyword(String kw, SearchConf conf){
    	return null;
    }
    
    public String[][] getLiteralsByKeyword(String ontoURI, String keyword){
    	Element el = isIn.get(ontoURI);
    	uk.ac.open.kmi.watson.clientapi.EntitySearch service = null;
    	if (el != null) {
    		service = (uk.ac.open.kmi.watson.clientapi.EntitySearch)el.getValue();
    		try {
				return service.getLiteralsByKeyword(ontoURI, keyword);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    	}
    	else {
    		for (uk.ac.open.kmi.watson.clientapi.EntitySearch s : ess){
    			String[][] toRet = null;
    			try {
					toRet = s.getLiteralsByKeyword(ontoURI, keyword);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				if (toRet!=null){
					isIn.put(new Element(ontoURI, s));
					return toRet;
				}
    		}
    	}
     	return null;
    }

    public String getType(String ontoURI, String entityURI) {
    	Element el = isIn.get(ontoURI);
    	uk.ac.open.kmi.watson.clientapi.EntitySearch service = null;
    	if (el != null) {
    		service = (uk.ac.open.kmi.watson.clientapi.EntitySearch)el.getValue();
    		try {
				return service.getType(ontoURI, entityURI);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    	}
    	else {
    		for (uk.ac.open.kmi.watson.clientapi.EntitySearch s : ess){
    			String toRet = null;
    			try {
					toRet = s.getType(ontoURI, entityURI);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				if (toRet!=null){
					isIn.put(new Element(ontoURI, s));
					return toRet;
				}
    		}
    	}
     	return null;
    }

    public String[] getLabels(String ontoURI, String entityURI){
    	Element el = isIn.get(ontoURI);
    	uk.ac.open.kmi.watson.clientapi.EntitySearch service = null;
    	if (el != null) {
    		service = (uk.ac.open.kmi.watson.clientapi.EntitySearch)el.getValue();
    		try {
				return service.getLabels(ontoURI, entityURI);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    	}
    	else {
    		for (uk.ac.open.kmi.watson.clientapi.EntitySearch s : ess){
    			String[] toRet = null;
    			try {
					toRet = s.getLabels(ontoURI, entityURI);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				if (toRet!=null){
					isIn.put(new Element(ontoURI, s));
					return toRet;
				}
    		}
    	}
     	return null;
    }
 
    public String[] getComments(String ontoURI, String entityURI){
    	Element el = isIn.get(ontoURI);
    	uk.ac.open.kmi.watson.clientapi.EntitySearch service = null;
    	if (el != null) {
    		service = (uk.ac.open.kmi.watson.clientapi.EntitySearch)el.getValue();
    		try {
				return service.getComments(ontoURI, entityURI);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    	}
    	else {
    		for (uk.ac.open.kmi.watson.clientapi.EntitySearch s : ess){
    			String[] toRet = null;
    			try {
					toRet = s.getComments(ontoURI, entityURI);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				if (toRet!=null){
					isIn.put(new Element(ontoURI, s));
					return toRet;
				}
    		}
    	}
     	return null;
    }

    // TODO: need parallel calls...
    public String[] getBelongsTo(String entityURI){
    	Vector<String> result = new Vector<String>();
    	for (uk.ac.open.kmi.watson.clientapi.EntitySearch es : ess){
    		String[] res = null;
			try {
				res = es.getBelongsTo(entityURI);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    		if (res!=null){
    			for (String r : res){
    				if (r!=null) {
    					result.add(r);
    				}
    			}
    		}
    	}
    	return toArray(result);
    }
    
    private String[] toArray(Vector<String> v) {
		String[] a = new String[v.size()];
		for(int i = 0; i < v.size(); i++)
			a[i] = v.elementAt(i);
		return a;
	}

    public String[][] getRelationsFrom(String ontoURI, String entityURI){
    	Element el = isIn.get(ontoURI);
    	uk.ac.open.kmi.watson.clientapi.EntitySearch service = null;
    	if (el != null) {
    		service = (uk.ac.open.kmi.watson.clientapi.EntitySearch)el.getValue();
    		try {
				return service.getRelationsFrom(ontoURI, entityURI);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    	}
    	else {
    		for (uk.ac.open.kmi.watson.clientapi.EntitySearch s : ess){
    			String[][] toRet = null;
    			try {
					toRet = s.getRelationsFrom(ontoURI, entityURI);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				if (toRet!=null){
					isIn.put(new Element(ontoURI, s));
					return toRet;
				}
    		}
    	}
     	return null;
    }
 
    public String[][] getRelationsTo(String ontoURI, String entityURI){
    	Element el = isIn.get(ontoURI);
    	uk.ac.open.kmi.watson.clientapi.EntitySearch service = null;
    	if (el != null) {
    		service = (uk.ac.open.kmi.watson.clientapi.EntitySearch)el.getValue();
    		try {
				return service.getRelationsTo(ontoURI, entityURI);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    	}
    	else {
    		for (uk.ac.open.kmi.watson.clientapi.EntitySearch s : ess){
    			String[][] toRet = null;
    			try {
					toRet = s.getRelationsTo(ontoURI, entityURI);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				if (toRet!=null){
					isIn.put(new Element(ontoURI, s));
					return toRet;
				}
    		}
    	}
     	return null;
     	}
 
    public String[][] getLiteralsFor(String ontoURI, String entityURI){
     	Element el = isIn.get(ontoURI);
    	uk.ac.open.kmi.watson.clientapi.EntitySearch service = null;
    	if (el != null) {
    		service = (uk.ac.open.kmi.watson.clientapi.EntitySearch)el.getValue();
    		try {
				return service.getLiteralsFor(ontoURI, entityURI);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    	}
    	else {
    		for (uk.ac.open.kmi.watson.clientapi.EntitySearch s : ess){
    			String[][] toRet = null;
    			try {
					toRet = s.getLiteralsFor(ontoURI, entityURI);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				if (toRet!=null){
					isIn.put(new Element(ontoURI, s));
					return toRet;
				}
    		}
    	}
     	return null;
    }
    
    public String[] getSubClasses(String ontoURI, String entityURI){
     	Element el = isIn.get(ontoURI);
    	uk.ac.open.kmi.watson.clientapi.EntitySearch service = null;
    	if (el != null) {
    		service = (uk.ac.open.kmi.watson.clientapi.EntitySearch)el.getValue();
    		try {
				return service.getSubClasses(ontoURI, entityURI);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    	}
    	else {
    		for (uk.ac.open.kmi.watson.clientapi.EntitySearch s : ess){
    			String[] toRet = null;
    			try {
					toRet = s.getSubClasses(ontoURI, entityURI);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				if (toRet!=null){
					isIn.put(new Element(ontoURI, s));
					return toRet;
				}
    		}
    	}
     	return null;
    }
    
    public String[] getAllSubClasses(String ontoURI, String entityURI){
     	Element el = isIn.get(ontoURI);
    	uk.ac.open.kmi.watson.clientapi.EntitySearch service = null;
    	if (el != null) {
    		service = (uk.ac.open.kmi.watson.clientapi.EntitySearch)el.getValue();
    		try {
				return service.getAllSubClasses(ontoURI, entityURI);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    	}
    	else {
    		for (uk.ac.open.kmi.watson.clientapi.EntitySearch s : ess){
    			String[] toRet = null;
    			try {
					toRet = s.getAllSubClasses(ontoURI, entityURI);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				if (toRet!=null){
					isIn.put(new Element(ontoURI, s));
					return toRet;
				}
    		}
    	}
     	return null;
    }
        
    public String[] getSuperClasses(String ontoURI, String entityURI){
     	Element el = isIn.get(ontoURI);
    	uk.ac.open.kmi.watson.clientapi.EntitySearch service = null;
    	if (el != null) {
    		service = (uk.ac.open.kmi.watson.clientapi.EntitySearch)el.getValue();
    		try {
				return service.getSuperClasses(ontoURI, entityURI);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    	}
    	else {
    		for (uk.ac.open.kmi.watson.clientapi.EntitySearch s : ess){
    			String[] toRet = null;
    			try {
					toRet = s.getSuperClasses(ontoURI, entityURI);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				if (toRet!=null){
					isIn.put(new Element(ontoURI, s));
					return toRet;
				}
    		}
    	}
     	return null;
    }
    
    public String[] getAllSuperClasses(String ontoURI, String entityURI){
     	Element el = isIn.get(ontoURI);
    	uk.ac.open.kmi.watson.clientapi.EntitySearch service = null;
    	if (el != null) {
    		service = (uk.ac.open.kmi.watson.clientapi.EntitySearch)el.getValue();
    		try {
				return service.getAllSuperClasses(ontoURI, entityURI);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    	}
    	else {
    		for (uk.ac.open.kmi.watson.clientapi.EntitySearch s : ess){
    			String[] toRet = null;
    			try {
					toRet = s.getAllSuperClasses(ontoURI, entityURI);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				if (toRet!=null){
					isIn.put(new Element(ontoURI, s));
					return toRet;
				}
    		}
    	}
     	return null;
    }
    
     public String[] getSubProperties(String ontoURI, String entityURI){
      	Element el = isIn.get(ontoURI);
    	uk.ac.open.kmi.watson.clientapi.EntitySearch service = null;
    	if (el != null) {
    		service = (uk.ac.open.kmi.watson.clientapi.EntitySearch)el.getValue();
    		try {
				return service.getSubProperties(ontoURI, entityURI);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    	}
    	else {
    		for (uk.ac.open.kmi.watson.clientapi.EntitySearch s : ess){
    			String[] toRet = null;
    			try {
					toRet = s.getSubProperties(ontoURI, entityURI);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				if (toRet!=null){
					isIn.put(new Element(ontoURI, s));
					return toRet;
				}
    		}
    	}
     	return null;
    }
 
    public String[] getAllSubProperties(String ontoURI, String entityURI){
     	Element el = isIn.get(ontoURI);
    	uk.ac.open.kmi.watson.clientapi.EntitySearch service = null;
    	if (el != null) {
    		service = (uk.ac.open.kmi.watson.clientapi.EntitySearch)el.getValue();
    		try {
				return service.getAllSubProperties(ontoURI, entityURI);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    	}
    	else {
    		for (uk.ac.open.kmi.watson.clientapi.EntitySearch s : ess){
    			String[] toRet = null;
    			try {
					toRet = s.getAllSubProperties(ontoURI, entityURI);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				if (toRet!=null){
					isIn.put(new Element(ontoURI, s));
					return toRet;
				}
    		}
    	}
     	return null;
    }
    
    public String[] getSuperProperties(String ontoURI, String entityURI){
     	Element el = isIn.get(ontoURI);
    	uk.ac.open.kmi.watson.clientapi.EntitySearch service = null;
    	if (el != null) {
    		service = (uk.ac.open.kmi.watson.clientapi.EntitySearch)el.getValue();
    		try {
				return service.getSuperProperties(ontoURI, entityURI);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    	}
    	else {
    		for (uk.ac.open.kmi.watson.clientapi.EntitySearch s : ess){
    			String[] toRet = null;
    			try {
					toRet = s.getSuperProperties(ontoURI, entityURI);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				if (toRet!=null){
					isIn.put(new Element(ontoURI, s));
					return toRet;
				}
    		}
    	}
     	return null;
    }
 
    public String[] getAllSuperProperties(String ontoURI, String entityURI){
     	Element el = isIn.get(ontoURI);
    	uk.ac.open.kmi.watson.clientapi.EntitySearch service = null;
    	if (el != null) {
    		service = (uk.ac.open.kmi.watson.clientapi.EntitySearch)el.getValue();
    		try {
				return service.getAllSuperProperties(ontoURI, entityURI);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    	}
    	else {
    		for (uk.ac.open.kmi.watson.clientapi.EntitySearch s : ess){
    			String[] toRet = null;
    			try {
					toRet = s.getAllSuperProperties(ontoURI, entityURI);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				if (toRet!=null){
					isIn.put(new Element(ontoURI, s));
					return toRet;
				}
    		}
    	}
     	return null;
    }
     
    public String[] getEquivalentClasses(String ontoURI, String entityURI){
     	Element el = isIn.get(ontoURI);
    	uk.ac.open.kmi.watson.clientapi.EntitySearch service = null;
    	if (el != null) {
    		service = (uk.ac.open.kmi.watson.clientapi.EntitySearch)el.getValue();
    		try {
				return service.getEquivalentClasses(ontoURI, entityURI);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    	}
    	else {
    		for (uk.ac.open.kmi.watson.clientapi.EntitySearch s : ess){
    			String[] toRet = null;
    			try {
					toRet = s.getEquivalentClasses(ontoURI, entityURI);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				if (toRet!=null){
					isIn.put(new Element(ontoURI, s));
					return toRet;
				}
    		}
    	}
     	return null;
    }
    
    public String[] getInstances(String ontoURI, String entityURI){
     	Element el = isIn.get(ontoURI);
    	uk.ac.open.kmi.watson.clientapi.EntitySearch service = null;
    	if (el != null) {
    		service = (uk.ac.open.kmi.watson.clientapi.EntitySearch)el.getValue();
    		try {
				return service.getInstances(ontoURI, entityURI);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    	}
    	else {
    		for (uk.ac.open.kmi.watson.clientapi.EntitySearch s : ess){
    			String[] toRet = null;
    			try {
					toRet = s.getInstances(ontoURI, entityURI);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				if (toRet!=null){
					isIn.put(new Element(ontoURI, s));
					return toRet;
				}
    		}
    	}
     	return null;
    }

    public String[] getAllInstances(String ontoURI, String entityURI){
    	Element el = isIn.get(ontoURI);
    	uk.ac.open.kmi.watson.clientapi.EntitySearch service = null;
    	if (el != null) {
    		service = (uk.ac.open.kmi.watson.clientapi.EntitySearch)el.getValue();
    		try {
				return service.getAllInstances(ontoURI, entityURI);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    	}
    	else {
    		for (uk.ac.open.kmi.watson.clientapi.EntitySearch s : ess){
    			String[] toRet = null;
    			try {
					toRet = s.getAllInstances(ontoURI, entityURI);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				if (toRet!=null){
					isIn.put(new Element(ontoURI, s));
					return toRet;
				}
    		}
    	}
     	return null;
    }
    
    public String[] getClasses(String ontoURI, String entityURI){
    	Element el = isIn.get(ontoURI);
    	uk.ac.open.kmi.watson.clientapi.EntitySearch service = null;
    	if (el != null) {
    		service = (uk.ac.open.kmi.watson.clientapi.EntitySearch)el.getValue();
    		try {
				return service.getClasses(ontoURI, entityURI);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    	}
    	else {
    		for (uk.ac.open.kmi.watson.clientapi.EntitySearch s : ess){
    			String[] toRet = null;
    			try {
					toRet = s.getClasses(ontoURI, entityURI);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				if (toRet!=null){
					isIn.put(new Element(ontoURI, s));
					return toRet;
				}
    		}
    	}
     	return null;
    }
  
    public String[] getAllClasses(String ontoURI, String entityURI){
       	Element el = isIn.get(ontoURI);
    	uk.ac.open.kmi.watson.clientapi.EntitySearch service = null;
    	if (el != null) {
    		service = (uk.ac.open.kmi.watson.clientapi.EntitySearch)el.getValue();
    		try {
				return service.getAllClasses(ontoURI, entityURI);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    	}
    	else {
    		for (uk.ac.open.kmi.watson.clientapi.EntitySearch s : ess){
    			String[] toRet = null;
    			try {
					toRet = s.getAllClasses(ontoURI, entityURI);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				if (toRet!=null){
					isIn.put(new Element(ontoURI, s));
					return toRet;
				}
    		}
    	}
     	return null;
    }
    
    
    public String[] getDomain(String ontoURI, String entityURI){
       	Element el = isIn.get(ontoURI);
    	uk.ac.open.kmi.watson.clientapi.EntitySearch service = null;
    	if (el != null) {
    		service = (uk.ac.open.kmi.watson.clientapi.EntitySearch)el.getValue();
    		try {
				return service.getDomain(ontoURI, entityURI);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    	}
    	else {
    		for (uk.ac.open.kmi.watson.clientapi.EntitySearch s : ess){
    			String[] toRet = null;
    			try {
					toRet = s.getDomain(ontoURI, entityURI);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				if (toRet!=null){
					isIn.put(new Element(ontoURI, s));
					return toRet;
				}
    		}
    	}
     	return null;
    }
    
    public String[] getAllDomain(String ontoURI, String entityURI){
       	Element el = isIn.get(ontoURI);
    	uk.ac.open.kmi.watson.clientapi.EntitySearch service = null;
    	if (el != null) {
    		service = (uk.ac.open.kmi.watson.clientapi.EntitySearch)el.getValue();
    		try {
				return service.getAllDomain(ontoURI, entityURI);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    	}
    	else {
    		for (uk.ac.open.kmi.watson.clientapi.EntitySearch s : ess){
    			String[] toRet = null;
    			try {
					toRet = s.getAllDomain(ontoURI, entityURI);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				if (toRet!=null){
					isIn.put(new Element(ontoURI, s));
					return toRet;
				}
    		}
    	}
     	return null;
    }
    
    public String[] getRange(String ontoURI, String entityURI){
       	Element el = isIn.get(ontoURI);
    	uk.ac.open.kmi.watson.clientapi.EntitySearch service = null;
    	if (el != null) {
    		service = (uk.ac.open.kmi.watson.clientapi.EntitySearch)el.getValue();
    		try {
				return service.getRange(ontoURI, entityURI);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    	}
    	else {
    		for (uk.ac.open.kmi.watson.clientapi.EntitySearch s : ess){
    			String[] toRet = null;
    			try {
					toRet = s.getRange(ontoURI, entityURI);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				if (toRet!=null){
					isIn.put(new Element(ontoURI, s));
					return toRet;
				}
    		}
    	}
     	return null;
    }
 
    public String[] getAllRange(String ontoURI, String entityURI){
       	Element el = isIn.get(ontoURI);
    	uk.ac.open.kmi.watson.clientapi.EntitySearch service = null;
    	if (el != null) {
    		service = (uk.ac.open.kmi.watson.clientapi.EntitySearch)el.getValue();
    		try {
				return service.getAllRange(ontoURI, entityURI);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    	}
    	else {
    		for (uk.ac.open.kmi.watson.clientapi.EntitySearch s : ess){
    			String[] toRet = null;
    			try {
					toRet = s.getAllRange(ontoURI, entityURI);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				if (toRet!=null){
					isIn.put(new Element(ontoURI, s));
					return toRet;
				}
    		}
    	}
     	return null;
    }
    
    public String[] getAllDomainOf(String ontoURI, String entityURI){
       	Element el = isIn.get(ontoURI);
    	uk.ac.open.kmi.watson.clientapi.EntitySearch service = null;
    	if (el != null) {
    		service = (uk.ac.open.kmi.watson.clientapi.EntitySearch)el.getValue();
    		try {
				return service.getAllDomainOf(ontoURI, entityURI);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    	}
    	else {
    		for (uk.ac.open.kmi.watson.clientapi.EntitySearch s : ess){
    			String[] toRet = null;
    			try {
					toRet = s.getAllDomainOf(ontoURI, entityURI);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				if (toRet!=null){
					isIn.put(new Element(ontoURI, s));
					return toRet;
				}
    		}
    	}
     	return null;
    }
    
    public String[] getDomainOf(String ontoURI, String entityURI){
    	Element el = isIn.get(ontoURI);
    	uk.ac.open.kmi.watson.clientapi.EntitySearch service = null;
    	if (el != null) {
    		service = (uk.ac.open.kmi.watson.clientapi.EntitySearch)el.getValue();
    		try {
				return service.getDomainOf(ontoURI, entityURI);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    	}
    	else {
    		for (uk.ac.open.kmi.watson.clientapi.EntitySearch s : ess){
    			String[] toRet = null;
    			try {
					toRet = s.getDomainOf(ontoURI, entityURI);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				if (toRet!=null){
					isIn.put(new Element(ontoURI, s));
					return toRet;
				}
    		}
    	}
     	return null;
    }
    
    public String[] getRangeOf(String ontoURI, String entityURI){
       	Element el = isIn.get(ontoURI);
    	uk.ac.open.kmi.watson.clientapi.EntitySearch service = null;
    	if (el != null) {
    		service = (uk.ac.open.kmi.watson.clientapi.EntitySearch)el.getValue();
    		try {
				return service.getRangeOf(ontoURI, entityURI);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    	}
    	else {
    		for (uk.ac.open.kmi.watson.clientapi.EntitySearch s : ess){
    			String[] toRet = null;
    			try {
					toRet = s.getRangeOf(ontoURI, entityURI);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				if (toRet!=null){
					isIn.put(new Element(ontoURI, s));
					return toRet;
				}
    		}
    	}
     	return null;
    }
    
    public String[] getAllRangeOf(String ontoURI, String entityURI){
    	Element el = isIn.get(ontoURI);
    	uk.ac.open.kmi.watson.clientapi.EntitySearch service = null;
    	if (el != null) {
    		service = (uk.ac.open.kmi.watson.clientapi.EntitySearch)el.getValue();
    		try {
				return service.getAllRangeOf(ontoURI, entityURI);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    	}
    	else {
    		for (uk.ac.open.kmi.watson.clientapi.EntitySearch s : ess){
    			String[] toRet = null;
    			try {
					toRet = s.getAllRangeOf(ontoURI, entityURI);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				if (toRet!=null){
					isIn.put(new Element(ontoURI, s));
					return toRet;
				}
    		}
    	}
     	return null;
    }
    
    public String[] getSameIndividuals(String ontoURI, String entityURI){
       	Element el = isIn.get(ontoURI);
    	uk.ac.open.kmi.watson.clientapi.EntitySearch service = null;
    	if (el != null) {
    		service = (uk.ac.open.kmi.watson.clientapi.EntitySearch)el.getValue();
    		try {
				return service.getSameIndividuals(ontoURI, entityURI);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    	}
    	else {
    		for (uk.ac.open.kmi.watson.clientapi.EntitySearch s : ess){
    			String[] toRet = null;
    			try {
					toRet = s.getSameIndividuals(ontoURI, entityURI);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				if (toRet!=null){
					isIn.put(new Element(ontoURI, s));
					return toRet;
				}
    		}
    	}
     	return null;
    }
    
    public String[] getDifferentFrom(String ontoURI, String entityURI){
       	Element el = isIn.get(ontoURI);
    	uk.ac.open.kmi.watson.clientapi.EntitySearch service = null;
    	if (el != null) {
    		service = (uk.ac.open.kmi.watson.clientapi.EntitySearch)el.getValue();
    		try {
				return service.getDifferentFrom(ontoURI, entityURI);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    	}
    	else {
    		for (uk.ac.open.kmi.watson.clientapi.EntitySearch s : ess){
    			String[] toRet = null;
    			try {
					toRet = s.getDifferentFrom(ontoURI, entityURI);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				if (toRet!=null){
					isIn.put(new Element(ontoURI, s));
					return toRet;
				}
    		}
    	}
     	return null;
    }
    
    public String[] getDisjointWith(String ontoURI, String entityURI){
       	Element el = isIn.get(ontoURI);
    	uk.ac.open.kmi.watson.clientapi.EntitySearch service = null;
    	if (el != null) {
    		service = (uk.ac.open.kmi.watson.clientapi.EntitySearch)el.getValue();
    		try {
				return service.getDisjointWith(ontoURI, entityURI);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    	}
    	else {
    		for (uk.ac.open.kmi.watson.clientapi.EntitySearch s : ess){
    			String[] toRet = null;
    			try {
					toRet = s.getDisjointWith(ontoURI, entityURI);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				if (toRet!=null){
					isIn.put(new Element(ontoURI, s));
					return toRet;
				}
    		}
    	}
     	return null;
    }
    
    public String[] getIsDisjointWith(String ontoURI, String entityURI){
       	Element el = isIn.get(ontoURI);
    	uk.ac.open.kmi.watson.clientapi.EntitySearch service = null;
    	if (el != null) {
    		service = (uk.ac.open.kmi.watson.clientapi.EntitySearch)el.getValue();
    		try {
				return service.getIsDisjointWith(ontoURI, entityURI);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    	}
    	else {
    		for (uk.ac.open.kmi.watson.clientapi.EntitySearch s : ess){
    			String[] toRet = null;
    			try {
					toRet = s.getIsDisjointWith(ontoURI, entityURI);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				if (toRet!=null){
					isIn.put(new Element(ontoURI, s));
					return toRet;
				}
    		}
    	}
     	return null;
    }
    
    public String[] getAllDisjointWith(String ontoURI, String entityURI){
       	Element el = isIn.get(ontoURI);
    	uk.ac.open.kmi.watson.clientapi.EntitySearch service = null;
    	if (el != null) {
    		service = (uk.ac.open.kmi.watson.clientapi.EntitySearch)el.getValue();
    		try {
				return service.getAllDisjointWith(ontoURI, entityURI);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    	}
    	else {
    		for (uk.ac.open.kmi.watson.clientapi.EntitySearch s : ess){
    			String[] toRet = null;
    			try {
					toRet = s.getAllDisjointWith(ontoURI, entityURI);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				if (toRet!=null){
					isIn.put(new Element(ontoURI, s));
					return toRet;
				}
    		}
    	}
     	return null;
    }
           	
}
