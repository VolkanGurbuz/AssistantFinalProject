package com.volkangurbuz.intro;



/**
 * Created by volkan on 08.05.2017.
 */

public class User {

    private String userAd, userCinsiyet;

    private int userYas, userKsanat, userBTeknoloji, userOrtam, userid;

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public User() {
    }

    public User(String userAd, int userYas, String userCinsiyet, int userKsanat, int userBTeknoloji, int userOrtam) {
        this.userAd = userAd;
        this.userCinsiyet = userCinsiyet;
        this.userYas = userYas;
        this.userKsanat = userKsanat;
        this.userBTeknoloji = userBTeknoloji;
        this.userOrtam = userOrtam;
    }

    public User(int userID, String userAd, String userCinsiyet, int userYas, int userKsanat, int userBTeknoloji, int userOrtam) {
        this.userid = userID;
        this.userAd = userAd;
        this.userCinsiyet = userCinsiyet;
        this.userYas = userYas;
        this.userKsanat = userKsanat;
        this.userBTeknoloji = userBTeknoloji;
        this.userOrtam = userOrtam;
    }


    public String getUserAd() {
        return userAd;
    }

    public void setUserAd(String userAd) {
        this.userAd = userAd;
    }

    public String getUserCinsiyet() {
        return userCinsiyet;
    }

    public void setUserCinsiyet(String userCinsiyet) {
        this.userCinsiyet = userCinsiyet;
    }

    public int getUserYas() {
        return userYas;
    }

    public void setUserYas(int userYas) {
        this.userYas = userYas;
    }

    public int getUserKsanat() {
        return userKsanat;
    }

    public void setUserKsanat(int userKsanat) {
        this.userKsanat = userKsanat;
    }

    public int getUserBTeknoloji() {
        return userBTeknoloji;
    }

    public void setUserBTeknoloji(int userBTeknoloji) {
        this.userBTeknoloji = userBTeknoloji;
    }

    public int getUserOrtam() {
        return userOrtam;
    }

    public void setUserOrtam(int userOrtam) {
        this.userOrtam = userOrtam;
    }

    @Override
    public String toString() {
        return "User{" +
                "userAd='" + userAd + '\'' +
                ", userCinsiyet='" + userCinsiyet + '\'' +
                ", userYas=" + userYas +
                ", userKsanat=" + userKsanat +
                ", userBTeknoloji=" + userBTeknoloji +
                ", userOrtam=" + userOrtam +
                '}';
    }


}
