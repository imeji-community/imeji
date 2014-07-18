package de.mpg.imeji.service;

public interface Pathes {
	
	public static final String PATH_HELLO_WORLD = "/helloworld";
	
	/* collections */
	
	public static final String PATH_COLLECTIONS = "/collections";
	
	public static final String PATH_COLLECTION = "/collections/{id}";
	
	public static final String PATH_COLLECTION_RELEASE = "/collections/{id}/release";
	
	public static final String PATH_COLLECTION_DISCARD = "/collections/{id}/discard";
	
	public static final String PATH_COLLECTION_MDPROFILES = "/collections/{id}/mdprofiles";
	
	public static final String PATH_COLLECTION_VERSIONS = "/collections/{id}/versions";
	
	public static final String PATH_COLLECTION_SHARE = "/collections/{id}/share";
	
	public static final String PATH_COLLECTIONS_QUERY = "/collections/search?q={query}";
	
	/* profiles */
	
	public static final String PATH_PROFILES = "/profiles";
	
	public static final String PATH_PROFILE = "/profiles/{id}";
	
	public static final String PATH_PROFILE_STATEMENTS = "/profiles/{pid}/statements";
	
	public static final String PATH_PROFILE_STATEMENT = "/profiles/{pid}/statements/{sid}";
	
	/* albums */
	
	public static final String PATH_ALBUMS = "/albums";
	
	public static final String PATH_ALBUM = "/albums/{id}";
	
	public static final String PATH_ALBUM_RELEASE = "/albums/{id}/release";
	
	public static final String PATH_ALBUM_DISCARD = "/albums/{id}/discard";
	
	public static final String PATH_ALBUM_MEMEBERS = "/albums/{id}/members";
	
	public static final String PATH_ALBUM_MEMEBERS_LINK = "/albums/{id}/members/link";
	
	public static final String PATH_ALBUM_MEMEBERS_UNLINK = "/albums/{id}/members/unlink";
	
	public static final String PATH_ALBUM_VERSIONS = "/albums/{id}/versions";
	
	public static final String PATH_ALBUM_SHARE = "/albums/{id}/share";

	public static final String PATH_ALBUMS_QUERY = "/albums/search?q={query}";

	/* items */
	
	public static final String PATH_ITEMS = "/items";
	
	public static final String PATH_ITEM = "/items/{id}";
	
	public static final String PATH_ITEM_CONTENT = "/items/{id}/content";
	
	public static final String PATH_ITEM_VERSIONS = "/items/{id}/versions";

	public static final String PATH_ITEM_SHARE = "/items/{id}/share";

	public static final String PATH_ITEMS_QUERY = "/items/search?q={query}";
	
	/* AA */
	
	public static final String PATH_LOGIN = "/login";
	
	public static final String PATH_LOGOUT = "/logout";

	/* global search */
	
	public static final String PATH_GLOBAL_QUERY = "/search?q={query}";

	


}
