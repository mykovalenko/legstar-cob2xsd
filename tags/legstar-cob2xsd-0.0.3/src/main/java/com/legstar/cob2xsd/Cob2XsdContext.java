package com.legstar.cob2xsd;

import java.io.File;

/**
 * This class gathers execution parameters for the COBOL to XSD utility.
 *
 */
public class Cob2XsdContext {

    /** Default target namespace. */
    public static final String DEFAULT_TARGET_NAMESPACE = "http://www.acme.com/test";

    /** Default JAXB package name. */
    public static final String DEFAULT_JAXB_PACKAGE_NAME = "com.acme.test";
    
    /** Default Currency symbol used (CURRENCY SIGN clause in the SPECIAL-NAMES). */
    public static final char DEFAULT_CURRENCY_SYMBOL = '$';


    /** Target namespace for generated XML schema.*/
    private String _targetNamespace = DEFAULT_TARGET_NAMESPACE;

    /** Whether we should generate COBOL/JAXB annotations. */
    private boolean _addLegStarAnnotations = false;
    
    /** The JAXB package name (appears in schema annotations).*/
    private String _jaxbPackageName = DEFAULT_JAXB_PACKAGE_NAME;

    /** JAXB appends this suffix to all generated types.*/
    private String _jaxbTypeClassesSuffix;
    
    /** Whether COBOL conditions (level 88) should be mapped to facets. Facets 
     * restrict the content which might not be desirable. */
    private boolean _mapConditionsToFacets = false;
    
    /** An optional XSLT transform for XML schema customization. */
    private File _customXslt;
    
    /** True if parent complex type name should be prepended in case of name conflict
     * (otherwise, the COBOL source line will be appended). */
    private boolean _nameConflictPrependParentName = false;
    
    /** True if XSD element names should start with an uppercase 
     * (compatible with LegStar 1.2).*/
    private boolean _elementNamesStartWithUppercase = false;
    
    /** Currency symbol used (CURRENCY SIGN clause in the SPECIAL-NAMES). */
    private char _currencySymbol = DEFAULT_CURRENCY_SYMBOL;
    
    /** Whether comma is the decimal point (DECIMAL-POINT IS COMMA clause in the SPECIAL-NAMES). */
    private boolean _decimalPointIsComma = false;

    /** COBOL NSYMBOL(DBCS) compiler option. Assume NSYMBOL(NATIONAL) if false.*/
    private boolean _nSymbolDbcs = false;
    
    /** COBOL QUOTE|APOST compiler option. False means APOST.*/
    private boolean _quoteIsQuote = true;
    
    /**
     * @return the currency symbol used (CURRENCY SIGN clause in the SPECIAL-NAMES)
     */
    public char getCurrencySymbol() {
        return _currencySymbol;
    }

    /**
     * @param currencySymbol the currency symbol used (CURRENCY SIGN clause in the SPECIAL-NAMES)
     */
    public void setCurrencySymbol(final char currencySymbol) {
        _currencySymbol = currencySymbol;
    }

    /**
     * @return the NSYMBOL(DBCS) compiler option. Assume NSYMBOL(NATIONAL) if false
     */
    public boolean isNSymbolDbcs() {
        return _nSymbolDbcs;
    }

    /**
     * @param nSymbolDbcs the NSYMBOL(DBCS) compiler option. Assume NSYMBOL(NATIONAL) if false
     */
    public void setNSymbolDbcs(final boolean nSymbolDbcs) {
        _nSymbolDbcs = nSymbolDbcs;
    }

    /**
     * @return whether comma is the decimal point (DECIMAL-POINT IS COMMA clause in the SPECIAL-NAMES)
     */
    public boolean decimalPointIsComma() {
        return _decimalPointIsComma;
    }

    /**
     * @param decimalPointIsComma whether comma is the decimal point
     *  (DECIMAL-POINT IS COMMA clause in the SPECIAL-NAMES)
     */
    public void setDecimalPointIsComma(final boolean decimalPointIsComma) {
        _decimalPointIsComma = decimalPointIsComma;
    }

    /**
     * @return whether we should generate COBOL/JAXB annotations
     */
    public boolean addLegStarAnnotations() {
        return _addLegStarAnnotations;
    }

    /**
     * @param addLegStarAnnotations whether we should generate COBOL/JAXB annotations
     */
    public void setAddLegStarAnnotations(final boolean addLegStarAnnotations) {
        _addLegStarAnnotations = addLegStarAnnotations;
    }

    /**
     * @return the JAXB package name for generated Java classes
     */
    public String getJaxbPackageName() {
        return _jaxbPackageName;
    }

    /**
     * @return the JAXB type name prefix (generated JAXB class names will have this suffix)
     */
    public String getJaxbTypeClassesSuffix() {
        return _jaxbTypeClassesSuffix;
    }

    /**
     * @param jaxbPackageName the JAXB package name for generated Java classes
     */
    public void setJaxbPackageName(final String jaxbPackageName) {
        _jaxbPackageName = jaxbPackageName;
    }

    /**
     * @param jaxbTypeClassesSuffix the JAXB type name prefix (generated JAXB class names will have this suffix)
     */
    public void setJaxbTypeClassesSuffix(final String jaxbTypeClassesSuffix) {
        _jaxbTypeClassesSuffix = jaxbTypeClassesSuffix;
    }

    /**
     * @return the target namespace for generated XML schema
     */
    public String getTargetNamespace() {
        return _targetNamespace;
    }

    /**
     * @param targetNamespace the target namespace for generated XML schema
     */
    public void setTargetNamespace(final String targetNamespace) {
        _targetNamespace = targetNamespace;
    }

    /**
     * @return whether COBOL conditions (level 88) should be mapped to facets. Facets 
     * restrict the content which might not be desirable
     */
    public boolean mapConditionsToFacets() {
        return _mapConditionsToFacets;
    }

    /**
     * @param mapConditionsToFacets Whether COBOL conditions (level 88) should be mapped to facets. Facets 
     * restrict the content which might not be desirable
     */
    public void setMapConditionsToFacets(final boolean mapConditionsToFacets) {
        _mapConditionsToFacets = mapConditionsToFacets;
    }

    /**
     * @return an optional XSLT transform for XML schema customization
     */
    public File getCustomXslt() {
        return _customXslt;
    }

    /**
     * @param customXslt an optional XSLT transform for XML schema customization
     */
    public void setCustomXslt(final File customXslt) {
        _customXslt = customXslt;
    }

    /**
     * @return true if parent complex type name should be prepended in case of name conflict
     * (otherwise, the COBOL source line will be appended)
     */
    public boolean nameConflictPrependParentName() {
        return _nameConflictPrependParentName;
    }

    /**
     * @param nameConflictPrependParentName true if parent complex type name should be prepended
     * in case of name conflict (otherwise, the COBOL source line will be appended)
     */
    public void setNameConflictPrependParentName(
            final boolean nameConflictPrependParentName) {
        _nameConflictPrependParentName = nameConflictPrependParentName;
    }

    /**
     * @return true if XSD element names should start with an uppercase 
     * (compatible with LegStar 1.2)
     */
    public boolean elementNamesStartWithUppercase() {
        return _elementNamesStartWithUppercase;
    }

    /**
     * @param elementNamesStartWithUppercase true if XSD element names should start with an uppercase 
     * (compatible with LegStar 1.2)
     */
    public void setElementNamesStartWithUppercase(
            final boolean elementNamesStartWithUppercase) {
        _elementNamesStartWithUppercase = elementNamesStartWithUppercase;
    }

    /**
     * @return the COBOL QUOTE|APOST compiler option. False means APOST
     */
    public boolean quoteIsQuote() {
        return _quoteIsQuote;
    }

    /**
     * @param quoteIsQuote the COBOL QUOTE|APOST compiler option. False means APOST
     */
    public void setQuoteIsQuote(final boolean quoteIsQuote) {
        _quoteIsQuote = quoteIsQuote;
    }


}
