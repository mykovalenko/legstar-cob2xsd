package com.legstar.cob2xsd.gwt.client;

import java.io.Serializable;
import java.util.List;

/**
 * Something went wrong running the backend service.
 *
 */
public class Cob2XsdException extends Exception implements Serializable {

	/** serial ID. */
	private static final long serialVersionUID = -2998284863711460943L;
	
	/** List of error messages. */
	private List < String > errorMessages;
	
	public Cob2XsdException() {
		
	}

	public Cob2XsdException(final List < String > errorMessages) {
		this.errorMessages = errorMessages;
	}

	public List < String > getErrorMessages() {
		return errorMessages;
	}

}
