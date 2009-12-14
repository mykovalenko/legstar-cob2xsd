/*******************************************************************************
 * Copyright (c) 2009 LegSem.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     LegSem - initial API and implementation
 ******************************************************************************/
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
