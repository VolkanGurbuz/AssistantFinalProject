package com.volkangurbuz.intro;

/**
 * Created by VolkanGurbuz on 12/16/2017.
 */

public class HaberModel {
    private String Description,Title,Url;
    public HaberModel(){}
    public HaberModel(String description, String title, String url) {
        Description = description;
        Title = title;
        Url = url;
        }
    public String getDescription() {
        return Description;
    }
    public void setDescription(String description) {
        Description = description;
    }

    public String getTitle() {
        return Title;
    }
    public void setTitle(String title) {
        Title = title;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }


}
