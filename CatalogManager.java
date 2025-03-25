import javax.accessibility.AccessibleAction;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.KeyException;
import java.util.Arrays;

public class CatalogManager {

  //constants
  final int NUM_TABLES_CAPACITY = 4;
  final int SINGLE_TABLE_ROW_CAPACITY = 100;

  private LRU buffer;

  public int numTables;
  public int tableOffset;
  public int versionInfo;

  public CatalogManager(LRU lru) throws IOException, KeyException {
    buffer = lru;

    byte[] adminPage = buffer.get(0);

    ByteBuffer reader = ByteBuffer.wrap(adminPage);

    numTables = reader.getInt();
    tableOffset = reader.getInt();
    versionInfo = reader.getInt();

    buffer.put(0, adminPage);

  }


  //valid input for schema: id:INT,name:STRING,age:INT,salary:FLOAT,active:BOOLEAN
  public int addTable(String tablename, String schema) throws IOException, KeyException {
    if (numTables == NUM_TABLES_CAPACITY)
      throw new IOException("Reached capacity");

    byte[] adminPage = buffer.get(0);
    ByteBuffer writer = ByteBuffer.wrap(adminPage);

    writer.putInt(0, numTables + 1); // increment table count
    writer.putInt(4, 20); // offset

    int start = -1;
    // look for empty slot, yes i KNOW this is terrible code.
    if ((int) writer.get(56) == 0) {
      start = 20;
    } else if ((int) writer.get(235) == 0) {
      start = 199;
    } else if ((int) writer.get(414) == 0) {
      start = 378;
    } else {
      start = 557;
    }

    // table info
    // generate a table id
    writer.putInt(start, numTables + 1);

    // write table name
    byte[] stringToByte = stringToBytes(tablename);
    writer.position(start + 4);
    writer.put(stringToByte);

    // Set active flag (1 byte)
    writer.put(start + 36, (byte) 1);

    // Parse schema string to get column count
    String[] columns = schema.split(",");
    int numCols = columns.length;

    // Write number of columns (4 bytes)
    writer.putInt(start + 37, numCols);

    // Write number of rows - 0 for new table (4 bytes)
    writer.putInt(start + 41, 0);

    // Fill 40 bytes of space for page IDs with -1
    writer.position(start + 45);
    for (int i = 0; i < 10; i++) {
      writer.putInt(-1);  // Each page ID is 4 bytes
    }

    // Write number of pages - 0 for new table (4 bytes)
    writer.putInt(start + 85, 0);

    // Write schema information
    int schemaStart = start + 89;

    // Process each column from the schema string
    for (int i = 0; i < numCols && i < 5; i++) {
      String colDef = columns[i].trim();
      String[] parts = colDef.split(":");
      String colName = parts[0];
      String dataType = parts.length > 1 ? parts[1] : "STRING";

      // Write column name (16 bytes)
      byte[] colNameBytes = stringToBytes(colName);
      int nameLength = Math.min(colNameBytes.length, 16);
      writer.position(schemaStart + (i * 18));
      writer.put(colNameBytes, 0, nameLength);
      // Pad remaining bytes
      for (int j = nameLength; j < 16; j++) {
        writer.put((byte) 0);
      }

      // Write data type (1 byte)
      byte typeCode = 0;
      if (dataType.equalsIgnoreCase("INT")) typeCode = 1;
      else if (dataType.equalsIgnoreCase("FLOAT")) typeCode = 2;
      else if (dataType.equalsIgnoreCase("STRING")) typeCode = 3;
      else if (dataType.equalsIgnoreCase("BOOLEAN")) typeCode = 4;
      writer.put(schemaStart + (i * 18) + 16, typeCode);

      // Write constraints (1 byte) - 0 for no constraints
      writer.put(schemaStart + (i * 18) + 17, (byte) 0);
    }

    // Fill remaining columns with zeros if fewer than 5
    for (int i = numCols; i < 5; i++) {
      writer.position(schemaStart + (i * 18));
      for (int j = 0; j < 18; j++) {
        writer.put((byte) 0);
      }
    }

    // Update the buffer with the modified page
    buffer.put(0, adminPage);

    numTables++;

    return numTables - 1;
  }

  private byte[] stringToBytes(String input) {
    byte[] result = new byte[32]; // Fixed 32-byte array

    if (input != null) {
      byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);

      int copyLength = Math.min(inputBytes.length, 32);
      System.arraycopy(inputBytes, 0, result, 0, copyLength);

    }

    return result;
  }

  private String bytesToString(byte[] bytes) {

    int nullPos = 0;
    while (nullPos < bytes.length && bytes[nullPos] != 0) {
      nullPos++;
    }

    return new String(bytes, 0, nullPos, StandardCharsets.UTF_8);
  }

  private void readTableIDs() throws IOException, KeyException {
    byte[] adminPage = buffer.get(0);

  }


  public String tableInfoGetter(int id) throws IOException, KeyException {
    if (id >= numTables || id < 0)
      throw new IndexOutOfBoundsException("Wrong id");
    int start = 20 + id * 179;
    int stop = start + 179;
    byte[] arr = buffer.get(0);

    byte[] slicedArray = Arrays.copyOfRange(arr, start, stop);

    return tableInfoHelper(slicedArray);
  }

  private String tableInfoHelper(byte[] tableBytes) {
    ByteBuffer buffer = ByteBuffer.wrap(tableBytes);
    StringBuilder result = new StringBuilder();

    int tableId = buffer.getInt(0);

    byte[] nameBytes = new byte[32];
    buffer.position(4);
    buffer.get(nameBytes);
    String tableName = bytesToString(nameBytes);

    byte activeFlag = buffer.get(36);

    int numColumns = buffer.getInt(37);
    int numRows = buffer.getInt(41);

    int[] pageIds = new int[10];
    buffer.position(45);
    for (int i = 0; i < 10; i++) {
      pageIds[i] = buffer.getInt();
    }

    int numPages = buffer.getInt(85);

    result.append("===== TABLE INFORMATION =====\n");
    result.append("Table ID: ").append(tableId).append("\n");
    result.append("Table Name: ").append(tableName).append("\n");
    result.append("Active: ").append(activeFlag == 1 ? "Yes" : "No").append("\n");
    result.append("Number of Columns: ").append(numColumns).append("\n");
    result.append("Number of Rows: ").append(numRows).append("\n");
    result.append("Number of Pages: ").append(numPages).append("\n");

    result.append("\n--- Page IDs ---\n");
    for (int i = 0; i < 10; i++) {
      if (pageIds[i] != -1) {
        result.append("Page ").append(i).append(": ").append(pageIds[i]).append("\n");
      }
    }

    result.append("\n--- Schema Information ---\n");
    result.append("Column Name                Type       Constraints\n");
    result.append("----------------------------------------\n");

    int schemaStart = 89;
    for (int i = 0; i < numColumns; i++) {
      byte[] colNameBytes = new byte[16];
      buffer.position(schemaStart + (i * 18));
      buffer.get(colNameBytes);
      String colName = bytesToString(colNameBytes);

      byte typeCode = buffer.get(schemaStart + (i * 18) + 16);
      String dataType = "UNKNOWN";
      switch (typeCode) {
        case 1:
          dataType = "INT";
          break;
        case 2:
          dataType = "FLOAT";
          break;
        case 3:
          dataType = "STRING";
          break;
        case 4:
          dataType = "BOOLEAN";
          break;
        default:
          dataType = "UNKNOWN";
      }

      byte constraints = buffer.get(schemaStart + (i * 18) + 17);
      String constraintStr = constraints == 0 ? "None" : "Constraint(" + constraints + ")";

      result.append(String.format("%-25s %-10s %s%n", colName, dataType, constraintStr));
    }

    result.append("===== END TABLE INFORMATION =====\n");
    return result.toString();
  }


  public void close() throws IOException {
    buffer.flushAll();
    buffer.engine.close();
  }

  public static void main(String[] args) throws IOException, KeyException {
    StorageEngine engine = new StorageEngine("testing");
    LRU buffer = new LRU(10, engine);
    CatalogManager cat = new CatalogManager(buffer);

//    cat.addTable("random", "id:INT,name:STRING,age:INT,salary:FLOAT,active:BOOLEAN");

    System.out.println(cat.tableInfoGetter(2));

    cat.close();
  }
}
