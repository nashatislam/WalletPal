package app.walletpal;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;

public class BudgetController {

    // Income Tab
    @FXML private TextField incomeSourceField;
    @FXML private TextField incomeAmountField;
    @FXML private Button addIncomeButton;
    @FXML private TableView<Income> incomeTable;
    @FXML private TableColumn<Income, String> incomeSourceCol;
    @FXML private TableColumn<Income, Double> incomeAmountCol;

    // Expense Tab
    @FXML private TextField expenseCategoryField;
    @FXML private TextField expenseLimitField;
    @FXML private Button addExpenseButton;
    @FXML private TableView<Expense> expenseTable;
    @FXML private TableColumn<Expense, String> expenseCategoryCol;
    @FXML private TableColumn<Expense, Double> expenseLimitCol;
    @FXML private TableColumn<Expense, Double> expenseSpentCol;
    @FXML private TableColumn<Expense, Double> expenseRemainingCol;

    // Savings Tab
    @FXML private TextField savingsCategoryField;
    @FXML private TextField savingsGoalField;
    @FXML private Button addSavingsButton;
    @FXML private TableView<Savings> savingsTable;
    @FXML private TableColumn<Savings, String> savingsCategoryCol;
    @FXML private TableColumn<Savings, Double> savingsGoalCol;
    @FXML private TableColumn<Savings, Double> savingsSavedCol;
    @FXML private TableColumn<Savings, Double> savingsMoreToGoCol;

    private final ObservableList<Income> incomes = FXCollections.observableArrayList();
    private final ObservableList<Expense> expenses = FXCollections.observableArrayList();
    private final ObservableList<Savings> savings = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupIncomeTable();
        setupExpenseTable();
        setupSavingsTable();
    }

    private void setupIncomeTable() {
        incomeSourceCol.setCellValueFactory(data -> data.getValue().sourceProperty());
        incomeAmountCol.setCellValueFactory(data -> data.getValue().amountProperty().asObject());

        incomeSourceCol.setCellFactory(TextFieldTableCell.forTableColumn());
        incomeAmountCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));

        incomeTable.setItems(incomes);

        addIncomeButton.setOnAction(e -> addIncome());

        incomeTable.setRowFactory(tv -> {
            TableRow<Income> row = new TableRow<>();
            ContextMenu menu = new ContextMenu();
            MenuItem delete = new MenuItem("Delete");
            delete.setOnAction(event -> incomes.remove(row.getItem()));
            menu.getItems().add(delete);
            row.setContextMenu(menu);
            return row;
        });
    }

    private void addIncome() {
        if (incomes.size() >= 5) { showAlert("Income limit reached (5 max)"); return; }
        String source = incomeSourceField.getText().trim();
        double amount;
        try { amount = Double.parseDouble(incomeAmountField.getText().trim()); }
        catch (Exception e) { showAlert("Invalid amount!"); return; }
        incomes.add(new Income(source, amount));
        incomeSourceField.clear(); incomeAmountField.clear();
    }

    private void setupExpenseTable() {
        expenseCategoryCol.setCellValueFactory(data -> data.getValue().categoryProperty());
        expenseLimitCol.setCellValueFactory(data -> data.getValue().limitProperty().asObject());
        expenseSpentCol.setCellValueFactory(data -> data.getValue().spentProperty().asObject());
        expenseRemainingCol.setCellValueFactory(data -> data.getValue().remainingProperty().asObject());

        expenseCategoryCol.setCellFactory(TextFieldTableCell.forTableColumn());
        expenseLimitCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));

        expenseTable.setItems(expenses);

        addExpenseButton.setOnAction(e -> addExpense());

        expenseTable.setRowFactory(tv -> {
            TableRow<Expense> row = new TableRow<>();
            ContextMenu menu = new ContextMenu();
            MenuItem delete = new MenuItem("Delete");
            delete.setOnAction(event -> expenses.remove(row.getItem()));
            MenuItem spend = new MenuItem("Add Spent");
            spend.setOnAction(event -> addSpent(row.getItem()));
            menu.getItems().addAll(spend, delete);
            row.setContextMenu(menu);
            return row;
        });
    }

    private void addExpense() {
        if (expenses.size() >= 10) { showAlert("Expense limit reached (10 max)"); return; }
        String category = expenseCategoryField.getText().trim();
        double limit;
        try { limit = Double.parseDouble(expenseLimitField.getText().trim()); }
        catch (Exception e) { showAlert("Invalid limit!"); return; }
        expenses.add(new Expense(category, limit));
        expenseCategoryField.clear(); expenseLimitField.clear();
    }

    private void addSpent(Expense exp) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Spent");
        dialog.setHeaderText("Enter amount spent for " + exp.getCategory());
        dialog.setContentText("Amount:");
        dialog.showAndWait().ifPresent(input -> {
            try {
                double amt = Double.parseDouble(input);
                if (amt < 0 || amt > exp.getRemaining()) {
                    showAlert("Invalid amount!");
                    return;
                }
                exp.addSpent(amt);
            } catch (Exception e) {
                showAlert("Invalid input!");
            }
        });
    }

    private void setupSavingsTable() {
        savingsCategoryCol.setCellValueFactory(data -> data.getValue().categoryProperty());
        savingsGoalCol.setCellValueFactory(data -> data.getValue().goalProperty().asObject());
        savingsSavedCol.setCellValueFactory(data -> data.getValue().savedProperty().asObject());
        savingsMoreToGoCol.setCellValueFactory(data -> data.getValue().moreToGoProperty().asObject());

        savingsCategoryCol.setCellFactory(TextFieldTableCell.forTableColumn());
        savingsGoalCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));

        savingsTable.setItems(savings);

        addSavingsButton.setOnAction(e -> addSavings());

        savingsTable.setRowFactory(tv -> {
            TableRow<Savings> row = new TableRow<>();
            ContextMenu menu = new ContextMenu();
            MenuItem delete = new MenuItem("Delete");
            delete.setOnAction(event -> savings.remove(row.getItem()));
            MenuItem save = new MenuItem("Add Saved");
            save.setOnAction(event -> addSaved(row.getItem()));
            menu.getItems().addAll(save, delete);
            row.setContextMenu(menu);
            return row;
        });
    }

    private void addSavings() {
        if (savings.size() >= 5) { showAlert("Savings limit reached (5 max)"); return; }
        String category = savingsCategoryField.getText().trim();
        double goal;
        try { goal = Double.parseDouble(savingsGoalField.getText().trim()); }
        catch (Exception e) { showAlert("Invalid goal!"); return; }
        savings.add(new Savings(category, goal));
        savingsCategoryField.clear(); savingsGoalField.clear();
    }

    private void addSaved(Savings s) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Saved");
        dialog.setHeaderText("Enter amount saved for " + s.getCategory());
        dialog.setContentText("Amount:");
        dialog.showAndWait().ifPresent(input -> {
            try {
                double amt = Double.parseDouble(input);
                if (amt < 0 || amt + s.getSaved() > s.getGoal()) {
                    showAlert("Invalid amount!");
                    return;
                }
                s.addSaved(amt);
            } catch (Exception e) {
                showAlert("Invalid input!");
            }
        });
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        alert.showAndWait();
    }
}
