package de.mpg.imeji.logic.controller;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import util.JenaUtil;

/**
 * Created by vlad on 15.04.15.
 */
public class ControllerTest {

    @BeforeClass
    public static void setup() {
        JenaUtil.initJena();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        JenaUtil.closeJena();
    }


}