package com.atguigu.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.atguigu.utils.JwtHelper;
import com.atguigu.utils.MD5Util;
import com.atguigu.utils.Result;
import com.atguigu.utils.ResultCodeEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.pojo.User;
import com.atguigu.service.UserService;
import com.atguigu.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
* @author 尹子豪
* @description 针对表【news_user】的数据库操作Service实现
* @createDate 2024-04-01 19:07:30
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JwtHelper jwtHelper;
    /**
     * 登陆业务
     *
     *      1.根据账号，查询用户对象  -loginUser
     *      2.如果用户对象为null，查询失败，“账号错误” 501
     *      3.对比，密码，失败 返回503错误
     *      4.根据用户id生成一个token，token装入result  返回
     * @param user
     * @return
     */
    @Override
    public Result login(User user) {
        //根据账号查询数据
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getUsername,user.getUsername());
        //根据用户名得到数据库对象
        User LoginUser = userMapper.selectOne(lambdaQueryWrapper);
        //如果账号错误，返回501错误
        if (LoginUser == null) {
            return Result.build(null, ResultCodeEnum.USERNAME_ERROR);
        }
        //对比，密码
        if (!StringUtils.isEmpty(user.getUserPwd()) &&
                MD5Util.encrypt(user.getUserPwd()).equals(LoginUser.getUserPwd())
        ){
            //登陆成功
            //根据用户id生成token
            String token = jwtHelper.createToken(Long.valueOf(LoginUser.getUid()));
            //将token填充到返回信息中
            Map data = new HashMap();
            data.put("token",token);
            return  Result.ok(data);
        }
        //密码错误
        return Result.build(null,ResultCodeEnum.PASSWORD_ERROR);
    }

    /**
     * 根据token获取用户数据
     *
     * 1.校验token是否在有效期
     * 2.根据token解析出userId
     * 3.根据用户id查询用户信息
     * 4.去掉密码，封装result返回即可
     * @return
     */
    @Override
    public Result getUserInfo(String token) {
        //校验token是否在有效期
        boolean expiration = jwtHelper.isExpiration(token);//true表示失效了，false表示在有效期
        if (expiration){
            //失效，未登录看待
            return Result.build(null,ResultCodeEnum.NOTLOGIN);
        }
        //在有效期，则根据token获取userId,返回int类型id
        int userId = jwtHelper.getUserId(token).intValue();
        //根据userId查询用户信息
        User user = userMapper.selectById(userId);
        //将密码隐藏
        user.setUserPwd("");
        //将用户信息装进result返回
        Map data = new HashMap<>();
        data.put("loginUser",user);
        return Result.ok(data);
    }

    /**
     * 检查账户名是否可用
     *  1.根据账号名进行count查询
     *  2.count == 0 可用
     *  3.count > 0 不可用
     * @param username
     * @return
     */
    @Override
    public Result checkUserName(String username) {
        //根据账户名进行count查询
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(User::getUsername,username);
        Long count = userMapper.selectCount(lambdaQueryWrapper);
        //判断count的值，等于0返回成功，大于零返回失败，505
        if (count == 0){
            return Result.ok(null);
        }
        return Result.build(null,ResultCodeEnum.USERNAME_USED);
    }

    /**
     * 用户注册功能
     *  1.依然检查账号是否已被注册
     *  2.密码加密处理
     *  3.账号数据保存
     *  4.返回result
     * @param user
     * @return
     */
    @Override
    public Result register(User user) {
        //检查账号是否被注册
        //根据账户名进行count查询
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(User::getUsername,user.getUsername());
        Long count = userMapper.selectCount(lambdaQueryWrapper);
        //判断count的值，等于0返回成功，大于零返回失败，505
        if (count > 0){
            return Result.build(null,ResultCodeEnum.USERNAME_USED);
        }
        //程序执行到这，表示账号可以注册，执行注册业务
        //进行密码加密处理
        user.setUserPwd(MD5Util.encrypt(user.getUserPwd()));
        //将用户信息插入到数据库中
        userMapper.insert(user);
        return Result.ok(null);
    }
}




