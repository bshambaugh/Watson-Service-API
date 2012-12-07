package uk.ac.open.kmi.watson.services.utils;

import java.util.Vector;

import uk.ac.open.kmi.watson.services.EntityResult;

public class Array {

	public static EntityResult[] toArray(Vector<EntityResult> toReturn) {
		EntityResult[] result = new EntityResult[toReturn.size()];
		for (int i = 0; i < toReturn.size(); i ++)
			result[i] = toReturn.elementAt(i);
		return result;
	}
	
	 public static String[][] toArray(Vector<String[]> v) {
			String[][] result = new String[v.size()][];
			for (int i = 0; i < v.size(); i++) result[i] = v.elementAt(i);
			return result;
	}
	
}
