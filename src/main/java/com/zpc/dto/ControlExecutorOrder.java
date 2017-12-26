package com.zpc.dto;

/**
 * Created by 和谐社会人人有责 on 2017/12/5.
 * 控制线程池指令
 */
public class ControlExecutorOrder {

    public static final String NEW = "new";    // 新建线程池指令

    public static final String SUSPEND = "suspend";    // 暂停线程池指令

    public static final String DESTROY = "destroy";   // 销毁线程池指令

    public static final String SEED = "seed";   // 下载种子页面指令

    public static final String PARSE = "parse";   // 解析服务器需要从数据库获取对应的页面并解析

}
