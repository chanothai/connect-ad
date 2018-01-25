package com.company.zicure.campusconnect.modelview;

import java.util.ArrayList;
import java.util.List;

import gallery.zicure.company.com.modellibrary.models.profile.ProfileResponse;

/**
 * Created by macintosh on 19/1/18.
 */

public class Item {
    private String text;
    private ArrayList<ProfileResponse.ProfileResult.ProfileData.ProfileLanguage.ListLanguage> subText;
    private ArrayList<String> menu;
    private boolean isExpandable;

    public Item(String text, ArrayList<ProfileResponse.ProfileResult.ProfileData.ProfileLanguage.ListLanguage> subText, ArrayList<String> menu,boolean isExpandable) {
        this.text = text;
        this.subText = subText;
        this.isExpandable = isExpandable;
        this.menu = menu;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isExpandable() {
        return isExpandable;
    }

    public void setExpandable(boolean expandable) {
        isExpandable = expandable;
    }

    public ArrayList<ProfileResponse.ProfileResult.ProfileData.ProfileLanguage.ListLanguage> getSubText() {
        return subText;
    }

    public void setSubText(ArrayList<ProfileResponse.ProfileResult.ProfileData.ProfileLanguage.ListLanguage> subText) {
        this.subText = subText;
    }

    public ArrayList<String> getMenu() {
        return menu;
    }

    public void setMenu(ArrayList<String> menu) {
        this.menu = menu;
    }
}
