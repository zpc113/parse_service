package com.zpc.dtcrawler;

import com.zpc.dto.Request;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by 和谐社会人人有责 on 2017/12/11.
 */
public class Result {

    private List<Request> childLinks;

    private List<Map<String , String>> dataMapList;

    public void addChildLink(Request request) {
        childLinks.add(request);
    }

    public void addDataMap(Map<String , String> dataMap) {
        dataMapList.add(dataMap);
    }

    public List<Request> getChildLinks() {
        return childLinks;
    }

    public void setChildLinks(List<Request> childLinks) {
        this.childLinks = childLinks;
    }

    public List<Map<String, String>> getDataMapList() {
        return dataMapList;
    }

    public void setDataMapList(List<Map<String, String>> dataMapList) {
        this.dataMapList = dataMapList;
    }

    @Override
    public String toString() {
        return "Result{" +
                "childLinks=" + childLinks +
                ", dataMapList=" + dataMapList +
                '}';
    }

    public Result(List<Request> childLinks, List<Map<String, String>> dataMapList) {
        this.childLinks = childLinks;
        this.dataMapList = dataMapList;
    }

    public Result() {
        dataMapList = new ArrayList<Map<String, String>>();
        childLinks = new ArrayList<Request>();
    }
}
