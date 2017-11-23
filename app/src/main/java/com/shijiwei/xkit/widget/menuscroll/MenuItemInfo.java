package com.shijiwei.xkit.widget.menuscroll;

/**
 * Created by shijiwei on 2016/12/22.
 */
public class MenuItemInfo {

    @ScrollMenuItemField(lableFiled = "lable")
    private String lable;
    @ScrollMenuItemField(iconResourceIdFiled = "iconResourceId")
    private int id;
    @ScrollMenuItemField(iconURLFiled = "iconURL")
    private String url;

    public MenuItemInfo(String lable, int id, String url) {
        this.lable = lable;
        this.id = id;
        this.url = url;
    }

    public String getLable() {
        return lable;
    }

    public void setLable(String lable) {
        this.lable = lable;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
