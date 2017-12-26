package com.zpc.dao.datacenter;

import com.zpc.entity.datacenter.DataInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by 和谐社会人人有责 on 2017/12/13.
 */
public interface DataInfoDao {

    /**
     * 批量插入数据
     * @param dataInfos
     */
    @Transactional
    void saveData(@Param("dataInfos") List<DataInfo> dataInfos , @Param("tableName") String tableName);

}
