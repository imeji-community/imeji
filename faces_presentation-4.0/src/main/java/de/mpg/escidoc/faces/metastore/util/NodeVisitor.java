package de.mpg.escidoc.faces.metastore.util;

import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFVisitor;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.function.library.localname;

public class NodeVisitor implements RDFVisitor
{
    public Object visitBlank(Resource res, AnonId anon)
    {
        String label = anon.getLabelString();
        return label;
    }

    public Object visitLiteral(Literal l)
    {
        String lexical = l.asNode().getLiteralLexicalForm();
        return lexical;
    }

    public Object visitURI(Resource r, String uri)
    {
        String localName;
        if (r.getNameSpace().equals(uri))
        {
            localName = r.getURI();
        }
        else
        {
            localName = r.asNode().getLocalName();
        }
        return localName;
    }
}
