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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.util.MetadataFactory;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.MetadataSet;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.presentation.metadata.util.MetadataHelper;
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
    private MetadataProfile profile = null;
    private SuperMetadataTree metadataTree;
    private static final Logger logger = Logger.getLogger(MetadataSetBean.class);

    /**
     * Constructor for a {@link MetadataSet}
     * 
     * @param mds
     * @param addEmtpyValue if true, add an emtpy metadata for all {@link Statement} which don't have any value
     */
    public MetadataSetBean(MetadataSet mds, boolean addEmtpyValue)
    {
        // Get the profile
        profile = ObjectCachedLoader.loadProfile(mds.getProfile());
        // Init the list of metadata
        initTreeFromList(toSuperList((List<Metadata>)mds.getMetadata()));
        if (addEmtpyValue)
            addEmtpyValues();
    }

    /**
     * Initialize the {@link MetadataSetBean} with a flat {@link List} of {@link SuperMetadataBean}
     * 
     * @param flat
     */
    public void initTreeFromList(List<SuperMetadataBean> list)
    {
        metadataTree = new SuperMetadataTree(list);
    }

    /**
     * Remove all Emtpy {@link SuperMetadataBean} of the {@link MetadataSetBean} (if and only if the
     * {@link SuperMetadataBean} doesn't have a child)
     */
    public void trim()
    {
        for (SuperMetadataBean smb : metadataTree.getList())
        {
            if (smb.isEmpty())
                metadataTree.remove(smb);
        }
    }

    /**
     * Add a new emtpy {@link Metadata} for the passed statement
     * 
     * @param st
     */
    public void appendEmtpyMetadata(Statement st)
    {
        metadataTree.add(new SuperMetadataBean(MetadataFactory.createMetadata(st), st));
    }

    /**
     * Create a {@link List} of {@link SuperMetadataBean} from a list of {@link Metadata}
     * 
     * @return
     */
    private List<SuperMetadataBean> toSuperList(List<Metadata> l)
    {
        List<SuperMetadataBean> flat = new ArrayList<SuperMetadataBean>();
        for (Metadata md : l)
        {
            Statement st = ProfileHelper.getStatement(md.getStatement(), profile);
            if (st != null)
            {
                SuperMetadataBean smd = new SuperMetadataBean(md, st);
                flat.add(smd);
            }
            else
            {
                logger.error("A metadata " + md.getId() + " is defined with a non existing statement in profile "
                        + this.profile.getId());
            }
        }
        return flat;
    }

    /**
     * Add to the {@link SuperMetadataTree} new emtpy {@link SuperMetadataBean} when it hadn't been already defined one.
     * this method does the contrary as the trim() method
     */
    public void addEmtpyValues()
    {
        // Find the Metadata which have not be defined with a value into the Metadataset
        List<SuperMetadataBean> l = new ArrayList<SuperMetadataBean>(metadataTree.getList());
        for (Statement st : profile.getStatements())
        {
            if (!exists(st))
                l.add(new SuperMetadataBean(MetadataFactory.createMetadata(st), st));
        }
        // Reinit the tree
        initTreeFromList(l);
    }

    /**
     * True if the {@link MetadataSetBean} has one {@link Metadata} with this {@link Statement}
     * 
     * @param st
     * @return
     */
    public boolean exists(Statement st)
    {
        for (SuperMetadataBean md : metadataTree.getList())
        {
            if (md.getStatement().getId().compareTo(st.getId()) == 0)
                return true;
        }
        return false;
    }

    /**
     * True if the {@link MetadataSetBean} has one {@link Metadata} with this {@link Statement} which is not emtpy
     * 
     * @param st
     * @return
     */
    public boolean existsNotEmtpy(Statement st)
    {
        for (SuperMetadataBean md : metadataTree.getList())
        {
            if (!MetadataHelper.isEmpty(md.asMetadata()))
                if (md.getStatement().getId().compareTo(st.getId()) == 0
                        || ProfileHelper.isParent(st, md.getStatement(), profile))
                    return true;
        }
        return false;
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

    /**
     * @return the metadataTree
     */
    public SuperMetadataTree getTree()
    {
        return metadataTree;
    }

    /**
     * @param metadataTree the metadataTree to set
     */
    public void setTree(SuperMetadataTree metadataTree)
    {
        this.metadataTree = metadataTree;
    }
}
