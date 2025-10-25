package app.walletpal;

import javafx.collections.ObservableList;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static final String DATA_FILE = "walletpal_data.txt";

    public static void saveData(ObservableList<Income> incomes,
                                ObservableList<Expense> expenses,
                                ObservableList<Savings> savings) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_FILE))) {

            writer.println("WalletPal Data File");

            writer.println("[INCOMES]");
            for (Income income : incomes) {
                writer.printf("%s|%.2f|%s%n",
                        income.getSource(),
                        income.getAmount(),
                        income.getNotes());
            }

            writer.println("[EXPENSES]");
            for (Expense expense : expenses) {
                writer.printf("%s|%.2f|%.2f|%s%n",
                        expense.getCategory(),
                        expense.getLimit(),
                        expense.getSpent(),
                        expense.getNotes());
            }

            writer.println("[SAVINGS]");
            for (Savings saving : savings) {
                writer.printf("%s|%.2f|%.2f|%s%n",
                        saving.getCategory(),
                        saving.getGoal(),
                        saving.getSaved(),
                        saving.getNotes());
            }

            System.out.println("Data saved to: " + DATA_FILE);
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }

    public static void loadData(ObservableList<Income> incomes,
                                ObservableList<Expense> expenses,
                                ObservableList<Savings> savings) {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            System.out.println("No data file found. Starting fresh.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            String section = "";

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.equals("[INCOMES]")) {
                    section = "INCOMES";
                } else if (line.equals("[EXPENSES]")) {
                    section = "EXPENSES";
                } else if (line.equals("[SAVINGS]")) {
                    section = "SAVINGS";
                } else if (!line.startsWith("WalletPal") && !line.startsWith("[")) {
                    String[] parts = line.split("\\|");

                    try {
                        switch (section) {
                            case "INCOMES":
                                if (parts.length >= 2) {
                                    String source = parts[0];
                                    double amount = Double.parseDouble(parts[1]);
                                    String notes = parts.length > 2 ? parts[2] : "";
                                    incomes.add(new Income(source, amount, notes));
                                }
                                break;
                            case "EXPENSES":
                                if (parts.length >= 3) {
                                    String category = parts[0];
                                    double limit = Double.parseDouble(parts[1]);
                                    double spent = Double.parseDouble(parts[2]);
                                    String notes = parts.length > 3 ? parts[3] : "";
                                    Expense expense = new Expense(category, limit, notes);
                                    expense.setSpentDirectly(spent);
                                    expenses.add(expense);
                                }
                                break;
                            case "SAVINGS":
                                if (parts.length >= 3) {
                                    String category = parts[0];
                                    double goal = Double.parseDouble(parts[1]);
                                    double saved = Double.parseDouble(parts[2]);
                                    String notes = parts.length > 3 ? parts[3] : "";
                                    Savings saving = new Savings(category, goal, notes);
                                    saving.setSavedDirectly(saved);
                                    savings.add(saving);
                                }
                                break;
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing number in: " + line);
                    }
                }
            }
            System.out.println("Data loaded successfully!");
        } catch (IOException e) {
            System.err.println("Error loading data: " + e.getMessage());
        }
    }
}