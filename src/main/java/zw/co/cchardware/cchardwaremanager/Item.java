package zw.co.cchardware.cchardwaremanager;

public class Item {

    private int id;
    private String name;
    private String category;
    private double purchasePrice;  //don
    private double sellingPrice;
    private int quantity;

    public Item(int id, String name, String category,
                double purchasePrice, double sellingPrice, int quantity) {

        this.id = id;
        this.name = name;
        this.category = category;
        this.purchasePrice = purchasePrice;
        this.sellingPrice = sellingPrice;
        this.quantity = quantity;
    }

    public Item(String name, String category,
                double purchasePrice, double sellingPrice, int quantity) {

        this.name = name;
        this.category = category;
        this.purchasePrice = purchasePrice;
        this.sellingPrice = sellingPrice;
        this.quantity = quantity;
    }
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getCategory() {
        return category;
    }
    public double getPurchasePrice() {
        return purchasePrice;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

     public void setPurchasePrice(double purchasePrice) {
    this.purchasePrice = purchasePrice;
     }

    public void setSellingPrice(double sellingPrice) {
    this.sellingPrice = sellingPrice;
    }


    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
