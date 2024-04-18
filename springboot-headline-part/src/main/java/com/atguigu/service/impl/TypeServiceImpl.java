package com.atguigu.service.impl;

import com.atguigu.utils.Result;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.pojo.Type;
import com.atguigu.service.TypeService;
import com.atguigu.mapper.TypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 尹子豪
* @description 针对表【news_type】的数据库操作Service实现
* @createDate 2024-04-01 19:07:30
*/
@Service
public class TypeServiceImpl extends ServiceImpl<TypeMapper, Type>
    implements TypeService{
    @Autowired
    private TypeMapper typeMapper;

    /**
     * 查询所有的新闻类别
     * @return
     */
    @Override
    public Result findAllTypes() {
        List<Type> types = typeMapper.selectList(null);
        return Result.ok(types);
    }
}




