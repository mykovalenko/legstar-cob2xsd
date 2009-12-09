package com.legstar.cob2xsd.gwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Cob2XsdWui implements EntryPoint {

	private VerticalPanel mainPanel = new VerticalPanel();

	private HorizontalPanel buttonsPanel = new HorizontalPanel();
	private Label instructions = new Label("Edit COBOL structure below then click the Translate button.");
	private Button translateButton = new Button("Translate");
	private Button optionsButton = new Button("Options...");

	private TabPanel inputFeedbackPanel = new TabPanel();
	private VerticalPanel rulerPanel = new VerticalPanel();
	private Label cobolRuler1 = new Label("0--------1---------2---------3---------4---------5---------6---------7---------8");
	private Label cobolRuler2 = new Label("12345678901234567890123456789012345678901234567890123456789012345678901234567890");
	private TextArea inputCobol = new TextArea();
	private TextArea outputXsd = new TextArea();
	private TextArea outputErrors = new TextArea();

	private TextBox xsdEncoding = new TextBox();
	private TextBox targetNamespace = new TextBox();
	private CheckBox elementNamesStartWithUppercase = new CheckBox();
	private CheckBox mapConditionsToFacets = new CheckBox();
	private CheckBox nameConflictPrependParentName = new CheckBox();

	private CheckBox addLegStarAnnotations = new CheckBox();
	private TextBox jaxbPackageName = new TextBox();
	private TextBox jaxbTypeClassesSuffix = new TextBox();
	
	private CheckBox decimalPointIsComma = new CheckBox();
    private TextBox currencySign = new TextBox();
	private TextBox currencySymbol = new TextBox();
	private CheckBox nSymbolDbcs = new CheckBox();
	private CheckBox quoteIsQuote = new CheckBox();

	private Cob2XsdServiceAsync cob2xsdSvc = GWT.create(Cob2XsdService.class);

	private static final String SAMPLE_COBOL_STRUCTURE 	=
		"       01 DFHCOMMAREA.\n"
		+ "           05 QUERY-DATA.\n"
		+ "              10 CUSTOMER-NAME               PIC X(20).\n"
		+ "              10 MAX-REPLIES                 PIC S9(4) COMP VALUE -1.\n"
		+ "                  88 UNLIMITED     VALUE -1.\n"
		+ "           05 REPLY-DATA.\n"
		+ "              10 REPLY-COUNT                 PIC 9(8) COMP-3.\n"
		+ "              10 CUSTOMER OCCURS 1 TO 100 DEPENDING ON REPLY-COUNT.\n"
		+ "                  15 CUSTOMER-ID             PIC 9(6).\n"
		+ "                  15 PERSONAL-DATA.\n"
		+ "                     20 CUSTOMER-NAME        PIC X(20).\n"
		+ "                     20 CUSTOMER-ADDRESS     PIC X(20).\n"
		+ "                     20 CUSTOMER-PHONE       PIC X(8).\n"
		+ "                  15 LAST-TRANS-DATE         PIC X(8).\n"
		+ "                  15 FILLER REDEFINES LAST-TRANS-DATE.\n"
		+ "                     20 LAST-TRANS-DAY       PIC X(2).\n"
		+ "                     20 FILLER               PIC X.\n"
		+ "                     20 LAST-TRANS-MONTH     PIC X(2).\n"
		+ "                     20 FILLER               PIC X.\n"
		+ "                     20 LAST-TRANS-YEAR      PIC X(2).\n"
		+ "                  15 LAST-TRANS-AMOUNT       PIC $9999.99.\n"
		+ "                  15 LAST-TRANS-COMMENT      PIC X(9).\n"
		;

	/**
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		/* Initialize options.*/
		optionsButton.addStyleDependentName("options");

		xsdEncoding.setText(Cob2XsdContextClone.DEFAULT_XSD_ENCODING);
		targetNamespace.setText(Cob2XsdContextClone.DEFAULT_TARGET_NAMESPACE);
		jaxbPackageName.setText(Cob2XsdContextClone.DEFAULT_JAXB_PACKAGE_NAME);
        currencySign.setText(Cob2XsdContextClone.DEFAULT_CURRENCY_SIGN);
		currencySymbol.setText(Cob2XsdContextClone.DEFAULT_CURRENCY_SYMBOL);

		xsdEncoding.setStyleName("xsdEncoding");
		targetNamespace.setStyleName("targetNamespace");
		jaxbPackageName.setStyleName("jaxbPackageName");
		jaxbTypeClassesSuffix.setStyleName("jaxbTypeClassesSuffix");
		currencySign.setStyleName("currencySign");
		currencySymbol.setStyleName("currencySymbol");


		/* Attach dialog box to options button. */
		final DialogBox dialogBox = createDialogBox();
		
		// Setup button to show the dialog Box
		optionsButton.addClickHandler(
				new ClickHandler() {
					public void onClick(ClickEvent sender) {
						dialogBox.center();
						dialogBox.show();
					}
				});
		
		// Setup button to launch server service
		translateButton.addClickHandler(
				new ClickHandler() {
					public void onClick(ClickEvent sender) {
						callServerService();
					}
				});
		
		/* Assemble the buttons panel.  */
		instructions.setStyleName("instructions");
		buttonsPanel.add(instructions);
		buttonsPanel.add(translateButton);
		buttonsPanel.add(optionsButton);
		buttonsPanel.setStyleName("buttonsPanel");
		
		/* Assemble the ruler panel */
		cobolRuler1.setStyleName("cobolRuler");
		cobolRuler2.setStyleName("cobolRuler");
		rulerPanel.add(cobolRuler1);
		rulerPanel.add(cobolRuler2);
		
		/* Style the text areas */
		inputCobol.addStyleName("inputCobol");
		inputCobol.setText(SAMPLE_COBOL_STRUCTURE);
		outputXsd.addStyleName("outputXsd");
		outputErrors.addStyleName("outputErrors");

		/* Assemble InputFeedback panel. All inner textareas are
		 * wrapped in panels otherwise style is not honored. */
		VerticalPanel cobolPanel = new VerticalPanel();
		cobolPanel.add(rulerPanel);
		cobolPanel.add(inputCobol);
		inputFeedbackPanel.add(cobolPanel, "COBOL source");
		FlowPanel flowpanel = new FlowPanel();
		flowpanel.add(outputXsd);
		inputFeedbackPanel.add(flowpanel, "XML Schema");
		flowpanel = new FlowPanel();
		flowpanel.add(outputErrors);
		inputFeedbackPanel.add(flowpanel, "Errors");
		inputFeedbackPanel.selectTab(0);

		// Assemble Actions panel.
		mainPanel.add(buttonsPanel);
		mainPanel.add(inputFeedbackPanel);
		mainPanel.addStyleName("mainPanel");
		translateButton.setFocus(true);

		// Associate the Main panel with the HTML host page.
		RootPanel.get("mainPanel").add(mainPanel);
	}

	/**
	 * Create the dialog box for options.
	 * 
	 * @return the new dialog box
	 */
	private DialogBox createDialogBox() {

		final DialogBox dialogBox = new DialogBox();
		dialogBox.ensureDebugId("optionsDialogBox");
		dialogBox.setText("Options");

		VerticalPanel dialogContents = new VerticalPanel();
		dialogBox.setWidget(dialogContents);
		
		// Create a table to layout the content
		Grid dialogGrid = new Grid(13, 2);
		dialogGrid.addStyleName("dialogGrid");
		
        int row = 0;
		/* -------------------------------------------------------------------
         * XML Schema related options
         * */
		dialogGrid.setWidget(row, 0, new Label("XML Schema encoding"));
		dialogGrid.setWidget(row, 1, xsdEncoding);
		row++;
		dialogGrid.setWidget(row, 0, new Label("Target namespace"));
		dialogGrid.setWidget(row, 1, targetNamespace);
		row++;
		dialogGrid.setWidget(row, 0, new Label("Element names start with uppercase"));
		dialogGrid.setWidget(row, 1, elementNamesStartWithUppercase);
		row++;
		dialogGrid.setWidget(row, 0, new Label("Map conditions to facets"));
		dialogGrid.setWidget(row, 1, mapConditionsToFacets);
		row++;
		dialogGrid.setWidget(row, 0, new Label("Name conflict prepend parent name"));
		dialogGrid.setWidget(row, 1, nameConflictPrependParentName);


        /* -------------------------------------------------------------------
         * LegStar annotations related options
         * */
		row++;
		dialogGrid.setWidget(row, 0, new Label("Add LegStar annotations"));
		dialogGrid.setWidget(row, 1, addLegStarAnnotations);
		row++;
		dialogGrid.setWidget(row, 0, new Label("JAXB package name"));
		dialogGrid.setWidget(row, 1, jaxbPackageName);
		row++;
		dialogGrid.setWidget(row, 0, new Label("JAXB type classes suffix"));
		dialogGrid.setWidget(row, 1, jaxbTypeClassesSuffix);

        /* -------------------------------------------------------------------
         * COBOL compiler related options
         * */
		row++;
		dialogGrid.setWidget(row, 0, new Label("Decimal point is comma"));
		dialogGrid.setWidget(row, 1, decimalPointIsComma);
		row++;
		dialogGrid.setWidget(row, 0, new Label("Currency sign"));
		dialogGrid.setWidget(row, 1, currencySign);
        row++;
        dialogGrid.setWidget(row, 0, new Label("Currency symbol"));
        dialogGrid.setWidget(row, 1, currencySymbol);
		row++;
		dialogGrid.setWidget(row, 0, new Label("National symbol dbcs"));
		dialogGrid.setWidget(row, 1, nSymbolDbcs);
		row++;
		dialogGrid.setWidget(row, 0, new Label("Quote is quote"));
		dialogGrid.setWidget(row, 1, quoteIsQuote);

		dialogContents.add(dialogGrid);

		Button closeButton = new Button("Close",
				new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
			}
		});
		FlowPanel closeButtonPanel = new FlowPanel();
		closeButtonPanel.add(closeButton);
		closeButtonPanel.addStyleName("closeButtonPanel");

		dialogContents.add(closeButtonPanel);

		return dialogBox;
	}
	
	/**
	 * Call the server service.
	 */
	private void callServerService() {
		// Initialize the service proxy.
		if (cob2xsdSvc == null) {
			cob2xsdSvc = GWT.create(Cob2XsdService.class);
		}
		
		// Cleanup previous results
		outputXsd.setText("Translating...");
		inputFeedbackPanel.selectTab(1);
		outputErrors.setText("");
		
		Cob2XsdContextClone context = createCob2XsdContext();
		String cobolSource = inputCobol.getText();

		// Set up the callback object.
		AsyncCallback < Cob2XsdServiceReply > callback = new AsyncCallback < Cob2XsdServiceReply >() {

			public void onFailure(Throwable caught) {
				if (caught instanceof Cob2XsdException) {
					StringBuilder sb = new StringBuilder();
					for (String errorMessage : ((Cob2XsdException) caught).getErrorMessages()) {
						sb.append(errorMessage + '\n');
					}
					outputErrors.setText(sb.toString());
				} else {
					outputErrors.setText(caught.getMessage());
				}
				inputFeedbackPanel.selectTab(2);
				
			}

			public void onSuccess(Cob2XsdServiceReply result) {
				outputXsd.setText(result.getXsdSchema());
				StringBuilder sb = new StringBuilder();
				for (String errorMessage : result.getErrorMessages()) {
					sb.append(errorMessage + '\n');
				}
				outputErrors.setText(sb.toString());
				inputFeedbackPanel.selectTab(1);
				
			}
		};

		// Make the call to the service.
		cob2xsdSvc.cob2xsdServer(cobolSource, context, callback);
	}
	
	/**
	 * Gather the options from the UI and create a context.
	 * @return a context using current options selected
	 */
	private Cob2XsdContextClone createCob2XsdContext() {
		Cob2XsdContextClone context = new Cob2XsdContextClone();
		context.setTargetNamespace(targetNamespace.getText());
		context.setXsdEncoding(xsdEncoding.getText());
		context.setNameConflictPrependParentName(nameConflictPrependParentName.getValue());
		context.setMapConditionsToFacets(mapConditionsToFacets.getValue());
		context.setElementNamesStartWithUppercase(elementNamesStartWithUppercase.getValue());
		context.setAddLegStarAnnotations(addLegStarAnnotations.getValue());
		context.setJaxbPackageName(jaxbPackageName.getText());
		context.setJaxbTypeClassesSuffix(jaxbTypeClassesSuffix.getText());
        context.setCurrencySign(currencySign.getText());
		context.setCurrencySymbol(currencySymbol.getText());
		context.setDecimalPointIsComma(decimalPointIsComma.getValue());
		context.setNSymbolDbcs(nSymbolDbcs.getValue());
		context.setQuoteIsQuote(quoteIsQuote.getValue());
		return context;
	}
}
