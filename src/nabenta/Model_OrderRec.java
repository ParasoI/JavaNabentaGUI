package nabenta;

public class Model_OrderRec {
    
    private long id;
    private long barcode;
    private String itemName;
    private int quantity;

    public Model_OrderRec(long id, long barcode, String itemName, int quantity) {
        this.id = id;
        this.barcode = barcode;
        this.itemName = itemName;
        this.quantity = quantity;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getBarcode() {
        return barcode;
    }

    public void setBarcode(long barcode) {
        this.barcode = barcode;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    
    
}
