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
package com.legstar.cob2xsd.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.legstar.antlr.RecognizerException;
import com.legstar.cob2xsd.Cob2Xsd;
import com.legstar.cob2xsd.Cob2XsdModel;
import com.legstar.cob2xsd.XsdGenerationException;

@SuppressWarnings("serial")
public class Cob2xsdWebServlet extends HttpServlet {

	private static final Log LOG = LogFactory.getLog(Cob2xsdWebServlet.class
			.getName());

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		try {
			String cobolSource = req.getParameter("cobol");
			notifyByMail(cobolSource);
			LOG.info("Got a request");

			Cob2Xsd cob2xsd = new Cob2Xsd(new Cob2XsdModel());
			String xmlSchema = cob2xsd.translate(cobolSource);
			resp.setContentType("text/plain");
			resp.getWriter().println(xmlSchema);
		} catch (RecognizerException e) {
			throw new IOException(e.getMessage());
		} catch (XsdGenerationException e) {
			throw new IOException(e.getMessage());
		}
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.sendRedirect("/");
	}

	public void notifyByMail(final String cobolSource) {
		try {
			UserService userService = UserServiceFactory.getUserService();
			User user = userService.getCurrentUser();
			Properties props = new Properties();
			Session session = Session.getDefaultInstance(props, null);
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress("fady.moussallam@gmail.com",
					"legsem.com Admin"));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
					"admin@legsem.com", "legsem.com Admin"));
			msg.setSubject("User " + user.getEmail()
					+ " has tried cob2xsd on the cloud");
			msg.setText(cobolSource);
			Transport.send(msg);
		} catch (UnsupportedEncodingException e) {
			LOG.error("Notification failed", e);
		} catch (MessagingException e) {
			LOG.error("Notification failed", e);
		}

	}
}
