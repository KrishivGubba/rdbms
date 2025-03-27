import java.io.IOException;
import java.security.KeyException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QueryParser {

  Map<String, Integer> ourMap;
  private  CatalogManager manager;


  public QueryParser() throws IOException, KeyException {
    StorageEngine engine = new StorageEngine("newest");
    LRU buffer = new LRU(100, engine);
    manager = new CatalogManager(buffer);
    ourMap = new HashMap<>();
    for (Integer id: manager.tableMap.keySet()){
      ourMap.put(manager.tableMap.get(id).tableName, id);
    }
  }

  public void NLPQuery(String query){
    //todo: add langchain logic?
    dealQuery(query);
  }

  public void dealQuery(String query){
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
      System.out.println("Was not able to insert row.");
    }
  }

  private void handleSelect(String trimmedQuery) {
  }

  public static void beginLoop(){

    System.out.println("BadgerDB");

    while (true){
      System.out.println("hi");
    }
  }

  public void close() throws IOException {
    manager.close();;
  }

  public static void main(String[] args) throws IOException, KeyException {
    QueryParser whatnot =  new QueryParser();
//    thing.dealQuery("CREATE table krishiv boolean yes, int age, float nincompoop");
    whatnot.dealQuery("LIST");
    whatnot.dealQuery("INSERT into krishiv values true, 10, 10.0");
    whatnot.dealQuery("LIST");
//    Map<String, Object> thing = new HashMap<>();
//    thing.put("id",  11111);
//    thing.put("name", "person");
//    thing.put("age", 33);
//    thing.put("salary",  110123);
//    thing.put("active", false);
//    System.out.println(thing);

    whatnot.close();
}

}
