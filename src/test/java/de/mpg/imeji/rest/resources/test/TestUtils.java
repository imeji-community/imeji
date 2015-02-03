package de.mpg.imeji.rest.resources.test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.ByteStreams;

/**
 * Created by vlad on 11.12.14.
 */
public class TestUtils {

    public static String getStringFromPath(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), "UTF-8");
    }
    public static Map<String, Object> jsonToPOJO(Response response) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(ByteStreams.toByteArray(response.readEntity(InputStream.class)), Map.class);
    }
    public static Map<String, Object> jsonToPOJO(String str) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(str, Map.class);
    }
}
