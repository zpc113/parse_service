package com.zpc.entity.datacenter;

import java.util.Date;

/**
 * Created by 和谐社会人人有责 on 2017/12/13.
 */
public class DataInfo {

    private long data_id;

    private long schedule_id;

    private String dt;

    private String data_info;

    private Date create_time;

    public long getData_id() {
        return data_id;
    }

    public void setData_id(long data_id) {
        this.data_id = data_id;
    }

    public long getSchedule_id() {
        return schedule_id;
    }

    public void setSchedule_id(long schedule_id) {
        this.schedule_id = schedule_id;
    }

    public String getDt() {
        return dt;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }

    public String getData_info() {
        return data_info;
    }

    public void setData_info(String data_info) {
        this.data_info = data_info;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    @Override
    public String toString() {
        return "DataInfo{" +
                "data_id=" + data_id +
                ", schedule_id=" + schedule_id +
                ", dt='" + dt + '\'' +
                ", data_info='" + data_info + '\'' +
                ", create_time=" + create_time +
                '}';
    }

    public DataInfo() {
    }

    public DataInfo(long data_id, long schedule_id, String dt, String data_info, Date create_time) {
        this.data_id = data_id;
        this.schedule_id = schedule_id;
        this.dt = dt;
        this.data_info = data_info;
        this.create_time = create_time;
    }
}
