package com.zpc.dao.controlcenter;

import com.zpc.entity.controlcenter.Schedule;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * Created by 和谐社会人人有责 on 2017/11/20.
 * 任务调度DAO
 */
public interface ScheduleDao {

    /**
     * 查找对应任务下的所有调度
     * @param taskId
     * @return
     */
    List<Schedule> findAll(long taskId);

    /**
     * 设置调度结束时间
     * @param entTime
     */
    void setEnd(@Param("endTime") Date entTime, @Param("scheduleId") long scheduleId);

    /**
     * 新建调度
     * @param schedule
     */
    long create(Schedule schedule);

    /**
     * 查询单条调度
     * @param scheduleId
     * @return
     */
    Schedule findById(long scheduleId);
}
