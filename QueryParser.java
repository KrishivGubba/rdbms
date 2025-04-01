import java.io.IOException;
import java.security.KeyException;
import java.util.*;

public class QueryParser {

  Map<String, Integer> ourMap;
  private  CatalogManager manager;


  public QueryParser() throws IOException, KeyException {
    StorageEngine engine = new StorageEngine("trialtesting");
    LRU buffer = new LRU(100, engine);
    manager = new CatalogManager(buffer);
    ourMap = new HashMap<>();
    for (Integer id: manager.tableMap.keySet()){
      ourMap.put(manager.tableMap.get(id).tableName, id);
    }
  }

  public void NLPQuery(String query) throws IOException, KeyException {
    //todo: add langchain logic?
    String prompt =
        """
        You are an expert at decoding natural language into queries for my custom database.
        
        These are the possible commands that a user can make to my database:
        
        PLEASE PAY ATTENTION TO THE SYNTAX, EVERY SPACE AND EVERY COMMA MATTERS
        
        - select:
          format:
            a) select * from <tablename>
            b) select <colname1>,<colname2>,... from <tablename>
          This is used to select data from a specific table.
          Examples:
            - "select * from employees"
            - "select name,salary from employees"
         
        - insert:
          format:
            insert into <tablename> values (<val1>, <val2>, ...)
          Every column within the table needs a corresponding value. There are no default values.
          NOTE: if you are using STRING datatype, you do not have to put quotation marks around the
          input.
          NOTE: for the boolean type, the possibilities are true and false. Case matters.
          Example:
            - "insert into employees values 101, John Doe, Engineering, 75000, 2022-03-15"
        
        - create:
          format:
            create table <tablename> <dtype> <colname>, <dtype> <colname>, ...
          The possible datatypes are BOOLEAN, INT, FLOAT and STRING
          If only column names are provided, make an educated guess about appropriate datatypes.
          Example:
            - "create table employees INT id, STRING name, STRING department, INT salary, STRING hire_date"
        
        - list:
          format:
            list
          This will list all tables in the database.
        
        - show:
          format:
            show <tablename>
          This shows the structure (columns and datatypes) of a specific table.

        Your task is to take natural language input and convert it to the appropriate database command using ONLY the formats specified above. If the request is ambiguous or can't be properly translated using the available commands, then your output must be EXIT.
        
        Always return ONLY the database command without additional explanation or text.
        
        Here is the natural language input from the user:
        
        
        """;
    prompt += query;
    System.out.println(prompt);
    //todo: add langchain logic...
//    dealQuery(query);
  }

  public void dealQuery(String query) throws IOException, KeyException {
    String trimmedQuery = query.trim();

    String firstWord = trimmedQuery.split("\\s+")[0].toLowerCase();

    switch (firstWord) {
      case "select":
        handleSelect(trimmedQuery);
        return;

      case "insert":
        handleInsert(trimmedQuery);
        return;

      case "delete":
        handleDelete(trimmedQuery);
        return;

      case "create":
        handleCreate(trimmedQuery);
        return;

      case "list":
        handleList(trimmedQuery);
        return;

      case "show":
        handleShow(trimmedQuery);
        return;

      default:
        NLPQuery(trimmedQuery);
    }

  }

  private void handleShow(String trimmedQuery) {
    String[] split = trimmedQuery.split(" ");
    int key = ourMap.get(split[1]);
    System.out.println(manager.tableMap.get(key).infoString);
  }

  private void handleDelete(String trimmedQuery) {

  }

  //format: create table <tablename>  <dtype> <colname>, <dtype> <colname> ...
  private void handleCreate(String trimmedQuery) {
    try{
      String[] split = trimmedQuery.split(" ");
      String tableName = split[2];
      int pointer = 3;
      String tableString = "";
      while (pointer < split.length) {
        String datatype = split[pointer];
        String colname = split[pointer + 1].endsWith(",") ?
            split[pointer + 1].substring(0, split[pointer + 1].length() - 1) :
            split[pointer + 1];
        tableString += colname + ":" + datatype.toUpperCase();
        pointer += 2;
        if (pointer < split.length)
          tableString += ",";
      }
      manager.addTable(tableName, tableString);
      System.out.println("Table " + tableName + " succesfully created.");
    }catch (Exception exception){
      System.out.println("Was not able to create table.");
    }
  }

  //expected format: "LIST"
  private void handleList(String trimmedQuery) {
    System.out.println("Tables within this db:");
    System.out.println("\n");
    for (int i = 0; i < manager.tableMap.size(); i++){
      String printable = manager.tableMap.get(i).tableName;
      System.out.println(printable + "-> " + manager.tableMap.get(i).numRows
          + " rows and " + manager.tableMap.get(i).numColumns + " columns.");
      System.out.println("\n");
    }
  }

  //format: insert into <tablename> values <val1>, <val2> ...
  private void handleInsert(String trimmedQuery) {
    try{
      String[] split = trimmedQuery.split(" ");
      String tableName = split[2];
      int tableId = ourMap.get(tableName);

      ArrayList<HashMap<String, String>> columns = manager.tableMap.get(tableId).columns;

      StringBuilder valuesStr = new StringBuilder();
      for (int i = 4; i < split.length; i++) {
        valuesStr.append(split[i]).append(" ");
      }

      String[] valueTokens = valuesStr.toString().split(",");

      HashMap<String, Object> rowValues = new HashMap<>();

      // Populate the HashMap with values
      for (int i = 0; i < columns.size(); i++) {
        String columnName = columns.get(i).get("name");
        String valueStr = valueTokens[i].trim();
        rowValues.put(columnName, valueStr);
      }
      manager.insertRow(tableId, rowValues);
      System.out.println("Successfully inserted row.");
    }catch (Exception e){
      System.out.println(e.getMessage());
      System.out.println("Was not able to insert row.");
    }
  }

  //format: select * from <tablename> where <condition>
  //format: select col1,col2,col3 from <tablename> where <condition>
  private void handleSelect(String trimmedQuery) throws IOException, KeyException {
    try {
      String[] split = trimmedQuery.split(" ");

      // Extract requested columns
      String columnsStr = split[1];
      boolean selectAll = columnsStr.equals("*");

      // Get list of requested columns if not selecting all
      List<String> requestedColumns = new ArrayList<>();
      if (!selectAll) {
        String[] columnNames = columnsStr.split(",");
        for (String col : columnNames) {
          requestedColumns.add(col.trim());
        }
      }

      // Extract table name
      String tableName = split[3];
      int id = ourMap.get(tableName);

      // Get data
      List<Map<String, Object>> output = manager.getAllRows(id);

      if (output.isEmpty()) {
        System.out.println("No rows in this table.");
        return;
      }

      // Get column metadata
      tableItem tableInfo = manager.tableMap.get(id);
      ArrayList<HashMap<String, String>> allColumns = tableInfo.columns;

      // Determine which columns to display
      ArrayList<HashMap<String, String>> columnsToDisplay = new ArrayList<>();
      if (selectAll) {
        columnsToDisplay = allColumns;
      } else {
        // Filter columns based on user request
        for (HashMap<String, String> column : allColumns) {
          if (requestedColumns.contains(column.get("name"))) {
            columnsToDisplay.add(column);
          }
        }
      }

      // Calculate column widths
      Map<String, Integer> columnWidths = new HashMap<>();

      // Initialize with column name lengths
      for (HashMap<String, String> column : columnsToDisplay) {
        String colName = column.get("name");
        columnWidths.put(colName, colName.length());
      }

      // Update widths based on actual data
      for (Map<String, Object> row : output) {
        for (HashMap<String, String> column : columnsToDisplay) {
          String colName = column.get("name");
          Object value = row.get(colName);
          if (value != null) {
            int valueLength = value.toString().length();
            columnWidths.put(colName, Math.max(columnWidths.get(colName), valueLength));
          }
        }
      }

      // Print header
      System.out.println("Table: " + tableInfo.tableName);

      // Print column names
      for (HashMap<String, String> column : columnsToDisplay) {
        String colName = column.get("name");
        int width = columnWidths.get(colName);
        System.out.printf("| %-" + width + "s ", colName);
      }
      System.out.println("|");

      // Print separator
      for (HashMap<String, String> column : columnsToDisplay) {
        String colName = column.get("name");
        int width = columnWidths.get(colName);
        System.out.print("+-");
        for (int i = 0; i < width; i++) {
          System.out.print("-");
        }
        System.out.print("-");
      }
      System.out.println("+");

      // Print rows
      for (Map<String, Object> row : output) {
        for (HashMap<String, String> column : columnsToDisplay) {
          String colName = column.get("name");
          Object value = row.get(colName);
          int width = columnWidths.get(colName);
          System.out.printf("| %-" + width + "s ", value);
        }
        System.out.println("|");
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
      System.out.println("something went wrong while selecting");
    }
  }

  public static void beginLoop() throws IOException, KeyException {
    System.out.println("BadgerDB");
    QueryParser whatnot =  new QueryParser();
    Scanner scanner = new Scanner(System.in);
    whatnot.NLPQuery("pup");
    while (true){
      System.out.print(">> ");
      String input = scanner.nextLine();
      if (input.strip().equals("exit"))
        break;
      whatnot.dealQuery(input);

    }
    whatnot.close();
  }

  public void close() throws IOException {
    manager.close();;
  }

  //TODO: the info string is not being updated.
  // also, the ourmap has to be updated upon table creation
  public static void main(String[] args) throws IOException, KeyException {
//    QueryParser whatnot =  new QueryParser();
//    thing.dealQuery("CREATE table krishiv boolean yes, int age, float nincompoop");
//    whatnot.dealQuery("show krishiv");
//    whatnot.dealQuery("LIST");
//    Map<String, Object> thing = new HashMap<>();
//    thing.put("id",  11111);
//    thing.put("name", "person");
//    thing.put("age", 33);
//    thing.put("salary",  110123);
//    thing.put("active", false);
//    System.out.println(thing);
      QueryParser.beginLoop();
//    whatnot.close();
}

}
