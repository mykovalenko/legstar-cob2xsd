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
package com.legstar.cob2xsd.gwt.server;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.legstar.antlr.RecognizerException;
import com.legstar.cob2xsd.Cob2XsdContext;
import com.legstar.cob2xsd.CobolStructureToXsd;
import com.legstar.cob2xsd.XsdGenerationException;
import com.legstar.cob2xsd.Cob2XsdContext.CodeFormat;
import com.legstar.cob2xsd.gwt.client.Cob2XsdContextClone;
import com.legstar.cob2xsd.gwt.client.Cob2XsdException;
import com.legstar.cob2xsd.gwt.client.Cob2XsdService;
import com.legstar.cob2xsd.gwt.client.Cob2XsdServiceReply;
import com.legstar.cob2xsd.web.Cob2xsdWebServlet;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class Cob2XsdServiceImpl extends RemoteServiceServlet implements
		Cob2XsdService {

	private static final Log LOG = LogFactory.getLog(Cob2xsdWebServlet.class.getName());

	public Cob2XsdServiceReply cob2xsdServer(
			final String cobolSource,
			final Cob2XsdContextClone context) throws Cob2XsdException {
		notifyByMail(cobolSource);
		// Moving from GAE 1.26 to 1.3.5 the internal JDK xalan does not seem
		// to be found (at least locally). Either uncomment next line or add explicit
		// xalan.jar in dependencies
		System.setProperty("javax.xml.transform.TransformerFactory", "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
		LOG.info("Got a request");
		
		List < String > errorMessages = new ArrayList < String >();
		Cob2XsdServiceReply reply;
		try {
			CobolStructureToXsd cob2xsd = new CobolStructureToXsd(cloneContext(context));
			String xmlSchema = cob2xsd.translate(cobolSource);
			errorMessages = cob2xsd.getErrorHistory();
			reply = new Cob2XsdServiceReply(xmlSchema, errorMessages);
		} catch (RecognizerException e) {
			errorMessages.add(e.getMessage());
			throw new Cob2XsdException(errorMessages);
		} catch (XsdGenerationException e) {
			errorMessages.add(e.getMessage());
			throw new Cob2XsdException(errorMessages);
		}
		return reply;
	}
	
	/**
	 * The GWT Client code cannot use classes from a standard jar so we
	 * have to clone the context.
	 * @param contextClone the context clone 
	 * @return
	 */
	public Cob2XsdContext cloneContext(final Cob2XsdContextClone contextClone) {
		Cob2XsdContext context = new Cob2XsdContext();
		context.setCodeFormat(CodeFormat.valueOf(contextClone.getCodeFormat().toString()));
		context.setStartColumn(contextClone.getStartColumn());
        context.setEndColumn(contextClone.getEndColumn());
		context.setTargetNamespace(contextClone.getTargetNamespace());
		context.setXsdEncoding(contextClone.getXsdEncoding());
		context.setNameConflictPrependParentName(contextClone.nameConflictPrependParentName());
		context.setMapConditionsToFacets(contextClone.mapConditionsToFacets());
		context.setElementNamesStartWithUppercase(contextClone.elementNamesStartWithUppercase());
		context.setAddLegStarAnnotations(contextClone.addLegStarAnnotations());
        context.setCurrencySign(contextClone.getCurrencySign());
		context.setCurrencySymbol(contextClone.getCurrencySymbol());
		context.setDecimalPointIsComma(contextClone.decimalPointIsComma());
		context.setNSymbolDbcs(contextClone.nSymbolDbcs());
		context.setQuoteIsQuote(contextClone.quoteIsQuote());
		return context;
	}
	
	public void notifyByMail(final String cobolSource) {
        try {
			UserService userService = UserServiceFactory.getUserService();
			User user = userService.getCurrentUser();
			Properties props = new Properties();
			Session session = Session.getDefaultInstance(props, null);
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress("fady.moussallam@gmail.com", "legsem.com Admin"));
			msg.addRecipient(Message.RecipientType.TO,
			                 new InternetAddress("admin@legsem.com", "legsem.com Admin"));
			msg.setSubject("User " + user.getEmail() + " has tried cob2xsd on the cloud");
			msg.setText(cobolSource);
			Transport.send(msg);
		} catch (UnsupportedEncodingException e) {
			LOG.error("Notification failed", e);
		} catch (MessagingException e) {
			LOG.error("Notification failed", e);
		}
		
	}
}