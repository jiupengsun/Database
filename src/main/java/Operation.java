/**
 * Operation class for aggregate function
 */
public class Operation {
  Type type;
  String column;

  Operation(Type t, String c) {
    type = t;
    column = c;
  }
}

enum Type {
  NONE, SUM, MAX, MIN, COUNT
}
