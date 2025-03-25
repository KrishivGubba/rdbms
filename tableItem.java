
//convenience class


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//  [numPages, pageIds, infoString, numRows, columns, tableId, active, numColumns, tableName]
public class tableItem {
  public int numPages;
  public int[] pageIds;
  public String infoString;
  public int numRows;
  public ArrayList<HashMap<String, String>> columns;
  public int tableId;
  public boolean active;
  public int numColumns;
  public String tableName;

}
