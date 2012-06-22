///**
// * License: src/main/resources/license/escidoc.license
// */
//
//package de.mpg.imeji.presentation.metadata.editors;
//
//import java.net.URI;
//import java.util.ArrayList;
//import java.util.List;
//
//import de.mpg.imeji.logic.vo.Item;
//import de.mpg.imeji.logic.vo.Metadata;
//import de.mpg.imeji.logic.vo.MetadataProfile;
//import de.mpg.imeji.logic.vo.Statement;
//
//public class MetadataBatchEditor extends MetadataEditor 
//{
//	private List<Item> originalImages;
//	
//	public MetadataBatchEditor(List<Item> items, MetadataProfile profile,	Statement statement) 
//	{
//		super(items, profile, statement);
//	}
//
//	@Override
//	public void initialize() 
//	{
//		originalImages = items;
////		this.items = new ArrayList<Item>();
////		this.items.add(new Item());
////		this.items.get(0).getMetadataSet().getMetadata().add(newMetadata());
//	}
//	
//
//	@Override
//	public boolean prepareUpdate() 
//	{
//		if (items.size() == 0)
//		{
//			return false;
//		}
//
//		Metadata md = items.get(0).getMetadataSet().getMetadata().iterator().next();
//		for (Item im: originalImages)
//		{
//		    md.setId(URI.create(im.getMetadataSet().getId().toString() + "/0"));
//			im.getMetadataSet().getMetadata().add(md);
//		}
//		items = originalImages;
//		return true;
//	}
//	
//
//	@Override
//	public boolean validateMetadataofImages() 
//	{
////		for (Image im : images)
////		{
////			validator = new Validator(im.getMetadata(), profile);
////			if (!(validator.valid()))
////			{
////				return false;
////			}
////		}
//		return true;
//	}
//	
//
//	@Override
//	public void addMetadata(int imagePos, int metadataPos) {
//		// TODO Auto-generated method stub		
//	}
//
//	@Override
//	public void addMetadata(Item item, int metadataPos)
//	{
//		if (metadataPos <= item.getMetadataSet().getMetadata().size()) 
//		{
//			List<Metadata> newList = new ArrayList<Metadata>();
//			newList.add(metadataPos, newMetadata());
//			item.getMetadataSet().getMetadata().addAll(newList);
//			//image.getMetadata().add(metadataPos, newMetadata());
//		}
//	}
//
//	@Override
//	public void removeMetadata(int imagePos, int metadataPos) {
//		// TODO Auto-generated method stub		
//	}
//
//	@Override
//	public void removeMetadata(Item item, int metadataPos) {
//		// TODO Auto-generated method stub
//	}
//
//}
