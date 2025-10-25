package app.walletpal;

import javafx.beans.property.*;

public class Expense {
    private final SimpleStringProperty category;
    private final SimpleDoubleProperty limit;
    private final SimpleDoubleProperty spent;
    private final SimpleDoubleProperty remaining;
    private final SimpleStringProperty notes;

    public Expense(String category, double limit) {
        this(category, limit, "");
    }

    public Expense(String category, double limit, String notes) {
        this.category = new SimpleStringProperty(category);
        this.limit = new SimpleDoubleProperty(limit);
        this.spent = new SimpleDoubleProperty(0);
        this.remaining = new SimpleDoubleProperty(limit);
        this.notes = new SimpleStringProperty(notes);
    }

    public String getCategory() { return category.get(); }
    public void setCategory(String value) { category.set(value); }
    public SimpleStringProperty categoryProperty() { return category; }

    public double getLimit() { return limit.get(); }
    public void setLimit(double value) { limit.set(value); updateRemaining(); }
    public SimpleDoubleProperty limitProperty() { return limit; }

    public double getSpent() { return spent.get(); }
    public void addSpent(double value) { spent.set(spent.get() + value); updateRemaining(); }
    public void setSpentDirectly(double value) { spent.set(value); updateRemaining(); }
    public SimpleDoubleProperty spentProperty() { return spent; }

    public double getRemaining() { return remaining.get(); }
    private void updateRemaining() { remaining.set(limit.get() - spent.get()); }
    public SimpleDoubleProperty remainingProperty() { return remaining; }

    public String getNotes() { return notes.get(); }
    public void setNotes(String value) { notes.set(value); }
    public SimpleStringProperty notesProperty() { return notes; }
}