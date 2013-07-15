package de.mpg.imeji.logic.digilib;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.ws.Holder;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.shared.NotFoundException;

import de.mpg.imeji.logic.storage.util.StorageUtils;
import digilib.image.ImageJobDescription;
import digilib.image.ImageLoaderDocuImage;
import digilib.image.ImageOpException;
import digilib.image.ImageWorker;
import digilib.io.ImageCacheStream;
import digilib.conf.DigilibConfiguration;
import digilib.conf.DigilibRequest;

public class Scaler {

    
    private static Logger logger = Logger.getLogger(Scaler.class);
	
    // currently only one output format
    private final String outputFormat = StorageUtils.getMimeType("jpeg");

	
    public void getScaledImage(String sessionId, String url, String query, String logParameter, Holder<String> mimeType,
            Holder<byte[]> imageData, Holder<Integer> width, Holder<Integer> height, Holder<Integer> originalWidth,
            Holder<Integer> originalHeight, Holder<Integer> originalDpi) {    	    	
    	
//        logger.debug("getScaledImage: sid=" + sessionId + " uri=" + url + " query=" + query);

        try {
//            long startTime = System.currentTimeMillis();
            // do read         
//            logger.debug(Long.toString(System.currentTimeMillis() - startTime) + " ms");

            InputStream istream = new FileInputStream(url);
            // get mime-type from metadata
            String mt = mimeType.value;
//            logger.debug("Stream=" + istream.toString() + " type=" + mt);
            /*
             * set up digilib operation
             */
            ImageCacheStream imgStream = new ImageCacheStream(istream, mt);
//            logger.debug("iis=" + imgStream.getImageInputStream());
            ImageLoaderDocuImage img = new ImageLoaderDocuImage();
            // reuse reader for stream input
            img.reuseReader = true;
            // identify image size
//            logger.debug("Identifying...");
            img.identify(imgStream);
            // save original size
            originalWidth.value = img.getWidth();
            originalHeight.value = img.getHeight();
            // set up job description
            DigilibConfiguration dlConfig = new DigilibConfiguration();
            DigilibRequest dlReq = new DigilibRequest();
            // get operation parameters from query String 
            dlReq.setWithParamString(query, "&");
            ImageJobDescription job = ImageJobDescription.getInstance(dlReq, dlConfig);
            job.setDocuImage(img);
            job.setInput(imgStream);
            ImageWorker digilib = new ImageWorker(dlConfig, job);
//            logger.debug("Scaling with " + digilib);
            /*
             * do process image
             */
            digilib.call();
//            logger.debug(Long.toString(System.currentTimeMillis() - startTime) + " ms");
            // write image to buffer
            ByteArrayOutputStream ostream = new ByteArrayOutputStream();
            img.writeImage(outputFormat, ostream);
//            logger.debug("written in " + (System.currentTimeMillis() - startTime) + " ms");
            // set buffer in Holder
            imageData.value = ostream.toByteArray();
            mimeType.value = outputFormat;
            width.value = img.getWidth();
            height.value = img.getHeight();
            // we have no original-dpi yet
            originalDpi.value = 0;
//            logger.debug("done.");

        } catch (NotFoundException e) {
            logger.error("Object not found", e);
        } catch (IOException e) {
            logger.error("IO Exception", e);
        } catch (ImageOpException e) {
            logger.error("ImageOp exception", e);
        }
    }
    
    public byte[] getScaledImage(String objectPath, String query) {    	    	

    	InputStream istream = null;
    	
    	try {
			istream = new URL(objectPath).openStream();
		} catch (MalformedURLException e1) {
			try {
				istream = new FileInputStream(objectPath);
			} catch (FileNotFoundException e) {
				logger.error("File Not Found Exception", e);
				return null;
			}
		} catch (IOException e1) {
			logger.error("IO Exception", e1);
			return null;
		}   

        try {
            /*
             * set up digilib operation
             */
            ImageCacheStream imgStream = new ImageCacheStream(istream, null);
            ImageLoaderDocuImage img = new ImageLoaderDocuImage();
            // reuse reader for stream input
            img.reuseReader = true;
            // identify image size
            img.identify(imgStream);

            // set up job description
            DigilibConfiguration dlConfig = new DigilibConfiguration();
            DigilibRequest dlReq = new DigilibRequest();
            // get operation parameters from query String 
            dlReq.setWithParamString(query, "&");
            ImageJobDescription job = ImageJobDescription.getInstance(dlReq, dlConfig);
            job.setDocuImage(img);
            job.setInput(imgStream);
            ImageWorker digilib = new ImageWorker(dlConfig, job);
            /*
             * do process image
             */
            digilib.call();
            // write image to buffer
            ByteArrayOutputStream ostream = new ByteArrayOutputStream();
            img.writeImage(outputFormat, ostream);
            ostream.close();
            return ostream.toByteArray();

        } catch (IOException e) {
            logger.error("IO Exception", e);
        } catch (ImageOpException e) {
            logger.error("ImageOp exception", e);
        }
        
        return null;
    }
}
