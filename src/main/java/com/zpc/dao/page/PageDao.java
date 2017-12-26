package com.zpc.dao.page;

import com.zpc.entity.page.Page;
import org.apache.ibatis.annotations.Param;

/**
 * Created by 和谐社会人人有责 on 2017/12/10.
 */
public interface PageDao {

    /**
     * 页面入库
     * @param page
     */
    void save(@Param("pageString") String pageString, @Param("tableName") String tableName);

    /**
     * 已解析页面入库
     * @param page
     */
    void saveParsed(@Param("pageString") String pageString, @Param("pageId") long pageId , @Param("tableName") String tableName);


    /**
     * 获取一个page页面
     * @return
     */
    Page getPage(@Param("tableName") String tableName);

    /**
     * 删除一条数据
     * @param pageId
     */
    void delete(@Param("pageId") long pageId , @Param("tableName") String tableName);
}
