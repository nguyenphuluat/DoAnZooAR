package com.lteam.zooar.Model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Animal implements Parcelable {
    @SerializedName("Id")
    @Expose
    private String id;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("Urlimage")
    @Expose
    private String urlimage;
    @SerializedName("Urlmodel")
    @Expose
    private String urlmodel;
    @SerializedName("Urlaudio")
    @Expose
    private String urlaudio;
    @SerializedName("Infomation")
    @Expose
    private String infomation;
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("Level")
    @Expose
    private String level;

    public Animal() {
    }

    protected Animal(Parcel in) {
        id = in.readString();
        name = in.readString();
        urlimage = in.readString();
        urlmodel = in.readString();
        urlaudio = in.readString();
        infomation = in.readString();
        status = in.readString();
        level = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(urlimage);
        dest.writeString(urlmodel);
        dest.writeString(urlaudio);
        dest.writeString(infomation);
        dest.writeString(status);
        dest.writeString(level);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Animal> CREATOR = new Creator<Animal>() {
        @Override
        public Animal createFromParcel(Parcel in) {
            return new Animal(in);
        }

        @Override
        public Animal[] newArray(int size) {
            return new Animal[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrlimage() {
        return urlimage;
    }

    public void setUrlimage(String urlimage) {
        this.urlimage = urlimage;
    }

    public String getUrlmodel() {
        return urlmodel;
    }

    public void setUrlmodel(String urlmodel) {
        this.urlmodel = urlmodel;
    }

    public String getUrlaudio() {
        return urlaudio;
    }

    public void setUrlaudio(String urlaudio) {
        this.urlaudio = urlaudio;
    }

    public String getInfomation() {
        return infomation;
    }

    public void setInfomation(String infomation) {
        this.infomation = infomation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
