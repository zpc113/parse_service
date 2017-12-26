package com.zpc.dao.controlcenter;

import com.zpc.entity.controlcenter.TaskConfig;
import org.apache.ibatis.annotations.Param;

/**
 * Created by 和谐社会人人有责 on 2017/11/25.
 */
public interface TaskConfigDao {

    /**
     * 新增任务配置
     * @param taskConfig
     */
    void create(TaskConfig taskConfig);

    /**
     * 修改任务配置
     * @param taskConfig
     */
    void updateTaskConfig(TaskConfig taskConfig);

    /**
     * 根据任务id查找任务配置
     * @param taskId
     */
    TaskConfig findByTaskId(@Param("taskId") long taskId);
}
