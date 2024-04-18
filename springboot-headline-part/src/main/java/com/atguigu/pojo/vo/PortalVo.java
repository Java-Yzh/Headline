package com.atguigu.pojo.vo;

import lombok.Data;

/**
 * projectName: com.atguigu.pojo.vo
 *
 * @author: yzh
 * description:
 */

@Data
public class PortalVo {

    private String keyWords ;
    private int type = 0;

    private int pageNum = 1;

    private int pageSize = 10;
}
