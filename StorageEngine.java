import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;


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

  public static void main(String[] args) throws IOException {
//    StorageEngine thing = new St  orageEngine("hi");
    StorageEngine thing = new StorageEngine("randomdb");
    System.out.println(thing.MAGICNUM);
  }
}
