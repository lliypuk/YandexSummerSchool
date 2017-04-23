package com.home.aleksandrnazarenko.yandextranslator;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DirectionTranslateJSON {

    @SerializedName("dirs")
    @Expose
    private List<String> dirs = new ArrayList<String>();

    @SerializedName("langs")
    @Expose
    private Map<String, String> langs = new HashMap<String, String>();


    public Map<String, String> getLangs() {
    return langs;
    }

    public void setLangs( Map<String, String> langs) {
        this.langs = langs;
    }


    public List<String> getDirs() {
        return dirs;
    }

    public void setDirs(List<String> dirs) {
        this.dirs = dirs;
    }



}
