package com.legstar.cob2xsd.gwt.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>Cob2XsdService</code>.
 */
public interface Cob2XsdServiceAsync {
	void cob2xsdServer(String cobolSource, Cob2XsdContextClone context, AsyncCallback < Cob2XsdServiceReply > callback);
}
