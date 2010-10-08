package example;

import thewebsemantic.Id;

public class Genre {
	
	public static Genre ROCK = new Genre("ROCK");
	public static Genre CLASSICAL = new Genre("CLASSICAL");
	public static Genre JAZZ = new Genre("JAZZ");
	
	private String id;
	
	public Genre(String id) {
		this.id = id;
	}
	
	@Id
	public String getName() {
		return id;
	}
	
}
