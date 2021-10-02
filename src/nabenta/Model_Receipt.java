package nabenta;

/**
 *
 * @author SAFE
 */
public class Model_Receipt {
    
    private long id;
    private String date;
    private double discount;
    private double total;
    private int cashTendered;

    public Model_Receipt(long id, String date, double discount, double total, int cashTendered) {
        this.id = id;
        this.date = date;
        this.discount = discount;
        this.total = total;
        this.cashTendered = cashTendered;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public int getCashTendered() {
        return cashTendered;
    }

    public void setCashTendered(int cashTendered) {
        this.cashTendered = cashTendered;
    }
    
    
    
}
