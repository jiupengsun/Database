import java.util.List;

/**
 * parse sql statement
 */
public class SqlParser {

  /**
   * parse sql statement to a list of columns, a list of group by conditions
   * and at most one operator
   * @param sql
   * @param columns
   * @param groupConditions
   * @param ops
   */
  public static void parser(String sql, List<String> columns, List<String> groupConditions, List<Operation> ops) {
    sql = sql.toLowerCase();
    if(sql.contains("group by")) {
      //
      String s = sql.substring(sql.indexOf("group by") + 8);
      sql = sql.substring(0, sql.indexOf("group by"));
      String[] cols = s.split("[ ,]+");
      for(String c: cols)
        if(!c.equals(""))
          groupConditions.add(c);
    }
    sql = sql.substring(sql.indexOf("select") + 6);
    String[] cols = sql.split("[ ,]+");
    Operation op = null;
    for(String c: cols) {
      if(c.equals(""))
        continue;
      if(c.contains("(")) {
        // operation
        String s = c.substring(0, c.indexOf("("));
        String cc = c.substring(c.indexOf("(")+1, c.indexOf(")"));
        op = new Operation(Type.valueOf(s.toUpperCase()), cc);
        columns.add(cc);
      } else
        columns.add(c);
    }
    if(op != null)
      ops.add(op);
  }
}
