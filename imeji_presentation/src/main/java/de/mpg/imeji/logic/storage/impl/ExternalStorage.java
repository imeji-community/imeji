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
package de.mpg.imeji.logic.storage.impl;

import de.mpg.imeji.logic.storage.Storage;
import de.mpg.imeji.logic.storage.UploadResult;
import de.mpg.imeji.logic.storage.Storage.FileResolution;

/**
 * The {@link Storage} implementation for external Storages. Can only read files (if the files are publicly available).
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class ExternalStorage implements Storage
{
    /*
     * (non-Javadoc)
     * @see de.mpg.imeji.logic.storage.Storage#getName()
     */
    @Override
    public String getName()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see de.mpg.imeji.logic.storage.Storage#upload(byte[])
     */
    @Override
    public UploadResult upload(String filename, byte[] bytes)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see de.mpg.imeji.logic.storage.Storage#read(java.lang.String)
     */
    @Override
    public byte[] read(String url)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see de.mpg.imeji.logic.storage.Storage#delete(java.lang.String)
     */
    @Override
    public void delete(String url)
    {
        // TODO Auto-generated method stub
    }

    /*
     * (non-Javadoc)
     * @see de.mpg.imeji.logic.storage.Storage#update(java.lang.String, byte[])
     */
    @Override
    public void update(String url, byte[] bytes)
    {
        // TODO Auto-generated method stub
    }
}
