package com.zpc.dtcrawler;

import com.zpc.entity.page.Page;

/**
 * Created by 和谐社会人人有责 on 2017/12/11.
 */
public interface DtcrawlerScript {

    Result parse(Page page);

}
