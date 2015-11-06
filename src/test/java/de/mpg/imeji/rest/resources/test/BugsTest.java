package de.mpg.imeji.rest.resources.test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mpg.imeji.logic.util.UrlHelper;
import de.mpg.imeji.logic.vo.Person;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.user.util.EmailMessages;

/**
 * Created by vlad on 24.03.15.
 */
public class BugsTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(BugsTest.class);

  /**
   * See bug https://github.com/MPDL-Innovations/imeji/issues/42
   */
  @Test
  public void notificationUrlEscapingTest_bug42() {

    String templ =
        "Dear XXX_USER_NAME_XXX,\\r\\n\\r\\nThe zipped items have been downloaded by user \"XXX_ACTOR_NAME_XXX\": XXX_ACTOR_EMAIL_XXX\\r\\nat XXX_TIME_XXX.\\r\\nXXX_ITEMS_DOWNLOADED_XXX\\r\\n\\r\\nThe items have been found by query: XXX_QUERY_URL_XXX\\r\\n\\r\\nWARNING: This is an automatically generated email, please do not reply!";
    String url = "http://dev-faces.mpdl.mpg.de/imeji/collection/SoL9eg21DU7tlwAg/browse?q="
        + "NOTstatement==\"http://dev-faces.mpdl.mpg.de/imeji/statement/UkSj3SDNS0DYMvDA\" "
        + "NOT AND statement==\"http://dev-faces.mpdl.mpg.de/imeji/statement/G0rE6dg5KMTLLVIe";
    String expected =
        "Dear User Name,\\r\\n\\r\\nThe zipped items have been downloaded by user \"non_logged_in_user\": \\r\\nat Tue Mar 24 17:44:40 CET 2015.\\r\\nhttp://dev-faces.mpdl.mpg.de/imeji/collection/SoL9eg21DU7tlwAg/browse?q=NOTstatement==%22http://dev-faces.mpdl.mpg.de/imeji/statement/UkSj3SDNS0DYMvDA%22%20NOT%20AND%20statement==%22http://dev-faces.mpdl.mpg.de/imeji/statement/G0rE6dg5KMTLLVIe\\r\\n\\r\\nThe items have been found by query: http://dev-faces.mpdl.mpg.de/imeji/collection/SoL9eg21DU7tlwAg/browse?q=NOTstatement==%22http://dev-faces.mpdl.mpg.de/imeji/statement/UkSj3SDNS0DYMvDA%22%20NOT%20AND%20statement==%22http://dev-faces.mpdl.mpg.de/imeji/statement/G0rE6dg5KMTLLVIe\\r\\n\\r\\nWARNING: This is an automatically generated email, please do not reply!";

    SessionBean mockedSession = mock(SessionBean.class);
    when(mockedSession.getMessage("email_zip_images_downloaded_body")).thenReturn(templ);
    when(mockedSession.getMessage("collection")).thenReturn("Collection");
    when(mockedSession.getMessage("filtered")).thenReturn("filtered");
    when(mockedSession.getMessage("items_count")).thenReturn("items count");

    User mockedToUser = mock(User.class);
    Person mockedPerson = mock(Person.class);
    when(mockedToUser.getPerson()).thenReturn(mockedPerson);
    when(mockedPerson.getCompleteName()).thenReturn("User Name");
    EmailMessages em = new EmailMessages();
    assertThat(
        em.getEmailOnZipDownload_Body(mockedToUser, null, UrlHelper.encodeQuery(url), url,
            mockedSession).replaceAll("at.*\\d{4}\\.", ""),
        equalTo(expected.replaceAll("at.*\\d{4}\\.", "")));

  }



}
