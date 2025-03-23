import java.io.*;


public class StorageEngine {

  private final String EXTENSION = ".db";
  private final int MAGICNUM = 0xDB0023FF;

  private String filename;
  private RandomAccessFile fileObject;

  private boolean isValid = true;
  private int numPages;
  private int pageSize;

  public StorageEngine(String filename) throws IOException {
    //check to see if the file exists or not.
    String fullname = filename + EXTENSION;
    File file = new File(fullname);
    boolean exists = file.exists();
    this.filename = fullname;
    this.fileObject = new RandomAccessFile(fullname, "rw");
    if (exists){ //here you're just reading the metadata
      boolean output = readMetaData();
      if (!output){
        this.isValid = false; //use this to check before calling any other methods
        throw new IOException("File corrupted and/or not of KrishivDB type.");
      }
    }else{ //here you're ADDING metadata (file's already been created for you)
      boolean success = addMetaData();
      if (!success)
        throw new IOException("Was not able to load metadata.");
      System.out.println("Metadata exists");
    }
  }

  private boolean readMetaData() throws IOException {
    this.fileObject.seek(0);

    //read the magic num
    int readMagicNum = this.fileObject.readInt();
    if (readMagicNum != MAGICNUM){
      System.out.println("The magic number is not matching");
      return false;
    }

    //then the version num
    int version = this.fileObject.readInt();

    //read and set the number of pages and the pagesize
    this.numPages = this.fileObject.readInt();

    this.pageSize = this.fileObject.readInt();

    return true;
  }

  private boolean addMetaData() throws IOException {
    try{
      this.fileObject.seek(0);

      this.fileObject.writeInt(MAGICNUM);

      this.fileObject.writeInt(1);

      this.fileObject.writeInt(0);

      this.fileObject.writeInt(4096);

      this.pageSize = 4096;
      this.numPages = 0;

    }catch (Exception e){
      return false; //TODO: make this more descriptive
    }
    return true;
  }

  private int allocatePage() throws IOException {
    if (!isValid)
      throw new InvalidObjectException("This file is not a valid type.");
    //go to read the number of pages and increment
    this.fileObject.seek(8);
    this.fileObject.writeInt(this.numPages + 1);
    this.numPages += 1;
    //go to new page location
    int offset = 16 + this.pageSize * numPages;
    this.fileObject.seek(offset);
    //write placeholder data
    byte[] buffer = new byte[this.pageSize];
    this.fileObject.write(buffer);
    //return index of new page
    return this.numPages - 1;
  }

  private byte[] readPage(int id) throws IndexOutOfBoundsException, IOException {
    if (!isValid)
      throw new InvalidObjectException("This file is not a valid type.");
    if (id >= this.numPages || id < 0){
      throw new IndexOutOfBoundsException("Index goes beyond the number of pages");
    }
    int offset = 16 + id*this.pageSize;
    byte[] readMem = new byte[this.pageSize];
    this.fileObject.seek(offset);
    int output  = this.fileObject.read(readMem);
    return readMem;
  }

  private void writePage(byte[] toDisk) throws IOException {
    writePage(toDisk, this.numPages - 1); //appends to the end
  }


  /**
   * WARNING: this OVERWRITES any existing data.
   * @param toDisk: bytes arr that should be written to disk from memory
   * @param id
   * @return
   */
  private void writePage(byte[] toDisk, int id) throws IOException {
    //check to see len of arr matches pagesize
    if (toDisk.length > this.pageSize)
      throw new IndexOutOfBoundsException("The bytes array is too large to be written");
    if (id >= this.numPages || id < 0){
      throw new IndexOutOfBoundsException("Index goes beyond the number of pages");
    }
    int offset = 16 + this.pageSize * id;
    if (toDisk.length < this.pageSize){
      byte[] rightSize = new byte[this.pageSize];
      System.arraycopy(toDisk, 0, rightSize, 0, toDisk.length);
      toDisk = rightSize;
    }
    this.fileObject.seek(offset);
    this.fileObject.write(toDisk);
  }

  public static void main(String[] args) throws IOException {
//    StorageEngine thing = new St  orageEngine("hi");
    StorageEngine thing = new StorageEngine("randomdb");
//    int id = thing.allocatePage();
//    System.out.println(id);
//    System.out.println(thing.numPages);
    byte[] inMemory = new byte[5];
//    System.out.println(inMemory.length);
//    for (byte an: inMemory){
//      System.out.println(an);
//    }
    byte wr = 1;
    inMemory[2] = wr;
    thing.writePage(inMemory, 0);
    byte[] read = thing.readPage(0);
    for (byte readthis: read){
      System.out.println(readthis);
    }
  }
}
