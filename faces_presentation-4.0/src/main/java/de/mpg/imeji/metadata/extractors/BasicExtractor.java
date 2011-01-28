package de.mpg.imeji.metadata.extractors;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.imeji.util.LoginHelper;
import de.mpg.jena.vo.Image;

public class BasicExtractor {
	public static List<String> extractTechMd(Image image) throws Exception 
	{
		List<String> techMd = new ArrayList<String>();
		URI uri = image.getFullImageUrl();
		String imageUrl = uri.toURL().toString();
		GetMethod method = new GetMethod(imageUrl);
		method.setFollowRedirects(false);
		String userHandle = null;
		userHandle = LoginHelper.login(
				PropertyReader.getProperty("imeji.escidoc.user"),
				PropertyReader.getProperty("imeji.escidoc.password"));
		method.addRequestHeader("Cookie", "escidocCookie=" + userHandle);
		HttpClient client = new HttpClient();
		client.executeMethod(method);
		InputStream input = method.getResponseBodyAsStream();
		ImageInputStream iis = ImageIO.createImageInputStream(input);
		Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
		if (readers.hasNext()) {
			// pick the first available ImageReader
			ImageReader reader = readers.next();
			// attach source to the reader
			reader.setInput(iis, true);
			// read metadata of first image
			IIOMetadata metadata = reader.getImageMetadata(0);
			String[] names = metadata.getMetadataFormatNames();
			int length = names.length;
			for (int i = 0; i < length; i++) {
				displayMetadata(techMd, metadata.getAsTree(names[i]));
			}
		}
		return techMd;
	}

	static void displayMetadata(List<String> techMd, Node root) 
	{
		displayMetadata(techMd, root, 0);
	}

	static void indent(List<String> techMd, StringBuffer sb, int level) 
	{
		for (int i = 0; i < level; i++) 
		{
			sb.append("    ");
		}
	}

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
				sb.append(" " + attr.getNodeName() + "=\""
						+ attr.getNodeValue() + "\"");
			}
		}
		Node child = node.getFirstChild();
		if (child == null) {
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
