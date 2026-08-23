package zw.co.cchardware.cchardwaremanager;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

public class EditExpenseController {

    @FXML
    private TextField expenseNameField;

    @FXML
    private TextField categoryField;

    @FXML
    private TextField amountField;

    @FXML
    private TextArea notesArea;

    @FXML
    private Button updateExpenseButton;

    private Expense expense;

    private ExpensesController expensesController;

    public void setExpense(Expense expense) {

        this.expense = expense;

        expenseNameField.setText(
                expense.getExpenseName());

        categoryField.setText(
                expense.getCategory());

        amountField.setText(
                String.valueOf(expense.getAmount()));

        notesArea.setText(
                expense.getNotes());
    }

    public void setExpensesController(
            ExpensesController expensesController) {

        this.expensesController = expensesController;
    }

    @FXML
    private void updateExpense(ActionEvent event) {

        String expenseName =
                expenseNameField.getText().trim();

        String category =
                categoryField.getText().trim();

        String notes =
                notesArea.getText().trim();

        double amount;

        try {

            amount = Double.parseDouble(
                    amountField.getText().trim());

        } catch (NumberFormatException e) {

            Alert alert =
                    new Alert(Alert.AlertType.ERROR);

            alert.setTitle("Invalid Amount");
            alert.setHeaderText(null);
            alert.setContentText(
                    "Please enter a valid amount.");

            alert.showAndWait();

            return;
        }

        expense.setExpenseName(expenseName);
        expense.setCategory(category);
        expense.setAmount(amount);
        expense.setNotes(notes);

        DatabaseConnection.updateExpense(expense);

        expensesController.loadExpenses();

        Stage stage =
                (Stage) updateExpenseButton
                        .getScene()
                        .getWindow();

        stage.close();
    }
}