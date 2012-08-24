package de.mpg.j2j.helper;

import java.util.Calendar;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.datatypes.BaseDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;

public class LiteralHelper
{
    private Model model;
    private static Logger logger = Logger.getLogger(LiteralHelper.class);

    public LiteralHelper(Model model)
    {
        this.model = model;
    }

    /**
     * Transform data types used in jean by data types used for j2j
     * 
     * @param o
     * @return
     */
    public static Object jenaTypeToJ2jType(Object o)
    {
        if (o instanceof XSDDateTime)
        {
            return ((XSDDateTime)o).asCalendar();
        }
        else if (o instanceof BaseDatatype.TypedValue)
        {
            System.err.println(" BaseDatatype.TypedValue found, check what's happening: " + o);
            return o;
            // return ((BaseDatatype.TypedValue)o).lexicalValue;
        }
        return o;
    }

    /**
     * Transform a javo {@link Object} to a {@link Literal}
     * 
     * @param p
     * @param o
     * @return
     */
    public Literal java2Literal(Object o)
    {
        try
        {
            Literal l = null;
            if (o instanceof Calendar || o instanceof Boolean || o instanceof Character || o instanceof Float
                    || o instanceof Integer || o instanceof Long || o instanceof String || o instanceof Double)
            {
                l = model.createTypedLiteral(o);
            }
            else
            {
                logger.error("Unknown literal type " + o.toString() + ", parsing it to String... Might be problematic");
                model.createLiteral(o.toString());
            }
            return l;
        }
        catch (NullPointerException e)
        {
            return null;
        }
    }
}