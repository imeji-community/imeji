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
package de.mpg.imeji.presentation.metadata;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.MetadataSet;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.presentation.util.ObjectCachedLoader;
import de.mpg.imeji.presentation.util.ProfileHelper;

/**
 * The Java Bean for a {@link MetadataSet}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class MetadataSetBean
{
    private List<SuperMetadataBean> metadata = new ArrayList<SuperMetadataBean>();
    private MetadataProfile profile = null;

    /**
     * Constructor for a {@link MetadataSet}
     * 
     * @param mds
     */
    public MetadataSetBean(MetadataSet mds)
    {
        // Get the profile
        profile = ObjectCachedLoader.loadProfile(mds.getProfile());
        // Init the list of metadata
        for (Metadata md : mds.getMetadata())
        {
            Statement st = ProfileHelper.getStatement(md.getStatement(), profile);
            SuperMetadataBean smd = new SuperMetadataBean(md, st);
            smd.setParent(findParent(smd));
            smd.setPreview(st.isPreview());
            metadata.add(smd);
        }
    }

    /**
     * Find the parent of a {@link Metadata}, and return it as {@link SuperMetadataBean}
     * 
     * @param md
     * @return
     */
    private SuperMetadataBean findParent(SuperMetadataBean smd)
    {
        Statement st = ProfileHelper.getStatement(smd.getStatement().getId(), profile);
        if (st == null)
            return null;
        URI parentURI = st.getParent();
        if (parentURI == null)
            return null;
        // Go to the metadata list backward, and search for the parent
        for (int i = metadata.size() - 1; i >= 0; i--)
        {
            // If the metadata is the defined with the parent statement, then it is the parent metadata
            if (metadata.get(i).getStatement().getId().compareTo(parentURI) == 0)
            {
                return metadata.get(i);
            }
        }
        return null;
    }

    /**
     * getter
     * 
     * @return the metadata
     */
    public List<SuperMetadataBean> getMetadata()
    {
        return metadata;
    }

    /**
     * setter
     * 
     * @param metadata the metadata to set
     */
    public void setMetadata(List<SuperMetadataBean> metadata)
    {
        this.metadata = metadata;
    }

    /**
     * getter
     * 
     * @return the profile
     */
    public MetadataProfile getProfile()
    {
        return profile;
    }

    /**
     * setter
     * 
     * @param profile the profile to set
     */
    public void setProfile(MetadataProfile profile)
    {
        this.profile = profile;
    }
}
