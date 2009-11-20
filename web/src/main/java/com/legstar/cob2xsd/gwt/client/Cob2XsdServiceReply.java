package com.legstar.cob2xsd.gwt.client;

import java.io.Serializable;
import java.util.List;

public class Cob2XsdServiceReply implements Serializable {
	
	/** serial ID. 	 */
	private static final long serialVersionUID = 3707586858950614124L;

	/** XML Schema produced. */
	private String xsdSchema;
	
	/** List of error messages. */
	private List < String > errorMessages;
	
	public Cob2XsdServiceReply() {
		
	}
	
	public Cob2XsdServiceReply(final String xsdSchema, final List < String > errorMessages) {
		this.xsdSchema = xsdSchema;
		this.errorMessages = errorMessages;
	}

	public String getXsdSchema() {
		return xsdSchema;
	}

	public List < String > getErrorMessages() {
		return errorMessages;
	}

}
