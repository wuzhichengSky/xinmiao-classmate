package com.yupi.springbootinit.service.impl;

import static cn.hutool.core.util.ReUtil.isMatch;
import static com.yupi.springbootinit.constant.UserConstant.USER_LOGIN_STATE;
import static com.yupi.springbootinit.utils.FileUtils.fileValid;
import static com.yupi.springbootinit.utils.IdcardUtils.isValidID;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.constant.CommonConstant;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.mapper.UserMapper;
import com.yupi.springbootinit.model.IDcard;
import com.yupi.springbootinit.model.dto.user.UserQueryRequest;
import com.yupi.springbootinit.model.entity.ExcelUser;
import com.yupi.springbootinit.model.entity.Task;
import com.yupi.springbootinit.model.entity.TaskUser;
import com.yupi.springbootinit.model.entity.User;
import com.yupi.springbootinit.model.vo.LoginUserVO;
import com.yupi.springbootinit.model.vo.TaskVO;
import com.yupi.springbootinit.model.vo.UserVO;
import com.yupi.springbootinit.service.UserService;
import com.yupi.springbootinit.utils.*;

import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

import com.yupi.springbootinit.utils.excel.ExcelUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户服务实现
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 盐值，混淆密码
     */
    public static final String SALT = "xinmiao";



    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        synchronized (userAccount.intern()) {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = this.baseMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // 3. 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }
    }

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        if(user.getIsIdentify() == 0){
            throw new BusinessException(ErrorCode.NO_IDENTIFY_ERROR, "未认证，请先进行认证");
        }
        // 3. 记录用户的登录态
        String token = JwtUtil.createToken(user.getId().toString());
        return this.getLoginUserVO(user,token);
    }

    @Override
    public LoginUserVO userLoginByMpOpen(WxOAuth2UserInfo wxOAuth2UserInfo, HttpServletRequest request) {
        /*String unionId = wxOAuth2UserInfo.getUnionId();
        String mpOpenId = wxOAuth2UserInfo.getOpenid();
        // 单机锁
        synchronized (unionId.intern()) {
            // 查询用户是否已存在
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("unionId", unionId);
            User user = this.getOne(queryWrapper);
            // 被封号，禁止登录
            if (user != null && UserRoleEnum.BAN.getValue().equals(user.getUserRole())) {
                throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "该用户已被封，禁止登录");
            }
            // 用户不存在则创建
            if (user == null) {
                user = new User();
                user.setUnionId(unionId);
                user.setMpOpenId(mpOpenId);
                user.setUserAvatar(wxOAuth2UserInfo.getHeadImgUrl());
                user.setUserName(wxOAuth2UserInfo.getNickname());
                boolean result = this.save(user);
                if (!result) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "登录失败");
                }
            }
            // 记录用户的登录态
            request.getSession().setAttribute(USER_LOGIN_STATE, user);
            return getLoginUserVO(user);
        }*/
        return null;
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    /**
     * 获取当前登录用户（允许未登录）
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUserPermitNull(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            return null;
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        return this.getById(userId);
    }

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return isAdmin(user);
    }

    @Override
    public boolean isAdmin(User user) {
        //return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
        return false;
    }

    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        // 移除登录态
        return true;
    }

    @Override
    public LoginUserVO getLoginUserVO(User user,String token) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        loginUserVO.setToken(token);
        return loginUserVO;
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户不存在");
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVO(List<User> userList) {
        if (CollUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        String name = userQueryRequest.getName();
        String userAccount = userQueryRequest.getUserAccount();
        String academy = userQueryRequest.getAcademy();
        String classes = userQueryRequest.getClasses();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();


        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotBlank(name), "name", name);
        queryWrapper.eq(StringUtils.isNotBlank(userAccount), "userAccount", userAccount);
        queryWrapper.eq(StringUtils.isNotBlank(academy), "academy", academy);
        queryWrapper.like(StringUtils.isNotBlank(classes), "classes", classes);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public Boolean updatePassword(String oldPassword, String newPassword1, String newPassword2, HttpServletRequest request) {
        //校验两次密码
        if(!newPassword1.equals(newPassword2)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"两次密码不一致");
        }
        //校验新密码格式

        //判断旧密码正确性
        User user = UserThreadLocal.get();
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + oldPassword).getBytes());
        if(!encryptPassword.equals(user.getUserPassword())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"旧密码不正确");
        }
        //更新数据库
        user.setUserPassword(DigestUtils.md5DigestAsHex((SALT + newPassword1).getBytes()));
        return updateById(user);
    }

    @Override
    public Boolean userIdentify(MultipartFile iDcard, MultipartFile letter, MultipartFile avatar, HttpServletRequest request) throws Exception {
        //文件校验
        fileValid(iDcard);
        fileValid(letter);
        fileValid(avatar);
        //获取身份证信息、录取通知书信息
        IDcard idcard = IdcardUtils.idcard(iDcard);

        //验证库中是否存在该用户
        String name = idcard.getName();
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("name",name);
        User user = getOne(wrapper);
        if(user == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"不是大一新生，无法认证");
        }
        if(user.getIsIdentify() == 1){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户已认证");
        }

        //TODO 录取通知书信息校验
        //身份证姓名与录取通知书姓名对比
        //录取通知书学籍信息与库中对比

        //验证身份证头像与用户上传头像是否为同一人
        if(!FaceUtils.faceMatch(iDcard,avatar)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"上传证件照与身份证头像信息不一致");
        }
        //注册用户到头像库中
        FaceUtils.add(avatar, String.valueOf(user.getId()));

        //修改认证状态
        user.setIsIdentify(1);
        updateById(user);

        return true;
    }

    @Override
    public Boolean importUser(MultipartFile file) throws Exception {
        List<ExcelUser> array = ExcelUtils.readMultipartFile(file,ExcelUser.class);
        //保存到数据库
        for (ExcelUser excelUser : array) {
            User user = excelToUser(excelUser);
            //数据库中存在该用户，则不保存
            QueryWrapper<User> wrapper = new QueryWrapper<>();
            wrapper.eq("userAccount",user.getUserAccount());

            if(getOne(wrapper) == null){
                //将密码加密;
                String encryptPassword = DigestUtils.md5DigestAsHex((SALT + user.getUserAccount()).getBytes());
                user.setUserPassword(encryptPassword);
                if(!save(user)){
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                }
            }
        }

        return true;
    }

    @Override
    public Integer getTotal() {
        List<User> list = list();
        return list.size();
    }

    @Override
    public Integer getIdentifyTotal() {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getIsIdentify,1);
        return list(queryWrapper).size();
    }

    private User excelToUser(ExcelUser excelUser) {
        User user = new User();
        user.setName(excelUser.getName());
        user.setGender(excelUser.getGender());
        user.setUserAccount(excelUser.getUserAccount());
        //对身份证格式进行校验
        user.setIdNumber(excelUser.getIdNumber());
        if(!isValidID(excelUser.getIdNumber())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"存在身份证号不合法");
        }
        user.setAcademy(excelUser.getAcademy());
        user.setClasses(excelUser.getClasses());
        user.setBuilding(excelUser.getBuilding());
        user.setRoom(excelUser.getRoom());
        //初始化密码为学号
        user.setUserPassword(excelUser.getUserAccount());

        return user;
    }

    public Page<UserVO> getUserVOPage(Page<User> userPage, HttpServletRequest request) {
        List<User> userList = userPage.getRecords();
        Page<UserVO> userVOPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        if (CollUtil.isEmpty(userList)) {
            return userVOPage;
        }

        List<UserVO> userVOList = userList.stream().map(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user,userVO);
            return userVO;
        }).collect(Collectors.toList());


        userVOPage.setRecords(userVOList);
        return userVOPage;
    }

    @Override
    public Boolean importOneUser(User user) {
        String name = user.getName();
        String userAccount = user.getUserAccount();
        Integer gender = user.getGender();
        String idNumber = user.getIdNumber();
        String academy = user.getAcademy();
        String classes = user.getClasses();
        String building = user.getBuilding();
        String room = user.getRoom();
        if(gender == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if(StringUtils.isAnyBlank(name,userAccount,idNumber,academy,classes,building,room)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //身份证号校验
        if(!isValidID(idNumber)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"身份证号不合法");
        }
        //性别校验
        if(gender!=0 && gender!=1){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"性别错误");
        }

        //填充信息  密码初始化为学号
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + user.getUserAccount()).getBytes());
        user.setUserPassword(encryptPassword);
        return save(user);
    }

    @Override
    public Boolean deleteStudentInBatches( Long[] idList) {
        //校验id
        if (idList == null || idList.length==0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id列表为空，无法删除");
        }
        //判断用户是否存在
        for (Long id : idList) {
            if(getById(id) == null){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"存在非法用户，无法删除");
            }
        }

        if(!removeBatchByIds(Arrays.stream(idList).toList())){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }

        return true;

    }


}
