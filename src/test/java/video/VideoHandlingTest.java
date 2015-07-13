package video;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.openimaj.feature.local.list.LocalFeatureList;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.feature.local.engine.DoGSIFTEngine;
import org.openimaj.image.feature.local.keypoints.Keypoint;
import org.openimaj.video.Video;
import org.openimaj.video.xuggle.XuggleVideo;

public class VideoHandlingTest {
  // @Test
  public void test() throws FileNotFoundException, IOException {
    Video<MBFImage> video;
    video = new XuggleVideo(new File("src/test/resouces/video/keyboardcat.flv"));
    // video = new XuggleVideo(new
    // URL("http://download.blender.org/peach/bigbuckbunny_movies/big_buck_bunny_480p_surround-fix.avi"));
    for (MBFImage mbfImage : video) {
      // float d = 0;
      // float c = 1;
      // float m = 0;
      // for (int x = 0; x < mbfImage.getWidth(); x++) {
      // for (int y = 0; y < mbfImage.getHeight(); y++) {
      // Float[] t = mbfImage.getPixel(x, y);
      // d += (t[0] + t[1] + t[2]) / 3.0;
      // m = d / c++;
      // //System.out.println("pixel: ("+ t[0]+ ","+ t[1]+ ","+t[2]+" - "+ m +")");
      // }
      // }
      // if(m > 0.3) {
      // System.out.println(d);
      // ImageUtilities.write(mbfImage, "png", new File("src/test/resources/video/cat.jpg"));
      // break;
      // }
      DoGSIFTEngine engine = new DoGSIFTEngine();
      LocalFeatureList<Keypoint> queryKeypoints = engine.findFeatures(mbfImage.flatten());
      if (queryKeypoints.size() > 0) {
        System.out.println("features: " + queryKeypoints.size());
        DisplayUtilities.displayName(mbfImage, "videoFrames");
      }
      // FImage iimg =
      // ImageUtilities.createFImage(ImageUtilities.createBufferedImageForDisplay(mbfImage));
      // DifferenceOfGaussian e = new DifferenceOfGaussian();
      // e.processImage(iimg);
      // DisplayUtilities.displayName(mbfImage.process(new CannyEdgeDetector()), "videoFrames");
      // DisplayUtilities.displayName(iimg, "videoFrames");
    }
  }
}
