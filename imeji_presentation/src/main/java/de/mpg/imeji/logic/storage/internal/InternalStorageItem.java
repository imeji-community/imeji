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
package de.mpg.imeji.logic.storage.internal;

/**
 * An item for the internal storage of imeji
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class InternalStorageItem
{
    private String orignalPath;
    private String webPath;
    private String thumbnailPath;
    private String fileName;
    private String id;
    private int version;

    /**
     * Default constructor
     */
    public InternalStorageItem()
    {
    }

    /**
     * Construct a new {@link InternalStorageItem} with an id
     * 
     * @param id
     */
    public InternalStorageItem(String id)
    {
        this.id = id;
    }

    public String getOrignalPath()
    {
        return orignalPath;
    }

    public void setOrignalPath(String orignalPath)
    {
        this.orignalPath = orignalPath;
    }

    public String getWebPath()
    {
        return webPath;
    }

    public void setWebPath(String webPath)
    {
        this.webPath = webPath;
    }

    public String getThumbnailPath()
    {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath)
    {
        this.thumbnailPath = thumbnailPath;
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public int getVersion()
    {
        return version;
    }

    public void setVersion(int version)
    {
        this.version = version;
    }
}
