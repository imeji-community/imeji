package de.mpg.imeji.logic.controller;

import com.google.common.collect.Iterables;
import de.mpg.imeji.logic.vo.Space;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.rest.api.CollectionService;
import de.mpg.imeji.rest.process.RestProcessUtils;
import de.mpg.imeji.rest.resources.test.integration.ImejiTestBase;
import de.mpg.imeji.rest.to.CollectionTO;
import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URI;

import static de.mpg.imeji.logic.Imeji.adminUser;
import static de.mpg.imeji.logic.util.ResourceHelper.getStringFromPath;
import static de.mpg.imeji.rest.resources.test.integration.MyTestContainerFactory.STATIC_CONTEXT_REST;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static util.JenaUtil.testUser;

/**
 * Created by vlad on 15.04.15.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SpaceControllerTest extends ImejiTestBase{

    private static final Logger LOGGER = LoggerFactory
            .getLogger(SpaceControllerTest.class);
    public static final int COL_NUM = 100;

    private static SpaceController sc;
    private static CollectionController cc;
    private static URI spaceId;
    private static Space space;

    @BeforeClass
    public static void specificSetup() {
        sc = new SpaceController();
        cc = new CollectionController();
    }

    @Test
    public void test_1_Create() throws Exception {

        Space sp1 = ImejiFactory.newSpace();
        sp1.setTitle("Space 1");
        spaceId = sc.create(sp1, adminUser);
        space = sc.retrieve(sp1.getId(), adminUser);
        assertThat(sp1.getTitle(), equalTo(space.getTitle()));

    }

    @Test
    public void test_2_Update() throws Exception {
        String changed = "_CHANGED";
        space.setTitle(space.getTitle() + changed);
        sc.addCollection(space, initCollection(), adminUser);
        String collId2 = initCollection();
        sc.addCollection(space, collId2, adminUser);
        //duplicates are not added!
        sc.addCollection(space, collId2, adminUser);

        assertThat(space.getTitle(), endsWith(changed));
        assertThat(sc.retrieveCollections(space), hasSize(2));
    }

    @Test
    public void test_3_UpdateFile() throws Exception {
        File uploadFile = new File("src/test/resources/storage/test.jpg");
        space = sc.updateFile(space, uploadFile, adminUser);
        uploadFile = new File("src/test/resources/storage/test2.jpg");
        space = sc.updateFile(space, uploadFile, adminUser);
        assertTrue(FileUtils.contentEquals(uploadFile,
                new File(sc.transformUrlToPath(space.getLogoUrl().toURL().toString()))
        ));
    }

    @Test
    public void test_4_Retrieve() throws Exception {
        assertThat(sc.retrieve(space.getId(), testUser).getId(), equalTo(spaceId));
        assertThat(sc.retrieve(space.getId(), null).getId(), equalTo(spaceId));
    }

    @Test
    public void test_5_RetrieveAll() throws Exception {
        Space sp2 = ImejiFactory.newSpace();
        sp2.setTitle("Space 2");
        sc.create(sp2, adminUser);
        assertThat(sc.retrieveAll(), hasSize(2));
    }

    @Test
    public void test_6_RemoveCollection() throws Exception {
        assertThat(
                sc.removeCollection(space, Iterables.getLast(sc.retrieveCollections(space)), adminUser),
                hasSize(1)
        );
        assertThat(
                cc.retrieve(URI.create(Iterables.getFirst(space.getSpaceCollections(), null)), adminUser).getSpace(),
                equalTo(spaceId)
        );
    }

    @Test
    public void test_7_Delete() throws Exception {
        for (Space s : sc.retrieveAll()) {
            sc.delete(s, adminUser);
        }
        assertThat(sc.retrieveAll(), empty());
    }

    @Ignore
    @Test
    public void test_8_Performance() throws Exception {


        //create space
        Space sp1 = ImejiFactory.newSpace();
        sp1.setTitle("Space 1");
        spaceId = sc.create(sp1, adminUser);
        space = sc.retrieve(sp1.getId(), adminUser);
        assertThat(sp1.getTitle(), equalTo(space.getTitle()));

        //create COL_NUM collections
        String[] colIds = new String[COL_NUM];
        collectionTO= (CollectionTO) RestProcessUtils.buildTOFromJSON(
                getStringFromPath(STATIC_CONTEXT_REST + "/createCollection.json"), CollectionTO.class);
        CollectionService cs = new CollectionService();
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < COL_NUM; i++) {
            collectionTO.setTitle("Collection " + i);
            colIds[i] = cs.create(collectionTO, testUser).getId();
        }
        LOGGER.info("creation time: " + (System.currentTimeMillis() - startTime ) + "ms");

        startTime = System.currentTimeMillis();
        //add collections to space
        for (int i = 0; i < COL_NUM; i++) {
            sc.addCollection(space, colIds[i], adminUser);
        }
        LOGGER.info("addition time:" + (System.currentTimeMillis() - startTime )  + "ms");

    }


}