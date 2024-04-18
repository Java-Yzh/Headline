package com.atguigu.controller;

import com.atguigu.pojo.Headline;
import com.atguigu.service.HeadlineService;
import com.atguigu.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * author : 尹子豪
 */
@RestController
@RequestMapping("headline")
@CrossOrigin
public class HeadlineController {

    @Autowired
    private HeadlineService headlineService;

    //必须登陆以后才能访问，所以需要进行登陆检查，即检查token是否有效。
    //后续删除修改头条功能同样需要进行登录检查，因此写一个拦截器
    //发布头条
    @PostMapping("publish")
    public Result publish(@RequestBody Headline headline,@RequestHeader String token){
        Result result = headlineService.publish(headline,token);
        return result;
    }

    //查询头条
    @PostMapping("findHeadlineByHid")
    public Result findHeadlineByHid(Integer hid){
        Headline headline = headlineService.getById(hid);
        Map data = new HashMap<>();
        data.put("headline",headline);
        return Result.ok(data);
    }

    //头条更新
    @PostMapping("update")
    public Result update(@RequestBody Headline headline){
        Result result = headlineService.updateDate(headline);
        return result;
    }

    //头条删除
    @PostMapping("removeByHid")
    public Result removeByHid(Integer hid){
        headlineService.removeById(hid);
        return Result.ok(null);
    }
}
