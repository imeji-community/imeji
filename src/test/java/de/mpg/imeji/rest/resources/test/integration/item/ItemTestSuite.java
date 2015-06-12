package de.mpg.imeji.rest.resources.test.integration.item;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({
        ItemCreateTest.class,
        ItemReadTest.class,
        ItemUpdateBasicTest.class,
        ItemUpdateFileTest.class,
        ItemUpdateMetadataTest.class,
        ItemDeleteTest.class,
        ItemDefaultMdCreateTest.class,
        ItemEasyUpdateTest.class
})
public class ItemTestSuite { }