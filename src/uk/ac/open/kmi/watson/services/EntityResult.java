package uk.ac.open.kmi.watson.services;

import java.io.Serializable;

/**
 * Contains information about entities in the result of a search.
 * @author mda99
 */
public class EntityResult implements Serializable {
	
	private String URI;
	private String SCURI;
	private String type;
	private String[] labels;
	private String comment;
	private double score;
	
	private String[][] relationFrom;
	private String[][] relationTo;
	private String[][] literals;

	/** returns the label of the entity, or null if this information has not been requested */
	public String[] getLabels() {
		return labels;
	}

	/** set the label of the entity. Not usable in the client API. */
	public void setLabels(String[] labels) {
		this.labels = labels;
	}

	/** build the entity from its URI **/
	public EntityResult(String URI){
		this.URI = URI;
	}
	
	/** returns the URI of the entity */
	public String getURI(){
		return URI;
	}

	/** set the URI of the entity. Not useable in the client API. */
	public void setURI(String URI){
		this.URI = URI;
	}

	/** returns the type (class, property or individual) of the entity, or null if this information has not been requested */
	public String getType() {
		return type;
	}
	
	/** set the type of the entity. Not usable in the client API. */
	public void setType(String type) {
		this.type = type;
	}

	/** returns the relations to literals of the entity, or null if this information has not been requested */
	public String[][] getLiterals() {
		return literals;
	}

	/** set the literal relations of the entity. Not usable in the client API. */
	public void setLiterals(String[][] literals) {
		this.literals = literals;
	}

	/** returns the relations to the entity, i.e the property and subject of statements for which the entity is object, or null if this information has not been requested */
	public String[][] getRelationTo() {
		return relationTo;
	}

	/** set the relations to the entity. Not usable in the client API. */
	public void setRelationTo(String[][] relationTo) {
		this.relationTo = relationTo;
	}

	/** returns the relations from the entity, i.e. the property and object of statements for which the entity is subject, or null if this information has not been requested */
	public String[][] getRelationFrom() {
		return relationFrom;
	}
	
	/** set the relations from the entity. Not usable in the client API. */
	public void setRelationFrom(String[][] relationFrom) {
		this.relationFrom = relationFrom;
	}

	/** returns the comment of the entity, or null if this information has not been requested */
	public String getComment() {
		return comment;
	}

	/** set the comment of the entity. Not usable in the client API. */
	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getSCURI() {
		return SCURI;
	}

	public void setSCURI(String ontologyURI) {
		SCURI = ontologyURI;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}
	
}
