package de.mpg.imeji.logic.search.vo;

/**
 * The possible operators for a {@link SearchQuery}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public enum SearchOperators
{
    REGEX, URI, EQUALS, BOUND, EQUALS_NUMBER, GREATER_NUMBER, LESSER_NUMBER, EQUALS_DATE, GREATER_DATE, LESSER_DATE, NOT;
}
