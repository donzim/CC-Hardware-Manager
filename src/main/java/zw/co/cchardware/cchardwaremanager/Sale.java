package zw.co.cchardware.cchardwaremanager;


public class Sale {
    private int id;
    private String saleDate;
    private String productName;
    private int quantity;
    private double unitPrice;
    private double total;

    public Sale(int id, String saleDate, String productName, int quantity,
                double unitPrice, double total) {
        this.id = id;
        this.saleDate = saleDate;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.total = total;
    }

    public String getSaleDate() {

        try {

            java.time.LocalDateTime dateTime =
                    java.time.LocalDateTime.parse(saleDate);

            return dateTime.format(
                    java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy"));

        } catch (Exception e) {

            try {

                java.time.LocalDate date =
                        java.time.LocalDate.parse(saleDate);

                return date.format(
                        java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy"));

            } catch (Exception ex) {

                return saleDate;
            }
        }
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

    public double getUnitPrice() {
        return unitPrice;
    }

    public double getTotal() {
        return total;
    }
}