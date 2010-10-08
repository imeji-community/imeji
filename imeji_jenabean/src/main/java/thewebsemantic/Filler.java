package thewebsemantic;

public class Filler {
   
   private Object target;
   private RDF2Bean writer;

   public Filler(RDF2Bean w, Object o) {
      writer = w;
      target = o;
   }
   
   public void with(String propertyName) {
      writer.fill(target, propertyName);
   }

}
