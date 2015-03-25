package de.mpg.imeji.logic.util;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by vlad on 11.02.15.
 */
public class ResourceHelper {

    private static Logger LOGGER = Logger.getLogger(ResourceHelper.class);

    public static String getStringFromPath(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), "UTF-8");
    }

}
