package de.mpg.imeji.vo.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.mpg.imeji.vo.AlbumVO;
import de.mpg.imeji.vo.CollectionVO;
import de.mpg.imeji.vo.ImageVO;
import de.mpg.jena.vo.Album;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.Image;

public class ImejiFactory
{
    public static AlbumVO newAlbum(Album al)
    {
	AlbumVO vo = new AlbumVO();
	vo.setId(al.getId());
	vo.setImages(al.getImages());
	vo.setMetadata(al.getMetadata());
	vo.setProperties(al.getProperties());
	return vo;
    }
    
    public static CollectionVO newCollection(CollectionImeji ci)
    {
	CollectionVO vo = new CollectionVO();
	vo.setId(ci.getId());
	vo.setImages(ci.getImages());
	vo.setMetadata(ci.getMetadata());
	vo.setMetadataDSP(ci.getMetadataDSP());
	vo.setMetadataSchema(ci.getMetadataSchema());
	vo.setProperties(ci.getProperties());
	return vo;
    }
    
    
    
    public static List<CollectionVO> newCollectionList(Collection<CollectionImeji> ci)
    {
    	List<CollectionVO> collList = new ArrayList<CollectionVO>();
    	for(CollectionImeji coll : ci)
    	{
    		collList.add(newCollection(coll));
    	}
    	
    	return collList;
    }
    
    public static List<AlbumVO> newAlbumList(Collection<Album> ci)
    {
        List<AlbumVO> collList = new ArrayList<AlbumVO>();
        for(Album a : ci)
        {
            collList.add(newAlbum(a));
        }
        
        return collList;
    }
    
    public static ImageVO newImage(Image im)
    {
	ImageVO vo = new ImageVO();
	vo.setCollection(im.getCollection());
	vo.setFullImageUrl(im.getFullImageUrl());
	vo.setId(im.getId());
	vo.setMetadata(im.getMetadata());
	vo.setProperties(im.getProperties());
	vo.setThumbnailImageUrl(im.getThumbnailImageUrl());
	vo.setVisibility(im.getVisibility());
	vo.setWebImageUrl(im.getWebImageUrl());
	return vo;
    }
}
