package com.example.appetite;

import android.os.Parcel;
import android.os.Parcelable;

public class UserInfoActivity implements Parcelable {

    public static final Creator<UserInfoActivity> CREATOR = new Creator<UserInfoActivity>() {
        @Override
        public UserInfoActivity createFromParcel(Parcel in) {
            return new UserInfoActivity(in);
        }

        @Override
        public UserInfoActivity[] newArray(int size) {
            return new UserInfoActivity[size];
        }
    };
    private  String firstName;
    private  String lastName;
    private String email;
    private String userID;


    protected UserInfoActivity(String firstName,String lastName,String email,String userID) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.userID = userID;
    }

    public UserInfoActivity(Parcel in) {
        firstName= in.readString();
        lastName=  in.readString();
        email= in.readString();
        userID= in.readString();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(email);
        dest.writeString(userID);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getUserID() { return userID; }

}
