
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.KeyException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

public class LRU {

  private LinkedHashMap<Integer, byte[]> hmap;
  public StorageEngine engine;
  private Set<Integer> dirtyKeys;

  private int capacity;

  public LRU(int capacity, StorageEngine newEngine) throws IOException {
    hmap = new LinkedHashMap<>(capacity, 0.75f, true);
    engine = newEngine;
    dirtyKeys = new HashSet<>();
    this.capacity = capacity;
  }

  public byte[] get(int key) throws KeyException, IOException {
    if (hmap.containsKey(key)){
      byte[] output = hmap.get(key);
      return output;
    }else{
      byte[] output = engine.readPage(key);
      this.put(key, output);
      return output;
    }
  }

  public void put(int key, byte[] value) throws KeyException, IOException {
    if (hmap.containsKey(key))
      dirtyKeys.add(key);
    ByteBuffer checker = ByteBuffer.wrap(value);
    checker.position(61);

    hmap.put(key, value);

    if (hmap.size() > this.capacity){
      int oldest = hmap.keySet().iterator().next();
      evict(oldest);
    }
  }

  public void evict(int key) throws IOException {
    byte[] value = hmap.get(key);
    hmap.remove(key);
    //now we need to flush this as well
    if (dirtyKeys.contains(key)){
      flush(key, value);
    }
  }

  public void flush(int key, byte[] value) throws IOException {
    ByteBuffer checker = ByteBuffer.wrap(value);
    checker.position(61);
    engine.writePage(value, key);
    dirtyKeys.remove(key);
  }

  public void flushAll() throws IOException {
    Set<Integer> keysToFlush = new HashSet<>(dirtyKeys);
    // flush each dirty page
    for (int key : keysToFlush) {
      if (hmap.containsKey(key)) {
        byte[] thing = hmap.get(key);
        ByteBuffer checker = ByteBuffer.wrap(thing);
        checker.position(61);

        flush(key, hmap.get(key));
      }
    }
  }

//  public static void main(String[] args) throws IOException, KeyException {
//
//    StorageEngine engine = new StorageEngine("testing");
//    LRU thingy = new LRU(3, engine);
//    long beg = System.nanoTime();
//    thingy.get(0);
//    long end = System.nanoTime();
//    System.out.println(end - beg);
//    long start = System.nanoTime();
//    thingy.get(0);
//    long stop = System.nanoTime();
//    System.out.println(stop - start);
//  }
}
