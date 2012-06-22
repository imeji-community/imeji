package de.mpg.j2j;

import java.net.URI;

import de.mpg.imeji.logic.ImejiJena;
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.vo.Grant;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.logic.vo.Grant.GrantType;
import de.mpg.imeji.presentation.util.PropertyReader;

public class BasicTest
{
    public static void main(String[] args) throws Exception
    {
        //Initializer.init();
        ImejiJena.init();
//        Initializer.reset(ImejiJena.userModel);
//       // ImejiJena.userModel.begin();
//        ImejiJena.userModel.write(System.out, "RDF/XML-ABBREV");
        User user = new User();
        user.setEmail("sysadmin@imeji.org");
        user.setName("Imeji Sysadmin");
        user.setNick("sysadmin");
        user.setEncryptedPassword(UserController.convertToMD5("password"));
        user.getGrants().add(new Grant(GrantType.SYSADMIN, URI.create("http://imeji.org/")));
        UserController uc = new UserController(user);
        uc.create(user);
        User usercopy = uc.retrieve(user.getEmail());
        user.setName("Edit");
        uc.update(user);
//        ImejiJena.userModel.write(System.out, "RDF/XML-ABBREV");
        //ImejiJena.userModel.commit();
        System.out.println("EndBasicTest");
        // User user = new User();
        // user.setEmail("test@mail.com");
        // Grant g = new Grant(GrantType.SYSADMIN, null);
        // user.getGrants().add(g);
        // Image image = new Image();
        // ImageController ic = new ImageController(user);
        // ic.createTest(image, URI.create("http://imeji.org/collection/2"));
        // ImejiJena.imageModel.write(System.out, "RDF/XML-ABBREV");
    }
}
