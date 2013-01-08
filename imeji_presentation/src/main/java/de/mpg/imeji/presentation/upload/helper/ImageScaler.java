package de.mpg.imeji.presentation.upload.helper;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import org.imgscalr.Scalr;


public class ImageScaler {

//	public static BufferedImage scaleImageForThumbnail(BufferedImage image, int w, int h) {
//		
//		BufferedImage thumbnail =
//				  Scalr.resize(image, Scalr.Method.SPEED, Scalr.Mode.FIT_TO_HEIGHT,
//				               150, 100, Scalr.OP_ANTIALIAS);
//		
//		return null;
//	}
	
	public static BufferedImage scaleImage(BufferedImage img, int w, int h) {
		
		int height = img.getHeight();
		int width = img.getWidth();
		
		Dimension d = null;
		
		if(w < 0 && h < 0) {
			d = new Dimension(width, height);
		} else if(w < 0 || h < 0) {
			if(w < 0) {
				double f = (double)((double) h / height);
				w = (int) (f * width);
				d = new Dimension(w, h); 
			} else if(h < 0) {
				double f = (double)( (double) w / width);
				h = (int) (f * height);
				d = new Dimension(w, h);
			}
		} else {
			d = new Dimension(w, h);
		}

//        return scaleExact(img, d);
		return scaleExactBetter(img, d);
    }
	
	private static BufferedImage scaleExactBetter(BufferedImage img, Dimension d) {	
		return Scalr.resize(img, Scalr.Method.SPEED, Scalr.Mode.FIT_EXACT,(int)d.getWidth(), (int)d.getHeight(), Scalr.OP_ANTIALIAS);
    }
	
	
    public BufferedImage scaleImage(BufferedImage img, Dimension d) {
        img = scaleByHalf(img, d);
        img = scaleExact(img, d);
        return img;
    }

    private static BufferedImage scaleByHalf(BufferedImage img, Dimension d) {
        int w = img.getWidth();
        int h = img.getHeight();
        float factor = getBinFactor(w, h, d);

        // make new size
        w *= factor;
        h *= factor;
        BufferedImage scaled = new BufferedImage(w, h,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g = scaled.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(img, 0, 0, w, h, null);
        g.dispose();
        return scaled;
    }

    private static BufferedImage scaleExact(BufferedImage img, Dimension d) {
        float factor = getFactor(img.getWidth(), img.getHeight(), d);

        // create the image
        int w = (int) (img.getWidth() * factor);
        int h = (int) (img.getHeight() * factor);
        BufferedImage scaled = new BufferedImage(w, h,
                BufferedImage.TYPE_INT_RGB);

        Graphics2D g = scaled.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(img, 0, 0, w, h, null);
        g.dispose();
        return scaled;
    }

    private static float getBinFactor(int width, int height, Dimension dim) {
        float factor = 1;
        float target = getFactor(width, height, dim);
        if (target <= 1) { while (factor / 2 > target) { factor /= 2; }
        } else { while (factor * 2 < target) { factor *= 2; }         }
        return factor;
    }

    private static float getFactor(int width, int height, Dimension dim) {
        float sx = dim.width / (float) width;
        float sy = dim.height / (float) height;
        return Math.min(sx, sy);
    }
}