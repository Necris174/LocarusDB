package com.example.locarusdt2;

import android.os.Parcel;
import android.os.Parcelable;

public class SensorCard implements Parcelable {
    private int imageResource;
    private String text1;
    private String text2;

    public SensorCard(int imageResource, String text1, String text2) {
        this.imageResource = imageResource;
        this.text1 = text1;
        this.text2 = text2;
    }

    protected SensorCard(Parcel in) {
        imageResource = in.readInt();
        text1 = in.readString();
        text2 = in.readString();
    }

    public static final Creator<SensorCard> CREATOR = new Creator<SensorCard>() {
        @Override
        public SensorCard createFromParcel(Parcel in) {
            return new SensorCard(in);
        }

        @Override
        public SensorCard[] newArray(int size) {
            return new SensorCard[size];
        }
    };

    public int getImageResource() {
        return imageResource;
    }

    public String getText1() {
        return text1;
    }

    public String getText2() {
        return text2;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(text1);
            dest.writeString(text2);
            dest.writeInt(imageResource);
    }
}
