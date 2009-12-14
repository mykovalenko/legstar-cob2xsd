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

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>Cob2XsdService</code>.
 */
public interface Cob2XsdServiceAsync {
	void cob2xsdServer(String cobolSource, Cob2XsdContextClone context, AsyncCallback < Cob2XsdServiceReply > callback);
}
