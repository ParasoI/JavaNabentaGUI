package nabenta;

/**
 *
 * @author SAFE
 */
public class Model_Grocery {
    
    private long barcode;
    private String name;
    private int price;
    private int stocks;
    private String category;
    private String description;

    public Model_Grocery(long barcode, String name, int price, int stocks) {
        this.barcode = barcode;
        this.name = name;
        this.price = price;
        this.stocks = stocks;
    }

    public Model_Grocery(long barcode, String name, int price, int stocks, String category, String description) {
        this.barcode = barcode;
        this.name = name;
        this.price = price;
        this.stocks = stocks;
        this.category = category;
        this.description = description;
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getStocks() {
        return stocks;
    }

    public void setStocks(int stocks) {
        this.stocks = stocks;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    
}
