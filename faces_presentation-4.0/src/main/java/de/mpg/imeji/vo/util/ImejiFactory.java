package de.mpg.imeji.vo.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.xmlbeans.XmlException;
import org.dublincore.xml.dcDsp.x2008.x01.x14.DescriptionSetTemplateDocument;
import org.dublincore.xml.dcDsp.x2008.x01.x14.DescriptionSetTemplateDocument.DescriptionSetTemplate.DescriptionTemplate;
import org.dublincore.xml.dcDsp.x2008.x01.x14.DescriptionSetTemplateDocument.DescriptionSetTemplate.DescriptionTemplate.StatementTemplate;
import org.dublincore.xml.dcDsp.x2008.x01.x14.LiteralConstraintType.LiteralOption;

import de.mpg.imeji.vo.AlbumVO;
import de.mpg.imeji.vo.CollectionVO;
import de.mpg.imeji.vo.ImageVO;
import de.mpg.imeji.vo.MdProfileVO;
import de.mpg.imeji.vo.StatementVO;
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

    public static MdProfileVO newMdProfile(String str)
    {
        MdProfileVO vo = new MdProfileVO();
        DescriptionSetTemplateDocument dstDoc;
        try
        {
            dstDoc = DescriptionSetTemplateDocument.Factory.parse(str);
        }
        catch (XmlException e)
        {
            throw new RuntimeException("Error parsing Md-Profile: ", e);
        }
        
        for (DescriptionTemplate dt : dstDoc.getDescriptionSetTemplate().getDescriptionTemplateArray())
        {
            for (StatementTemplate st : dt.getStatementTemplateArray())
            {
               vo.getStatements().add(newStatementVO(st));
            }
        }
        return vo;
    }
    
    public static StatementVO newStatementVO(StatementTemplate st)
    {
        StatementVO vo = new StatementVO();
        vo.setName(st.getID());
        if (st.getPropertyArray().length > 0)
        {
            vo.setElementNamespace(st.getPropertyArray(0));
        }
        if (st.getNonLiteralConstraint().getVocabularyEncodingSchemeURIArray().length > 0)
        {
            vo.setVocabulary(st.getNonLiteralConstraint().getVocabularyEncodingSchemeURIArray(0));
        }
        if (st.getMinOccurs().intValue() > 0)
        {
            vo.setRequired(true);
        }
        if (st.getMaxOccurs().intValue() > 1)
        {
            vo.setMultiple(true);
        }
        for (LiteralOption lo : st.getLiteralConstraint().getLiteralOptionArray())
        {
            vo.getConstraints().add(lo.getStringValue());
        }
        return vo;
    }

    public static List<ImageVO> newImagesList(Collection<Image> images)
    {
       List<ImageVO> imgVOList = new ArrayList<ImageVO>();
       for(Image img : images)
       {
           imgVOList.add(newImage(img));
       }
       return  imgVOList;
    }
}
