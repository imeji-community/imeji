package example;

import java.util.Collection;

import thewebsemantic.Id;
import thewebsemantic.binding.Jenabean;
import thewebsemantic.binding.RdfBean;

public class Song extends RdfBean<Song> {
	private String composer;
	private Genre genre; 
	private String title;
	
	public static Collection<Song> load() {
		return Jenabean.load(Song.class);
	}
	
	@Id
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public String getComposer() {
		return composer;
	}
	public void setComposer(String composer) {
		this.composer = composer;
	}
	
	public Genre getGenre() {
		return genre;
	}
	public void setGenre(Genre genre) {
		this.genre = genre;
	}
}
