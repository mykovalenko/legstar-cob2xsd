package com.legstar.cob2xsd.gwt.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("cob2xsd")
public interface Cob2XsdService extends RemoteService {
	
	Cob2XsdServiceReply cob2xsdServer(
			final String cobolSource,
			final Cob2XsdContextClone context) throws Cob2XsdException;
}
