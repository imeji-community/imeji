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
package de.mpg.imeji.presentation.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.mpg.imeji.logic.search.SPARQLSearch;
import de.mpg.imeji.logic.search.vo.SearchGroup;
import de.mpg.imeji.logic.search.vo.SearchIndex;
import de.mpg.imeji.logic.search.vo.SearchOperators;
import de.mpg.imeji.logic.search.vo.SearchPair;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.logic.search.vo.SearchLogicalRelation.LOGICAL_RELATIONS;

/**
 * TODO Description
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class FileTypes
{
    /**
     * A Single File Type (video, image, sound, etc.)
     * 
     * @author saquet (initial creation)
     * @author $Author$ (last modification)
     * @version $Revision$ $LastChangedDate$
     */
    public class Type
    {
        private String name;
        private String extensions;

        /**
         * Default Constructor
         */
        public Type(String name, String extension)
        {
            this.name = name;
            this.extensions = extension;
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            return name + "=" + extensions;
        }

        /**
         * Return the type as a {@link SearchGroup} with can be used in the {@link SearchQuery}
         * 
         * @return
         */
        public SearchGroup getAsSearchGroup()
        {
            SearchGroup g = new SearchGroup();
            for (String extension : extensions.split(","))
            {
                if (!g.isEmpty())
                    g.addLogicalRelation(LOGICAL_RELATIONS.OR);
                g.addPair(new SearchPair(SPARQLSearch.getIndex(SearchIndex.names.filename), SearchOperators.REGEX, "."
                        + extension));
            }
            return g;
        }

        /**
         * @return the name
         */
        public String getName()
        {
            return name;
        }

        /**
         * @param name the name to set
         */
        public void setName(String name)
        {
            this.name = name;
        }

        /**
         * @return the extensions
         */
        public String getExtensions()
        {
            return extensions;
        }

        /**
         * @param extensions the extensions to set
         */
        public void setExtensions(String extensions)
        {
            this.extensions = extensions;
        }
    }

    private List<Type> types;
    private Pattern typePattern = Pattern.compile("\\[(.*?)\\]");

    /**
     * Initialize a new FilterTypeBean
     */
    public FileTypes(String s)
    {
        parse(s);
    }

    /**
     * Parse a String for the following format: <br/>
     * [image=jpg,png,tiff][video=avi,mp4]
     * 
     * @param s
     */
    private void parse(String s)
    {
        this.types = new ArrayList<>();
        if (s != null)
        {
            Matcher m = typePattern.matcher(s);
            while (m.find())
            {
                String typeString = m.group(1);
                this.types.add(new Type(typeString.split("=")[0], typeString.split("=")[1]));
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        String s = "";
        for (Type type : types)
        {
            s += "[" + type.toString() + "]";
        }
        return s;
    }

    /**
     * Return the type according to its name. If not found, return null.
     * 
     * @param name
     * @return
     */
    public Type getType(String name)
    {
        for (Type type : types)
            if (type.getName().equals(name))
                return type;
        return null;
    }

    /**
     * Add an emtpy type
     * 
     * @param pos
     */
    public void addType(int pos)
    {
        types.add(pos, new Type("", ""));
    }

    /**
     * Remove a type
     * 
     * @param pos
     */
    public void removeType(int pos)
    {
        types.remove(pos);
    }

    /**
     * @return the type
     */
    public List<Type> getTypes()
    {
        return types;
    }

    /**
     * @param type the type to set
     */
    public void setTypes(List<Type> types)
    {
        this.types = types;
    }
}
