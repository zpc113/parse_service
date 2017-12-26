package com.zpc.entity.controlcenter;

/**
 * Created by 和谐社会人人有责 on 2017/11/25.
 */
public class TaskConfig {
    // taskId，TaskInfo的id
    private long taskId;
    // 脚本
    private String script;
    // 种子链接
    private String seedUrl;
    // 线程数
    private int threadNum;
    // acceptCode
    private String acceptCode;
    // 数据表名
    private String dataTableName;

    public TaskConfig(long taskId) {
        this.taskId = taskId;
    }

    public TaskConfig(long taskId, String script, String seedUrl, int threadNum, String acceptCode, String dataTableName) {
        this.taskId = taskId;
        this.script = script;
        this.seedUrl = seedUrl;
        this.threadNum = threadNum;
        this.acceptCode = acceptCode;
        this.dataTableName = dataTableName;
    }

    public TaskConfig() {
    }

    @Override
    public String toString() {
        return "TaskConfig{" +
                "taskId=" + taskId +
                ", script='" + script + '\'' +
                ", seedUrl='" + seedUrl + '\'' +
                ", threadNum=" + threadNum +
                ", acceptCode='" + acceptCode + '\'' +
                ", dataTableName='" + dataTableName + '\'' +
                '}';
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getSeedUrl() {
        return seedUrl;
    }

    public void setSeedUrl(String seedUrl) {
        this.seedUrl = seedUrl;
    }

    public int getThreadNum() {
        return threadNum;
    }

    public void setThreadNum(int threadNum) {
        this.threadNum = threadNum;
    }

    public String getAcceptCode() {
        return acceptCode;
    }

    public void setAcceptCode(String acceptCode) {
        this.acceptCode = acceptCode;
    }

    public String getDataTableName() {
        return dataTableName;
    }

    public void setDataTableName(String dataTableName) {
        this.dataTableName = dataTableName;
    }
}
