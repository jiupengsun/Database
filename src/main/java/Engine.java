import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Engine {

  public static void main(String[] args) throws FileNotFoundException {
    String sql = args[0];

    Scanner in = new Scanner(System.in);
    // read column name
    String columnNams = in.nextLine();
    // read column type
    String columnTypes = in.nextLine();
    // construct class
    // read data
    List<Item> rows = new ArrayList<Item>();
    while(in.hasNextLine()) {
      String[] data = in.nextLine().split(",");
      rows.add(new Item(
                         data[0], data[1], data[2], Integer.parseInt(data[3]), Float.parseFloat(data[4])
      ));
    }
    List<String> groupByConditions = new ArrayList<>();
    List<String> columns = new ArrayList<>();
    List<Operation> ops = new ArrayList<>();
    SqlParser.parser(sql, columns, groupByConditions, ops);
    List<Item> results = rows;
    if(!groupByConditions.isEmpty()) {
      // has group condition
      try {
        results = selectWithGroup(rows, ops.get(0), groupByConditions);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    printColumn(results, columns);
  }

  /**
   * Reflection invoke method in object
   * @param i
   * @param name
   * @param type set or get method
   * @param args
   * @return
   */
  private static Object callMethodByName(final Item i, final String name, Class type, Object... args) {
    try {
      String methodName = (type!=null ? "set" : "get") + name.substring(0, 1).toUpperCase() + name.substring(1);
      return type == null ? Item.class.getMethod(methodName).invoke(i) :
               Item.class.getMethod(methodName, type).invoke(i, args);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Recusively add group by condition
   * @param conditions
   * @param index
   * @param ops
   * @param <T>
   * @param <K>
   * @return
   * @throws Exception
   */
  private static <T,K> Collector getCollector(List<String> conditions, int index, Operation ops)
    throws Exception{
    Function func1 = (Function<T, K>) k -> (K)callMethodByName((Item)k, conditions.get(index), null);
    ToDoubleFunction<Object> func2 = v -> (double) callMethodByName((Item)v, ops.column, null);
    if(conditions.size()-1 == index) {
      Collector col = null;
      if(ops != null) {
        switch (ops.type) {
          case SUM:
            col = Collectors.summingDouble(func2);
            break;
          case MAX:
            col = Collectors.maxBy(Comparator.comparingDouble(func2));
            break;
          case MIN:
            col = Collectors.minBy(Comparator.comparingDouble(func2));
            break;
          case COUNT:
            col = Collectors.counting();
            break;
        }
      }
      return col!=null ? Collectors.groupingBy(func1, col) : Collectors.groupingBy(func1);
    } else {
      return Collectors.groupingBy(func1, getCollector(conditions, index+1, ops));
    }
  }

  /**
   * retrieve result according to conditions
   * @param rows
   * @param ops
   * @param conditions
   * @return
   * @throws Exception
   */
  private static List<Item> selectWithGroup(List<Item> rows, Operation ops, List<String> conditions) throws Exception {
    // generate collector
    Map map = (Map)rows.stream()
      .collect(getCollector(conditions, 0, ops));
    List<Item> result = new LinkedList<>();
    helper(map,  0, conditions, result, new Item(), ops);
    return result;
  }

  /**
   * helper function, add group result into list
   * @param map
   * @param index
   * @param conditions
   * @param result
   * @param item
   * @param ops
   */
  private static void helper(Map<String, Object> map, int index, List<String> conditions,
                             List<Item> result, Item item, Operation ops) {
    for(Map.Entry<String, Object> entry: map.entrySet()) {
      if(index == conditions.size() - 1) {
        Item copy = item.copy();
        callMethodByName(copy, conditions.get(index), String.class, entry.getKey());
        if(ops == null) {
          // no aggregate function
          // continue
          result.add(copy);
          continue;
        }
        if(ops.type==Type.MAX || ops.type==Type.MIN) {
          copy = (Item) ((Optional)entry.getValue()).get();
        } else {
          // number
          callMethodByName(copy, ops.column, double.class, entry.getValue());
        }
        result.add(copy);
      } else {
        String key = entry.getKey();
        callMethodByName(item, conditions.get(index), String.class, key);
        Map<String, Object> nextMap = (Map<String, Object>) entry.getValue();
        helper(nextMap, index+1, conditions, result, item, ops);
      }
    }
  }

  /**
   * print result
   * @param rows
   * @param columns
   */
  private static void printColumn(List<Item> rows, final List<String> columns) {
    rows.stream().map(item -> {
      StringBuilder sb = new StringBuilder();
      for(String s: columns) {
        sb.append(callMethodByName(item, s, null));
        sb.append(",");
      }
      sb.deleteCharAt(sb.length() - 1);
      System.out.println(sb.toString());
      return null;
    }).count();
  }
}
