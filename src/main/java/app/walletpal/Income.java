package app.walletpal;

import javafx.beans.property.*;

public class Income {
    private final SimpleStringProperty source;
    private final SimpleDoubleProperty amount;
    private final SimpleStringProperty notes;

    public Income(String source, double amount) {
        this(source, amount, "");
    }

    public Income(String source, double amount, String notes) {
        this.source = new SimpleStringProperty(source);
        this.amount = new SimpleDoubleProperty(amount);
        this.notes = new SimpleStringProperty(notes);
    }

    public String getSource() { return source.get(); }
    public void setSource(String value) { source.set(value); }
    public SimpleStringProperty sourceProperty() { return source; }

    public double getAmount() { return amount.get(); }
    public void setAmount(double value) { amount.set(value); }
    public SimpleDoubleProperty amountProperty() { return amount; }

    public String getNotes() { return notes.get(); }
    public void setNotes(String value) { notes.set(value); }
    public SimpleStringProperty notesProperty() { return notes; }
}