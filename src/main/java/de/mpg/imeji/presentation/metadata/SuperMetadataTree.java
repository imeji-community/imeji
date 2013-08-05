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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.uib.cipr.matrix.sparse.ISparseVector;

import de.mpg.imeji.logic.vo.Metadata;

/**
 * Implements Hierarchy for a {@link List} of {@link SuperMetadataBean}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class SuperMetadataTree
{
    private Map<String, SuperMetadataBean> map = new HashMap<String, SuperMetadataBean>();

    /**
     * Default Constructor
     */
    public SuperMetadataTree(List<SuperMetadataBean> list)
    {
        Collections.sort(list);
        list = addParent(list);
        map = createMap(list, null, "0");
    }

    /**
     * Create the {@link Map} from the {@link List}
     * 
     * @param list
     * @param parent
     * @param index
     * @return
     */
    private Map<String, SuperMetadataBean> createMap(List<SuperMetadataBean> list, SuperMetadataBean parent,
            String index)
    {
        Map<String, SuperMetadataBean> map = new HashMap<String, SuperMetadataBean>();
        for (SuperMetadataBean smd : list)
        {
            if ((smd.getParent() == null && parent == null)
                    || (smd.getParent() != null && parent != null && smd.getParent().asMetadata().getId()
                            .compareTo(parent.asMetadata().getId()) == 0))
            {
                smd.setTreeIndex(index);
                map.put(smd.getTreeIndex(), smd);
                map.putAll(createMap(list, smd, addIndex(index, "0")));
                index = incrementIndex(index);
            }
        }
        return map;
    }

    /**
     * Get the {@link SuperMetadataTree} as a {@link List}
     * 
     * @return
     */
    public List<SuperMetadataBean> getList()
    {
        List<SuperMetadataBean> list = new ArrayList<SuperMetadataBean>(map.values());
        Collections.sort(list, new TreeComparator());
        return resetPosition(list);
    }

    /**
     * Add a {@link SuperMetadataBean} to the {@link SuperMetadataTree} at the next position
     * 
     * @param smd
     */
    public void add(SuperMetadataBean smd)
    {
        String newIndex = incrementIndex(smd.getTreeIndex());
        smd.setTreeIndex(newIndex);
        insert(smd);
    }

    /**
     * Remove a {@link SuperMetadataBean} from the {@link SuperMetadataTree}
     * 
     * @param smd
     */
    public void remove(SuperMetadataBean smd)
    {
        map.remove(smd.getTreeIndex());
        for (SuperMetadataBean child : getChilds(smd.getTreeIndex()))
            map.remove(child.getTreeIndex());
    }

    /**
     * Insert a {@link SuperMetadataBean} and all its childs in the {@link SuperMetadataTree} at the position defined in
     * the getIndexTree() method. If the position is alreay used by a {@link SuperMetadataBean}, move it to next
     * position
     * 
     * @param smd
     */
    private void insert(SuperMetadataBean smd)
    {
        String insert = smd.getTreeIndex();
        if (map.containsKey(insert))
        {
            moveToNextPosition(map.get(insert));
        }
        map.put(insert, smd);
        String childIndex = addIndex(smd.getTreeIndex(), "0");
        for (SuperMetadataBean child : smd.getChilds())
        {
            child.setTreeIndex(childIndex);
            insert(child);
            childIndex = incrementIndex(childIndex);
        }
    }

    /**
     * Move a {@link SuperMetadataBean} and all its childs to the next position in the tree
     * 
     * @param smd
     */
    private void moveToNextPosition(SuperMetadataBean smd)
    {
        smd.setChilds(getChilds(smd.getTreeIndex()));
        remove(smd);
        smd.setTreeIndex(incrementIndex(smd.getTreeIndex()));
        insert(smd);
    }

    /**
     * Get the childs of tree element
     * 
     * @param parentIndex
     * @return
     */
    private List<SuperMetadataBean> getChilds(String parentIndex)
    {
        List<SuperMetadataBean> childs = new ArrayList<SuperMetadataBean>();
        for (SuperMetadataBean smb : getList())
            if (isParent(parentIndex, smb.getTreeIndex()))
                childs.add(smb);
        return childs;
    }

    /**
     * True if the index1 is a parent of the index2 (not necessary the direct parent)
     * 
     * @param index1
     * @param index2
     * @return
     */
    private boolean isParent(String index1, String index2)
    {
        return index2.startsWith(index1) && index1.length() < index2.length();
    }

    /**
     * Increment the index to one position (1,1 -> 1,2 or 2,3,0,3 -> 2,3,0,4)
     * 
     * @param index
     * @return
     */
    private String incrementIndex(String index)
    {
        String[] indexes = index.split(",");
        int i = Integer.parseInt(indexes[indexes.length - 1]) + 1;
        int endIndex = index.lastIndexOf(",");
        if (endIndex > 0)
            return addIndex(index.substring(0, endIndex), Integer.toString(i));
        return Integer.toString(i);
    }

    /**
     * Add 2 indexes (1,2 + 1 = 1,2,1)
     * 
     * @param index1
     * @param index2
     * @return
     */
    private String addIndex(String index1, String index2)
    {
        return index1 = "".equals(index1) ? index2 : index1 + "," + index2;
    }

    /**
     * Reset the position to all Metadata according to the current order. The list should be flat
     */
    private List<SuperMetadataBean> resetPosition(List<SuperMetadataBean> l)
    {
        int i = 0;
        for (SuperMetadataBean smd : l)
        {
            smd.setPos(i);
            i++;
        }
        return l;
    }

    /**
     * {@link Comparator} to sort the {@link SuperMetadataBean} in the {@link SuperMetadataTree}
     * 
     * @author saquet (initial creation)
     * @author $Author$ (last modification)
     * @version $Revision$ $LastChangedDate$
     */
    private class TreeComparator implements Comparator<SuperMetadataBean>
    {
        /*
         * (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(SuperMetadataBean md1, SuperMetadataBean md2)
        {
            return md1.getTreeIndex().compareTo(md2.getTreeIndex());
        }
    }

    /**
     * Add the Parent {@link SuperMetadataBean} to all element of the list.
     * 
     * @param flat
     * @return
     */
    private List<SuperMetadataBean> addParent(List<SuperMetadataBean> list)
    {
        for (SuperMetadataBean smd : list)
        {
            smd.setParent(findBestParent(smd, list));
        }
        return list;
    }

    /**
     * Find the parent of a {@link Metadata} from the list, and return it as {@link SuperMetadataBean}. This is
     * Dependent to the statement (if it has a parent statement), and the position of the child in the list
     * 
     * @param md
     * @param list
     * @return
     */
    private SuperMetadataBean findBestParent(SuperMetadataBean child, List<SuperMetadataBean> list)
    {
        // If the statement has no parent, the metadata doens't as well
        if (child.getStatement().getParent() == null)
            return null;
        // The possible parents
        List<SuperMetadataBean> candidates = new ArrayList<SuperMetadataBean>();
        // the position in the list of metadata with the same statement
        int position = 0;
        // When the current child is found in the list, set to true
        boolean found = false;
        // Find all candidates
        for (SuperMetadataBean md : list)
        {
            if (child.asMetadata().getId().compareTo(md.asMetadata().getId()) == 0)
                found = true;
            if (child.getStatement().getParent().compareTo(md.getStatement().getId()) == 0)
                candidates.add(md);
            if (child.getStatement().getId().compareTo(md.getStatement().getId()) == 0 && !found)
                position++;
        }
        // Return the best candidate
        if (position < candidates.size())
            return candidates.get(position);
        else if (!candidates.isEmpty())
            return candidates.get(candidates.size() - 1);
        // nothing found...
        return null;
    }
}
