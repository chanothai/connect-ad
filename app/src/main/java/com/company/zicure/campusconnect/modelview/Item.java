package com.company.zicure.campusconnect.modelview;

import java.util.ArrayList;
import java.util.List;

import gallery.zicure.company.com.modellibrary.models.profile.ProfileResponse;

/**
 * Created by macintosh on 19/1/18.
 */

public class Item {
    private String header;
    private ArrayList<ProfileResponse.ProfileResult.ProfileData.ProfileLanguage.ListLanguage> arrLanguage;
    private ArrayList<ProfileResponse.ProfileResult.ProfileData.ProfileMenu> menus;
    private boolean isExpandable;

    public Item(String header, ArrayList<ProfileResponse.ProfileResult.ProfileData.ProfileLanguage.ListLanguage> arrLanguage, ArrayList<ProfileResponse.ProfileResult.ProfileData.ProfileMenu> menus,boolean isExpandable) {
        this.header = header;
        this.arrLanguage = arrLanguage;
        this.isExpandable = isExpandable;
        this.menus = menus;
    }

    public boolean isExpandable() {
        return isExpandable;
    }

    public void setExpandable(boolean expandable) {
        isExpandable = expandable;
    }

    public ArrayList<ProfileResponse.ProfileResult.ProfileData.ProfileLanguage.ListLanguage> getArrLanguage() {
        return arrLanguage;
    }

    public void setArrLanguage(ArrayList<ProfileResponse.ProfileResult.ProfileData.ProfileLanguage.ListLanguage> arrLanguage) {
        this.arrLanguage = arrLanguage;
    }

    public ArrayList<ProfileResponse.ProfileResult.ProfileData.ProfileMenu> getMenus() {
        return menus;
    }

    public void setMenus(ArrayList<ProfileResponse.ProfileResult.ProfileData.ProfileMenu> menus) {
        this.menus = menus;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }
}
