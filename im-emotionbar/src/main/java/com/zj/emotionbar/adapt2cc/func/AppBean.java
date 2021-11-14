package com.zj.emotionbar.adapt2cc.func;

public class AppBean {

    private final int id;
    private final int icon;
    private final String funcName;

    int getIcon() {
        return icon;
    }

    String getFuncName() {
        return funcName;
    }

    public int getId() {
        return id;
    }

    AppBean(int id, int icon, String funcName) {
        this.id = id;
        this.icon = icon;
        this.funcName = funcName;
    }
}
