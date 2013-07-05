package de.mpg.imeji.logic.storage.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.video.Video;
import org.openimaj.video.xuggle.XuggleVideo;


import de.mpg.imeji.presentation.util.PropertyReader;

public class VideoUtils {

	final static float IMAGE_DETECTION_THRESHOLD = 0.1F;

	private static float getGoodImageDetectionThreshold() {
		try {
			return Float
					.parseFloat(PropertyReader
							.getProperty("imeji.internal.video.imagedetection.threshold"));
		} catch (Exception e) {
			e.printStackTrace();
			return VideoUtils.IMAGE_DETECTION_THRESHOLD;
		}
	}

	public static byte[] videoToImageBytes(URL url)	throws FileNotFoundException, IOException {
		return VideoUtils.videoFileToByteAray(url, VideoUtils.getGoodImageDetectionThreshold());
	}

	public static byte[] videoToImageBytes(byte[] bytes) throws FileNotFoundException, IOException {
		return VideoUtils.videoFileToByteAray(bytes, VideoUtils.getGoodImageDetectionThreshold());
	}
	
	public static byte[] videoFileToByteAray(URL url, float threshold)	throws IOException {
		return VideoUtils.videoFileToByteAray(new XuggleVideo(url), threshold);	
	}

	public static byte[] videoFileToByteAray(byte[] bytes, float threshold)	throws IOException {

		File tempFile = MediaUtils.createTempDirectory();

		FileOutputStream fos = new FileOutputStream(tempFile);
		fos.write(bytes);
		fos.close();

		Video<MBFImage> video = new XuggleVideo(tempFile);

		return VideoUtils.videoFileToByteAray(video, threshold);	
	}

	private static byte[] videoFileToByteAray(Video<MBFImage> video, float threshold) throws IOException {		
		for (MBFImage mbfImage : video) {

			float d = 0;
			float c = 1;
			float m = 0;
			for (int x = 0; x < mbfImage.getWidth(); x++) {
				for (int y = 0; y < mbfImage.getHeight(); y++) {
					Float[] t = mbfImage.getPixel(x, y);
					d += (t[0] + t[1] + t[2]) / 3.0;
					m = d / c++;
				}
			}
			if (m >= threshold) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageUtilities.write(mbfImage, "png", baos);
				return baos.toByteArray();
			}
		}
		
		return null;
	}

}
