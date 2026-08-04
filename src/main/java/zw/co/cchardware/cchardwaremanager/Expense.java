package zw.co.cchardware.cchardwaremanager;

public class Expense {

    private int id;
    private String expenseName;
    private String category;
    private double amount;
    private String notes;
    private String expenseDate;

    public Expense(int id,
                   String expenseName,
                   String category,
                   double amount,
                   String notes,
                   String expenseDate) {

        this.id = id;
        this.expenseName = expenseName;
        this.category = category;
        this.amount = amount;
        this.notes = notes;
        this.expenseDate = expenseDate;
    }

    public int getId() {
        return id;
    }

    public String getExpenseName() {
        return expenseName;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    public String getNotes() {
        return notes;
    }

    public String getExpenseDate() {
        return expenseDate;
    }
}