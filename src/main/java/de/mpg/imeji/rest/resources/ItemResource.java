package de.mpg.imeji.rest.resources;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import de.mpg.imeji.rest.process.RestProcessUtils;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static de.mpg.imeji.rest.process.ItemProcess.createItem;
import static de.mpg.imeji.rest.process.ItemProcess.deleteItem;
import static de.mpg.imeji.rest.process.ItemProcess.readItem;
import static de.mpg.imeji.rest.process.ItemProcess.readItems;
import static de.mpg.imeji.rest.process.ItemProcess.updateItem;
import static de.mpg.imeji.rest.process.RestProcessUtils.buildJSONResponse;


@Path("/items")
@Api(value = "rest/items", description = "Operations on items")
public class ItemResource implements ImejiResource {


  @GET
  @ApiOperation(value = "Get all items filtered by query (optional)")
  @Produces(MediaType.APPLICATION_JSON)
  public Response readAll(@Context HttpServletRequest req, @QueryParam("q") String q
  ) {
      return buildJSONResponse(readItems(req, q));
  }

  @GET
  @Path("/{id}")
  @ApiOperation(value = "Get item by id")
  @Produces(MediaType.APPLICATION_JSON)
  public Response read(@Context HttpServletRequest req, @PathParam("id") String id
  ) {
      return RestProcessUtils.buildJSONResponse(readItem(req, id));
  }


  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @ApiOperation(
      value = "Create new item with a File",
      notes = "Create an item with a file. File can be defined either as (by order of priority):"
          + "<br/> 1) form parameter (multipart/form-data)<br/> 2) json parameter: \"fetchUrl\" : \"http://example.org/myFile.png\" (myFile.png will be uploaded in imeji) "
          + "<br/> 3) json parameter \"referenceUrl\" : \"http://example.org/myFile.png\" (myFile.png will be only referenced in imeji, i.e. not uploaded)"
 //         + "<br/> 4) syntax: json format syntax, values: default|raw. Omitted value is default."
          + "<br/><br/>"
//          + "Json example, default metadata syntax:"
          + "Json example:"
          + "<div class=\"json_example\">"
          + "{"
          + "<br/>\"collectionId\" : \"abc123\", (required)"
          + "<br/>\"fetchUrl\" : \"http://example.org/myFile.png\", (optional)"
          + "<br/>\"referenceUrl\" : \"http://example.org/myFile.png\", (optional)"
          + "<br/>\"filename\" : \"new filename\", (optional)"
//          + "<br/>\"metadata\" : "
//          + "{ \"Title\" : \"TitleOfItem\","
//          + " \"License\" : {"
//          + " \"license\" : \"CC0 1.0 Universal\", "
//          + " \"url\" : \"http://creativecommons.org/publicdomain/zero/1.0/\""
//          + " } }, (optional)"
          + "<br/>\"metadata\" : [] (optional)"
          + "</br> }"
          + "</div>"
          + "<br/><br/>"
          + "rename and save the uploaded image with filename parameter, create metadata"
          + "<br/><br/>"
          + "The metadata parameter allows to define the metadata of item during the creation of the item. To get an example of how to do it, please try the get item method")
  @Produces(MediaType.APPLICATION_JSON)
  public Response create(@Context HttpServletRequest req, @FormDataParam("file") InputStream file,
      @ApiParam(required = true) @FormDataParam("json") String json,
      @FormDataParam("file") FormDataContentDisposition fileDetail) {
    String origName = fileDetail != null ? fileDetail.getFileName() : null;
    return RestProcessUtils.buildJSONResponse(createItem(req, file, json, origName));
  }

  @PUT
  @Path("/{id}")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @ApiOperation(
      value = "Update an item",
      notes = "Update an already existed item. Both the item metadata and the item file can be updated. File can be defined either as (by order of priority):"
          + "<br/> 1) form parameter (multipart/form-data)<br/> 2) json parameter: \"fetchUrl\" : \"http://example.org/myFile.png\" (myFile.png will be uploaded in imeji) "
          + "<br/> 3) json parameter \"referenceUrl\" : \"http://example.org/myFile.png\" (myFile.png will be only referenced in imeji, i.e. not uploaded)"
//          + "<br/> 4) syntax: json format syntax, values: default|raw. Omitted value is default."
          + "<br/><br/>"
//          + "Json example: default metadata syntax:"
          + "Json example:"
          + "<div class=\"json_example\">"
          + "{"
          + "<br/>\"id\" : \"abc123\","
          + "<br/>\"collectionId\" : \"def123\","
          + "<br/>\"fetchUrl\" : \"http://example.org/myFile.png\","
          + "<br/>\"referenceUrl\" : \"http://example.org/myFile.png\","

/*
          + "<br/>\"metadata\" : "
          + "<br/>{"

          + "<br/>\"TEXT_LABEL\" : \"abc123\","
          + "<br/>\"NUMBER_LABEL\" : nr,"
          + "<br/>\"DATE_LABEL\" : \"YYYY-MM-DD\","

          + "<br/>\"PERSON_LABEL\" : "
          + "<br/>{"
          + "<br/>\"familyName\": \"familyname...\","
          + "<br/>\"givenName\" : \"givenname...\""
          + "<br/>},"

          + "<br/>\"GEOLOCATION_LABEL\" : "
          + "<br/>{"
          + "<br/>\"name\": \"name...\","
          + "<br/>\"longitude\" : longitude...,"
          + "<br/>\"latitude\" : latitude..."
          + "<br/>},"

          + "<br/>\"LICENSE_LABEL\" : "
          + "<br/>{"
          + "<br/>\"license\": \"license...\","
          + "<br/>\"url\" : \"license url...\""
          + "<br/>},"

          + "<br/>\"LINK_LABEL\" : "
          + "<br/>{"
          + "<br/>\"link\": \"link...\","
          + "<br/>\"url\" : \"link url...\""
          + "<br/>},"

          + "<br/>\"PUBLICATION_LABEL\" : "
          + "<br/>{"
          + "<br/>\"format\": \"format...\","
          + "<br/>\"publication\" : \"publication...\","
          + "<br/>\"citation\" : \"citation...\""
          + "<br/>}"

          + "<br/>}"
*/

          + " \"metadata\": [<br/>" +
              "    {<br/>" +
              "      \"value\": {<br/>" +
              "        \"text\": \"TEXT\"<br/>" +
              "      },<br/>" +
              "      \"statementUri\": \"STATEMENT_URI\",<br/>" +
              "      \"typeUri\": \"http://imeji.org/terms/metadata#text\",<br/>" +
              "      \"labels\": [<br/>" +
              "        {<br/>" +
              "          \"language\": \"en\",<br/>" +
              "          \"value\": \"Title\"<br/>" +
              "        },<br/>" +
              "        {<br/>" +
              "          \"language\": \"de\",<br/>" +
              "          \"value\": \"Titel\"<br/>" +
              "        }<br/>" +
              "      ]<br/>" +
              "    },<br/>" +
              "    {<br/>" +
              "      \"value\": {<br/>" +
              "        \"number\": 1.2345678E7<br/>" +
              "      },<br/>" +
              "      \"statementUri\": \"STATEMENT_URI\",<br/>" +
              "      \"typeUri\": \"http://imeji.org/terms/metadata#number\"<br/>" +
              "    },<br/>" +
              "    {<br/>" +
              "      \"value\": {<br/>" +
              "        \"person\": {<br/>" +
              "          \"id\": \"USER_ID\",<br/>" +
              "          \"familyName\": \"FAMILY_NAME\",<br/>" +
              "          \"givenName\": \"GIVEN_NAME\",<br/>" +
              "          \"completeName\": \"FAMILY_NAME, GIVEN_NAME\",<br/>" +
              "          \"alternativeName\": \"ALTERNATIVE_NAME\",<br/>" +
              "          \"role\": \"author\",<br/>" +
              "          \"identifiers\": [<br/>" +
              "            {<br/>" +
              "              \"type\": \"imeji\",<br/>" +
              "              \"value\": \"IDENTIFIER\"<br/>" +
              "            }<br/>" +
              "          ],<br/>" +
              "          \"organizations\": [<br/>" +
              "            {<br/>" +
              "              \"id\": \"ORG_ID\",<br/>" +
              "              \"name\": \"ORG_NAME\",<br/>" +
              "              \"description\": \"ORG_DESCRIPTION\",<br/>" +
              "              \"identifiers\": [<br/>" +
              "                {<br/>" +
              "                  \"type\": \"IDENTIFIER_TYPE\",<br/>" +
              "                  \"value\": \"IDENTIFIER\"<br/>" +
              "                }<br/>" +
              "              ],<br/>" +
              "              \"city\": \"CITY\",<br/>" +
              "              \"country\": \"COUNTRY\"<br/>" +
              "            }<br/>" +
              "          ]<br/>" +
              "        }<br/>" +
              "      },<br/>" +
              "      \"statementUri\": \"STATEMENT_URI\",<br/>" +
              "      \"typeUri\": \"http://imeji.org/terms/metadata#conePerson\"<br/>" +
              "    },<br/>" +
              "    {<br/>" +
              "      \"value\": {<br/>" +
              "        \"name\": \"NAME\",<br/>" +
              "        \"longitude\": 10.985278,<br/>" +
              "        \"latitude\": 47.421111<br/>" +
              "      },<br/>" +
              "      \"statementUri\": \"STATEMENT_URI\",<br/>" +
              "      \"typeUri\": \"http://imeji.org/terms/metadata#geolocation\"<br/>" +
              "    },<br/>" +
              "    {<br/>" +
              "      \"value\": {<br/>" +
              "        \"date\": \"YYYY-MM-DD\"<br/>" +
              "      },<br/>" +
              "      \"statementUri\": \"STATEMENT_URI\",<br/>" +
              "      \"typeUri\": \"http://imeji.org/terms/metadata#date\"<br/>" +
              "    },<br/>" +
              "    {<br/>" +
              "      \"value\": {<br/>" +
              "        \"license\": \"LICENSE\",<br/>" +
              "        \"url\": \"LICENSE_URL\"<br/>" +
              "      },<br/>" +
              "      \"statementUri\": \"STATEMENT_URI\",<br/>" +
              "      \"typeUri\": \"http://imeji.org/terms/metadata#license\"<br/>" +
              "    },<br/>" +
              "    {<br/>" +
              "      \"value\": {<br/>" +
              "        \"link\": \"LINk_TITLE\",<br/>" +
              "        \"url\": \"LINK_URL\"<br/>" +
              "      },<br/>" +
              "      \"statementUri\": \"STATEMENT_URI\",<br/>" +
              "      \"typeUri\": \"http://imeji.org/terms/metadata#link\"<br/>" +
              "    },<br/>" +
              "    {<br/>" +
              "      \"value\": {<br/>" +
              "        \"format\": \"FORMAT\",<br/>" +
              "        \"publication\": \"PUBLICATION_URL\",<br/>" +
              "        \"citation\": \"Citation\"<br/>" +
              "      },<br/>" +
              "      \"statementUri\": \"STATEMENT_URI\",<br/>" +
              "      \"typeUri\": \"http://imeji.org/terms/metadata#publication\"<br/>" +
              "    }<br/>" +
              "  ]"
              
              
          + "<br/>}"
          + "</div>"
          + "<br/><br/>"
          + "The metadata parameter allows to define the metadata of item during the creation of the item. To get an example of how to do it, please try the get item method")
  @Produces(MediaType.APPLICATION_JSON)
  public Response update(@Context HttpServletRequest req, @FormDataParam("file") InputStream file,
      @ApiParam(required = true) @FormDataParam("json") String json,
      @FormDataParam("file") FormDataContentDisposition fileDetail, @PathParam("id") String id) {
    String filename = fileDetail != null ? fileDetail.getFileName() : null;
    return RestProcessUtils.buildJSONResponse(updateItem(req, id, file, json, filename));
  }

/*

  @PATCH
  @Path("/{id}")
  @ApiOperation(
      value = "Easy Update Item Metadata by id",
      notes = "Update Metadata of an already existing item:"
          + "<br/><br/>"
          + "Json example:"
          + "<div class=\"json_example\">"
          + "{"

          + "<br/>\"metadata\":"
          + "<br/>{"

          + "<br/>\"TEXT_LABEL\" : \"abc123\","
          + "<br/>\"NUMBER_LABEL\" : nr,"
          + "<br/>\"DATE_LABEL\" : \"YYYY-MM-DD\","

          + "<br/>\"PERSON_LABEL\" : "
          + "<br/>{"
          + "<br/>\"familyName\": \"familyname...\","
          + "<br/>\"givenName\" : \"givenname...\""
          + "<br/>},"

          + "<br/>\"GEOLOCATION_LABEL\" : "
          + "<br/>{"
          + "<br/>\"name\": \"name...\","
          + "<br/>\"longitude\" : longitude...,"
          + "<br/>\"latitude\" : latitude..."
          + "<br/>},"

          + "<br/>\"LICENSE_LABEL\" : "
          + "<br/>{"
          + "<br/>\"license\": \"license...\","
          + "<br/>\"url\" : \"license url...\""
          + "<br/>},"

          + "<br/>\"LINK_LABEL\" : "
          + "<br/>{"
          + "<br/>\"link\": \"link...\","
          + "<br/>\"url\" : \"link url...\""
          + "<br/>},"

          + "<br/>\"PUBLICATION_LABEL\" : "
          + "<br/>{"
          + "<br/>\"format\": \"format...\","
          + "<br/>\"publication\" : \"publication...\","
          + "<br/>\"citation\" : \"citation...\""
          + "<br/>}"

          + "<br/>}"

          + "<br/>}"
          + "</div>"
          + "<br/><br/>"
          + "The metadata parameter allows to define the metadata of an item during the creation of the item.")
  @Produces(MediaType.APPLICATION_JSON)
  public Response easyUpdate(@PathParam("id") String id, @Context HttpServletRequest req,
      InputStream json) throws Exception {
    JSONResponse resp = ItemProcess.easyUpdateItem(req, id);
    return buildJSONResponse(resp);
  }
*/


  @Override
  public Response create(HttpServletRequest req) {
    return null;
  }

  @Override
  @DELETE
  @Path("/{id}")
  @ApiOperation(value = "Delete item by id")
  @Produces(MediaType.APPLICATION_JSON)
  public Response delete(@Context HttpServletRequest req, @PathParam("id") String id) {
      return RestProcessUtils.buildJSONResponse(deleteItem(req, id));
  }


}
