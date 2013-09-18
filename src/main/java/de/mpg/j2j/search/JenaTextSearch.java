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
package de.mpg.j2j.search;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import org.apache.jena.query.text.EntityDefinition;
import org.apache.jena.query.text.TextDatasetFactory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;

import com.hp.hpl.jena.vocabulary.RDFS;

import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.Album;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.predefinedMetadata.Text;
import de.mpg.j2j.helper.J2JHelper;

/**
 * TODO Description
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class JenaTextSearch
{
    /**
     * 
     */
    public JenaTextSearch()
    {
        // TODO Auto-generated constructor stub
    }

    public void init()
    {
        try
        {
            init(Item.class);
            init(CollectionImeji.class);
            init(Album.class);
            init(Text.class);
            init(Number.class);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error initialising Lucene Index");
        }
    }

    private void init(Class c) throws IOException
    {
        for (Field f : ObjectHelper.getAllObjectFields(c))
        {
            if (J2JHelper.isLiteral(f))
                addIndex(J2JHelper.getLiteralNamespace(f));
        }
    }

    private void addIndex(String namespace) throws IOException
    {
        EntityDefinition entDef = new EntityDefinition("uri", "text", RDFS.label);
        // Lucene
        Directory dir = new SimpleFSDirectory(new File(Imeji.tdbPath));
        // Join together into a dataset
        TextDatasetFactory.createLucene(Imeji.dataset, dir, entDef);
    }
}
