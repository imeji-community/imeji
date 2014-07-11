package de.mpg.imeji.presentation.metadata.extractors;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;

public class MetadataExtractor {
	
	public static void main(String[] args) {
		File f = new File("C:\\Users\\yu\\Desktop\\md\\1.gif");
		try {
			Metadata metadata = ImageMetadataReader.readMetadata(f);
			for (Directory directory : metadata.getDirectories()) {
			    for (Tag tag : directory.getTags()) {
			        System.out.println(tag);
			    }
			}
			
//			ExifSubIFDDirectory exifDirectory = metadata.getDirectory(ExifSubIFDDirectory.class);
//			System.out.println("Content-Type = " +  );
//			System.out.println(" Make = " +  );
//			System.out.println(" Model  = " +  );
//			System.out.println(" Artist = " +  );
//			System.out.println(" Software = " +  );
//			System.out.println(" Description = " + exifDirectory.getDescription(tagType) );
//
//			Date date = exifDirectory.getDate(ExifSubIFDDirectory.TAG_DATETIME_DIGITIZED);
//			System.out.println("date = " + date.toString());
//
//			System.out.println(" = " +  );
//			
//			System.out.println("color_space = " + exifDirectory.getString(ExifSubIFDDirectory.TAG_COLOR_SPACE));
//			
//			GpsDirectory gpsDirectory = metadata.getDirectory(GpsDirectory.class);
//			GeoLocation location = gpsDirectory.getGeoLocation();
//			System.out.println("latitude = " + location.getLatitude());
			
			
		} catch (ImageProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
