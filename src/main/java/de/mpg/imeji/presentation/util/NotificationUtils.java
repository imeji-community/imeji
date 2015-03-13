package de.mpg.imeji.presentation.util;

import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.export.Export;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.user.util.EmailClient;
import de.mpg.imeji.presentation.user.util.EmailMessages;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Created by vlad on 13.03.15.
 */
public class NotificationUtils {

    private static Logger LOGGER = Logger.getLogger(NotificationUtils.class);

    private static EmailMessages msgs = new EmailMessages();
    private static final EmailClient emailClient = new EmailClient();
    private static UserController uc = new UserController(Imeji.adminUser);
    private static CollectionController cc = new CollectionController();

    /**
     * Send email notifications to all users which checked
     * "Send notification email by item download" feature
     * for collection of item
     *
     * @param user
     * @param fileItem
     * @param session
     * @throws de.mpg.imeji.exceptions.ImejiException
     * @throws java.io.IOException
     * @throws java.net.URISyntaxException
     */
    public static void notifyByItemDownload(User user, Item fileItem, SessionBean session) throws ImejiException, IOException, URISyntaxException {
        final CollectionImeji c = cc.retrieve(fileItem.getCollection(), Imeji.adminUser);
        for(User u:  uc.searchUsersToBeNotified(user, c)) {
            emailClient.sendMail(u.getEmail(), null,
                    msgs.getEmailOnItemDownload_Subject(fileItem, session),
                    msgs.getEmailOnItemDownload_Body(u, user, fileItem, c, session));
            LOGGER.info("Sent notification email to user: " + u.getName() + "<" + u.getEmail()
                    + ">" + " by item " + fileItem.getId() + " download");
        }
    }

    /**
     * Send email notifications to all users which checked
     * "Send notification email by item download" feature
     * for collection of item
     *
     * @param user
     * @param export
     * @param session
     * @throws de.mpg.imeji.exceptions.ImejiException
     * @throws java.io.IOException
     * @throws java.net.URISyntaxException
     */
    public static void notifyByExport(User user, Export export, SessionBean session) throws ImejiException, IOException, URISyntaxException {
        //notify by zip export
        if ("zip".equals(export.getParam("format"))) {
            //only collection for the moment
            if (!isNullOrEmpty(export.getParam("col"))) {
                final CollectionImeji c = cc.retrieve(export.getParam("col"), Imeji.adminUser);
                for(User u:  uc.searchUsersToBeNotified(user, c)) {
                    emailClient.sendMail(u.getEmail(), null,
                            msgs.getEmailOnCollectionDownload_Subject(c, session),
                            msgs.getEmailOnCollectionDownload_Body(u, user, c, session));
                    LOGGER.info("Sent notification email to user: " + u.getName() + "<" + u.getEmail()
                            + ">" + " by collection " + c.getIdString() + " download");
                }
            }
        }
    }


}
