package example;

import java.util.Collection;

import thewebsemantic.Bean2RDF;
import thewebsemantic.binding.Jenabean;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class SimpleExample {
	public static void main(String[] args) {
		Model m = ModelFactory.createDefaultModel();
		Jenabean.instance().bind(m);
		
		
		AppInfo info = new AppInfo();
		Bean2RDF writer = new Bean2RDF(m);
		writer.save(info);
		
		Song s1 = new Song();
		s1.setTitle("Waters of March");
		s1.setComposer("Antonio Carlos Jobim");
		s1.setGenre(Genre.JAZZ);
		s1.save();s1.save();

		Song s2 = new Song();
		s2.setTitle("I.G.Y.");
		s2.setComposer("Donald Fagen");
		s2.setGenre(Genre.ROCK);
		s2.save();

		Collection<Song> songs = Song.load();
		assert(songs.size()==2);
		
		for (Song song : songs)
			System.out.println(song.getTitle() + ":" + 
					song.getGenre().getName());			
		m.write(System.out, "N3");
	}
}
