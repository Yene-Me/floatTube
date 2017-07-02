package org.model;

/**
 * Created by yeneneh.mulatu on 02/05/2017.
 */

public class MenuVideoList {

    public String name;
    public String url;


    MenuVideoList(){}



    MenuVideoList(String name, String url){

        this.name = name;
        this.url = url;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getListURL() {
        return url;
    }

    public void setListURL(String url) {
        this.url = url;
    }

}
