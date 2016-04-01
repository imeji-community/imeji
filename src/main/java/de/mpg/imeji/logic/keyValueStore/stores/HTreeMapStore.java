package de.mpg.imeji.logic.keyValueStore.stores;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.keyValueStore.KeyValueStore;
import de.mpg.imeji.logic.util.StringHelper;

/**
 * A Key Value store based on the MapsDB HTreeMapStore
 * 
 * @author bastiens
 *
 */
public class HTreeMapStore implements KeyValueStore {
  public static final String STORE_FILENAME_PREFIX = "imeji_HTreeMap_";
  protected static DB STORE;
  protected HTreeMap<Object, Object> map;
  protected String name;

  /**
   * Basic HTreeMapStore without expiration date
   * 
   * @param storeName
   */
  public HTreeMapStore(String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public byte[] get(String key) {
    return (byte[]) map.get(key);
  }

  @Override
  public void put(String key, byte[] value) {
    map.put(key, value);
  }

  @Override
  public void delete(String key) {
    map.remove(key);
  }

  @Override
  public List<byte[]> getList(String keyPattern) {
    List<byte[]> list = new ArrayList<>();
    for (Object key : map.keySet()) {
      if (((String) key).matches(keyPattern)) {
        list.add((byte[]) map.get(key));
      }
    }
    return list;
  }

  @Override
  public void start() {
    STORE = DBMaker
        .newFileDB(
            new File(StringHelper.normalizePath(Imeji.tdbPath) + STORE_FILENAME_PREFIX + name))
        .make();
    map = STORE.createHashMap(name).keySerializer(Serializer.STRING).makeOrGet();
  }

  @Override
  public synchronized void stop() {
    if (!STORE.isClosed()) {
      STORE.close();
    }
  }

  @Override
  public boolean isStarted() {
    return STORE != null && map != null;
  }
}
