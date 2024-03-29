/*******************************************************************************
 * Copyright (c) 2010 LegSem.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     LegSem - initial API and implementation
 ******************************************************************************/
package com.legstar.cob2xsd;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

import com.legstar.antlr.RecognizerException;
import com.legstar.cob2xsd.Cob2XsdModel.CodeFormat;

/**
 * COBOL Structure to XSD ANT Task. <code>
 * Usage:<br>
 * 
 * &lt;project ...&gt;<br>
 *   &lt;taskdef name="cob2xsd" classname="com.legstar.cob2xsd.Cob2XsdTask" /&gt;<br>
 *   &lt;property name="cobol.dir" value="../cobol"/&gt;<br>
 *   &lt;property name="xsd.dir" value="../xsd"/&gt;<br>
 *   &lt;target name="generate"&gt;<br>
 *     &lt;cob2xsd targetDir="${xsd.dir}"&gt;<br>
 *       &lt;fileset dir="${cobol.dir}"&gt;<br>
 *         &lt;include name="*.cob" /&gt;<br>
 *       &lt;/fileset&gt;<br>
 *     &lt;/cob2xsd&gt;<br>
 *    &lt;/target&gt;<br>
 * &lt;/project&gt;<br>
 * </code>
 * 
 */
public class Cob2XsdTask extends Task {

    /** Logger. */
    private final Log _log = LogFactory.getLog(getClass());

    /** The list of filesets that were setup. */
    private List < FileSet > _fileSets = new LinkedList < FileSet >();

    /**
     * The target can either be a folder where XML schema result is to be
     * written or a file in which case the XML schema is written there.
     */
    private File _target;

    /**
     * With this option turned on, the target namespace from the model will be
     * appended the input file base file name.
     */
    private boolean _appendBaseFileNameToNamespace;

    /** Set of translation options to use. */
    private Cob2XsdModel _model = new Cob2XsdModel();

    /**
     * The ant execution method. Check parameters and produce XSD files.
     */
    public final void execute() {
        _log.info("Started translation from COBOL to XML Schema");

        checkParameters();
        _log.info("Taking COBOL from     : " + _fileSets);
        _log.info("Output XML Schema to  : " + getTarget());
        _log.info("Append base file name : " + _appendBaseFileNameToNamespace);
        _log.info(getModel().toString());

        try {
            Iterator < FileSet > itor = _fileSets.iterator();
            while (itor.hasNext()) {
                FileSet fileset = itor.next();

                DirectoryScanner scanner = fileset
                        .getDirectoryScanner(getProject());
                scanner.scan();
                String[] files = scanner.getIncludedFiles();
                for (int i = 0; i < files.length; i++) {
                    File cobolFile = new File(fileset.getDir(getProject()),
                            files[i]);
                    translate(cobolFile, getTarget());
                }
            }
        } catch (IllegalStateException e) {
            throw (new BuildException(e));
        } catch (XsdGenerationException e) {
            throw (new BuildException(e));
        } catch (RecognizerException e) {
            throw (new BuildException(e));
        }
        _log.info("Finished translation");
    }

    /**
     * Translates a single COBOL source file.
     * <p/>
     * When requested the file base name is appended to the target namespace.
     * 
     * @param cobolFile COBOL source file
     * @param target target file or folder
     * @throws RecognizerException if parser fails
     * @throws XsdGenerationException if COBOL model interpretation fails
     */
    protected void translate(final File cobolFile, final File target)
            throws RecognizerException, XsdGenerationException {

        Cob2XsdIO cob2XsdIO = new Cob2XsdIO(getModel());
        cob2XsdIO.translate(cobolFile, target,
                isAppendBaseFileNameToNamespace());
    }

    /**
     * Check that we have enough parameters to get started. Check that we have a
     * valid target directory or file.
     */
    private void checkParameters() {
        if (_fileSets.isEmpty()) {
            throw new BuildException("No fileset specified");
        }

        if (getTarget() == null) {
            throw (new IllegalArgumentException(
                    "You must provide a target directory or file"));
        }
        if (_log.isDebugEnabled()) {
            _log.debug("Target folder or file: " + getTarget());
        }
    }

    /**
     * Gather all parameters into a model object.
     * 
     * @return a parameter model to be used throughout all code
     */
    private Cob2XsdModel getModel() {
        return _model;
    }

    /*
     * ------------------------------------------------------------------- COBOL
     * source format related options
     */
    /**
     * @return the Fixed or Free format COBOL source
     */
    public CodeFormat getCodeFormat() {
        return getModel().getCodeFormat();
    }

    /**
     * @param cobolFormat the Fixed or Free format COBOL source to set
     */
    public void setCodeFormat(final CodeFormat cobolFormat) {
        getModel().setCodeFormat(cobolFormat);
    }

    /**
     * @param cobolFormat the Fixed or Free format COBOL source to set
     */
    public void setCodeFormat(final String cobolFormat) {
        getModel().setCodeFormat(CodeFormat.valueOf(cobolFormat));
    }

    /**
     * @return the position of the indicator area for fixed format COBOL
     */
    public int getStartColumn() {
        return getModel().getStartColumn();
    }

    /**
     * @param startColumn the position of the indicator area for fixed format
     *            COBOL
     */
    public void setStartColumn(final int startColumn) {
        getModel().setStartColumn(startColumn);
    }

    /**
     * @return the position of the right margin for fixed format COBOL
     */
    public int getEndColumn() {
        return getModel().getEndColumn();
    }

    /**
     * @param endColumn the position of the right margin for fixed format COBOL
     */
    public void setEndColumn(final int endColumn) {
        getModel().setEndColumn(endColumn);
    }

    /**
     * @return the character set used to encode the input COBOL source files
     */
    public String getCobolSourceFileEncoding() {
        return getModel().getCobolSourceFileEncoding();
    }

    /**
     * @param cobolSourceFileEncoding the character set used to encode the input
     *            COBOL source files
     */
    public void setCobolSourceFileEncoding(final String cobolSourceFileEncoding) {
        getModel().setCobolSourceFileEncoding(cobolSourceFileEncoding);
    }

    /*
     * ------------------------------------------------------------------- XML
     * Schema related options
     */

    /**
     * @return the character set used to encode the output XML Schema
     */
    public String getXsdEncoding() {
        return getModel().getXsdEncoding();
    }

    /**
     * @param xsdEncoding the character set used to encode the output XML Schema
     *            to set
     */
    public void setXsdEncoding(final String xsdEncoding) {
        getModel().setXsdEncoding(xsdEncoding);
    }

    /**
     * @return the target namespace for generated XML schema
     */
    public String getTargetNamespace() {
        return getModel().getTargetNamespace();
    }

    /**
     * @param targetNamespace the target namespace for generated XML schema
     */
    public void setTargetNamespace(final String targetNamespace) {
        getModel().setTargetNamespace(targetNamespace);
    }

    /**
     * @return whether COBOL conditions (level 88) should be mapped to facets.
     *         Facets restrict the content which might not be desirable
     */
    public boolean mapConditionsToFacets() {
        return getModel().mapConditionsToFacets();
    }

    /**
     * @param mapConditionsToFacets Whether COBOL conditions (level 88) should
     *            be mapped to facets. Facets restrict the content which might
     *            not be desirable
     */
    public void setMapConditionsToFacets(final boolean mapConditionsToFacets) {
        getModel().setMapConditionsToFacets(mapConditionsToFacets);
    }

    /**
     * @return an optional XSLT transform for XML schema customization
     */
    public String getCustomXsltFileName() {
        return getModel().getCustomXsltFileName();
    }

    /**
     * @param customXsltFileName an optional XSLT transform for XML schema
     *            customization
     */
    public void setCustomXsltFileName(final String customXsltFileName) {
        getModel().setCustomXsltFileName(customXsltFileName);
    }

    /**
     * @return true if parent complex type name should be prepended in case of
     *         name conflict (otherwise, the COBOL source line will be appended)
     */
    public boolean nameConflictPrependParentName() {
        return getModel().nameConflictPrependParentName();
    }

    /**
     * @param nameConflictPrependParentName true if parent complex type name
     *            should be prepended in case of name conflict (otherwise, the
     *            COBOL source line will be appended)
     */
    public void setNameConflictPrependParentName(
            final boolean nameConflictPrependParentName) {
        getModel().setNameConflictPrependParentName(
                nameConflictPrependParentName);
    }

    /**
     * @return true if XSD element names should start with an uppercase
     *         (compatible with LegStar 1.2)
     */
    public boolean elementNamesStartWithUppercase() {
        return getModel().elementNamesStartWithUppercase();
    }

    /**
     * @param elementNamesStartWithUppercase true if XSD element names should
     *            start with an uppercase (compatible with LegStar 1.2)
     */
    public void setElementNamesStartWithUppercase(
            final boolean elementNamesStartWithUppercase) {
        getModel().setElementNamesStartWithUppercase(
                elementNamesStartWithUppercase);
    }

    /**
     * Ignore primitive data items which are not attached to a parent group.
     * 
     * @return true if primitive data items without a parent group are ignored
     */
    public boolean ignoreOrphanPrimitiveElements() {
        return getModel().ignoreOrphanPrimitiveElements();
    }

    /**
     * Ignore primitive data items which are not attached to a parent group.
     * 
     * @param ignoreOrphanPrimitiveElements set to true to ignore primitive data
     *            items without a parent group item
     */
    public void setIgnoreOrphanPrimitiveElements(
            boolean ignoreOrphanPrimitiveElements) {
        getModel().setIgnoreOrphanPrimitiveElements(
                ignoreOrphanPrimitiveElements);
    }

    /*
     * -------------------------------------------------------------------
     * LegStar annotations related options
     */

    /**
     * @return whether we should generate COBOL/JAXB annotations
     */
    public boolean addLegStarAnnotations() {
        return getModel().addLegStarAnnotations();
    }

    /**
     * @param addLegStarAnnotations whether we should generate COBOL/JAXB
     *            annotations
     */
    public void setAddLegStarAnnotations(final boolean addLegStarAnnotations) {
        getModel().setAddLegStarAnnotations(addLegStarAnnotations);
    }

    /*
     * ------------------------------------------------------------------- COBOL
     * compiler related options
     */

    /**
     * @return the currency sign used (CURRENCY SIGN clause in the
     *         SPECIAL-NAMES)
     */
    public String getCurrencySign() {
        return getModel().getCurrencySign();
    }

    /**
     * @param currencySign the currency sign used (CURRENCY SIGN clause in the
     *            SPECIAL-NAMES)
     */
    public void setCurrencySign(final String currencySign) {
        getModel().setCurrencySign(currencySign);
    }

    /**
     * @return the currency symbol used (CURRENCY PICTURE SYMBOL clause in the
     *         SPECIAL-NAMES)
     */
    public String getCurrencySymbol() {
        return getModel().getCurrencySymbol();
    }

    /**
     * @param currencySymbol the currency symbol used (CURRENCY PICTURE SYMBOL
     *            clause in the SPECIAL-NAMES)
     */
    public void setCurrencySymbol(final String currencySymbol) {
        getModel().setCurrencySymbol(currencySymbol);
    }

    /**
     * @return the NSYMBOL(DBCS) compiler option. Assume NSYMBOL(NATIONAL) if
     *         false
     */
    public boolean nSymbolDbcs() {
        return getModel().nSymbolDbcs();
    }

    /**
     * @param nSymbolDbcs the NSYMBOL(DBCS) compiler option. Assume
     *            NSYMBOL(NATIONAL) if false
     */
    public void setNSymbolDbcs(final boolean nSymbolDbcs) {
        getModel().setNSymbolDbcs(nSymbolDbcs);
    }

    /**
     * @return whether comma is the decimal point (DECIMAL-POINT IS COMMA clause
     *         in the SPECIAL-NAMES)
     */
    public boolean decimalPointIsComma() {
        return getModel().decimalPointIsComma();
    }

    /**
     * @param decimalPointIsComma whether comma is the decimal point
     *            (DECIMAL-POINT IS COMMA clause in the SPECIAL-NAMES)
     */
    public void setDecimalPointIsComma(final boolean decimalPointIsComma) {
        getModel().setDecimalPointIsComma(decimalPointIsComma);
    }

    /**
     * The COBOL QUOTE|APOST compiler option. False means APOST.
     * 
     * @return the COBOL QUOTE|APOST compiler option. False means APOST
     */
    public boolean quoteIsQuote() {
        return getModel().quoteIsQuote();
    }

    /**
     * @param quoteIsQuote the COBOL QUOTE|APOST compiler option. False means
     *            APOST
     */
    public void setQuoteIsQuote(final boolean quoteIsQuote) {
        getModel().setQuoteIsQuote(quoteIsQuote);
    }

    /**
     * @return a new FileSet
     */
    public FileSet createFileset() {
        FileSet fileset = new FileSet();
        _fileSets.add(fileset);
        return fileset;
    }

    /**
     * @return the current folder or file to receive the XML schema(s)
     */
    public File getTarget() {
        return _target;
    }

    /**
     * @param target the folder or file to receive the XML schema(s)
     */
    public void setTarget(final File target) {
        _target = target;
    }

    /**
     * @return whether the target namespace from the model should be appended
     *         the input file base file name
     */
    public boolean isAppendBaseFileNameToNamespace() {
        return _appendBaseFileNameToNamespace;
    }

    /**
     * @param whether the target namespace from the model should be appended the
     *            input file base file name
     */
    public void setAppendBaseFileNameToNamespace(
            boolean _appendBaseFileNameToNamespace) {
        this._appendBaseFileNameToNamespace = _appendBaseFileNameToNamespace;
    }

}
