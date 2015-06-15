package de.mpg.imeji.rest.resources.test.integration.item;

import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.controller.ProfileController;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.rest.process.RestProcessUtils;
import de.mpg.imeji.rest.process.ReverseTransferObjectFactory;
import de.mpg.imeji.rest.resources.test.integration.ImejiTestBase;
import de.mpg.imeji.rest.to.CollectionTO;
import de.mpg.j2j.misc.LocalizedString;
import org.apache.log4j.Logger;
import util.JenaUtil;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

import static de.mpg.imeji.logic.util.ResourceHelper.getStringFromPath;

/**
 * Created by vlad on 10.06.15.
 */
public class ItemTestBase extends ImejiTestBase {

    private static Logger logger = Logger.getLogger(ItemTestBase.class);

    public static MetadataProfile profile;
    public static Item item;

    protected static ProfileController pc = new ProfileController();

    protected static void initCollectionWithProfile(Collection<Statement> statements) throws Exception {

        MetadataProfile p = ImejiFactory.newProfile();
        p.setTitle("test");
        p.setStatements(statements);

        MetadataProfile mp = pc.create(p, JenaUtil.testUser);

        profileId = ObjectHelper.getId(mp.getId());

        try {

            collectionTO= (CollectionTO) RestProcessUtils.buildTOFromJSON(
                    getStringFromPath("src/test/resources/rest/createCollection.json"), CollectionTO.class);

            CollectionController cc = new CollectionController();
            CollectionImeji ci = new CollectionImeji();
            ReverseTransferObjectFactory.transferCollection(collectionTO, ci, ReverseTransferObjectFactory.TRANSFER_MODE.CREATE, JenaUtil.testUser);
            collectionId = ObjectHelper.getId(cc.create(ci, p, JenaUtil.testUser, CollectionController.MetadataProfileCreationMethod.REFERENCE, null));

        } catch (Exception e) {
            logger.error("Cannot init Collection", e);
        }

    }

    protected static Collection<Statement> getDefaultBasicStatements() {
        Collection<Statement> statements = new ArrayList<Statement>();
        Statement st;
        for (String type : new String[] { "text", "number", "person",
                "geolocation", "date", "license", "link", "publication" }) {
            st = new Statement();
            st.setType(URI.create("http://imeji.org/terms/metadata#" + type));
            st.getLabels().add(new LocalizedString(type, "en"));
            statements.add(st);
        }
        return statements;
    }

    protected static Collection<Statement> getBasicStatements() {
        Collection<Statement> statements = new ArrayList<Statement>();
        Statement st;
        for (String type: new String[]{"text", "number", "conePerson" , "geolocation", "date", "license", "link", "publication"}) {
            st = new Statement();
            st.setType(URI.create("http://imeji.org/terms/metadata#" + type));
            st.getLabels().add(new LocalizedString(type + "Label", "en"));
            statements.add(st);
        }
        return statements;
    }

    protected static void createItem() throws Exception {
        CollectionController cc = new CollectionController();
        ItemController ic = new ItemController();
        CollectionImeji coll = cc.retrieve(
                ObjectHelper.getURI(CollectionImeji.class, collectionId),
                JenaUtil.testUser);
        item = ImejiFactory.newItem(coll);
        item = ic.create(item, coll.getId(), JenaUtil.testUser);
        itemId = item.getIdString();
        //TransferObjectFactory.transferItem(item, itemTO);
    }

}
