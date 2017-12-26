package com.zpc.service;

import com.zpc.entity.controlcenter.TaskConfig;

/**
 * Created by 和谐社会人人有责 on 2017/11/25.
 */
public interface TaskConfigService {

    /**
     * 获取任务配置
     * @param taskId
     * @return
     */
    TaskConfig getConfig(long taskId);

    /**
     * 更新任务配置
     * @param taskConfig
     */
    void updateConfig(TaskConfig taskConfig);

}
