package app.walletpal;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

public class SummaryController {

    @FXML private Label totalIncomeLabel;
    @FXML private Label totalExpensesLabel;
    @FXML private Label totalSpentLabel;
    @FXML private Label totalSavedLabel;
    @FXML private Label netBalanceLabel;
    @FXML private Label budgetUtilizationLabel;
    @FXML private Label remainingBudgetLabel;
    @FXML private Label savingsProgressLabel;
    @FXML private StackedBarChart<String, Number> budgetChart;

    private ObservableList<Income> incomes;
    private ObservableList<Expense> expenses;
    private ObservableList<Savings> savings;

    public void setData(ObservableList<Income> incomes, ObservableList<Expense> expenses, ObservableList<Savings> savings) {
        System.out.println("SummaryController: Setting data with " +
                incomes.size() + " incomes, " +
                expenses.size() + " expenses, " +
                savings.size() + " savings");

        this.incomes = incomes;
        this.expenses = expenses;
        this.savings = savings;
        updateSummary();
        createChart();
    }

    private void updateSummary() {
        double totalIncome = calculateTotalIncome();
        double totalExpenseLimits = calculateTotalExpenseLimits();
        double totalSpent = calculateTotalSpent();
        double totalSaved = calculateTotalSaved();
        double totalSavingsGoals = calculateTotalSavingsGoals();

        double netBalance = totalIncome - totalSpent;
        double budgetUtilization = totalIncome > 0 ? (totalExpenseLimits + totalSavingsGoals) / totalIncome * 100 : 0;
        double remainingBudget = totalIncome - totalExpenseLimits - totalSavingsGoals;
        double savingsProgress = totalSavingsGoals > 0 ? (totalSaved / totalSavingsGoals) * 100 : 0;

        totalIncomeLabel.setText(String.format("Total Income: $%.2f", totalIncome));
        totalExpensesLabel.setText(String.format("Total Expenses: $%.2f", totalExpenseLimits));
        totalSpentLabel.setText(String.format("Actual Spent: $%.2f", totalSpent));
        totalSavedLabel.setText(String.format("Total Saved: $%.2f", totalSaved));
        netBalanceLabel.setText(String.format("Net Balance: $%.2f", netBalance));
        budgetUtilizationLabel.setText(String.format("Utilization: %.1f%%", budgetUtilization));
        remainingBudgetLabel.setText(String.format("Remaining: $%.2f", remainingBudget));
        savingsProgressLabel.setText(String.format("Savings Progress: %.1f%%", savingsProgress));

        if (netBalance < 0) {
            netBalanceLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        } else {
            netBalanceLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        }
    }

    private void createChart() {
        budgetChart.getData().clear();

        double totalIncome = calculateTotalIncome();
        double totalExpenseLimits = calculateTotalExpenseLimits();
        double totalSpent = calculateTotalSpent();
        double totalSavingsGoals = calculateTotalSavingsGoals();
        double totalSaved = calculateTotalSaved();

        XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Income");
        incomeSeries.getData().add(new XYChart.Data<>("Income", totalIncome));

        XYChart.Series<String, Number> expenseSpentSeries = new XYChart.Series<>();
        expenseSpentSeries.setName("Expense - Spent");
        expenseSpentSeries.getData().add(new XYChart.Data<>("Expense", totalSpent));

        XYChart.Series<String, Number> expenseRemainingSeries = new XYChart.Series<>();
        expenseRemainingSeries.setName("Expense - Remaining");
        expenseRemainingSeries.getData().add(new XYChart.Data<>("Expense", Math.max(0, totalExpenseLimits - totalSpent)));

        XYChart.Series<String, Number> savingsSavedSeries = new XYChart.Series<>();
        savingsSavedSeries.setName("Savings - Saved");
        savingsSavedSeries.getData().add(new XYChart.Data<>("Savings", totalSaved));

        XYChart.Series<String, Number> savingsRemainingSeries = new XYChart.Series<>();
        savingsRemainingSeries.setName("Savings - Remaining");
        savingsRemainingSeries.getData().add(new XYChart.Data<>("Savings", Math.max(0, totalSavingsGoals - totalSaved)));

        budgetChart.getData().addAll(incomeSeries, expenseSpentSeries, expenseRemainingSeries, savingsSavedSeries, savingsRemainingSeries);
    }

    private double calculateTotalIncome() {
        return incomes.stream().mapToDouble(Income::getAmount).sum();
    }

    private double calculateTotalExpenseLimits() {
        return expenses.stream().mapToDouble(Expense::getLimit).sum();
    }

    private double calculateTotalSpent() {
        return expenses.stream().mapToDouble(Expense::getSpent).sum();
    }

    private double calculateTotalSavingsGoals() {
        return savings.stream().mapToDouble(Savings::getGoal).sum();
    }

    private double calculateTotalSaved() {
        return savings.stream().mapToDouble(Savings::getSaved).sum();
    }
}