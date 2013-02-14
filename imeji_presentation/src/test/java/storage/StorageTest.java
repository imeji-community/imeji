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
package storage;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Test;

import de.mpg.imeji.logic.storage.Storage;
import de.mpg.imeji.logic.storage.StorageController;
import de.mpg.imeji.logic.storage.UploadResult;
import de.mpg.imeji.logic.storage.impl.InternalStorage;
import de.mpg.imeji.logic.storage.internal.InternalStorageManager;
import de.mpg.imeji.logic.storage.util.StorageUtils;

/**
 * Test {@link Storage}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class StorageTest
{
    private static final String TEST_IMAGE = "./src/test/resources/storage/test.png";

    /**
     * Test for {@link InternalStorage}
     * 
     * @throws FileNotFoundException
     */
    @Test
    public void internaStorage()
    {
        StorageController sc = new StorageController("internal");
        InternalStorageManager ism = new InternalStorageManager();
        byte[] original = null;
        try
        {
            original = readFile(TEST_IMAGE);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        String filename = getFilename(TEST_IMAGE);
        // UPLOAD
        UploadResult res = sc.upload(filename, original);
        Assert.assertFalse(res.getOrginal() + " url is same as path",
                res.getOrginal() == ism.transformUrlToPath(res.getOrginal()));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // READ THE URL
        sc.read(res.getOrginal(), baos);
        byte[] stored = baos.toByteArray();
        // DELETE THE FILE
        sc.delete(res.getId());
        baos = new ByteArrayOutputStream();
        try
        {
            // READ TO CHECK IF THE FILE HAS BEEN DELETED
            readFile(ism.transformUrlToPath(res.getOrginal()));
            Assert.fail("File has not been deleted: " + res.getOrginal());
        }
        catch (FileNotFoundException e)
        {
            // OK, file should not be found
        }
        Assert.assertTrue(Arrays.equals(original, stored));
        Assert.assertTrue(Arrays.hashCode(original) == Arrays.hashCode(stored));
    }

    private String getFilename(String path)
    {
        return path.substring(path.lastIndexOf("/") + 1);
    }

    private byte[] readFile(String path) throws FileNotFoundException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FileInputStream fis;
        fis = new FileInputStream(path);
        StorageUtils.writeInOut(fis, baos);
        return baos.toByteArray();
    }
}
