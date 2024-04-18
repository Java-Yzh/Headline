package com.atguigu.service.impl;

import com.atguigu.pojo.vo.PortalVo;
import com.atguigu.utils.JwtHelper;
import com.atguigu.utils.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.pojo.Headline;
import com.atguigu.service.HeadlineService;
import com.atguigu.mapper.HeadlineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author 尹子豪
* @description 针对表【news_headline】的数据库操作Service实现
* @createDate 2024-04-01 19:07:30
*/
@Service
public class HeadlineServiceImpl extends ServiceImpl<HeadlineMapper, Headline>
    implements HeadlineService{
    @Autowired
    private HeadlineMapper headlineMapper;
    @Autowired
    private JwtHelper jwtHelper;
    /**
     * 首页数据查询
     *
     *   1.进行分页数据查询
     *   2.分页数据，拼接到result即可
     *
     *   注意一:查询需要自定义语句，自定义mapper方法，携带分页
     *   注意二:返回的结果List<Map>
     * @param portalVo
     * @return
     */
    @Override
    public Result findNewsPage(PortalVo portalVo) {
        //进行分页数据查询
        //Page ->   当前页数   页容量
        IPage<Map> page = new Page<>(portalVo.getPageNum(),portalVo.getPageSize());
        headlineMapper.selectMyPage(page,portalVo);
        //获取分页数据集合
        List<Map> records = page.getRecords();
        //封装数据为相应格式
        Map data = new HashMap<>();
        data.put("pageData",records);
        data.put("pageNum",page.getCurrent());
        data.put("pageSize",page.getSize());
        data.put("totalPage",page.getPages());
        data.put("totalSize",page.getTotal());
        Map pageInfo = new HashMap<>();
        pageInfo.put("pageInfo",data);

        return Result.ok(pageInfo);
    }

    /**
     * 根据查询详情
     *
     *
     *    2.查询对应的数据即可（多表联查，头条和用户表，方法需要自定义，返回map即可）
     *    1.修改阅读量 +1（version乐观锁，当前数据对应的版本）
     * @param hid
     * @return
     */
    @Override
    public Result showHeadlineDetail(Integer hid) {
        //根据id查询数据
        Map data =  headlineMapper.queryDetailMap(hid);
        Map headlineMap = new HashMap();
        headlineMap.put("headline",data);

        //修改阅读量 + 1
        Headline headline = new Headline();
        headline.setHid((Integer) data.get("hid"));
        headline.setVersion((Integer) data.get("version"));
        //阅读量+1
        headline.setPageViews((Integer) data.get("pageViews") +1);
        headlineMapper.updateById(headline);

        return Result.ok(headlineMap);
    }

    /**
     * 头条修改
     *
     *      1.根据hid查询数据的最新version
     *      2.修改数据的修改时间为当前节点
     * @param headline
     * @return
     */
    @Override
    public Result updateDate(Headline headline) {
        Integer version = headlineMapper.selectById(headline.getHid()).getVersion();
        headline.setVersion(version);//乐观锁
        //修改数据的更新时间
        headline.setUpdateTime(new Date());
        headlineMapper.updateById(headline);
        return Result.ok(null);

    }

    /**
     * 发布头条方法
     * <p>
     * 1.补全数据
     *
     * @param headline
     * @param token
     * @return
     */
    @Override
    public Result publish(Headline headline, String token) {
        //token查询用户id
        int userId = jwtHelper.getUserId(token).intValue();
        //数据装配
        headline.setPublisher(userId);
        headline.setPageViews(0);
        headline.setCreateTime(new Date());
        headline.setUpdateTime(new Date());
        //进行数据库的插入
        headlineMapper.insert(headline);
        return Result.ok(null);
    }
}




