package com.quanliren.quan_two.bean;

import java.io.Serializable;
import java.util.List;

public class ExchangeGroupBean implements Serializable {

    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private List<ExchangeItemBean> itemlist;

    public List<ExchangeItemBean> getItemlist() {
        return itemlist;
    }

    public void setItemlist(List<ExchangeItemBean> itemlist) {
        this.itemlist = itemlist;
    }

    public ExchangeGroupBean(String title, List<ExchangeItemBean> itemlist) {
        super();
        this.title = title;
        this.itemlist = itemlist;
    }

    public ExchangeGroupBean() {
        super();
        // TODO Auto-generated constructor stub
    }


}
