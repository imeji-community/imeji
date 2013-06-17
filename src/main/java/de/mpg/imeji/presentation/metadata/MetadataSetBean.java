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

import de.mpg.imeji.logic.util.MetadataFactory;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.MetadataSet;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.presentation.metadata.editors.MetadataEditor;
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
    private MetadataSet mds = null;

    /**
     * Constructor for a {@link MetadataSet}
     * 
     * @param mds
     */
    public MetadataSetBean(MetadataSet mds)
    {
        this.mds = mds;
        // Get the profile
        profile = ObjectCachedLoader.loadProfile(mds.getProfile());
        // Init the list of metadata
        initMetadataList();
    }

    /**
     * Prepare the {@link MetadataSet} for the {@link MetadataEditor}, i.e, add emtpy {@link Metadata} if none is
     * defined for one {@link Statement}
     */
    public void prepareMetadataSetForEditor()
    {
        mds.sortMetadata();
        mds.setMetadata(createListOfMetadataWithExistingValuesAndEmtpyValues());
        initMetadataList();
    }

    /**
     * Initialize the {@link List} of {@link SuperMetadataBean} with the current {@link MetadataSet}
     */
    private void initMetadataList()
    {
        metadata = new ArrayList<SuperMetadataBean>();
        for (Metadata md : mds.getMetadata())
        {
            Statement st = ProfileHelper.getStatement(md.getStatement(), profile);
            SuperMetadataBean smd = new SuperMetadataBean(md, st);
            smd.setParent(findParent(smd));
            smd.setLastParent(ProfileHelper.getLastParent(st, profile));
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
     * Create a new {@link List} of {@link Metadata} with the {@link Metadata} of the current {@link Item} plus, if
     * missing according to the {@link MetadataProfile}, new emtpy {@link Metadata}
     * 
     * @return
     */
    private List<Metadata> createListOfMetadataWithExistingValuesAndEmtpyValues()
    {
        List<Metadata> l = new ArrayList<Metadata>();
        // add the existing Metadata to the list, and if they is a missing metadata, add a new emtpy one
        for (Metadata md : mds.getMetadata())
        {
            if (l.isEmpty() && !isFirstStatement(md.getStatement()))
            {
                // Add all metadata that should be before the first existing metadata
                l.addAll(createMetadataBetween(null, md.getStatement()));
            }
            else if (!l.isEmpty() && !isNextStatement(l.get(l.size() - 1).getStatement(), md.getStatement())
                    && !isbefore(md.getStatement(), l.get(l.size() - 1).getStatement()))
            {
                // Add all metadata that should be before the next metadata in the list
                l.addAll(createMetadataBetween(l.get(l.size() - 1).getStatement(), md.getStatement()));
            }
            // Add the existing metadata
            l.add(md);
        }
        URI lastStatement = null;
        if (!l.isEmpty())
            lastStatement = l.get(l.size() - 1).getStatement();
        // add all no created metadata after the last metadata
        l.addAll(createMetadataBetween(lastStatement, null));
        return setPositionToMetadata(l);
    }

    /**
     * Create new {@link Metadata} for the {@link Statement} which are ordered betwenn from and to according to the
     * {@link MetadataProfile}
     * 
     * @param from
     * @param to
     * @return
     */
    private List<Metadata> createMetadataBetween(URI from, URI to)
    {
        List<Metadata> l = new ArrayList<Metadata>();
        int fromPosition = 0;
        if (from != null)
            fromPosition = ProfileHelper.getStatement(from, profile).getPos() + 1;
        int toPosition = profile.getStatements().size();
        if (to != null)
            toPosition = ProfileHelper.getStatement(to, profile).getPos();
        for (Statement st : profile.getStatements())
        {
            if (st.getPos() >= fromPosition && st.getPos() < toPosition)
            {
                l.add(MetadataFactory.createMetadata(st));
            }
        }
        return l;
    }

    /**
     * True if the {@link Statement} with the give {@link URI} is the first in the current {@link MetadataProfile}
     * 
     * @param st
     * @return
     */
    private boolean isFirstStatement(URI st)
    {
        return ProfileHelper.getStatement(st, profile).getPos() == 0;
    }

    /**
     * True if the {@link Statement} st2 is next to st1 according to the order in the current {@link MetadataProfile}
     * 
     * @param st1
     * @param st2
     * @return
     */
    private boolean isNextStatement(URI st1, URI st2)
    {
        return ProfileHelper.getStatement(st1, profile).getPos() + 1 == ProfileHelper.getStatement(st2, profile)
                .getPos();
    }

    /**
     * True if st1 is before than st2 according to the order in the current {@link MetadataProfile}
     * 
     * @param st1
     * @param st2
     * @return
     */
    private boolean isbefore(URI st1, URI st2)
    {
        return ProfileHelper.getStatement(st1, profile).getPos() < ProfileHelper.getStatement(st2, profile).getPos();
    }

    /**
     * /** Set the position of the {@link Metadata} according to their current order
     * 
     * @param mds
     * @return
     */
    private List<Metadata> setPositionToMetadata(List<Metadata> l)
    {
        int pos = 0;
        for (Metadata md : l)
        {
            md.setPos(pos);
            pos++;
        }
        return l;
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
