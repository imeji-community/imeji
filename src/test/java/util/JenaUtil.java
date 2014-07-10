package util;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.commons.io.FileUtils;

import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.util.StringHelper;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.beans.PropertyBean;
import de.mpg.imeji.presentation.util.PropertyReader;

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
/**
 * Utility class to use Jena in the unit test
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class JenaUtil
{
    /**
     * Init a Jena Instance for Testing
     */
    public static void initJena()
    {
        try
        {
            // Init PropertyBean
            new PropertyBean();
            // Read tdb location
            String tdb = PropertyReader.getProperty("imeji.tdb.path");
            // remove old Database
            File f = new File(tdb);
            if(f.exists())
            	FileUtils.cleanDirectory(f);
            // Create new tdb
            Imeji.init(tdb);
        }
        catch (IOException | URISyntaxException e)
        {
            throw new RuntimeException("Error initialiting Jena for testing: ", e);
        }
    }

    /**
     * Add a User to Jena
     * 
     * @param email
     * @param name
     * @param pwd
     */
    public static void addUser(String email, String name, String pwd)
    {
        try
        {
            UserController c = new UserController(Imeji.adminUser);
            User user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setEncryptedPassword(StringHelper.convertToMD5(pwd));
            c.create(user);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
