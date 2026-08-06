package zw.co.cchardware.cchardwaremanager;

public class TopSellingProduct {

    private String productName;
    private int quantitySold;

    public TopSellingProduct(String productName, int quantitySold) {
        this.productName = productName;
        this.quantitySold = quantitySold;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantitySold() {
        return quantitySold;
    }
}