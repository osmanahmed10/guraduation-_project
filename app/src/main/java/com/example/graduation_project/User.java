package com.example.graduation_project;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String id, imageUrl, userName, emailAddress, password, phoneNumber, area, profession;
    private double latitude, longitude;
    private int role;
    private float rate;

    public User() {
    }

    public User(String id, String userName, String emailAddress, String password, String phoneNumber, String area, String profession, double latitude, double longitude, String imageUrl, float rate, int role) {
        this.id = id;
        this.userName = userName;
        this.emailAddress = emailAddress;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.area = area;
        this.profession = profession;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageUrl = imageUrl;
        this.rate = rate;
        this.role = role;
    }

    protected User(Parcel in) {
        id = in.readString();
        imageUrl = in.readString();
        userName = in.readString();
        emailAddress = in.readString();
        password = in.readString();
        phoneNumber = in.readString();
        area = in.readString();
        profession = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        role = in.readInt();
        rate = in.readFloat();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", imageUrl=" + imageUrl +
                ", userName='" + userName + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", password='" + password + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", area='" + area + '\'' +
                ", profession='" + profession + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", role=" + role +
                ", rate=" + rate +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(imageUrl);
        dest.writeString(userName);
        dest.writeString(emailAddress);
        dest.writeString(password);
        dest.writeString(phoneNumber);
        dest.writeString(area);
        dest.writeString(profession);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeInt(role);
        dest.writeFloat(rate);
    }
}
