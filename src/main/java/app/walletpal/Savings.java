package app.walletpal;

import javafx.beans.property.*;

public class Savings {
    private final SimpleStringProperty category;
    private final SimpleDoubleProperty goal;
    private final SimpleDoubleProperty saved;
    private final SimpleDoubleProperty moreToGo;
    private final SimpleStringProperty notes;

    public Savings(String category, double goal) {
        this(category, goal, "");
    }

    public Savings(String category, double goal, String notes) {
        this.category = new SimpleStringProperty(category);
        this.goal = new SimpleDoubleProperty(goal);
        this.saved = new SimpleDoubleProperty(0);
        this.moreToGo = new SimpleDoubleProperty(goal);
        this.notes = new SimpleStringProperty(notes);
    }

    public String getCategory() { return category.get(); }
    public void setCategory(String value) { category.set(value); }
    public SimpleStringProperty categoryProperty() { return category; }

    public double getGoal() { return goal.get(); }
    public void setGoal(double value) { goal.set(value); updateMoreToGo(); }
    public SimpleDoubleProperty goalProperty() { return goal; }

    public double getSaved() { return saved.get(); }
    public void addSaved(double value) { saved.set(saved.get() + value); updateMoreToGo(); }
    public void setSavedDirectly(double value) { saved.set(value); updateMoreToGo(); }
    public SimpleDoubleProperty savedProperty() { return saved; }

    public double getMoreToGo() { return moreToGo.get(); }
    private void updateMoreToGo() { moreToGo.set(goal.get() - saved.get()); }
    public SimpleDoubleProperty moreToGoProperty() { return moreToGo; }

    public String getNotes() { return notes.get(); }
    public void setNotes(String value) { notes.set(value); }
    public SimpleStringProperty notesProperty() { return notes; }
}