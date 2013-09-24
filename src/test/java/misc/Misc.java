package misc;

import static org.junit.Assert.*;

import org.junit.Test;

import de.mpg.imeji.presentation.util.CommonUtils;

public class Misc {

	@Test
	public void test() {
		String dis = "<div align=\"justify\"><font size=\"+1\">This collection contains several technical drawings of various machines such as headlights and sketches of other ideas Zuse had such as methods for the reconstruction of vases.<br/>Find out more about Konrad Zuse in the <a href=\"http://zuse.zib.de/zuse\" target=\"_self\">Encyclopedia</a>.</font><br/></div>";
		
		System.out.println(CommonUtils.removeTags(dis));
	}

}
