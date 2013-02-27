/*
 *
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License"). You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE
 * or http://www.escidoc.de/license.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at license/ESCIDOC.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */
/*
 * Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */
package de.mpg.imeji.logic.export.format;

import java.io.OutputStream;
import java.io.PrintWriter;

import de.mpg.imeji.logic.export.Export;
import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.search.util.SearchIndexInitializer;
import de.mpg.imeji.logic.search.vo.SearchIndex;

/**
 * Explain the index for the search
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class ExplainSearch extends Export
{
    /*
     * (non-Javadoc)
     * @see de.mpg.imeji.logic.export.Export#export(java.io.OutputStream, de.mpg.imeji.logic.search.SearchResult)
     */
    @Override
    public void export(OutputStream out, SearchResult sr)
    {
        PrintWriter writer = new PrintWriter(out);
        writer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        writer.println();
        writer.append("<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:imeji=\"http://imeji.org/terms/\" xmlns:dcterms=\"http://purl.org/dc/terms/\">\n");
        for (SearchIndex index : Search.indexes.values())
        {
            writer.append("<imeji:index rdf:about=\"" + index.getNamespace() + "\">");
            writer.append("<dcterms:title>" + index.getName() + "</dcterms:title>");
            if (index.getParent() != null)
            {
                writer.append("<imeji:parent rdf:about=\">" + index.getParent().getNamespace() + "\"/>");
            }
            writer.append("</imeji:index>");
        }
        writer.append("</rdf:RDF>");
        writer.flush();
        writer.close();
    }

    /*
     * (non-Javadoc)
     * @see de.mpg.imeji.logic.export.Export#getContentType()
     */
    @Override
    public String getContentType()
    {
        return "application/xml";
    }

    /*
     * (non-Javadoc)
     * @see de.mpg.imeji.logic.export.Export#init()
     */
    @Override
    public void init()
    {
        // TODO Auto-generated method stub
    }
}
