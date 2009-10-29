package com.legstar.cob2xsd;

import java.io.IOException;
import java.io.StringReader;

import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.legstar.antlr.ANTLRNoCaseReaderStream;
import com.legstar.antlr.AbstractAntlrTester;
import com.legstar.cob2xsd.CobolStructureParser.cobdata_return;

/**
 * Generic test code for ANTLR based lexers parsers and tree walkers.
 *
 */
public abstract class AbstractCob2XsdTester extends AbstractAntlrTester {

    /** Logger. */
    private final Log _log = LogFactory.getLog(getClass());

    /**
     * {@inheritDoc}
     */
    public String clean(final String source) {
        CobolSourceCleaner cleaner = new CobolSourceCleaner();
        return cleaner.execute(source);
    }

    /**
     * {@inheritDoc}
     */
    public CommonTokenStream lex(final String source) {
        try {
            CobolStructureLexer lex = new CobolStructureLexerImpl(
                    new ANTLRNoCaseReaderStream(
                            new StringReader(
                                    clean(source))));
            CommonTokenStream tokens = new CommonTokenStream(lex);
            if (lex.getNumberOfSyntaxErrors() > 0) {
                _log.warn(lex.getNumberOfSyntaxErrors() + " lex errors");
            }
            assertTrue(tokens != null);
            return tokens;
        } catch (IOException e) {
            _log.error("test failed", e);
            fail(e.toString());
        }
        return null;
    }
    
    /**
     * Apply Lexer + Parser to produce an abstract syntax tree from source. 
     * @param source the source code
     * @return an antlr abstract syntax tree
     */
    public CommonTree parse(final String source) {
        try {
            CommonTokenStream tokens = lex(source);
            CobolStructureParser parser = new CobolStructureParserImpl(tokens);
            cobdata_return parserResult = parser.cobdata();
            if (parser.getNumberOfSyntaxErrors() > 0) {
                _log.warn(parser.getNumberOfSyntaxErrors() + " parse errors");
            }
            assertTrue(parserResult != null);
            return (CommonTree) parserResult.getTree();
        } catch (RecognitionException e) {
            _log.error("test failed", e);
            fail(e.toString());
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String[] getTokenNames() {
        return CobolStructureParser.tokenNames;
    }

}
