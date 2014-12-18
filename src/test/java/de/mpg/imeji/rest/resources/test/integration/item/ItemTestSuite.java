package de.mpg.imeji.rest.resources.test.integration.item;

import de.mpg.imeji.rest.resources.test.integration.item.ItemCreateTest;
import de.mpg.imeji.rest.resources.test.integration.item.ItemUpdateTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({
        ItemCreateTest.class,
        ItemUpdateTest.class
})
public class ItemTestSuite { }