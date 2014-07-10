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
package search;

import java.io.IOException;
import java.net.URLEncoder;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import util.JenaUtil;

import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.presentation.beans.PropertyBean;
import de.mpg.imeji.presentation.search.URLQueryTransformer;

/**
 * Tests for the methods in {@link URLQueryTransformer}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class URLQueryTransformerTest
{
    /**
     * TODO Non working characters: ()+=
     */
    private static String specialsChar = "japanese:テスト  chinese:實驗 yiddish:פּראָבע arab:اختبار bengali: পরীক্ষা other:öäü@ß$&@*~!?{}[]-#'.,áò";
    private static String advancedQuery = "(col==\"http://imeji.org/collection/86\" AND (e6537a19-86b6-47ca-bba7-c8cc4d6bd6bc:text=\"TEST\") OR (750c1b37-f766-4b74-9d83-ddc858ff4365:title=\"TEST\") OR (type==\"http://imeji.org/terms/metadata#number\"))";
    private static String simpleQuery = "TEST";

    @Before
    public void init()
    {
        new PropertyBean();
        JenaUtil.initJena();
        JenaUtil.addUser("saquet@mpdl.mpg.de", "saquet", "test");
        UserController c = new UserController(Imeji.adminUser);
        try
        {
            c.retrieve("saquet@mpdl.mpg.de");
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Test the methods for an advanced search query
     * 
     * @throws IOException
     */
    @Test
    public void testAdvancedSearch() throws IOException
    {
        test(advancedQuery, false);
    }

    /**
     * Test the methods for an simple search query
     * 
     * @throws IOException
     */
    @Test
    public void testSimpleSeach() throws IOException
    {
        test(simpleQuery, true);
    }

    /**
     * Test the methods for an advanced search query with special characters
     * 
     * @throws IOException
     */
    @Test
    public void testAdvancedSearchWithSpecialCharacter() throws IOException
    {
        test(advancedQuery.replace("TEST", specialsChar), false);
    }

    /**
     * Test the methods for an simple search query with special characters
     * 
     * @throws IOException
     */
    @Test
    public void testSimpleSearchWithSpecialCharacter() throws IOException
    {
        test(simpleQuery.replace("TEST", specialsChar), true);
    }

    /**
     * Make the test for one String (encoded and non encoded)
     * 
     * @param query
     * @throws IOException
     */
    private void test(String query, boolean simple) throws IOException
    {
        String encodedQuery = URLEncoder.encode(query, "UTF-8");
        SearchQuery sq = URLQueryTransformer.parseStringQuery(query);
        String resultEncoded = URLQueryTransformer.transform2UTF8URL(sq);
        String resultNotCoded = URLQueryTransformer.transform2URL(sq);
        // Prepare the query for comparison
        // remove non relevant spaces
        query = query.trim();
        encodedQuery = encodedQuery.trim();
        // Set the simple query
        if (simple)
        {
            query = toSimpleQuery(query);
            encodedQuery = URLEncoder.encode(query, "UTF-8");
        }
        Assert.assertEquals(query, resultNotCoded);
        Assert.assertEquals(encodedQuery, resultEncoded);
    }

    /**
     * Transform a string query to a imeji simple query
     * 
     * @param q
     * @return
     */
    private String toSimpleQuery(String q)
    {
        return "all=\"" + q + "\"";
    }
}
