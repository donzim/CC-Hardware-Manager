package zw.co.cchardware.cchardwaremanager;

public class Sale {

    private String saleDate;
    private String productName;
    private int quantity;
    private double unitPrice;
    private double total;

    public Sale(String saleDate, String productName, int quantity,
                double unitPrice, double total) {

        this.saleDate = saleDate;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.total = total;
    }

    public String getSaleDate() {

        java.time.LocalDateTime dateTime =
                java.time.LocalDateTime.parse(saleDate);

        java.time.format.DateTimeFormatter formatter =
                java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy");

        return dateTime.format(formatter);
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public double getTotal() {
        return total;
    }
}