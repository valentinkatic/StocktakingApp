package eu.fiskaljdoo.stocktaking.models;

/**
 * Created by Valentin on 1.3.2018..
 */

public class Stocktaking {

    private int number;
    private String date;

    public Stocktaking(int number, String date) {
        this.number = number;
        this.date = date;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
