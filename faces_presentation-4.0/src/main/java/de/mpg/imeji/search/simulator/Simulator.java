package de.mpg.imeji.search.simulator;

import java.util.ArrayList;
import java.util.List;

public class Simulator {
	private MetaDataSimulator md1;
	private MetaDataSimulator md2;
	private MetaDataSimulator md3;
	private MetaDataSimulator md4;
	private MetaDataSimulator md5;
	private MetaDataSimulator md6;
	
	private CollectionSimulator defaultCollection;
	private CollectionSimulator collection1;
	private CollectionSimulator collection2;
	private CollectionSimulator collection3;
	
	private ArrayList<MetaDataSimulator> mdList1;
	private ArrayList<MetaDataSimulator> mdList2;
	private ArrayList<MetaDataSimulator> mdList3;
	private ArrayList<MetaDataSimulator> defaultMdList;
	
	private CollectionSimulator selectedCollection;

	
	
	public CollectionSimulator getSelectedCollection() {
		return selectedCollection;
	}


	public void setSelectedCollection(CollectionSimulator selectedCollection) {

		this.selectedCollection = selectedCollection;
	}


	public Simulator(){
		md1 = new MetaDataSimulator("Title","Title");
		md2 = new MetaDataSimulator("Description","Description");
		md3 = new MetaDataSimulator("Author","Author");
		md4 = new MetaDataSimulator("Organisation","Organisation");
		md5 = new MetaDataSimulator("Genre","Genre");
		md6 = new MetaDataSimulator("Datum","Datum");
		
		mdList1 = new ArrayList<MetaDataSimulator>();
		mdList2 = new ArrayList<MetaDataSimulator>();
		mdList3 = new ArrayList<MetaDataSimulator>();
		defaultMdList = new ArrayList<MetaDataSimulator>();
		
		mdList1.add(md1);
		mdList1.add(md2);
		mdList1.add(md3);
		
		mdList2.add(md1);
		mdList2.add(md2);
		mdList2.add(md3);
		mdList2.add(md4);
		mdList2.add(md5);
		
		mdList3.add(md1);
		mdList3.add(md2);
		mdList3.add(md3);
		mdList3.add(md4);
		mdList3.add(md5);
		mdList3.add(md6);
		
		defaultMdList.add(md1);
		defaultMdList.add(md2);
		
		defaultCollection = new CollectionSimulator("--", defaultMdList);
		collection1 = new CollectionSimulator("Birds", mdList1);
		collection2 = new CollectionSimulator("Faces", mdList2);
		collection3 = new CollectionSimulator("Diamonds", mdList3);
	}


	public MetaDataSimulator getMd1() {
		return md1;
	}


	public void setMd1(MetaDataSimulator md1) {
		this.md1 = md1;
	}


	public MetaDataSimulator getMd2() {
		return md2;
	}


	public void setMd2(MetaDataSimulator md2) {
		this.md2 = md2;
	}


	public MetaDataSimulator getMd3() {
		return md3;
	}


	public void setMd3(MetaDataSimulator md3) {
		this.md3 = md3;
	}


	public MetaDataSimulator getMd4() {
		return md4;
	}


	public void setMd4(MetaDataSimulator md4) {
		this.md4 = md4;
	}


	public MetaDataSimulator getMd5() {
		return md5;
	}


	public void setMd5(MetaDataSimulator md5) {
		this.md5 = md5;
	}


	public MetaDataSimulator getMd6() {
		return md6;
	}


	public void setMd6(MetaDataSimulator md6) {
		this.md6 = md6;
	}


	public CollectionSimulator getCollection1() {
		return collection1;
	}


	public void setCollection1(CollectionSimulator collection1) {
		this.collection1 = collection1;
	}


	public CollectionSimulator getCollection2() {
		return collection2;
	}


	public void setCollection2(CollectionSimulator collection2) {
		this.collection2 = collection2;
	}


	public CollectionSimulator getCollection3() {
		return collection3;
	}


	public void setCollection3(CollectionSimulator collection3) {
		this.collection3 = collection3;
	}


	public ArrayList<MetaDataSimulator> getMdList1() {
		return mdList1;
	}


	public void setMdList1(ArrayList<MetaDataSimulator> mdList1) {
		this.mdList1 = mdList1;
	}


	public ArrayList<MetaDataSimulator> getMdList2() {
		return mdList2;
	}


	public void setMdList2(ArrayList<MetaDataSimulator> mdList2) {
		this.mdList2 = mdList2;
	}


	public ArrayList<MetaDataSimulator> getMdList3() {
		return mdList3;
	}


	public void setMdList3(ArrayList<MetaDataSimulator> mdList3) {
		this.mdList3 = mdList3;
	}
	
	public CollectionSimulator getDefaultCollection() {
		return defaultCollection;
	}


	public void setDefaultCollection(CollectionSimulator defaultCollection) {
		this.defaultCollection = defaultCollection;
	}


	public ArrayList<MetaDataSimulator> getDefaultMdList() {
		return defaultMdList;
	}


	public void setDefaultMdList(ArrayList<MetaDataSimulator> defaultMdList) {
		this.defaultMdList = defaultMdList;
	}



	



	
}
