package de.mpg.imeji.rest.resources.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.mpg.imeji.exceptions.BadRequestException;
import de.mpg.imeji.rest.process.RestProcessUtils;
import de.mpg.imeji.rest.to.ItemTO;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static de.mpg.imeji.logic.util.ResourceHelper.getStringFromPath;
import static de.mpg.imeji.rest.process.RestProcessUtils.jsonToPOJO;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Created by vlad on 11.12.14.
 */
public class JSONDeserializationTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(JSONDeserializationTest.class);

  @Test
  public void testBuildItemTOFromJSON() throws IOException, BadRequestException {
    // String jsonStringIn = getStringFromPath("src/test/resources/rest/itemFull.json");
    String jsonStringIn = getStringFromPath("src/test/resources/rest/itemFull2.json");
    ItemTO item = (ItemTO) RestProcessUtils.buildTOFromJSON(jsonStringIn, ItemTO.class);
    ObjectMapper mapper = new ObjectMapper();
    // mapper.enable(SerializationFeature.INDENT_OUTPUT);
    String jsonStringOut = mapper.writeValueAsString(item);
    assertThat("Bad deserialization of ItemTO JSON", jsonToPOJO(jsonStringIn),
        equalTo(jsonToPOJO(jsonStringOut)));

  }

}
