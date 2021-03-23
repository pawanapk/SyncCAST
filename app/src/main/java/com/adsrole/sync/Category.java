package com.adsrole.sync;

public class Category {
    private String catname,catimage;

    public Category(String catname, String catimage) {
        this.catname = catname;
        this.catimage = catimage;
    }

    public String getCatname() {
        return catname;
    }

    public String getCatimage() {
        return catimage;
    }
}
