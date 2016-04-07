/*
 * 
 * metadataTree.remove(smb);metadataTree.remove(smb); * CDDL HEADER START
 * 
 * The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with
 * the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license.
 * See the License for the specific language governing permissions and limitations under the
 * License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License
 * file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with
 * the fields enclosed by brackets "[]" replaced with your own identifying information: Portions
 * Copyright [yyyy] [name of copyright owner]
 * 
 * CDDL HEADER END
 */
/*
 * Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */
package de.mpg.imeji.presentation.metadata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mpg.imeji.logic.vo.Metadata;

/**
 * Implements Hierarchy for a {@link List} of {@link MetadataWrapper}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class MetadataWrapperTree implements Serializable {
  private static final long serialVersionUID = 8726381279225329881L;
  private Map<String, MetadataWrapper> map = new HashMap<String, MetadataWrapper>();

  /**
   * Default Constructor
   */
  public MetadataWrapperTree(List<MetadataWrapper> list) {
    // Sort according to profile and to previous metadata position
    Collections.sort(list);
    // A Set the parent according to the current order
    list = setParents(list);
    // Create the map
    map = createMap(list, null, "0");
    // Set the childs
    for (MetadataWrapper smb : map.values()) {
      smb.setChilds(getChilds(smb.getTreeIndex()));
    }
  }

  /**
   * Create the {@link Map} from the {@link List}
   * 
   * @param list
   * @param parent
   * @param index
   * @return
   */
  private Map<String, MetadataWrapper> createMap(List<MetadataWrapper> list, MetadataWrapper parent,
      String index) {
    Map<String, MetadataWrapper> map = new HashMap<String, MetadataWrapper>();
    for (MetadataWrapper smd : list) {
      if ((parent == null && isRootMetadata(smd)) || isChild(smd, parent)) {
        smd.setTreeIndex(index);
        map.put(smd.getTreeIndex(), smd);
        map.putAll(createMap(removeFromList(list, smd), smd, addIndex(index, "0")));
        index = incrementIndex(index);
      }
    }
    return map;
  }

  /**
   * True if the {@link MetadataWrapper} is a root metadata
   * 
   * @param smb
   * @return
   */
  private boolean isRootMetadata(MetadataWrapper smb) {
    return smb.getParent() == null;
  }

  /**
   * True if the {@link MetadataWrapper} smb is child of parent
   * 
   * @param smb
   * @param parent
   * @return
   */
  private boolean isChild(MetadataWrapper smb, MetadataWrapper parent) {
    if (parent == null || smb.getParent() == null) {
      return false;
    } else {
      return smb.getParent().getMetadata().getId().compareTo(parent.getMetadata().getId()) == 0;
    }
  }

  /**
   * Remove the {@link MetadataWrapper} smb from the {@link List}. A new list is returned, which
   * allows to use this method within the for loop on the list elements
   * 
   * @param list
   * @param smb
   * @return
   */
  private List<MetadataWrapper> removeFromList(List<MetadataWrapper> list, MetadataWrapper smb) {
    List<MetadataWrapper> subList = new ArrayList<MetadataWrapper>(list);
    subList.remove(smb);
    return subList;
  }

  /**
   * Get the {@link MetadataWrapperTree} as a {@link List}
   * 
   * @return
   */
  public List<MetadataWrapper> getList() {
    List<MetadataWrapper> list = new ArrayList<MetadataWrapper>(map.values());
    Collections.sort(list, new TreeComparator());
    return resetPosition(list);
  }

  /**
   * Add a {@link MetadataWrapper} to the {@link MetadataWrapperTree} at the next position
   * 
   * @param smd
   */
  public void add(MetadataWrapper smd) {
    String newIndex = incrementIndex(smd.getTreeIndex());
    smd.setTreeIndex(newIndex);
    insert(smd);
  }

  /**
   * Remove a {@link MetadataWrapper} from the {@link MetadataWrapperTree}. The childs of the metadata
   * are removed as well
   * 
   * @param smd
   */
  public void remove(MetadataWrapper smd) {
    map.remove(smd.getTreeIndex());
    for (MetadataWrapper child : getChilds(smd.getTreeIndex())) {
      map.remove(child.getTreeIndex());
    }
  }

  /**
   * Remove this metadata but don't remove its childs.
   * 
   * @param smd
   */
  public void removeButKeepChilds(MetadataWrapper smd) {
    map.remove(smd.getTreeIndex());
  }

  /**
   * Insert a {@link MetadataWrapper} and all its childs in the {@link MetadataWrapperTree} at the
   * position defined in the getIndexTree() method. If the position is alreay used by a
   * {@link MetadataWrapper}, move it to next position
   * 
   * @param smd
   */
  private void insert(MetadataWrapper smd) {
    String insert = smd.getTreeIndex();
    if (map.containsKey(insert)) {
      moveToNextPosition(map.get(insert));
    }
    map.put(insert, smd);
    String childIndex = addIndex(smd.getTreeIndex(), "0");
    for (MetadataWrapper child : smd.getChilds()) {
      child.setTreeIndex(childIndex);
      insert(child);
      childIndex = incrementIndex(childIndex);
    }
  }

  /**
   * Move a {@link MetadataWrapper} and all its childs to the next position in the tree
   * 
   * @param smd
   */
  private void moveToNextPosition(MetadataWrapper smd) {
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
  public List<MetadataWrapper> getChilds(String parentIndex) {
    List<MetadataWrapper> childs = new ArrayList<MetadataWrapper>();
    // for (MetadataWrapper smb : getList())
    for (MetadataWrapper smb : map.values()) {
      if (isParent(parentIndex, smb.getTreeIndex())) {
        childs.add(smb);
      }
    }
    return childs;
  }

  /**
   * True if the index1 is a parent of the index2 (not necessary the direct parent):<br/>
   * - 1 is parent of all index 1,....
   * 
   * @param index1
   * @param index2
   * @return
   */
  private boolean isParent(String index1, String index2) {
    return index2.startsWith(index1 + ",") && index1.length() < index2.length();
  }

  /**
   * Increment the index to one position (1,1 -> 1,2 or 2,3,0,3 -> 2,3,0,4)
   * 
   * @param index
   * @return
   */
  private String incrementIndex(String index) {
    String[] indexes = index.split(",");
    int i = Integer.parseInt(indexes[indexes.length - 1]) + 1;
    int endIndex = index.lastIndexOf(",");
    if (endIndex > 0) {
      return addIndex(index.substring(0, endIndex), Integer.toString(i));
    }
    return Integer.toString(i);
  }

  /**
   * Add 2 indexes (1,2 + 1 = 1,2,1)
   * 
   * @param index1
   * @param index2
   * @return
   */
  private String addIndex(String index1, String index2) {
    return index1 = "".equals(index1) ? index2 : index1 + "," + index2;
  }

  /**
   * Reset the position to all Metadata according to the current order. The list should be flat
   */
  public static List<MetadataWrapper> resetPosition(List<MetadataWrapper> l) {
    int i = 0;
    for (MetadataWrapper smd : l) {
      smd.setPos(i);
      i++;
    }
    return l;
  }

  /**
   * {@link Comparator} to sort the {@link MetadataWrapper} in the {@link MetadataWrapperTree}
   * 
   * @author saquet (initial creation)
   * @author $Author$ (last modification)
   * @version $Revision$ $LastChangedDate$
   */
  private class TreeComparator implements Comparator<MetadataWrapper> {
    /*
     * (non-Javadoc)
     * 
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(MetadataWrapper md1, MetadataWrapper md2) {
      String[] index1 = md1.getTreeIndex().split(",");
      String[] index2 = md2.getTreeIndex().split(",");
      int minLength = (index1.length < index2.length ? index1.length : index2.length);
      for (int i = 0; i < minLength; i++) {
        int v1 = Integer.parseInt(index1[i]);
        int v2 = Integer.parseInt(index2[i]);
        if (v1 > v2) {
          return 1;
        } else if (v1 < v2) {
          return -1;
        }
      }
      if (index1.length > index2.length) {
        return 1;
      } else if (index1.length < index2.length) {
        return -1;
      }
      return 0;
    }
  }

  /**
   * Set the Parent {@link MetadataWrapper} to all element of the list. This is made according to
   * the current order of the list (see findBestParent() method)
   * 
   * @param flat
   * @return
   */
  private List<MetadataWrapper> setParents(List<MetadataWrapper> list) {
    for (MetadataWrapper smd : list) {
      smd.setParent(findBestParent(smd, list));
    }
    return list;
  }

  /**
   * Find the parent of a {@link Metadata} from the list, and return it as {@link MetadataWrapper} .
   * This is Dependent to the statement (if it has a parent statement), and the position of the
   * child in the list
   * 
   * @param md
   * @param list
   * @return
   */
  private MetadataWrapper findBestParent(MetadataWrapper child, List<MetadataWrapper> list) {
    // If the statement has no parent, the metadata doens't as well
    if (child.getStatement().getParent() == null)
      return null;
    // The possible parents
    List<MetadataWrapper> candidates = new ArrayList<MetadataWrapper>();
    // Find all candidates
    for (MetadataWrapper md : list) {
      if (child.getStatement().getParent().compareTo(md.getStatement().getId()) == 0) {
        candidates.add(md);
      }
    }
    // Return the best candidate
    for (int i = candidates.size() - 1; i >= 0; i--) {
      if (candidates.get(i).getPos() < child.getPos()) {
        return candidates.get(i);
      }
    }
    return null;
  }
}
