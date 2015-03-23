package de.mpg.imeji.rest.resources.test.integration.item;

import de.mpg.imeji.exceptions.UnprocessableError;
import de.mpg.imeji.rest.resources.test.integration.ImejiTestBase;
import de.mpg.imeji.rest.to.*;
import de.mpg.imeji.rest.to.predefinedMetadataTO.*;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import static de.mpg.imeji.rest.process.RestProcessUtils.buildJSONFromObject;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;

/**
 * Created by vlad on 09.12.14.
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ItemUpdateMetadataTest extends ImejiTestBase {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ItemUpdateMetadataTest.class);

    private static final String PATH_PREFIX = "/rest/items";

    @BeforeClass
    public static void specificSetup() throws Exception {
        initCollectionWithProfile();
        initItemWithFullMedatada();
    }

    @Test
    public void test_1_UpdateItem_1_Change_Metadata_Statements_Allowed_Common() throws IOException, UnprocessableError {

        final String CHANGED = "allowed_change";
        double NUM = 12345;
        final String REP_CHANGED = "$1\"" + CHANGED + "\"";
        final String NUM_CHANGED = "$1"+ NUM;
        DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-DD");
        Date date = new Date();
        final String DATE_CHANGED = "$1\"" + dateFormat.format(date) + "\"";

        FormDataMultiPart multiPart = new FormDataMultiPart();

        multiPart.field("json", buildJSONFromObject(itemTO)
                        .replace("___FILE_NAME___", CHANGED)
                                //text
                        .replaceAll("(\"text\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
                                //person
                        .replaceAll("(\"familyName\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
                        .replaceAll("(\"givenName\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
                        .replaceAll("(\"completeName\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
                        .replaceAll("(\"alternativeName\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
                        .replaceAll("(\"role\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
                                //organization
                        .replaceAll("(\"name\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
                        .replaceAll("(\"description\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
                        .replaceAll("(\"country\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
                        .replaceAll("(\"city\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
                                //number
                        .replaceAll("(\"number\"\\s*:\\s*).+", NUM_CHANGED)
                                //geo
                        .replaceAll("(\"longitude\"\\s*:\\s*).+", NUM_CHANGED + ",")
                        .replaceAll("(\"latitude\"\\s*:\\s*).+", NUM_CHANGED)
                                //date
                        .replaceAll("(\"date\"\\s*:\\s*)\"(.+)\"", DATE_CHANGED)
                                //license
                        .replaceAll("(\"license\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
                        .replaceAll("(\"url\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
                                //link
                        .replaceAll("(\"link\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
                                //publication
                        .replaceAll("(\"format\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
                        .replaceAll("(\"publication\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
                        .replaceAll("(\"citation\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
        );

        LOGGER.info(multiPart.getField("json").getValue());

        Response response = target(PATH_PREFIX).path("/" + itemId)
                .register(authAsUser)
                .register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(multiPart, multiPart.getMediaType()));
        assertEquals(response.getStatus(), OK.getStatusCode());
        ItemTO updatedItem = (ItemTO) response.readEntity(ItemWithFileTO.class);


        LOGGER.info(buildJSONFromObject(updatedItem));

        assertThat(updatedItem.getFilename(), equalTo(CHANGED));

        List<MetadataSetTO> mds = updatedItem.getMetadata();

        //text
        assertThat( ((TextTO) mds.get(0).getValue()).getText(), equalTo(CHANGED));

        //geolocation
        final GeolocationTO geolocationTO = (GeolocationTO) mds.get(3).getValue();
        assertThat( geolocationTO.getName(), equalTo(CHANGED));
        assertThat( geolocationTO.getLatitude(), equalTo(NUM));
        assertThat( geolocationTO.getLongitude(), equalTo(NUM));

        //number
        assertThat( ((NumberTO) mds.get(1).getValue()).getNumber(), equalTo(NUM));

        //person
        PersonTO person = ((ConePersonTO) mds.get(2).getValue()).getPerson();
        assertThat(person.getFamilyName(), equalTo(CHANGED));
        assertThat(person.getGivenName(), equalTo(CHANGED));
        final OrganizationTO organizationTO = person.getOrganizations().get(0);
        assertThat(organizationTO.getName(), equalTo(CHANGED));
        assertThat(organizationTO.getCountry(), equalTo(CHANGED));
        assertThat(organizationTO.getCity(), equalTo(CHANGED));
        assertThat(organizationTO.getDescription(), equalTo(CHANGED));

        //date
        assertThat( ((DateTO) mds.get(4).getValue()).getDate(), equalTo(dateFormat.format(date)));

        //license
        final LicenseTO licenseTO = (LicenseTO) mds.get(5).getValue();
        assertThat(licenseTO.getLicense(), equalTo(CHANGED));
        assertThat(licenseTO.getUrl(), equalTo(CHANGED));

        //link
        assertThat( ((LinkTO) mds.get(6).getValue()).getLink(), equalTo(CHANGED));
        assertThat( ((LinkTO) mds.get(6).getValue()).getUrl(), equalTo(CHANGED));

        //publication
        assertThat( ((PublicationTO) mds.get(7).getValue()).getFormat(), equalTo(CHANGED));
        assertThat( ((PublicationTO) mds.get(7).getValue()).getPublication(), equalTo(CHANGED));
    }

    @Test
    public void test_2_UpdateItem_2_Change_Metadata_Statements_Not_Allowed() throws IOException, UnprocessableError {

        final String CHANGED = "not_allowed_change";
        final String REP_CHANGED = "$1\"" + CHANGED + "\"";

        FormDataMultiPart multiPart = new FormDataMultiPart();

        //identifiers
        MetadataSetTO md = itemTO.filterMetadataByTypeURI(MetadataTO.getTypeURI(ConePersonTO.class)).get(0);
        PersonTO p = ((ConePersonTO) md.getValue()).getPerson();

        for (IdentifierTO ident : p.getIdentifiers() ) {
            ident.setType(CHANGED);
            ident.setValue(CHANGED);
        }
        for (OrganizationTO org : p.getOrganizations() ) {
            for (IdentifierTO ident : org.getIdentifiers() ) {
                ident.setType(CHANGED);
                ident.setValue(CHANGED);
            }
        }

        IdentifierTO identifier = p.getIdentifiers().get(0);
        identifier.setValue(CHANGED);
        identifier.setType(CHANGED);
        identifier = p.getOrganizations().get(0).getIdentifiers().get(0);
        identifier.setValue(CHANGED);
        identifier.setType(CHANGED);

        //labels
        for ( MetadataSetTO mds: itemTO.getMetadata() ) {
            for (LabelTO lb: mds.getLabels()) {
                lb.setValue(CHANGED);
                lb.setLanguage(CHANGED);
            }
        }
        multiPart.field("json", buildJSONFromObject(itemTO)
                        //item properties
                        .replaceAll("(\"fullname\"\\s*:\\s*)\"(.*)\"", REP_CHANGED)
                        .replaceAll("(\"userId\"\\s*:\\s*)\"(.*)\"", REP_CHANGED)
                        .replaceAll("(\"createdDate\"\\s*:\\s*)\"(.*)\"", REP_CHANGED)
                        .replaceAll("(\"modifiedDate\"\\s*:\\s*)\"(.*)\"", REP_CHANGED)
                        .replaceAll("(\"versionDate\"\\s*:\\s*)\"(.*)\"", REP_CHANGED)
                        .replaceAll("(\"status\"\\s*:\\s*)\"(.*)\"", REP_CHANGED)
                        .replaceAll("(\"version\"\\s*:\\s*)(.*)", "$1-1,")
                        .replaceAll("(\"discardComment\"\\s*:\\s*)\"(.*)\"", REP_CHANGED)
                        .replaceAll("(\"visibility\"\\s*:\\s*)\"(.*)\"", REP_CHANGED)
                        .replaceAll("(\"collectionId\"\\s*:\\s*)\"(.*)\"", REP_CHANGED)
                        .replaceAll("(\"mimetype\"\\s*:\\s*)\"(.*)\"", REP_CHANGED)
                        .replaceAll("(\"checksumMd5\"\\s*:\\s*)\"(.*)\"", REP_CHANGED)
                        .replaceAll("(\"webResolutionUrlUrl\"\\s*:\\s*)\"(.*)\"", REP_CHANGED)
                        .replaceAll("(\"thumbnailUrl\"\\s*:\\s*)\"(.*)\"", REP_CHANGED)
                        .replaceAll("(\"fileUrl\"\\s*:\\s*)\"(.*)\"", REP_CHANGED)
        );

        //LOGGER.info(multiPart.getField("json").getValue());

        Response response = target(PATH_PREFIX).path("/" + itemId)
                .register(authAsUser)
                .register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(multiPart, multiPart.getMediaType()));
        assertEquals(response.getStatus(), OK.getStatusCode());
        ItemTO updItem = (ItemTO) response.readEntity(ItemWithFileTO.class);

        //LOGGER.info(buildJSONFromObject(updItem));

        assertThat(updItem.getCreatedBy().getFullname(), not(equalTo(CHANGED)));
        assertThat(updItem.getCreatedBy().getUserId(), not(equalTo(CHANGED)));
        assertThat(updItem.getModifiedBy().getFullname(), not(equalTo(CHANGED)));
        assertThat(updItem.getModifiedBy().getUserId(), not(equalTo(CHANGED)));

        assertThat(updItem.getCreatedDate(), not(equalTo(CHANGED)));
        assertThat(updItem.getModifiedDate(), not(equalTo(CHANGED)));
        assertThat(updItem.getVersionDate(), not(equalTo(CHANGED)));

        assertThat(updItem.getStatus(), not(equalTo(CHANGED)));

        //TODO: check version update
        assertThat(updItem.getVersion(), not(equalTo(-1)));

        assertThat(updItem.getDiscardComment(), not(equalTo(CHANGED)));
        assertThat(updItem.getVisibility(), not(equalTo(CHANGED)));
        assertThat(updItem.getCollectionId(), not(equalTo(CHANGED)));
        assertThat(updItem.getMimetype(), not(equalTo(CHANGED)));
        assertThat(updItem.getChecksumMd5(), not(equalTo(CHANGED)));
        assertThat(updItem.getWebResolutionUrlUrl().toString(), not(equalTo(CHANGED)));
        assertThat(updItem.getThumbnailUrl().toString(), not(equalTo(CHANGED)));
        assertThat(updItem.getFileUrl().toString(), not(equalTo(CHANGED)));


        List<MetadataSetTO> mdsList = updItem.getMetadata();

        for (MetadataSetTO mds : mdsList) {
            //statements
            assertThat( mds.getStatementUri().toString(), not(equalTo(CHANGED)));
            assertThat( mds.getTypeUri().toString(), not(equalTo(CHANGED)));
            //labels
            //very strange....
            for (Object o: mds.getLabels()) {
                LinkedHashMap<String, String> lbl = (LinkedHashMap<String, String>) o;
                assertThat( lbl.get("value"), not(equalTo(CHANGED)));
                assertThat( lbl.get("language"), not(equalTo(CHANGED)));
            }
        }

        //person
        p = ((ConePersonTO) mdsList.get(2).getValue()).getPerson();
        assertThat(p.getId(), not(equalTo(CHANGED)));
        //person identifiers
        for (IdentifierTO ident : p.getIdentifiers() ) {
            assertThat(ident.getType(), not(equalTo(CHANGED)));
            assertThat(ident.getValue(), not(equalTo(CHANGED)));
        }
        for (OrganizationTO org : p.getOrganizations() ) {
            assertThat(org.getId(), not(equalTo(CHANGED)));
            for (IdentifierTO ident : org.getIdentifiers() ) {
                assertThat(ident.getType(), not(equalTo(CHANGED)));
                assertThat(ident.getValue(), not(equalTo(CHANGED)));
            }
        }
    }


    @Test
    public void test_2_UpdateItem_3_Change_Metadata_Statements_Wrong_StatementUri() throws IOException, UnprocessableError {

        final String CHANGED = "wrong_statementUri";
        final String REP_CHANGED = "$1\"" + CHANGED + "\"";

        FormDataMultiPart multiPart = new FormDataMultiPart();

        multiPart.field("json", buildJSONFromObject(itemTO)
                        .replaceAll("(\"statementUri\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
        );

        LOGGER.info(multiPart.getField("json").getValue());

        Response response = target(PATH_PREFIX).path("/" + itemId)
                .register(authAsUser)
                .register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(multiPart, multiPart.getMediaType()));

        assertEquals(BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void test_2_UpdateItem_4_Change_Metadata_Statements_Wrong_typeUri() throws IOException, UnprocessableError {

        final String CHANGED = "wrong_typeUri";
        final String REP_CHANGED = "$1\"" + CHANGED + "\"";

        FormDataMultiPart multiPart = new FormDataMultiPart();

        multiPart.field("json", buildJSONFromObject(itemTO)
                        .replaceAll("(\"statementUri\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
        );

        LOGGER.info(multiPart.getField("json").getValue());

        Response response = target(PATH_PREFIX).path("/" + itemId)
                .register(authAsUser)
                .register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(multiPart, multiPart.getMediaType()));

        assertEquals(BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void test_3_UpdateItem_1_Change_Metadata_Statements_EmptyValues() throws IOException, UnprocessableError {

        final String REP_CHANGED = "$1\"\"";

        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.field("json", buildJSONFromObject(itemTO)
                        //TODO: make sense only for
                        .replaceAll("(\"text\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
                        .replaceAll("(\"date\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
                        .replaceAll("(\"license\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
                        .replaceAll("(\"link\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
                        .replaceAll("(\"url\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
                                ///only publication should be filled, not citation or format
                        .replaceAll("(\"publication\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
        );
        
        LOGGER.info(multiPart.getField("json").getValue());

        Response response = target(PATH_PREFIX).path("/" + itemId)
                .register(authAsUser)
                .register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(multiPart, multiPart.getMediaType()));
        assertEquals(response.getStatus(), OK.getStatusCode());

        final String json = response.readEntity(String.class);
        LOGGER.info(json);

        assertThat(json, not(containsString("\"text\"")));
        assertThat(json, not(containsString("\"date\"")));
        assertThat(json, allOf(
                not(containsString("\"license\"")),
                not(containsString("\"url\""))) );
        assertThat(json, allOf(
                not(containsString("\"link\"")),
                not(containsString("\"url\""))) );
        assertThat(json, allOf(
                not(containsString("\"format\"")),
                not(containsString("\"publication\"")),
                not(containsString("\"citation\""))  ) );
   }

    @Test
    public void test_3_UpdateItem_2_Change_Metadata_Statements_EmptyStatements_SomeSections() throws IOException, UnprocessableError {

        FormDataMultiPart multiPart = new FormDataMultiPart();

        itemTO.getMetadata().remove(0);
        itemTO.getMetadata().remove(2);

        multiPart.field("json", buildJSONFromObject(itemTO));

        LOGGER.info(multiPart.getField("json").getValue());

        Response response = target(PATH_PREFIX).path("/" + itemId)
                .register(authAsUser)
                .register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(multiPart, multiPart.getMediaType()));

        assertEquals(response.getStatus(), OK.getStatusCode());
        ItemTO updItem = (ItemTO) response.readEntity(ItemTO.class);

        assertThat(updItem.getMetadata(), hasSize(itemTO.getMetadata().size()));

    }

    @Test
    public void test_3_UpdateItem_3_Change_Metadata_Statements_EmptyStatements_CompleteSection() throws IOException, UnprocessableError {

        final String CHANGED = "$1";
        FormDataMultiPart multiPart = new FormDataMultiPart();

        itemTO.getMetadata().clear();
        multiPart.field("json", buildJSONFromObject(itemTO));

        Response response = target(PATH_PREFIX).path("/" + itemId)
                .register(authAsUser)
                .register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(multiPart, multiPart.getMediaType()));

        assertEquals(response.getStatus(), OK.getStatusCode());
        ItemTO updItem = (ItemTO) response.readEntity(ItemWithFileTO.class);

        assertThat(updItem.getMetadata(), is(empty()));

    }








}