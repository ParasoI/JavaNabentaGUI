package nabenta;

/**
 *
 * @author SAFE
 */
public class Model_Orders {
    
    private long barcode;
    private String name;
    private int quantity;
    private int price;
    private int groceryPrice;

    public Model_Orders(long barcode, String name, int quantity, int price, int groceryPrice) {
        this.barcode = barcode;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.groceryPrice = groceryPrice;
    }
    
    public Model_Orders(long barcode, String name, int quantity, int groceryPrice) {
        this.barcode = barcode;
        this.name = name;
        this.quantity = quantity;
        this.groceryPrice = groceryPrice;
    }

    public long getBarcode() {
        return barcode;
    }

    public void setBarcode(long barcode) {
        this.barcode = barcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getGroceryPrice() {
        return groceryPrice;
    }

    public void setGroceryPrice(int groceryPrice) {
        this.groceryPrice = groceryPrice;
    }
    
    
}
