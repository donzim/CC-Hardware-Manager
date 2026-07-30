package zw.co.cchardware.cchardwaremanager;


public class Purchase {

    private int id;
    private String productName;
    private int quantity;
    private double purchasePrice;
    private String supplier;
    private String notes;
    private String purchaseDate;

    public Purchase(int id,
                    String productName,
                    int quantity,
                    double purchasePrice,
                    String supplier,
                    String notes,
                    String purchaseDate) {

        this.id = id;
        this.productName = productName;
        this.quantity = quantity;
        this.purchasePrice = purchasePrice;
        this.supplier = supplier;
        this.notes = notes;
        this.purchaseDate = purchaseDate;
    }

    public int getId() {
        return id;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public String getSupplier() {
        return supplier;
    }

    public String getNotes() {
        return notes;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }
}
