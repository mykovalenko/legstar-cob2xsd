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
package com.legstar.cobol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.legstar.antlr.CleanerException;

/**
 * In order to reduce the lexer/parser grammar complexity, this class will remove
 * all unnecessary characters from the original source.
 * This way, the ANTLR lexer will be presented with a purified source that only
 * contains data division entries.
 * <p/>
 * This allows users to submit complete COBOL programs or fragments of COBOL programs
 * with non data description statements to the parser without the need to add
 * grammar rules for all these cases.
 *
 */
public abstract class AbstractCobolSourceCleaner {

    /** Line separator (OS specific).*/
    public static final String LS = System.getProperty("line.separator");

    /** Pattern that recognizes the start of a data description entry. */
    public static final Pattern DATA_DESCRIPTION_START = Pattern.compile("(^|\\s|\\.)\\d(\\d)?(\\s|\\.|$)");

    /** Pattern that recognizes the end of a data description entry. */
    public static final Pattern DATA_DESCRIPTION_END = Pattern.compile("(\\.$)|(\\.\\s)");

    /** Pattern that recognizes the start of a procedure division. */
    public static final Pattern PROCEDURE_DIVISION =
        Pattern.compile("^(\\s)*PROCEDURE DIVISION", Pattern.CASE_INSENSITIVE);
    
    /** Handles error messages.*/
    private RecognizerErrorHandler _errorHandler;
    
    /**
     * Construct with a shared error handler.
     * @param errorHandler handles error messages
     */
    public AbstractCobolSourceCleaner(
            final RecognizerErrorHandler errorHandler) {
        _errorHandler = errorHandler;

    }

    /**
     * Takes in a raw COBOL source, potentially containing
     * sequence numbers or non data description statements and produces a clean
     * source code.
     * <p/>
     * Statements which are not data descriptions become empty lines in order to
     * preserve original line numbering.
     * 
     * @param cobolSource  the raw COBOL source
     * @return the source cleaned up
     * @throws CleanerException if source cannot be read
     */
    public String clean(
            final String cobolSource) throws CleanerException {
        if (cobolSource != null) {
            BufferedReader reader = new BufferedReader(
                    new StringReader(cobolSource));
            String line;
            StringBuilder cleanedSource =  new StringBuilder();
            CleaningContext context = new CleaningContext();
            try {
                while ((line = reader.readLine()) != null) {
                    if (isDataDivision(line, context)) {
                        cleanedSource.append(
                                removeExtraneousCharacters(
                                        cleanLine(line), context));
                    }
                    cleanedSource.append(LS);
                }
                if (cleanedSource.length() <= LS.length()) {
                    throw new CleanerException(
                            "No data descriptions found. Are you sure this is COBOL source?");
                }
                return cleanedSource.toString();
            } catch (IOException e) {
                throw new CleanerException(e);
            }
        } else {
            throw new CleanerException("COBOL source was null");
        }
    }
    
    /**
     * Remove characters that should not be passed to the lexer.
     * <p/>
     * Replace token separators such as ", " and "; " which complicate
     * matters uselessly. Replacement should not change column numbers though
     * so we simply replace the extra separators with a whitespace.
     * <p/>
     * Clear comment lines. We don't remove the line so that line numbers
     * are preserved.
     * @param line before cleaning
     * @return a cleaner line of code
     */
    public String cleanLine(final String line) {
        int length = line.length();
        if (length < getIndicatorAreaPos() + 1) {
            return "";
        }

        /* Remove comments and special lines */
        char indicatorArea = line.charAt(getIndicatorAreaPos());
        if (indicatorArea == '*' || indicatorArea == '/' || indicatorArea == '$') {
            return "";
        }
        
        String cleanedLine = extendedCleanLine(line);
 
        /* Replace long separator forms */
        cleanedLine = cleanedLine.replace(", ", "  ").replace("; ", "  ");

        /* Right trim, no need to over burden the lexer with spaces*/
        cleanedLine = ("a" + cleanedLine).trim().substring(1);
        return cleanedLine;
    }
    
    /**
     * Derived classes can extend this method to further clean a line of code.
     * @param line the current line of code
     * @return a cleaner line of code
     */
    public String extendedCleanLine(final String line) {
        return line;
    }
    

    /**
     * Rough triage of statements which are not strictly part of the data division.
     * <ul>
     * <li>Removes comments</li>
     * <li>Detects end of DATA DIVISION by looking for PROCEDURE DIVISION.</li>
     * </ul>
     * 
     * @param line the line to set data description status from
     * @param context the data description detection context
     * @return true if we are within the data division
     */
    public boolean isDataDivision(
            final String line,
            final CleaningContext context) {
        if (context.isDataDivision()) {
            Matcher matcher = PROCEDURE_DIVISION.matcher(line);
            if (matcher.find()) {
                context.setDataDivision(false);
                emitErrorMessage("Procedure division found. The rest of the source code will be ignored.");
            } else {
                int commentPos = getIndicatorAreaPos();
                if (line != null && line.length() > commentPos
                        && (line.charAt(commentPos) == '*'
                            || line.charAt(commentPos) == '/')) {
                    return false;
                }
            }
        }
        return context.isDataDivision();
    }
    
    /**
     * @return the zero-based position of the indicator area 
     */
    public abstract int getIndicatorAreaPos();

    /**
     * Removes characters which are not part of a data description entry.
     * <p/>
     * Data description entries start with an integer (the level) and end with a
     * period followed by either space, newline or EOF.
     * <p/>
     * A single line might hold multiple data descriptions. This method is recursive,
     * and is called multiple times for each line fragment holding a new data
     * description.
     * <p/>
     * Data description entries might span multiple lines which is why we need to keep
     * a context. Context tells us if we need to start by looking for a level (no data
     * description has started on some previous line) or for a period.
     * <p/>
     * Unsupported data description instructions such as COPY might appear on the same
     * line as data instructions. They also can span multiple lines. This code blanks
     * out such "non data description" statements.
     * 
     * @param fragment a fragment of a line which might hold a data description
     * @param context the data description detection context
     * @return a line holding only data description parts or blank
     */
    public String removeExtraneousCharacters(final String fragment, final CleaningContext context) {
        if (fragment == null || fragment.length() == 0) {
            return fragment;
        }
        Matcher matcher;
        StringBuilder cleanedLine = new StringBuilder();
        if (context.isLookingForLevel()) {
            matcher = DATA_DESCRIPTION_START.matcher(fragment);
            if (matcher.find()) {
                               
                /* if the level does not start on the first character, the regex
                 * starts on the space or period character that precedes the level.*/
                int start = (matcher.start() > 0) ? matcher.start() + 1 : matcher.start();
                
                /* If there are non blank extraneous characters, issue a warning. */
                String extraneous = fragment.substring(0, start).trim();
                if (start > 0 && extraneous.length() > 0) {
                    emitErrorMessage("Extraneous characters ignored: " + extraneous);
                }
                /* Any extraneous character is replaced with spaces. */
                for (int i = 0; i < start; i++) {
                    cleanedLine.append(' ');
                }
                cleanedLine.append(fragment.substring(start, matcher.end() - 1));
                context.setLookingForLevel(false);
                cleanedLine.append(removeExtraneousCharacters(
                        fragment.substring(matcher.end() - 1), context));
            } else {
                if (fragment.trim().length() > 0) {
                    emitErrorMessage("Extraneous characters ignored: " + fragment);
                }
            }
        } else {
            matcher = DATA_DESCRIPTION_END.matcher(fragment);
            if (matcher.find()) {
                cleanedLine.append(fragment.substring(0, matcher.end()));
                context.setLookingForLevel(true);
                cleanedLine.append(removeExtraneousCharacters(
                        fragment.substring(matcher.end()), context));
            } else {
                cleanedLine.append(fragment);
            }
        }
        return cleanedLine.toString();
    }

    /**
     * Describes the cleaning context.
     * Because data description sentences can be multiline or because it
     * does not make sense to look for data description entries once we
     * past a PROCEDURE DIVISION section, we need to keep track of the context.
     *
     */
    public static class CleaningContext {

        /** True when we are looking for a level (start of a data description entry). */
        private boolean _lookingForLevel = true;

        /** True if we are likely to be in a COBOL DATA DIVISION section. */
        private boolean _inDataDivision = true;

        /**
         * @return true when we are looking for a level
         */
        public boolean isLookingForLevel() {
            return _lookingForLevel;
        }

        /**
         * @param isLookingForLevel set to true when we are looking for
         *  a level (start of a data description entry)
         */
        public void setLookingForLevel(final boolean isLookingForLevel) {
            _lookingForLevel = isLookingForLevel;
        }

        /**
         * @return true if we are likely to be in a COBOL DATA DIVISION section
         */
        public boolean isDataDivision() {
            return _inDataDivision;
        }

        /**
         * @param dataDivision set to true if we are likely to be in a COBOL DATA DIVISION section
         */
        public void setDataDivision(final boolean dataDivision) {
            _inDataDivision = dataDivision;
        }


    }

    /** 
     * Add an error message to the history.
     * @param msg the error message
     * */
    public void emitErrorMessage(final String msg) {
        getErrorHandler().addMessageToHistory(msg);
    }

    /**
     * @return the error messages handler
     */
    public RecognizerErrorHandler getErrorHandler() {
        return _errorHandler;
    }
}