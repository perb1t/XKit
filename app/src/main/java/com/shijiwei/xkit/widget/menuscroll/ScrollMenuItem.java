package com.shijiwei.xkit.widget.menuscroll;

/**
 * Created by shijiwei on 2016/10/6.
 */
public class ScrollMenuItem<T extends Object> {

    private String lable;
    private int iconResourceId;
    private String iconURL;
    private T data;

    public ScrollMenuItem(String lable, int iconResourceId, String iconURL, T t) {
        this.lable = lable;
        this.iconResourceId = iconResourceId;
        this.iconURL = iconURL;
        this.data = t;
    }

    public String getLable() {
        return lable;
    }

    public void setLable(String lable) {
        this.lable = lable;
    }

    public int getIconResourceId() {
        return iconResourceId;
    }

    public void setIconResourceId(int iconResourceId) {
        this.iconResourceId = iconResourceId;
    }

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
