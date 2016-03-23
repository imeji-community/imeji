package de.mpg.imeji.logic.message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.fusesource.leveldbjni.JniDBFactory;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;

import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.exceptions.NotFoundException;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.util.StringHelper;

/**
 * Business Controller to persisted messages. This can be used to store Notifications, Invitations,
 * etc. Messages are stored in a Key/Value store
 * 
 * @author bastiens
 *
 */
public class KeyValueStoreBusinessController {

  public static final String STORE_FILENAME = "imeji_message_store";
  public static final File STORE_FILE =
      new File(StringHelper.normalizePath(Imeji.tdbPath) + STORE_FILENAME);
  private static DB STORE;
  private static final Logger LOGGER = Logger.getLogger(KeyValueStoreBusinessController.class);

  /**
   * Start the Key/Value Store
   * 
   * @throws IOException
   */
  public synchronized static void startStore() throws IOException {
    Options options = new Options();
    options.createIfMissing(true);
    try {
      STORE = JniDBFactory.factory.open(STORE_FILE, options);
    } catch (Exception e) {
      JniDBFactory.factory.repair(STORE_FILE, options);
      STORE = JniDBFactory.factory.open(STORE_FILE, options);
    }
  }

  /**
   * Close the Key/value Store
   * 
   * @throws IOException
   */
  public synchronized static void stopStore() {
    try {
      STORE.close();
    } catch (IOException e) {
      LOGGER.error("Error stopping key value store: ", e);
    }
  }

  /**
   * Get an Object from the Key/Value Store
   * 
   * @param id
   * @return
   * @throws ImejiException
   */
  public Object get(Object key) throws NotFoundException {
    try {
      return deserialize(STORE.get(serialize(key)));
    } catch (Exception e) {
      throw new NotFoundException(
          "Key " + key + " not found in key/value store: " + e.getMessage());
    }
  }

  /**
   * Return all elements with a Key matching the Key pattern (according to REGEX Rules)
   * 
   * @param keyPattern
   * @return
   * @throws ImejiException
   * @throws ClassNotFoundException
   * @throws IOException
   */

  public <T> List<T> getList(String keyPattern, Class<T> clazz) throws ImejiException {
    DBIterator iterator = STORE.iterator();
    List<T> list = new ArrayList<>();
    try {
      try {
        for (iterator.seekToFirst(); iterator.hasNext(); iterator.next()) {
          String key = (String) deserialize(iterator.peekNext().getKey());
          if (key.matches(keyPattern)) {
            list.add(clazz.cast(deserialize(iterator.peekNext().getValue())));
          }
        }
      } finally {
        iterator.close();
      }
    } catch (Exception e) {
      LOGGER.error("Error reading list from key/value store with Pattern: " + keyPattern, e);
      throw new ImejiException(
          "Error reading list from key/value store with Pattern: " + keyPattern);
    }
    return list;
  }

  /**
   * Put an object to the Key/Value Store
   * 
   * @param id
   * @param o
   * @throws ImejiException
   */
  public void put(Object key, Object value) throws ImejiException {
    try {
      STORE.put(serialize(key), serialize(value));
    } catch (Exception e) {
      throw new ImejiException("Error writing Data in Key/Value Store", e);
    }
  }

  public void delete(Object key) throws ImejiException {
    try {
      STORE.delete(serialize(key));
    } catch (Exception e) {
      throw new ImejiException("Error deleting Data in Key/Value Store", e);
    }
  }

  /**
   * Create the Object from its serialized byte representation
   * 
   * @param bytes
   * @return
   * @throws IOException
   * @throws ClassNotFoundException
   */
  private Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
    ObjectInputStream is = new ObjectInputStream(in);
    return is.readObject();
  }


  /**
   * Serialize an Object to a byte array
   * 
   * @param obj
   * @return
   * @throws IOException
   */
  private byte[] serialize(Object obj) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ObjectOutputStream os = new ObjectOutputStream(out);
    os.writeObject(obj);
    return out.toByteArray();
  }

}
