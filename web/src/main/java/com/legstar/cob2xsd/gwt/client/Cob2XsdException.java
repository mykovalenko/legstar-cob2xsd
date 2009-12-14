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
