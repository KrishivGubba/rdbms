import javax.accessibility.AccessibleAction;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyException;
import java.util.*;

public class CatalogManager {

  //constants
  final int NUM_TABLES_CAPACITY = 4;
  final int SINGLE_TABLE_ROW_CAPACITY = 100;

  private LRU buffer;

  public int numTables;
  public int tableOffset;
  public int versionInfo;
  private Map<Integer, tableItem> tableMap;

  public CatalogManager(LRU lru) throws IOException, KeyException {
    buffer = lru;

    byte[] adminPage = buffer.get(0);

    ByteBuffer reader = ByteBuffer.wrap(adminPage);

    numTables = reader.getInt();
    tableOffset = reader.getInt();
    versionInfo = reader.getInt();
    tableMap = new HashMap<>();
    for (int i = 0; i < numTables; i++)
      tableMap.put(i, tableInfoGetter(i));

    reader.position(65);
    System.out.println(reader.getInt());
    System.out.println(reader.getInt());
    System.out.println(reader.getInt());
//    System.out.println("these are the ids");
//    System.out.println(tableMap.keySet());

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
    writer.putInt(start, numTables);
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


  public tableItem tableInfoGetter(int id) throws IOException, KeyException {
    if (id >= numTables || id < 0)
      throw new IndexOutOfBoundsException("Wrong id");
    int start = 20 + id * 179;
    int stop = start + 179;
    byte[] arr = buffer.get(0);

    byte[] slicedArray = Arrays.copyOfRange(arr, start, stop);

    return tableInfoHelper(slicedArray);
  }

  private tableItem tableInfoHelper(byte[] tableBytes) {
    ByteBuffer buffer = ByteBuffer.wrap(tableBytes);
    StringBuilder result = new StringBuilder();
    HashMap<String, Object> tableInfo = new HashMap<>();
    tableItem table = new tableItem();
    int tableId = buffer.getInt(0);
    tableInfo.put("tableId", tableId);
    table.tableId = tableId;
    byte[] nameBytes = new byte[32];
    buffer.position(4);
    buffer.get(nameBytes);
    String tableName = bytesToString(nameBytes);
    tableInfo.put("tableName", tableName);
    table.tableName = tableName;

    byte activeFlag = buffer.get(36);
    tableInfo.put("active", activeFlag == 1);
    table.active = activeFlag == 1;
    int numColumns = buffer.getInt(37);
    tableInfo.put("numColumns", numColumns);
    table.numColumns = numColumns;

    int numRows = buffer.getInt(41);
    tableInfo.put("numRows", numRows);
    table.numRows = numRows;

    int[] pageIds = new int[10];
    buffer.position(45);
    for (int i = 0; i < 10; i++) {
      pageIds[i] = buffer.getInt();
    }
    tableInfo.put("pageIds", pageIds);
    table.pageIds = pageIds;

    int numPages = buffer.getInt(85);
    tableInfo.put("numPages", numPages);
    table.numPages = numPages;

    ArrayList<HashMap<String, String>> columns = new ArrayList<>();
    int schemaStart = 89;
    for (int i = 0; i < numColumns; i++) {
      HashMap<String, String> column = new HashMap<>();

      byte[] colNameBytes = new byte[16];
      buffer.position(schemaStart + (i * 18));
      buffer.get(colNameBytes);
      String colName = bytesToString(colNameBytes);
      column.put("name", colName);

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
      column.put("type", dataType);
      column.put("typeCode", Integer.toString(typeCode));

//      byte constraints = buffer.get(schemaStart + (i * 18) + 17); TODO: fix this later, we're ignoring contstraints fornow
      column.put("constraints", "noconstraints");

      columns.add(column);
    }
    tableInfo.put("columns", columns);
    table.columns = columns;
    // Build the string representation as before
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

    for (HashMap<String, String> column : columns) {
      String colName = (String) column.get("name");
      String dataType = (String) column.get("type");
      String constraints = column.get("constraints");
      String constraintStr = true ? "None" : "Constraint(" + constraints + ")";

      result.append(String.format("%-25s %-10s %s%n", colName, dataType, constraintStr));
    }

    result.append("===== END TABLE INFORMATION =====\n");

    tableInfo.put("infoString", result.toString());
    table.infoString = result.toString();
    return table;
  }

//  [numPages, pageIds, infoString, numRows, columns, tableId, active, numColumns, tableName]
  public void insertRow(int tableId, Map<String, Object> values) throws KeyException, IOException {
    if (!tableMap.containsKey(tableId))
      throw new InvalidKeyException("Invalid table id");

    //make checks first
    tableItem tableInfo = tableMap.get(tableId);
    if (values.size() != tableInfo.numColumns)
      throw new InvalidObjectException("Wrong number of values passed in");

    //match cols
    for (HashMap<String, String> col : tableInfo.columns)
      if (!values.containsKey(col.get("name")))
        throw new InvalidKeyException("You did not pass in all the column values");

    System.out.println(tableInfo.pageIds[0]);
    int latestPage = -1;
    int latestIdx = -1;
    for (int i = 0 ; i < tableInfo.pageIds.length; i++){
      if (tableInfo.pageIds[i] != -1){
        latestPage = tableInfo.pageIds[i];
        latestIdx = i;
      }
    }
    //check the -1th page id
    if (tableInfo.pageIds[0]==-1 || !canAccommodate(tableInfo.pageIds[latestIdx], values, tableInfo.columns)) {
      // Allocate a new page
      int newPage = buffer.engine.allocatePage();
      byte[] page = buffer.get(newPage);
      byte[] adminPage = buffer.get(0);

      // Find the table in the admin page by table id
      int tableOffset = -1;
      for (int i = 0; i < NUM_TABLES_CAPACITY; i++) {
        int offset = 20 + i * 179;
        ByteBuffer reader = ByteBuffer.wrap(adminPage);
        int currId = reader.getInt(offset);
        if (currId == tableId) {
          tableOffset = offset;
          break;
        }
      }

      if (tableOffset == -1) {
        throw new InvalidKeyException("Table ID not found in admin page");
      }

      // Increment number of rows counter
      ByteBuffer adminBuffer = ByteBuffer.wrap(adminPage);
      int currentRows = adminBuffer.getInt(tableOffset + 41);
      adminBuffer.putInt(tableOffset + 41, currentRows + 1);

      // Increment number of pages counter
      int currentPages = adminBuffer.getInt(tableOffset + 85);
      adminBuffer.putInt(tableOffset + 85, currentPages + 1);

      // Add the page id to the page id array in the admin page
      // The page IDs start at offset 45
      adminBuffer.putInt(tableOffset + 45 + (currentPages * 4), newPage);

      // Update table info in memory
      tableInfo.pageIds[tableInfo.numPages] = newPage;
      tableInfo.numPages += 1;
      tableInfo.numRows += 1;


      // Initialize new page as a data page
      ByteBuffer pageBuffer = ByteBuffer.wrap(page);

      // Set number of records to 1
      pageBuffer.putInt(0, 1);

      // Set free space pointer to 16 (after record count, free space ptr, and first slot entry)
      pageBuffer.putInt(4, 16);

      // Convert values to byte array
      byte[] recordData = recordToBytes(values, tableInfo.columns);
      int recordLength = recordData.length;

      // Calculate offset for record (at end of page)
      int recordOffset = 4096 - recordLength;

      // Write the record at the calculated offset
      pageBuffer.position(recordOffset);
      pageBuffer.put(recordData);

      // Create slot entry (first slot)
      // Offset to record
      pageBuffer.putInt(8, recordOffset);
      // Length of record
      pageBuffer.putInt(12, recordLength);

      // Write pages back to buffer
      ByteBuffer checkme = ByteBuffer.wrap(adminPage);
      checkme.position(tableOffset + 41);
      System.out.println("time");
      System.out.println(tableOffset + 41);
      ByteBuffer watch = ByteBuffer.wrap(adminPage);
      watch.position(tableOffset + 41);
      System.out.println(watch.getInt());
      buffer.put(newPage, page);
      buffer.put(0, adminPage);

      adminBuffer.position(41 + tableOffset);
      int ro = adminBuffer.getInt();
      System.out.println(tableOffset);
      System.out.println("here");
      System.out.println(ro);
    } else {
      // Code for adding to existing page will go here
      byte[] adminPage = buffer.get(0);

      // Find the table in the admin page by table id
      int tableOffset = -1;
      for (int i = 0; i < NUM_TABLES_CAPACITY; i++) {
        int offset = 20 + i * 179;
        ByteBuffer reader = ByteBuffer.wrap(adminPage);
        int currId = reader.getInt(offset);
        if (currId == tableId) {
          tableOffset = offset;
          break;
        }
      }

      if (tableOffset == -1) {
        throw new InvalidKeyException("Table ID not found in admin page");
      }

      // Increment number of rows counter in admin page
      ByteBuffer adminBuffer = ByteBuffer.wrap(adminPage);
      int currentRows = adminBuffer.getInt(tableOffset + 41);
      adminBuffer.putInt(tableOffset + 41, currentRows + 1);

      // Update table info in memory - rows only, not pages since we're using existing page
      tableInfo.numRows += 1;

      // Get the existing page
      byte[] page = buffer.get(latestPage);
      ByteBuffer pageBuffer = ByteBuffer.wrap(page);

      // Get current record count and increment
      int recordCount = pageBuffer.getInt(0);
      pageBuffer.putInt(0, recordCount + 1);

      // Get the current free space pointer
      int freeSpacePtr = pageBuffer.getInt(4);

      // Convert values to byte array
      byte[] recordData = recordToBytes(values, tableInfo.columns);
      int recordLength = recordData.length;

      // Calculate offset for new record (at end of page if first record, or before previous record)
      int oldestRecordOffset = 4096;
      for (int i = 0; i < recordCount; i++) {
        int slotOffset = 8 + (i * 8);
        int recOffset = pageBuffer.getInt(slotOffset);
        if (recOffset < oldestRecordOffset) {
          oldestRecordOffset = recOffset;
        }
      }
      int recordOffset = oldestRecordOffset - recordLength;

      // Check if we have enough space
      if (freeSpacePtr > recordOffset) {
        throw new IOException("Page is full, cannot add more records");
      }

      // Write the record at the calculated offset
      pageBuffer.position(recordOffset);
      pageBuffer.put(recordData);

      // Add slot entry for the new record
      pageBuffer.putInt(freeSpacePtr, recordOffset);     // Offset to record
      pageBuffer.putInt(freeSpacePtr + 4, recordLength); // Length of record

      // Update free space pointer to include the new slot entry
      pageBuffer.putInt(4, freeSpacePtr + 8);

      // Write pages back to buffer
      buffer.put(latestPage, page);
      buffer.put(0, adminPage);

    }


    System.out.println(tableInfo.columns);
    System.out.println(tableMap.get(tableId).numRows);

  }

  private byte[] recordToBytes(Map<String, Object> values, ArrayList<HashMap<String, String>> columns) {
    // Initialize a ByteArrayOutputStream to build our record
    ByteArrayOutputStream recordStream = new ByteArrayOutputStream();
    DataOutputStream dataStream = new DataOutputStream(recordStream);

    try {
      // Write record header (status byte - 1 means active)
      dataStream.writeByte(1);

      // Write each field according to its type
      for (HashMap<String, String> column : columns) {
        String colName = column.get("name");
        String type = column.get("type");
        Object value = values.get(colName);

        switch (type) {
          case "INT":
            if (value instanceof Integer) {
              dataStream.writeInt((Integer) value);
            } else {
              dataStream.writeInt(Integer.parseInt(value.toString()));
            }
            break;

          case "FLOAT":
            if (value instanceof Float) {
              dataStream.writeFloat((Float) value);
            } else if (value instanceof Double) {
              dataStream.writeFloat(((Double) value).floatValue());
            } else {
              dataStream.writeFloat(Float.parseFloat(value.toString()));
            }
            break;

          case "BOOLEAN":
            if (value instanceof Boolean) {
              dataStream.writeBoolean((Boolean) value);
            } else {
              dataStream.writeBoolean(Boolean.parseBoolean(value.toString()));
            }
            break;

          case "STRING":
          default:
            // For strings, write length prefix followed by UTF-8 bytes
            byte[] strBytes = value.toString().getBytes(StandardCharsets.UTF_8);
            dataStream.writeInt(strBytes.length);
            dataStream.write(strBytes);
            break;
        }
      }

      return recordStream.toByteArray();
    } catch (IOException e) {
      // This shouldn't happen with ByteArrayOutputStream
      throw new RuntimeException("Error serializing record", e);
    }
  }

  private boolean canAccommodate(int pageId, Map<String, Object> values, ArrayList<HashMap<String, String>> columnInfo) throws IOException, KeyException {
    // pull the page
    byte[] page = buffer.get(pageId);
    ByteBuffer pageBuffer = ByteBuffer.wrap(page);

    int recordCount = pageBuffer.getInt(0);
    int freeSpacePtr = pageBuffer.getInt(4);

    //  size of the new record
    byte[] potentialRecord = recordToBytes(values, columnInfo);
    int recordSize = potentialRecord.length;

    // Calculate size needed for slot entry (8 bytes)
    int slotSize = 8;

    // Find the start of record data (end of page - size of first record)
    int oldestRecordOffset = 4096;
    for (int i = 0; i < recordCount; i++) {
      int slotOffset = 8 + (i * 8);
      int recOffset = pageBuffer.getInt(slotOffset);
      if (recOffset < oldestRecordOffset) {
        oldestRecordOffset = recOffset;
      }
    }

    // Calculate where the new record would go
    int newRecordOffset = oldestRecordOffset - recordSize;

    // Check if there's enough space between free space pointer and record data
    int newFreeSpacePtr = freeSpacePtr + slotSize;

    return newFreeSpacePtr <= newRecordOffset;
  }


  //TODO: this is not a robust method and ONLY works for the table structure given in the main method
  // do NOT use for prod purposes.
  public void printRows(int pageId) throws IOException, KeyException {
    byte[] page = buffer.get(pageId);
    ByteBuffer buf = ByteBuffer.wrap(page);

    int recordCount = buf.getInt(0);
    System.out.println("Page " + pageId + " has " + recordCount + " records");

    for (int i = 0; i < recordCount; i++) {
      int slotOffset = 8 + (i * 8);
      int recordOffset = buf.getInt(slotOffset);
      int recordLength = buf.getInt(slotOffset + 4);

      System.out.println("Record #" + i + " (offset=" + recordOffset + ", length=" + recordLength + ")");

      byte[] recordData = new byte[recordLength];
      System.arraycopy(page, recordOffset, recordData, 0, recordLength);
      ByteBuffer reader = ByteBuffer.wrap(recordData);

      // Skip status byte
      reader.get();

      // Read id (INT)
      int id = reader.getInt();
      System.out.println("id: " + id);

      // Read name (STRING)
      int nameLength = reader.getInt();
      byte[] nameBytes = new byte[nameLength];
      reader.get(nameBytes);
      String name = new String(nameBytes, StandardCharsets.UTF_8);
      System.out.println("name: \"" + name + "\"");

      // Read age (INT)
      int age = reader.getInt();
      System.out.println("age: " + age);

      // Read salary (assuming FLOAT based on your code)
      float salary = reader.getFloat();
      System.out.println("salary: " + salary);

      // Read active (BOOLEAN)
      boolean active = reader.get() != 0;
      System.out.println("active: " + active);

      System.out.println("\n");
    }
  }

  public void close() throws IOException {
    buffer.flushAll();
    buffer.engine.close();
  }

  public static void main(String[] args) throws IOException, KeyException {
//    StorageEngine engine = new StorageEngine("ration");
//    LRU buffer = new LRU(10, engine);
//    CatalogManager cat = new CatalogManager(buffer);
//    cat.addTable("random", "id:INT,name:STRING,age:INT,salary:FLOAT,active:BOOLEAN");
//    Map<String, Object> thing = new HashMap<>();
//    thing.put("id",  11111);
//    thing.put("name", "person");
//    thing.put("age", 33);
//    thing.put("salary",  110123);
//    thing.put("active", false);
//    System.out.println(cat.tableInfoGetter(1).keySet());
//    cat.insertRow(1, thing);
//    System.out.println("hi");
//    cat.printRows(2);
//    System.out.println(cat.tableMap.get(0).numRows);
//    for (int wat : cat.tableMap.get(0).pageIds)
//      System.out.println(wat);
//    cat.close();
  }
}
