package com.company.zicure.campusconnect.modelview;

import java.util.List;

/**
 * Created by macintosh on 19/1/18.
 */

public class Item {
    private String text, codeLanguage;
    private List<String> subText;
    private boolean isExpandable;

    public Item(String text, List<String> subText, boolean isExpandable) {
        this.text = text;
        this.subText = subText;
        this.isExpandable = isExpandable;
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

    public List<String> getSubText() {
        return subText;
    }

    public void setSubText(List<String> subText) {
        this.subText = subText;
    }

    public String getCodeLanguage() {
        return codeLanguage;
    }

    public void setCodeLanguage(String codeLanguage) {
        this.codeLanguage = codeLanguage;
    }
}
