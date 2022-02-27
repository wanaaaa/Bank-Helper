package com.zybooks.thebanddatabase;

public class WanVariables {
    private static WanVariables instance;
    public String urlStr;

    public static WanVariables getInstance() {
        if (instance == null) {
            instance = new WanVariables();
        }
        return instance;
    }
}
