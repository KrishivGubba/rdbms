import java.io.IOException;
import java.security.KeyException;
import java.util.Map;

public class QueryParser {

  Map<Integer, String> tableMap;
  private  CatalogManager manager;

  public QueryParser() throws IOException, KeyException {
    StorageEngine engine = new StorageEngine("ration");
    LRU buffer = new LRU(100, engine);
    manager = new CatalogManager(buffer);

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

      default:
        NLPQuery(trimmedQuery);
    }

  }

  private void handleDelete(String trimmedQuery) {

  }

  private void handleCreate(String trimmedQuery) {
  }

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

  private void handleInsert(String trimmedQuery) {

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
    QueryParser thing =  new QueryParser();
    thing.dealQuery("LIST");


    thing.close();
  }

}
