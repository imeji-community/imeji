package de.mpg.imeji.logic.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by vlad on 11.02.15.
 */
public class ResourceHelper {

    public static String getStringFromPath(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), "UTF-8");
    }
}
