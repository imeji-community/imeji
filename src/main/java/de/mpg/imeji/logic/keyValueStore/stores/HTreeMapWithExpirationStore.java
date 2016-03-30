package de.mpg.imeji.logic.keyValueStore.stores;

import java.util.concurrent.TimeUnit;

import org.mapdb.DBMaker;
import org.mapdb.Serializer;

/**
 * Extends the HTreeMapStore to add expiration capability
 * 
 * @author bastiens
 *
 */
public class HTreeMapWithExpirationStore extends HTreeMapStore {
  private long expiration = -1;

  public HTreeMapWithExpirationStore(String name, long expirationInDays) {
    super(name);
    this.expiration = expirationInDays;
  }

  @Override
  public void start() {
    STORE = DBMaker.newFileDB(STORE_FILE).make();
    map = STORE.createHashMap(name).keySerializer(Serializer.STRING)
        .expireAfterWrite(this.expiration, TimeUnit.DAYS).makeOrGet();
  }

}
