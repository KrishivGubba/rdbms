import java.io.*;
import java.nio.ByteBuffer;


public class StorageEngine {

  private final String EXTENSION = ".db";
  private final int MAGICNUM = 0xDB0023FF;
  private final int FREE_PAGE_CAPACITY = 10;

  private final int NUM_ADMIN_PAGES = 1;
  private final int METADATA_CAPACITY = 60;

  private String filename;
  private RandomAccessFile fileObject;

  private boolean isValid = true;
  private int numPages;
  private int pageSize;
  private int numFreePages;

  //important indices
  private final int MAGIC_NUM_IDX = 0;
  private final int VERSION_IDX = 4;
  private final int NUM_PAGES_IDX = 8;
  private final int PAGE_SIZE_IDX = 12;
  private final int NUM_FREE_PAGES_IDX = 16;
  private final int FREE_PAGE_ARR_IDX = 20;

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
        throw new IOException("File corrupted and/or not of BadgerDB type.");
      }
    }else{ //here you're ADDING metadata (file's already been created for you)
      boolean success = addMetaData();
      //now add the admin pages
      for (int i = 0; i < NUM_ADMIN_PAGES; i++){
        allocatePage();
      }
      fillAdmin();
      if (!success)
        throw new IOException("Was not able to load metadata.");
      System.out.println("Metadata exists");
    }
  }

  private void fillAdmin() throws IOException {
    byte[] adminPage = readPage(0);
    ByteBuffer buffer = ByteBuffer.wrap(adminPage);
    buffer.putInt(0, 0); //number of tables
    buffer.putInt(4, -1); // offset for table ID
    buffer.putInt(8, 1); // version info
    buffer.putInt(12, 0); //empty space

    writePage(adminPage, 0);
  }

  public boolean readMetaData() throws IOException {
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

    this.numFreePages = this.fileObject.readInt();

    return true;
  }

  public boolean addMetaData() throws IOException {
    try{
      this.fileObject.seek(0);

      this.fileObject.writeInt(MAGICNUM);

      this.fileObject.writeInt(1); //version

      this.fileObject.writeInt(0); //number of pages

      this.fileObject.writeInt(4096); //pagesize

      this.fileObject.writeInt(0); //number of free pages
      byte[] arr = new byte[40];
      for (int i = 0; i < 40; i++){
        arr[i] = -1;
      }
      fileObject.write(arr); // allocate null for the free pages

      this.pageSize = 4096;
      this.numPages = 0;

    }catch (Exception e){
      return false; //TODO: make this more descriptive
    }
    return true;
  }

  public int allocatePage() throws IOException {
    if (!isValid)
      throw new InvalidObjectException("This file is not a valid type.");
    //go to read the number of pages and increment
    this.fileObject.seek(NUM_PAGES_IDX);
    this.fileObject.writeInt(this.numPages + 1);

    if (numFreePages > 0) {

      // Look through the array for a valid free page ID
      for (int i = 0; i < 10; i++) {  // Assuming array size of 10
        this.fileObject.seek(FREE_PAGE_ARR_IDX + (i * 4));
        int pageId = fileObject.readInt();

        if (pageId != -1) {
          // Found a valid free page
          // Mark this slot as used by writing -1
          fileObject.seek(FREE_PAGE_ARR_IDX + (i * 4));
          fileObject.writeInt(-1);

          // Decrement the count of free pages
          numFreePages -= 1;

          // Update the free page count in the file
          fileObject.seek(NUM_FREE_PAGES_IDX);  // Assuming free page count is at offset 16
          fileObject.writeInt(numFreePages);

          return pageId;  // Return the free page ID
        }
      }
      // should not get to this at all.
      System.err.println("Warning: Free page count positive but no free pages found");
    }
    else {
      this.numPages += 1;
      int offset = getOffset(numPages - 1);
      this.fileObject.seek(offset);
      //write placeholder data
      byte[] buffer = new byte[this.pageSize];
      this.fileObject.write(buffer);
      //return index of new page
      return this.numPages - 1;
    }
    return 1;
  }

  private int getOffset(int id){
    int x = METADATA_CAPACITY + id*pageSize;
    System.out.println(x);
    return METADATA_CAPACITY + id*this.pageSize; //hardcode header size here.
  }

  public byte[] readPage(int id) throws IndexOutOfBoundsException, IOException {
    if (!isValid)
      throw new InvalidObjectException("This file is not a valid type.");
    if (id >= this.numPages || id < 0){
      throw new IndexOutOfBoundsException("Index goes beyond the number of pages");
    }
    int offset = getOffset(id);
    byte[] readMem = new byte[this.pageSize];
    this.fileObject.seek(offset);
    int output  = this.fileObject.read(readMem);
    return readMem;
  }

  public void writePage(byte[] toDisk) throws IOException {
    if (!this.isValid)
      throw new InvalidObjectException("This is not a valid file type");
    writePage(toDisk, this.numPages - 1); //appends to the end
  }


  /**
   * WARNING: this OVERWRITES any existing data.
   * @param toDisk: bytes arr that should be written to disk from memory
   * @param id
   * @return
   */
  public void writePage(byte[] toDisk, int id) throws IOException {
    //check to see len of arr matches pagesize
    if (!isValid){
      throw new InvalidObjectException("This is not a valid file type");
    }
    if (toDisk.length > this.pageSize)
      throw new IndexOutOfBoundsException("The bytes array is too large to be written");
    if (id >= this.numPages || id < 0){
      throw new IndexOutOfBoundsException("Index goes beyond the number of pages");
    }
    int offset = getOffset(id);
    if (toDisk.length < this.pageSize){
      byte[] rightSize = new byte[this.pageSize];
      System.arraycopy(toDisk, 0, rightSize, 0, toDisk.length);
      toDisk = rightSize;
    }
    this.fileObject.seek(offset);
    ByteBuffer checker = ByteBuffer.wrap(toDisk);
    checker.position(61);
    System.out.println("this that tint");
    System.out.println(checker.getInt());
    this.fileObject.write(toDisk);
  }

  private void freePage(int id) throws IOException {
    if (id >= numPages || id < 0)
      throw new IndexOutOfBoundsException("Index goes beyond the number of pages");
    // Check if this page is already in the free list
    if (checkFreed(id)){
      throw new IllegalStateException("Page " + id + " is already in the free list");
    }
    if (numFreePages == FREE_PAGE_CAPACITY){ // fixed capacity for now, could cause bloating
      System.err.println(
          "Warning: Free page array is full. Page " + id + " not added to free list.");
      return;
    }

    fileObject.seek(FREE_PAGE_ARR_IDX);

    for (int i = 0; i < 10; i++){
      int index = FREE_PAGE_ARR_IDX + i*4;
      fileObject.seek(index);
      int freeIdx = fileObject.readInt();
      if (freeIdx==-1){
        fileObject.seek(index);
        fileObject.writeInt(id);
        numFreePages += 1;
        fileObject.seek(NUM_FREE_PAGES_IDX);
        fileObject.writeInt(numFreePages);
        numPages -= 1;
        fileObject.seek(NUM_PAGES_IDX);
        fileObject.writeInt(numPages);
        return;
      }
    }
  }

  public void close() throws IOException {
    if (fileObject != null) {
      fileObject.close();
      fileObject = null;
    }
  }

  private boolean checkFreed(int id) throws IOException {
    int freePageArrayStart = FREE_PAGE_ARR_IDX;
    for (int i = 0; i < FREE_PAGE_CAPACITY; i++) {
      fileObject.seek(freePageArrayStart + (i * 4));
      int pageId = fileObject.readInt();
      if (pageId == id) {
        return true;
      }
    }
    return false;
  }

  public static void main(String[] args) throws IOException {

    StorageEngine thing = new StorageEngine("newdb");

//    thing.freePage(1);

    System.out.println(thing.numPages);
    System.out.println(thing.numFreePages);

    thing.readPage(1);
    thing.close();
  }
}
