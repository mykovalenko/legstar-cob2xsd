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

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.HtmlContainer;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.legstar.cob2xsd.gwt.client.Cob2XsdContextClone.CodeFormat;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Cob2XsdWui implements EntryPoint {

    private static final int MAIN_PANEL_WIDTH = 1024;
    private static final int INPUT_PANEL_WIDTH = 670;
    private static final int INPUT_PANEL_HEIGHT = 540;
    private static final int DIALOG_LABEL_WIDTH = 130;

    private FormData _formData = new FormData("-20");

    private TabPanel _inputFeedbackPanel;
    private TabItem _cobolTab;
    private TabItem _xsdTab;
    private TabItem _errorsTab;

    private Label _cobolRuler1;
    private Label _cobolRuler2;

    private Radio _freeRadio;
    private Radio _fixedRadio;
    private NumberField _startColumn;
    private NumberField _endColumn;

    private TextField<String> _xsdEncoding;
    private TextField<String> _xsdNamespace;
    private CheckBox _elementNamesStartWithUppercaseCheckBox;
    private CheckBox _mapConditionsToFacetsCheckBox;
    private CheckBox _nameConflictPrependParentNameCheckBox;
    private CheckBox _addLegStarAnnotationsCheckBox;

    private CheckBox _decimalPointIsCommaCheckBox;
    private TextField<String> _cobolCurrencySign;
    private TextField<String> _cobolCurrencySymbol;
    private CheckBox _nSymbolDbcsCheckBox;
    private CheckBox _quoteIsQuoteCheckBox;
    private Dialog _helpDialog;

    private TextArea _inputCobol;
    private TextArea _outputXsd;
    private TextArea _outputErrors;

    private Cob2XsdServiceAsync _cob2xsdSvc = GWT.create(Cob2XsdService.class);

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

        ContentPanel mainPanel = new ContentPanel();
        mainPanel.setHeading("COBOL Structure to XML Schema");  
        mainPanel.setLayout(new RowLayout(Orientation.VERTICAL));  
        mainPanel.setFrame(true);
        mainPanel.setWidth(MAIN_PANEL_WIDTH);

        mainPanel.getHeader().addTool(
                new ToolButton("x-tool-help",
                        new SelectionListener<IconButtonEvent>() {

                    @Override
                    public void componentSelected(IconButtonEvent ce) {
                        _helpDialog.show();
                    }

                }));

        /* Add the logo panel */
        HorizontalPanel logoPanel = new HorizontalPanel();
        logoPanel.setTableWidth("100%");

        Html logoImage = new Html(
                "<a href=\"http://www.legsem.com/legstar\">"
                + "<img src=\"images/legstar-logo-2008.png\" alt=\"LegStar\"  class=\"logo\"/>"
                + "</a> ");
        TableData td = new TableData();
        td.setHorizontalAlign(HorizontalAlignment.CENTER);
        logoPanel.add(logoImage, td);

        mainPanel.add(logoPanel);

        /* Add input panel */
        HorizontalPanel inputPanel = new HorizontalPanel();

        _inputFeedbackPanel = new TabPanel();
        _inputFeedbackPanel.addStyleName("tabPanel");
        _inputFeedbackPanel.setPlain(true);
        _inputFeedbackPanel.setWidth(INPUT_PANEL_WIDTH);
        _inputFeedbackPanel.setHeight(INPUT_PANEL_HEIGHT);

        _cobolRuler1 = new Label("0--------1---------2---------3---------4---------5---------6---------7---------8");
        _cobolRuler2 = new Label("12345678901234567890123456789012345678901234567890123456789012345678901234567890");
        _cobolRuler1.setStyleName("cobolRuler");
        _cobolRuler2.setStyleName("cobolRuler");

        _inputCobol = new TextArea();
        _inputCobol.addStyleName("inputCobol");
        _inputCobol.setValue(SAMPLE_COBOL_STRUCTURE);

        _cobolTab = new TabItem("COBOL");
        _cobolTab.setIcon(getIcon(Resources.ICONS.cobIcon()));
        _cobolTab.setScrollMode(Scroll.NONE);

        VerticalPanel cobolPanel = new VerticalPanel();
        cobolPanel.add(_cobolRuler1);
        cobolPanel.add(_cobolRuler2);
        cobolPanel.add(_inputCobol);
        _cobolTab.add(cobolPanel);

        _cobolTab.setToolTip("Edit or replace the sample COBOL structure, then click the Translate button");
        _inputFeedbackPanel.add(_cobolTab);  

        _outputXsd = new TextArea();
        _outputXsd.addStyleName("outputXsd");
        
        _xsdTab = new TabItem("XML Schema");  
        _xsdTab.setIcon(getIcon(Resources.ICONS.xsdIcon()));  
        _xsdTab.add(_outputXsd);  
        _inputFeedbackPanel.add(_xsdTab);  

        _outputErrors = new TextArea();
        _outputErrors.addStyleName("outputErrors");
        
        _errorsTab = new TabItem("Errors");  
        _errorsTab.setIcon(getIcon(Resources.ICONS.errorIcon()));  
        _errorsTab.add(_outputErrors);
        _inputFeedbackPanel.add(_errorsTab);  

        inputPanel.add(_inputFeedbackPanel);

        inputPanel.add(createOptionsForm());

        mainPanel.add(inputPanel);

        mainPanel.add(createCopyrightPanel());

        createHelpDialog();

        RootPanel.get("mainPanel").add(mainPanel);
    }

    /**
     * This is the options panel formed but multiple field sets.
     * @return the new options form panel
     */
    private FormPanel createOptionsForm() {  
        FormPanel optionsForm = new FormPanel();  
        optionsForm.addStyleName("optionsForm");  
        optionsForm.setFrame(false);  
        optionsForm.setHeading("Options");  
        optionsForm.setLayout(new FlowLayout());  

        optionsForm.add(createCodeFormatFieldSet());  
        optionsForm.add(createXsdFieldSet());
        optionsForm.add(createCobolFieldSet());

        optionsForm.setButtonAlign(HorizontalAlignment.CENTER);

        // Setup button to launch server service
        Button translateButton = new Button("Translate");
        translateButton.addSelectionListener(
                new SelectionListener<ButtonEvent>() {
                    @Override
                    public void componentSelected(ButtonEvent ce) {
                        callServerService();
                    }
                });
        optionsForm.addButton(translateButton);  

        return optionsForm;  
    }

    /**
     * These are the code format options.
     * @return the code format field set
     */
    private FieldSet createCodeFormatFieldSet() {
        FieldSet fieldSet = new FieldSet();  
        fieldSet.setHeading("Code format");  
        fieldSet.setCollapsible(true);  

        FormLayout layout = new FormLayout();  
        layout.setLabelWidth(DIALOG_LABEL_WIDTH);  
        fieldSet.setLayout(layout);  

        _fixedRadio = new Radio();  
        _fixedRadio.setBoxLabel("Fixed");  
        _fixedRadio.setValue(true);  

        _freeRadio = new Radio();  
        _freeRadio.setBoxLabel("Free");
        _freeRadio.addListener(Events.Change, new Listener<FieldEvent>(){

            @Override
            public void handleEvent(FieldEvent be) {
                if (_freeRadio.getValue()) {
                    _cobolRuler1.hide();
                    _cobolRuler2.hide();
                    _startColumn.disable();
                    _endColumn.disable();
                } else {
                    _cobolRuler1.show();
                    _cobolRuler2.show();
                    _startColumn.enable();
                    _endColumn.enable();
                }

            }
        });


        RadioGroup  radioGroup = new RadioGroup ();  
        radioGroup.setFieldLabel("Format");  
        radioGroup.add(_fixedRadio);  
        radioGroup.add(_freeRadio);  
        fieldSet.add(radioGroup, _formData);  

        _startColumn = new NumberField();  
        _startColumn.setFieldLabel("Start column");  
        _startColumn.setValue(Cob2XsdContextClone.DEFAULT_START_COLUMN);
        fieldSet.add(_startColumn, _formData);  

        _endColumn = new NumberField();  
        _endColumn.setFieldLabel("End column");  
        _endColumn.setValue(Cob2XsdContextClone.DEFAULT_END_COLUMN);
        fieldSet.add(_endColumn, _formData);

        return fieldSet;

    }

    /**
     * These are the XML schema options.
     * @return the XML schema field set
     */
    private FieldSet createXsdFieldSet() {
        FieldSet fieldSet = new FieldSet();  
        fieldSet.setHeading("XML Schema");  
        fieldSet.setCollapsible(true);  

        FormLayout layout = new FormLayout();  
        layout.setLabelWidth(DIALOG_LABEL_WIDTH);  
        fieldSet.setLayout(layout);  

        _xsdEncoding = new TextField<String>();  
        _xsdEncoding.setFieldLabel("XML Schema encoding");
        _xsdEncoding.setValue(Cob2XsdContextClone.DEFAULT_XSD_ENCODING);
        fieldSet.add(_xsdEncoding, _formData);  

        _xsdNamespace = new TextField<String>();  
        _xsdNamespace.setFieldLabel("Target namespace");
        _xsdNamespace.setValue(Cob2XsdContextClone.DEFAULT_TARGET_NAMESPACE);
        fieldSet.add(_xsdNamespace, _formData); 

        _elementNamesStartWithUppercaseCheckBox = new CheckBox();
        _elementNamesStartWithUppercaseCheckBox.setBoxLabel("Start with uppercase");
        CheckBoxGroup elementNamesStartWithUppercaseGroup = new CheckBoxGroup ();  
        elementNamesStartWithUppercaseGroup.setFieldLabel("Element names");  
        elementNamesStartWithUppercaseGroup.add(_elementNamesStartWithUppercaseCheckBox);  
        fieldSet.add(elementNamesStartWithUppercaseGroup, _formData);  

        _mapConditionsToFacetsCheckBox = new CheckBox();
        _mapConditionsToFacetsCheckBox.setBoxLabel("Map to facet");
        CheckBoxGroup mapConditionsToFacetsGroup = new CheckBoxGroup ();  
        mapConditionsToFacetsGroup.setFieldLabel("Level 88");  
        mapConditionsToFacetsGroup.add(_mapConditionsToFacetsCheckBox);  
        fieldSet.add(mapConditionsToFacetsGroup, _formData);  

        _nameConflictPrependParentNameCheckBox = new CheckBox();
        _nameConflictPrependParentNameCheckBox.setBoxLabel("Prepend parent name");
        CheckBoxGroup nameConflictPrependParentNameGroup = new CheckBoxGroup ();  
        nameConflictPrependParentNameGroup.setFieldLabel("Name conflict");  
        nameConflictPrependParentNameGroup.add(_nameConflictPrependParentNameCheckBox);  
        fieldSet.add(nameConflictPrependParentNameGroup, _formData);  

        _addLegStarAnnotationsCheckBox = new CheckBox();
        _addLegStarAnnotationsCheckBox.setBoxLabel("Add annotations");
        CheckBoxGroup addLegStarAnnotationsGroup = new CheckBoxGroup ();  
        addLegStarAnnotationsGroup.setFieldLabel("COBOL annotations");  
        addLegStarAnnotationsGroup.add(_addLegStarAnnotationsCheckBox);  
        fieldSet.add(addLegStarAnnotationsGroup, _formData);  

        return fieldSet;
    }

    /**
     * These are the COBOL compiler options.
     * @return the COBOL compiler field set
     */
    private FieldSet createCobolFieldSet() {
        FieldSet fieldSet = new FieldSet();  
        fieldSet.setHeading("COBOL compiler");  
        fieldSet.setCollapsible(true);

        FormLayout layout = new FormLayout();  
        layout.setLabelWidth(DIALOG_LABEL_WIDTH);  
        fieldSet.setLayout(layout);  

        _decimalPointIsCommaCheckBox = new CheckBox();
        _decimalPointIsCommaCheckBox.setBoxLabel("Is comma");
        CheckBoxGroup decimalPointIsCommaGroup = new CheckBoxGroup ();  
        decimalPointIsCommaGroup.setFieldLabel("Decimal point");  
        decimalPointIsCommaGroup.add(_decimalPointIsCommaCheckBox);  
        fieldSet.add(decimalPointIsCommaGroup, _formData);  

        _cobolCurrencySign = new TextField<String>();  
        _cobolCurrencySign.setFieldLabel("Currency sign");
        _cobolCurrencySign.setValue(Cob2XsdContextClone.DEFAULT_CURRENCY_SIGN);
        fieldSet.add(_cobolCurrencySign, _formData);  

        _cobolCurrencySymbol = new TextField<String>();  
        _cobolCurrencySymbol.setFieldLabel("Currency symbol"); 
        _cobolCurrencySymbol.setValue(Cob2XsdContextClone.DEFAULT_CURRENCY_SYMBOL);
        fieldSet.add(_cobolCurrencySymbol, _formData); 

        _nSymbolDbcsCheckBox = new CheckBox();
        _nSymbolDbcsCheckBox.setBoxLabel("Is DBCS");
        CheckBoxGroup nSymbolDbcsGroup = new CheckBoxGroup ();  
        nSymbolDbcsGroup.setFieldLabel("National symbol");  
        nSymbolDbcsGroup.add(_nSymbolDbcsCheckBox);  
        fieldSet.add(nSymbolDbcsGroup, _formData);  

        _quoteIsQuoteCheckBox = new CheckBox();
        _quoteIsQuoteCheckBox.setBoxLabel("Is quote");
        CheckBoxGroup quoteIsQuoteGroup = new CheckBoxGroup ();  
        quoteIsQuoteGroup.setFieldLabel("Quote");  
        quoteIsQuoteGroup.add(_quoteIsQuoteCheckBox);  
        fieldSet.add(quoteIsQuoteGroup, _formData);  

        fieldSet.collapse();
        return fieldSet;
    }

    /**
     * A dialog to provide some help and links.
     */
    public void createHelpDialog() {
        _helpDialog = new Dialog();  
        _helpDialog.setHeading("About COBOL Structure to XML Schema");  
        _helpDialog.setButtons(Dialog.OK);
        _helpDialog.setBodyStyleName("pad-text");
        _helpDialog.setWidth(400);  
        _helpDialog.setHeight(225);  
        HtmlContainer htmlHelp = new HtmlContainer(
                "<p>This application showcases the <a href=\"http://code.google.com/p/legstar-cob2xsd/\">LegStar COBOL to XML Schema feature</a></p>"
                +"<ul>"
                + "<li>Edit or replace the sample COBOL structure</li>"
                + "<li>Click on the translate button</li>"
                + "<li>Check the XML Schema tab for results</li>"
                + "<li>Also check the error tab for parsing issues</li>"
                +"</ul>"
                + "<p>Send any comments or questions to <a href=\"mailto:admin@legsem.com\">administrator</a></p>");
        _helpDialog.add(htmlHelp);  
        _helpDialog.setScrollMode(Scroll.AUTO);  
        _helpDialog.setHideOnButtonClick(true);
    }

    public Widget createCopyrightPanel() {
        HtmlContainer htmlCopyright = new HtmlContainer(
                "<p>Copyright &#169; 2009 LegSem. All rights reserved.</p>"
                + "<p>This program is made available under the terms of the"
                + " <a href=\"http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html\">GNU Lesser Public License v2.1</a>."
                + "This program is made available in the hope that it will be useful,"
                + " but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.</p>"
        );
        htmlCopyright.setBorders(true);
        htmlCopyright.addStyleName("htmlCopyright");
        return htmlCopyright;
    }

    /**
     * Call the server service.
     */
    private void callServerService() {
        // Initialize the service proxy.
        if (_cob2xsdSvc == null) {
            _cob2xsdSvc = GWT.create(Cob2XsdService.class);
        }

        // Cleanup previous results
        _outputXsd.setValue("Translating...");
        _inputFeedbackPanel.setSelection(_xsdTab);
        _outputErrors.setValue("");

        Cob2XsdContextClone context = createCob2XsdContext();
        String cobolSource = _inputCobol.getValue();

        // Set up the callback object.
        AsyncCallback < Cob2XsdServiceReply > callback = new AsyncCallback < Cob2XsdServiceReply >() {

            public void onFailure(Throwable caught) {
                _outputXsd.setValue("Translation error. Check errors tab.");
                if (caught instanceof Cob2XsdException) {
                    StringBuilder sb = new StringBuilder();
                    for (String errorMessage : ((Cob2XsdException) caught).getErrorMessages()) {
                        sb.append(errorMessage + '\n');
                    }
                    _outputErrors.setValue(sb.toString());
                } else {
                    _outputErrors.setValue(caught.getMessage());
                }
                _inputFeedbackPanel.setSelection(_errorsTab);

            }

            public void onSuccess(Cob2XsdServiceReply result) {
                _outputXsd.setValue(result.getXsdSchema());
                StringBuilder sb = new StringBuilder();
                for (String errorMessage : result.getErrorMessages()) {
                    sb.append(errorMessage + '\n');
                }
                _outputErrors.setValue(sb.toString());
                _inputFeedbackPanel.setSelection(_xsdTab);
            }
        };

        // Make the call to the service.
        _cob2xsdSvc.cob2xsdServer(cobolSource, context, callback);
    }

    /**
     * Gather the options from the UI and create a context.
     * @return a context using current options selected
     */
    private Cob2XsdContextClone createCob2XsdContext() {
        Cob2XsdContextClone context = new Cob2XsdContextClone();
        context.setCodeFormat((_freeRadio.getValue()) ? CodeFormat.FREE_FORMAT : CodeFormat.FIXED_FORMAT);
        context.setStartColumn(_startColumn.getValue().intValue());
        context.setEndColumn(_endColumn.getValue().intValue());
        context.setTargetNamespace(_xsdNamespace.getValue());
        context.setXsdEncoding(_xsdEncoding.getValue());
        context.setNameConflictPrependParentName(_nameConflictPrependParentNameCheckBox.getValue());
        context.setMapConditionsToFacets(_mapConditionsToFacetsCheckBox.getValue());
        context.setElementNamesStartWithUppercase(_elementNamesStartWithUppercaseCheckBox.getValue());
        context.setAddLegStarAnnotations(_addLegStarAnnotationsCheckBox.getValue());
        context.setCurrencySign(_cobolCurrencySign.getValue());
        context.setCurrencySymbol(_cobolCurrencySymbol.getValue());
        context.setDecimalPointIsComma(_decimalPointIsCommaCheckBox.getValue());
        context.setNSymbolDbcs(_nSymbolDbcsCheckBox.getValue());
        context.setQuoteIsQuote(_quoteIsQuoteCheckBox.getValue());
        return context;
    }

    private static AbstractImagePrototype getIcon(ImageResource resource) {
        return AbstractImagePrototype.create(resource);
    }

    /**
     * The resources for this implementation.
     */
    public interface Resources extends ClientBundle {
        Resources ICONS = GWT.create(Resources.class);

        /**
         * Contains the cobol icon.
         */
        @Source("cob_icon16.gif")
        ImageResource cobIcon();

        /**
         * Contains the xsd icon.
         */
        @Source("xsd_icon16.gif")
        ImageResource xsdIcon();

        /**
         * Contains the error icon.
         */
        @Source("error_icon16.gif")
        ImageResource errorIcon();

    }
}
