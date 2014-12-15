package de.mpg.imeji.rest.resources.test.integration;

import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.controller.ProfileController;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.util.StringHelper;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.rest.MyApplication;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.spi.TestContainerException;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import util.JenaUtil;

import javax.ws.rs.core.Application;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by vlad on 09.12.14.
 */
public class ImejiRestTest extends JerseyTest{

    private static String adminEmail = "admin@imeji.org";
    private static String adminName = "imeji admin";
    private static String adminPwd = "admin";

    private static String userEmail = "user@imeji.org";
    private static String userName = "imeji user";
    private static String userPwd = "user";


    public static HttpAuthenticationFeature authAsAdmin = HttpAuthenticationFeature.basic(adminEmail, adminPwd);
    public static HttpAuthenticationFeature authAsUser = HttpAuthenticationFeature.basic(userEmail, userPwd);
    protected static User adminUser = null;
    protected static User defaultUser = null;



    @Override
    protected Application configure() {
        return new MyApplication();
    }


    @Override
    protected TestContainerFactory getTestContainerFactory() throws TestContainerException {
        return new MyTestContainerFactory();
    }

    @BeforeClass
    public static void setup() throws IOException, URISyntaxException {
        JenaUtil.initJena();
        adminUser = createImejiUser(adminEmail, adminName, adminPwd, UserController.USER_TYPE.ADMIN);
        defaultUser = createImejiUser(userEmail, userName, userPwd, UserController.USER_TYPE.DEFAULT);
    }

    @AfterClass
    public static void shutdown() throws IOException, URISyntaxException {
        JenaUtil.closeJena();

    }

    public static User createImejiUser(String email, String name, String pwd, UserController.USER_TYPE type) {
        User user = null;
        try {
            UserController c = new UserController(Imeji.adminUser);
            user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setEncryptedPassword(StringHelper.convertToMD5(pwd));
            user = c.create(user, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    public static String createProfile(User u) {
        try {
            ProfileController c = new ProfileController();
            MetadataProfile p = c.create(ImejiFactory.newProfile(), u);
            return p.getId().toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



}
