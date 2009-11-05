package com.legstar.cobol.model;

/**
 * A picture symbol occurs a number of times in a picture string.
 * This class holds the corresponding number for a picture symbol.
 */
public class PictureSymbol {
    /** A picture symbol.*/
    private char _symbol;

    /** Number of times it occurs. */
    private int _number;

    /**
     * Constructor.
     * @param symbol picture symbol
     * @param number number of times it occurs
     */
    public PictureSymbol(final char symbol, final int number) {
        _symbol = symbol;
        _number = number;
    }

    /**
     * @return the picture symbol
     */
    public char getSymbol() {
        return _symbol;
    }

    /**
     * @return the number of times it occurs
     */
    public int getNumber() {
        return _number;
    }

    /**
     * @param symbol the picture symbol to set
     */
    public void setsymbol(final char symbol) {
        _symbol = symbol;
    }

    /**
     * @param number the number of times it occurs to set
     */
    public void setNumber(final int number) {
        _number = number;
    }
    
    /** {@inheritDoc}*/
    public String toString() {
        return "{" + "symbol:" + getSymbol() + "," + "occurs:" + getNumber() + "}";
    }
}

