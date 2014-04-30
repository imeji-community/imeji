/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.metadata.extractors;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import de.mpg.imeji.logic.storage.StorageController;
import de.mpg.imeji.logic.vo.Item;

/**
 * Extract technical metadata with {@link ImageIO}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class BasicExtractor
{
    /**
     * Extract Metadata from one {@link Item} with {@link ImageIO}
     * 
     * @param item
     * @return
     * @throws Exception
     */
    public static List<String> extractTechMd(Item item) throws Exception
    {
        List<String> techMd = new ArrayList<String>();
        URI uri = item.getFullImageUrl();
        String imageUrl = uri.toURL().toString();
        StorageController sc = new StorageController();
        ByteArrayOutputStream bous = new ByteArrayOutputStream();
        sc.read(imageUrl, bous, true);
        InputStream input = new ByteArrayInputStream(bous.toByteArray());
        ImageInputStream iis = ImageIO.createImageInputStream(input);
        Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
        if (readers.hasNext())
        {
            // pick the first available ImageReader
            ImageReader reader = readers.next();
            // attach source to the reader
            reader.setInput(iis, true);
            // read metadata of first image
            IIOMetadata metadata = reader.getImageMetadata(0);
            String[] names = metadata.getMetadataFormatNames();
            int length = names.length;
            for (int i = 0; i < length; i++)
            {
                displayMetadata(techMd, metadata.getAsTree(names[i]));
            }
        }
        return techMd;
    }

    /**
     * Format the metadata in a convenient xml format for user
     * 
     * @param techMd
     * @param root
     */
    private static void displayMetadata(List<String> techMd, Node root)
    {
        displayMetadata(techMd, root, 0);
    }

    private static void indent(List<String> techMd, StringBuffer sb, int level)
    {
        for (int i = 0; i < level; i++)
        {
            sb.append("    ");
        }
    }

    /**
     * Indent the the technical metadata which are diplayed in xml
     * 
     * @param techMd
     * @param node
     * @param level
     */
    static void displayMetadata(List<String> techMd, Node node, int level)
    {
        StringBuffer sb = new StringBuffer();
        // print open tag of element
        indent(techMd, sb, level);
        sb.append("<" + node.getNodeName());
        NamedNodeMap map = node.getAttributes();
        if (map != null)
        {
            // print attribute values
            int length = map.getLength();
            for (int i = 0; i < length; i++)
            {
                Node attr = map.item(i);
                sb.append(" " + attr.getNodeName() + "=\"" + attr.getNodeValue() + "\"");
            }
        }
        Node child = node.getFirstChild();
        if (child == null)
        {
            // no children, so close element and return
            sb.append("/>");
            techMd.add(sb.toString());
            sb.delete(0, sb.length());
            return;
        }
        // children, so close current tag
        sb.append(">");
        techMd.add(sb.toString());
        sb.delete(0, sb.length());
        while (child != null)
        {
            // print children recursively
            displayMetadata(techMd, child, level + 1);
            child = child.getNextSibling();
        }
        // print close tag of element
        indent(techMd, sb, level);
        sb.append("</" + node.getNodeName() + ">");
        techMd.add(sb.toString());
        sb.delete(0, sb.length());
    }
}
