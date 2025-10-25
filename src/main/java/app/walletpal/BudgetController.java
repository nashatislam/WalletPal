package app.walletpal;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.io.IOException;

public class BudgetController {

    // Income Tab
    @FXML private TextField incomeSourceField;
    @FXML private TextField incomeAmountField;
    @FXML private TextField incomeNotesField;
    @FXML private Button addIncomeButton;
    @FXML private TableView<Income> incomeTable;
    @FXML private TableColumn<Income, String> incomeSourceCol;
    @FXML private TableColumn<Income, Double> incomeAmountCol;
    @FXML private TableColumn<Income, String> incomeNotesCol;

    // Expense Tab
    @FXML private TextField expenseCategoryField;
    @FXML private TextField expenseLimitField;
    @FXML private TextField expenseNotesField;
    @FXML private Button addExpenseButton;
    @FXML private TableView<Expense> expenseTable;
    @FXML private TableColumn<Expense, String> expenseCategoryCol;
    @FXML private TableColumn<Expense, Double> expenseLimitCol;
    @FXML private TableColumn<Expense, Double> expenseSpentCol;
    @FXML private TableColumn<Expense, Double> expenseRemainingCol;
    @FXML private TableColumn<Expense, String> expenseNotesCol;

    // Savings Tab
    @FXML private TextField savingsCategoryField;
    @FXML private TextField savingsGoalField;
    @FXML private TextField savingsNotesField;
    @FXML private Button addSavingsButton;
    @FXML private TableView<Savings> savingsTable;
    @FXML private TableColumn<Savings, String> savingsCategoryCol;
    @FXML private TableColumn<Savings, Double> savingsGoalCol;
    @FXML private TableColumn<Savings, Double> savingsSavedCol;
    @FXML private TableColumn<Savings, Double> savingsMoreToGoCol;
    @FXML private TableColumn<Savings, String> savingsNotesCol;

    @FXML private Tab incomeTab;
    @FXML private Tab expenseTab;
    @FXML private Tab savingsTab;
    @FXML private TabPane mainTabPane;
    @FXML private Button summaryButton;

    private final ObservableList<Income> incomes = FXCollections.observableArrayList();
    private final ObservableList<Expense> expenses = FXCollections.observableArrayList();
    private final ObservableList<Savings> savings = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupIncomeTable();
        setupExpenseTable();
        setupSavingsTable();
        setupBudgetValidation();
        updateUIState();
    }

    private void setupBudgetValidation() {
        incomes.addListener((javafx.collections.ListChangeListener.Change<? extends Income> c) -> {
            updateUIState();
            updateAddButtons();
        });
        expenses.addListener((javafx.collections.ListChangeListener.Change<? extends Expense> c) -> {
            updateAddButtons();
        });
        savings.addListener((javafx.collections.ListChangeListener.Change<? extends Savings> c) -> {
            updateAddButtons();
        });
    }

    private void updateUIState() {
        boolean hasIncome = !incomes.isEmpty();
        expenseTab.setDisable(!hasIncome);
        savingsTab.setDisable(!hasIncome);
        summaryButton.setDisable(!hasIncome);
    }

    private void updateAddButtons() {
        double totalIncome = getTotalIncome();
        double totalExpenses = getTotalExpenseLimits();
        double totalSavings = getTotalSavingsGoals();
        double available = totalIncome - totalExpenses - totalSavings;

        addExpenseButton.setDisable(available <= 0);
        addSavingsButton.setDisable(available <= 0);
    }

    private double getTotalIncome() {
        return incomes.stream().mapToDouble(Income::getAmount).sum();
    }

    private double getTotalExpenseLimits() {
        return expenses.stream().mapToDouble(Expense::getLimit).sum();
    }

    private double getTotalSavingsGoals() {
        return savings.stream().mapToDouble(Savings::getGoal).sum();
    }

    private void setupIncomeTable() {
        incomeSourceCol.setCellValueFactory(data -> data.getValue().sourceProperty());
        incomeAmountCol.setCellValueFactory(data -> data.getValue().amountProperty().asObject());
        incomeNotesCol.setCellValueFactory(data -> data.getValue().notesProperty());

        incomeTable.setItems(incomes);
        addIncomeButton.setOnAction(e -> addIncome());

        incomeTable.setRowFactory(tv -> {
            TableRow<Income> row = new TableRow<>();
            ContextMenu menu = new ContextMenu();

            MenuItem edit = new MenuItem("Edit");
            edit.setOnAction(event -> {
                Income income = row.getItem();
                if (income == null) return;

                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.setTitle("Edit Income");

                Label sourceLabel = new Label("Source:");
                TextField sourceField = new TextField(income.getSource());
                Label amountLabel = new Label("Amount:");
                TextField amountField = new TextField(String.valueOf(income.getAmount()));
                Label notesLabel = new Label("Notes:");
                TextField notesField = new TextField(income.getNotes());

                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.add(sourceLabel, 0, 0);
                grid.add(sourceField, 1, 0);
                grid.add(amountLabel, 0, 1);
                grid.add(amountField, 1, 1);
                grid.add(notesLabel, 0, 2);
                grid.add(notesField, 1, 2);

                dialog.getDialogPane().setContent(grid);
                dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

                dialog.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        try {
                            income.setSource(sourceField.getText());
                            income.setAmount(Double.parseDouble(amountField.getText()));
                            income.setNotes(notesField.getText());
                            
                            updateAddButtons();
                            incomeTable.refresh();
                        } catch (NumberFormatException e) {
                            showAlert("Invalid amount format!");
                        }
                    }
                });
            });

            MenuItem delete = new MenuItem("Delete");
            delete.setOnAction(event -> {
                incomes.remove(row.getItem());
                
                updateUIState();
            });

            menu.getItems().addAll(edit, delete);
            row.setContextMenu(menu);
            return row;
        });
    }

    private void addIncome() {
        if (incomes.size() >= 5) { showAlert("Income limit reached (5 max)"); return; }
        String source = incomeSourceField.getText().trim();
        String notes = incomeNotesField.getText().trim();
        double amount;
        try { amount = Double.parseDouble(incomeAmountField.getText().trim()); }
        catch (Exception e) { showAlert("Invalid amount!"); return; }

        if (source.isEmpty()) { showAlert("Please enter a source!"); return; }

        incomes.add(new Income(source, amount, notes));
        incomeSourceField.clear(); incomeAmountField.clear(); incomeNotesField.clear();
        
        updateUIState();
    }

    private void setupExpenseTable() {
        expenseCategoryCol.setCellValueFactory(data -> data.getValue().categoryProperty());
        expenseLimitCol.setCellValueFactory(data -> data.getValue().limitProperty().asObject());
        expenseSpentCol.setCellValueFactory(data -> data.getValue().spentProperty().asObject());
        expenseRemainingCol.setCellValueFactory(data -> data.getValue().remainingProperty().asObject());
        expenseNotesCol.setCellValueFactory(data -> data.getValue().notesProperty());

        expenseTable.setItems(expenses);
        addExpenseButton.setOnAction(e -> addExpense());

        expenseTable.setRowFactory(tv -> {
            TableRow<Expense> row = new TableRow<>();
            ContextMenu menu = new ContextMenu();

            MenuItem edit = new MenuItem("Edit");
            edit.setOnAction(event -> {
                Expense expense = row.getItem();
                if (expense == null) return;

                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.setTitle("Edit Expense");

                Label categoryLabel = new Label("Category:");
                TextField categoryField = new TextField(expense.getCategory());
                Label limitLabel = new Label("Limit:");
                TextField limitField = new TextField(String.valueOf(expense.getLimit()));
                Label spentLabel = new Label("Spent:");
                TextField spentField = new TextField(String.valueOf(expense.getSpent()));
                Label notesLabel = new Label("Notes:");
                TextField notesField = new TextField(expense.getNotes());

                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.add(categoryLabel, 0, 0);
                grid.add(categoryField, 1, 0);
                grid.add(limitLabel, 0, 1);
                grid.add(limitField, 1, 1);
                grid.add(spentLabel, 0, 2);
                grid.add(spentField, 1, 2);
                grid.add(notesLabel, 0, 3);
                grid.add(notesField, 1, 3);

                dialog.getDialogPane().setContent(grid);
                dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

                dialog.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        try {
                            expense.setCategory(categoryField.getText());
                            expense.setLimit(Double.parseDouble(limitField.getText()));
                            expense.setSpentDirectly(Double.parseDouble(spentField.getText()));
                            expense.setNotes(notesField.getText());
                            
                            updateAddButtons();
                            expenseTable.refresh();
                        } catch (NumberFormatException e) {
                            showAlert("Invalid number format!");
                        }
                    }
                });
            });

            MenuItem delete = new MenuItem("Delete");
            delete.setOnAction(event -> {
                expenses.remove(row.getItem());
                
                updateAddButtons();
            });

            MenuItem spend = new MenuItem("Add Spent");
            spend.setOnAction(event -> addSpent(row.getItem()));

            menu.getItems().addAll(edit, spend, delete);
            row.setContextMenu(menu);
            return row;
        });
    }

    private void addExpense() {
        if (expenses.size() >= 10) { showAlert("Expense limit reached (10 max)"); return; }
        String category = expenseCategoryField.getText().trim();
        String notes = expenseNotesField.getText().trim();
        double limit;
        try { limit = Double.parseDouble(expenseLimitField.getText().trim()); }
        catch (Exception e) { showAlert("Invalid limit!"); return; }

        if (category.isEmpty()) { showAlert("Please enter a category!"); return; }

        double totalIncome = getTotalIncome();
        double currentExpenses = getTotalExpenseLimits();
        double currentSavings = getTotalSavingsGoals();

        if (currentExpenses + currentSavings + limit > totalIncome) {
            showAlert("Cannot add expense: Would exceed total income!");
            return;
        }

        expenses.add(new Expense(category, limit, notes));
        expenseCategoryField.clear(); expenseLimitField.clear(); expenseNotesField.clear();
        
    }

    private void addSpent(Expense exp) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Spent");
        dialog.setHeaderText("Enter amount spent for " + exp.getCategory());
        dialog.setContentText("Amount:");

        dialog.showAndWait().ifPresent(input -> {
            try {
                double amt = Double.parseDouble(input);
                if (amt < 0) {
                    showAlert("Amount cannot be negative!");
                    return;
                }
                if (amt > exp.getRemaining()) {
                    Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmAlert.setTitle("Over Budget Warning");
                    confirmAlert.setHeaderText("You are about to exceed your budget!");
                    confirmAlert.setContentText(String.format(
                            "This will exceed your remaining budget of $%.2f by $%.2f.\nDo you want to continue?",
                            exp.getRemaining(), amt - exp.getRemaining()
                    ));

                    confirmAlert.showAndWait().ifPresent(result -> {
                        if (result == ButtonType.OK) {
                            exp.addSpent(amt);
                            
                        }
                    });
                } else {
                    exp.addSpent(amt);
                    
                }
            } catch (NumberFormatException e) {
                showAlert("Please enter a valid number!");
            } catch (Exception e) {
                showAlert("An error occurred: " + e.getMessage());
            }
        });
    }

    private void setupSavingsTable() {
        savingsCategoryCol.setCellValueFactory(data -> data.getValue().categoryProperty());
        savingsGoalCol.setCellValueFactory(data -> data.getValue().goalProperty().asObject());
        savingsSavedCol.setCellValueFactory(data -> data.getValue().savedProperty().asObject());
        savingsMoreToGoCol.setCellValueFactory(data -> data.getValue().moreToGoProperty().asObject());
        savingsNotesCol.setCellValueFactory(data -> data.getValue().notesProperty());

        savingsTable.setItems(savings);
        addSavingsButton.setOnAction(e -> addSavings());

        savingsTable.setRowFactory(tv -> {
            TableRow<Savings> row = new TableRow<>();
            ContextMenu menu = new ContextMenu();

            MenuItem edit = new MenuItem("Edit");
            edit.setOnAction(event -> {
                Savings saving = row.getItem();
                if (saving == null) return;

                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.setTitle("Edit Savings");

                Label categoryLabel = new Label("Category:");
                TextField categoryField = new TextField(saving.getCategory());
                Label goalLabel = new Label("Goal:");
                TextField goalField = new TextField(String.valueOf(saving.getGoal()));
                Label savedLabel = new Label("Saved:");
                TextField savedField = new TextField(String.valueOf(saving.getSaved()));
                Label notesLabel = new Label("Notes:");
                TextField notesField = new TextField(saving.getNotes());

                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.add(categoryLabel, 0, 0);
                grid.add(categoryField, 1, 0);
                grid.add(goalLabel, 0, 1);
                grid.add(goalField, 1, 1);
                grid.add(savedLabel, 0, 2);
                grid.add(savedField, 1, 2);
                grid.add(notesLabel, 0, 3);
                grid.add(notesField, 1, 3);

                dialog.getDialogPane().setContent(grid);
                dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

                dialog.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        try {
                            saving.setCategory(categoryField.getText());
                            saving.setGoal(Double.parseDouble(goalField.getText()));
                            saving.setSavedDirectly(Double.parseDouble(savedField.getText()));
                            saving.setNotes(notesField.getText());
                            
                            updateAddButtons();
                            savingsTable.refresh();
                        } catch (NumberFormatException e) {
                            showAlert("Invalid number format!");
                        }
                    }
                });
            });

            MenuItem delete = new MenuItem("Delete");
            delete.setOnAction(event -> {
                savings.remove(row.getItem());
                
                updateAddButtons();
            });

            MenuItem save = new MenuItem("Add Saved");
            save.setOnAction(event -> addSaved(row.getItem()));

            menu.getItems().addAll(edit, save, delete);
            row.setContextMenu(menu);
            return row;
        });
    }

    private void addSavings() {
        if (savings.size() >= 5) { showAlert("Savings limit reached (5 max)"); return; }
        String category = savingsCategoryField.getText().trim();
        String notes = savingsNotesField.getText().trim();
        double goal;
        try { goal = Double.parseDouble(savingsGoalField.getText().trim()); }
        catch (Exception e) { showAlert("Invalid goal!"); return; }

        if (category.isEmpty()) { showAlert("Please enter a category!"); return; }

        double totalIncome = getTotalIncome();
        double currentExpenses = getTotalExpenseLimits();
        double currentSavings = getTotalSavingsGoals();

        if (currentExpenses + currentSavings + goal > totalIncome) {
            showAlert("Cannot add savings goal: Would exceed total income!");
            return;
        }

        savings.add(new Savings(category, goal, notes));
        savingsCategoryField.clear(); savingsGoalField.clear(); savingsNotesField.clear();
        
    }

    private void addSaved(Savings s) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Saved");
        dialog.setHeaderText("Enter amount saved for " + s.getCategory());
        dialog.setContentText("Amount:");

        dialog.showAndWait().ifPresent(input -> {
            try {
                double amt = Double.parseDouble(input);
                if (amt < 0) {
                    showAlert("Amount cannot be negative!");
                    return;
                }
                s.addSaved(amt);
                
            } catch (NumberFormatException e) {
                showAlert("Please enter a valid number!");
            } catch (Exception e) {
                showAlert("An error occurred: " + e.getMessage());
            }
        });
    }

    @FXML
    private void showSummary() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/summary.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));

            SummaryController controller = loader.getController();
            controller.setData(incomes, expenses, savings);

            stage.setTitle("WalletPal Summary");
            stage.show();
        } catch (IOException e) {
            showAlert("Error loading summary: " + e.getMessage());
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        alert.showAndWait();
    }
}