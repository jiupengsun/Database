/**
 * Database model
 */
public class Item {

  private String name;
  private String item;
  private String transaction_id;
  private double amount;
  private double spent;

  public Item() {}

  public Item(String n, String i, String ti, double a, double s) {
    name = n;
    item = i;
    transaction_id = ti;
    amount = a;
    spent = s;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getItem() {
    return item;
  }

  public void setItem(String item) {
    this.item = item;
  }

  public String getTransaction_id() {
    return transaction_id;
  }

  public void setTransaction_id(String transaction_id) {
    this.transaction_id = transaction_id;
  }

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  public double getSpent() {
    return spent;
  }

  public void setSpent(double spent) {
    this.spent = spent;
  }

  public Item copy() {
    return new Item(this.name, this.item, this.transaction_id, this.amount, this.spent);
  }
}
