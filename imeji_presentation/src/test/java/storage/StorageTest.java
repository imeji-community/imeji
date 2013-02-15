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
    private static final String FILENAME = "test";
    private static final String INTERNATIONAL_CHARACHTERS = "japanese:テスト  chinese:實驗 yiddish:פּראָבע arab:اختبار bengali: পরীক্ষা";
    private static final String LONG_NAME = "12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
    		"0123456789012345678901234567890adasd dsdfdj ghdjghfdgh gfhg df gfhdfghdgf hisfgshdfghsdi gfhsdigf sdi gfidsf gsidfhsidf gsdih " +
    		"hsgfhidsgfhdsg fh dsfshdgfhidsgfihsdgfiwuzfgisdh fg shdfg sdihfg sdihgfisdgfhsdgf ihsdg fhsdgfizsdgf zidsgfizsd fi fhsdhfgsdhfg" +
    		"hgf dhfgdshfgdshfghsdg fhsdf ghsdg fsdhf gsdjgf sdjgfsd fgdszfg sdfzgsdzgf sdfg dgfhisgfigifg i";
    /**
     * Not working: *
     */
    private static final String SPECIAL_CHARACHTERS = "!\"§$%&/()=? '#_-.,";

    /**
     * Test for {@link InternalStorage}
     * 
     * @throws FileNotFoundException
     */
    @Test
    public void internalStorageBasic()
    {
        uploadReadDelete(FILENAME + ".png");
    }

    @Test
    public void internalStorageSpecialFileName()
    {
        uploadReadDelete(SPECIAL_CHARACHTERS + ".png");
    }

    @Test
    public void internalStorageInternationalFileName()
    {
        uploadReadDelete(INTERNATIONAL_CHARACHTERS + ".png");
    }

    @Test
    public void internalStorageLongFileName()
    {
        uploadReadDelete(LONG_NAME + ".png");
    }

    private void uploadReadDelete(String filename)
    {
        StorageController sc = new StorageController("internal");
        InternalStorageManager manager = new InternalStorageManager();
        byte[] original = null;
        try
        {
            original = readFile(TEST_IMAGE);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        // UPLOAD
        UploadResult res = sc.upload(filename, original, "1");
        Assert.assertFalse(res.getOrginal() + " url is same as path",
                res.getOrginal() == manager.transformUrlToPath(res.getOrginal()));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // READ THE URL
        sc.read(res.getOrginal(), baos);
        byte[] stored = baos.toByteArray();
        // DELETE THE FILE
        sc.delete(res.getId());
        Assert.assertEquals(0, manager.getAdministrator().getNumberOfFiles());
        Assert.assertTrue(Arrays.equals(original, stored));
        Assert.assertTrue(Arrays.hashCode(original) == Arrays.hashCode(stored));
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
