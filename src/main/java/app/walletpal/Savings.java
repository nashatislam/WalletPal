package app.walletpal;

import javafx.beans.property.*;

public class Savings {
    private final SimpleStringProperty category;
    private final SimpleDoubleProperty goal;
    private final SimpleDoubleProperty saved;
    private final SimpleDoubleProperty moreToGo;

    public Savings(String category, double goal) {
        this.category = new SimpleStringProperty(category);
        this.goal = new SimpleDoubleProperty(goal);
        this.saved = new SimpleDoubleProperty(0);
        this.moreToGo = new SimpleDoubleProperty(goal);
    }

    public String getCategory() { return category.get(); }
    public void setCategory(String value) { category.set(value); }
    public SimpleStringProperty categoryProperty() { return category; }

    public double getGoal() { return goal.get(); }
    public void setGoal(double value) { goal.set(value); updateMoreToGo(); }
    public SimpleDoubleProperty goalProperty() { return goal; }

    public double getSaved() { return saved.get(); }
    public void addSaved(double value) { saved.set(saved.get() + value); updateMoreToGo(); }
    public SimpleDoubleProperty savedProperty() { return saved; }

    public double getMoreToGo() { return moreToGo.get(); }
    private void updateMoreToGo() { moreToGo.set(goal.get() - saved.get()); }
    public SimpleDoubleProperty moreToGoProperty() { return moreToGo; }
}
