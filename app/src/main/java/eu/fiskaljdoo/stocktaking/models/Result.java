package eu.fiskaljdoo.stocktaking.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Valentin on 27.2.2018..
 */

public class Result implements Parcelable{

    private Article article;
    private double amount;
    private String date;
    private String user;

    public Result() {
    }

    public Result(Article article) {
        this.article = article;
    }

    public Result(Article article, double amount, String date, String user) {
        this.article = article;
        this.amount = amount;
        this.date = date;
        this.user = user;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.article, flags);
        dest.writeDouble(this.amount);
        dest.writeString(this.date);
        dest.writeString(this.user);
    }

    protected Result(Parcel in) {
        this.article = in.readParcelable(Article.class.getClassLoader());
        this.amount = in.readDouble();
        this.date = in.readString();
        this.user = in.readString();
    }

    public static final Creator<Result> CREATOR = new Creator<Result>() {
        @Override
        public Result createFromParcel(Parcel source) {
            return new Result(source);
        }

        @Override
        public Result[] newArray(int size) {
            return new Result[size];
        }
    };
}
