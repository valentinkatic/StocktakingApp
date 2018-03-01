package eu.fiskaljdoo.stocktaking.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Valentin on 27.2.2018..
 */

public class Article implements Parcelable {

    private String code;
    private String name;
    private double price;

    public Article() {
    }

    public Article(String code) {
        this.code = code;
    }

    public Article(String code, String name, double price) {
        this.code = code;
        this.name = name;
        this.price = price;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.code);
        dest.writeString(this.name);
        dest.writeDouble(this.price);
    }

    protected Article(Parcel in) {
        this.code = in.readString();
        this.name = in.readString();
        this.price = in.readDouble();
    }

    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel source) {
            return new Article(source);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };
}
