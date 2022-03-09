package com.project.workplatform.service;

import com.project.workplatform.dao.*;
import com.project.workplatform.data.Constant;
import com.project.workplatform.data.enums.StudioRoleEnum;
import com.project.workplatform.data.request.studio.*;
import com.project.workplatform.data.response.studio.*;
import com.project.workplatform.exception.CustomException;
import com.project.workplatform.exception.CustomExceptionType;
import com.project.workplatform.exception.ExceptionMessage;
import com.project.workplatform.pojo.*;
import com.project.workplatform.util.RandomUtil;
import com.project.workplatform.util.ValidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/19 17:18
 */
@Service
@Transactional(rollbackFor = RuntimeException.class)
public class StudioService {

    @Autowired
    private StudioMapper mapper;

    @Autowired
    private StudioApplyMapper applyMapper;

    @Autowired
    private UserStudioMapper userStudioMapper;

    @Autowired
    private StudioDepartmentMapper departmentMapper;

    @Autowired
    private StudioContactInfoMapper contactInfoMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void create(Integer userId, CreateStudioRequest createStudioRequest) {
        Studio studio = new Studio();
        studio.setStudioName(createStudioRequest.getStudioName());
        studio.setStudioAbbreviation(createStudioRequest.getStudioAbbreviation());
        studio.setCreatorId(userId);
        studio.setClassify(createStudioRequest.getClassify());
        mapper.insertSelective(studio);
    }

    /**
     * 生成的inviteCode会放在mysql里，同时会设置存一份在redis并设置过期时间（这里的过期时间是邀请码的过期时间，也是key的过期时间）
     * 在新的邀请码放入redis之前，要先检查该工作室有没有旧的且没过期的邀请码。如果有，则先将其在redis中删除
     * 在校验邀请码的方法中，都去redis里查，如果查不到，说明邀请码错误或者过期了
     * 在管理后台展示工作室信息的时候，查询邀请码的时候，先查redis，如果发现key过期了，那么就把mysql里的邀请码清空，并在前端也渲染为没有设置邀请码
     */
    public String initInviteCode(int userId, InitInviteCodeRequest initInviteCodeRequest) {
        int studioId = initInviteCodeRequest.getStudioId();
        checkSuperAdmin(userId, initInviteCodeRequest.getStudioId());
        String inviteCode = null;
        Boolean exist = null;
        //防止重复的邀请码
        do {
            inviteCode = RandomUtil.randomString(8);
            exist = redisTemplate.hasKey(Constant.REDIS_INVITE_CODE_KEY_PREFIX + inviteCode);
        } while (exist != null && exist);

        //先检查工作室有没有旧的且没过期的邀请码，若有则先将其在缓存中删除
        String oldInviteCode = mapper.selectByPrimaryKey(studioId).getInviteCode();
        if (oldInviteCode != null) {
            redisTemplate.delete(Constant.REDIS_INVITE_CODE_KEY_PREFIX + oldInviteCode);
        }

        //将邀请码存入MySQL
        mapper.updateInviteCodeByPrimaryKey(inviteCode, studioId);

        //将邀请码及邀请码的过期时间存入Redis
        String redisKey = Constant.REDIS_INVITE_CODE_KEY_PREFIX + inviteCode;
        redisTemplate.opsForValue().set(redisKey, String.valueOf(studioId),
                Duration.ofDays(initInviteCodeRequest.getExpireDay()));
        return inviteCode;
    }

    public Integer checkInviteCode(String inviteCode) {
        String redisKey = Constant.REDIS_INVITE_CODE_KEY_PREFIX + inviteCode;
        String redisValue = (String) redisTemplate.opsForValue().get(redisKey);
        if (redisValue == null) {
            throw new CustomException(CustomExceptionType.NORMAL_ERROR, ExceptionMessage.INVALID_INVITE_CODE);
        }
        return Integer.valueOf(redisValue);
    }

    public void applyJoin(Integer userId, ApplyJoinStudioRequest applyJoinStudioRequest) {
        StudioApply apply = new StudioApply();
        apply.setUserId(userId);
        apply.setStudioId(applyJoinStudioRequest.getStudioId());
        apply.setApplyMessage(applyJoinStudioRequest.getApplyMessage());
        apply.setApplyTag(0);
        applyMapper.insertSelective(apply);
    }

    public void inviteJoin(Integer userId, InviteJoinStudioRequest inviteJoinStudioRequest) {
        if (!isDepartmentAdmin(userId, inviteJoinStudioRequest.getStudioId(),
                inviteJoinStudioRequest.getDepartmentId())) {
            throw new CustomException(CustomExceptionType.PERMISSION_ERROR, ExceptionMessage.NOT_STUDIO_ADMIN);
        }
        StudioApply apply = new StudioApply();
        apply.setUserId(inviteJoinStudioRequest.getInviteUserId());
        apply.setStudioId(inviteJoinStudioRequest.getStudioId());
        apply.setDepartmentId(inviteJoinStudioRequest.getDepartmentId());
        apply.setApplyTag(1);
        applyMapper.insertSelective(apply);
    }

    public StudioInfoResponse getStudioInfo(int userId, int studioId) {
        if (!isMember(userId, studioId)) {
            throw new CustomException(CustomExceptionType.PERMISSION_ERROR, ExceptionMessage.NOT_STUDIO_MEMBER);
        }
        return mapper.selectStudioInfoByPrimaryKey(studioId);
    }

    public void dealApply(Integer userId, DealStudioApplyRequest dealStudioApplyRequest) {
        StudioApply apply = applyMapper.selectByPrimaryKey(dealStudioApplyRequest.getApplyId());
        checkSuperAdmin(userId, apply.getStudioId());
        apply.setTag(dealStudioApplyRequest.isAgree() ? 1 : 2);
        applyMapper.updateByPrimaryKeySelective(apply);
        if (dealStudioApplyRequest.isAgree()) {
            UserStudio userStudio = new UserStudio();
            userStudio.setUserId(apply.getUserId());
            userStudio.setStudioId(apply.getStudioId());
            userStudio.setDepartmentId(apply.getDepartmentId() == null ?
                    dealStudioApplyRequest.getDepartmentId() : apply.getDepartmentId());
            userStudio.setRoleId(dealStudioApplyRequest.getAdminTag());
            userStudioMapper.insertSelective(userStudio);
        }
        //TODO 通过消息通知申请用户

    }

    public void createDepartment(Integer userId, CreateDepartmentRequest createDepartmentRequest) {
        checkSuperAdmin(userId, createDepartmentRequest.getStudioId());
        //TODO 校验部门名称是否符合格式规范
        StudioDepartment department = new StudioDepartment();
        department.setDepartmentName(createDepartmentRequest.getDepartmentName());
        department.setStudioId(createDepartmentRequest.getStudioId());
        department.setParentDepartmentId(createDepartmentRequest.getParentDepartmentId());
        department.setType(createDepartmentRequest.getType());
        departmentMapper.insertSelective(department);
    }

    public void distributeLeader(Integer userId, DistributeLeaderRequest distributeLeaderRequest) {
        checkSuperAdmin(userId, distributeLeaderRequest.getStudioId());
        //修改department表信息，修改user_studio表信息
        departmentMapper.updateLeaderByStudioAndDepartment(distributeLeaderRequest.getLeaderId(),
                distributeLeaderRequest.getStudioId(), distributeLeaderRequest.getDepartmentId());
        userStudioMapper.updateDepartmentInfoByUserAndStudio(distributeLeaderRequest.getDepartmentId(),
                StudioRoleEnum.DEPARTMENT_ADMIN.getRoleId(), distributeLeaderRequest.getLeaderId(), distributeLeaderRequest.getStudioId());
    }

    public Map<String, Object> getStudioList(Integer userId) {
        Map<String, Object> map = new HashMap<>();
        map.put("studioList", mapper.selectStudioBaseInfoByUser(userId));

        String key = Constant.REDIS_CURRENT_STUDIO_KEY_PREFIX + userId;
        Integer currentStudioId = (Integer) redisTemplate.opsForValue().get(key);
        map.put("currentStudioBaseInfo", getStudioBaseInfo(currentStudioId, userId));

        return map;
    }

    public void recordCurrentStudio(Integer studioId, Integer userId) {
        String key = Constant.REDIS_CURRENT_STUDIO_KEY_PREFIX + userId;
        redisTemplate.opsForValue().set(key, studioId);
    }

    public StudioBaseInfoResponse getStudioBaseInfo(Integer studioId, Integer userId) {
        return mapper.selectStudioBaseInfoByPrimaryKey(studioId, userId);
    }

    public StudioPeopleInfoResponse getStudioPeopleInfo(Integer studioId) {
        if (studioId <= 0) {
            throw new CustomException(CustomExceptionType.NORMAL_ERROR, ExceptionMessage.STUDIO_ID_INVALID);
        }
        StudioPeopleInfoResponse response = new StudioPeopleInfoResponse();
        response.setStudioMemberNumber(userStudioMapper.selectMemberNumberByStudio(studioId));
        response.setNotActivatedNumber(applyMapper.selectNotActivatedNumberByStudio(studioId));
        response.setDepartmentNumber(departmentMapper.selectDepartmentNumberByStudio(studioId));
        response.setSuperAdminNumber(userStudioMapper.selectSuperAdminNumberByStudio(studioId));
        response.setAdminNumber(userStudioMapper.selectAdminNumberByStudio(studioId));
        return response;
    }

    public StudioContactInfoResponse getStudioContactInfo(Integer studioId) {
        return contactInfoMapper.selectByStudio(studioId);
    }

    public void updateStudioInfo(UpdateStudioInfoRequest updateStudioInfoRequest, Integer userId) {
        checkSuperAdmin(userId, updateStudioInfoRequest.getStudioId());
        Studio studio = new Studio();
        studio.setId(updateStudioInfoRequest.getStudioId());
        studio.setStudioName(updateStudioInfoRequest.getStudioName());
        studio.setStudioAbbreviation(updateStudioInfoRequest.getStudioAbbreviation());
        studio.setClassify(updateStudioInfoRequest.getClassify());
        studio.setDescribe(updateStudioInfoRequest.getDescribe());
        mapper.updateByPrimaryKeySelective(studio);
    }

    public void updateStudioContactInfo(UpdateStudioContactInfoRequest updateStudioContactInfoRequest, Integer userId) {
        checkSuperAdmin(userId, updateStudioContactInfoRequest.getStudioId());
        StudioContactInfo contactInfo = new StudioContactInfo();
        contactInfo.setStudioId(updateStudioContactInfoRequest.getStudioId());
        contactInfo.setContactName(updateStudioContactInfoRequest.getContactName());
        contactInfo.setContactPhone(updateStudioContactInfoRequest.getContactPhone());
        contactInfo.setContactMail(updateStudioContactInfoRequest.getContactMail());
        contactInfo.setStudioPlace(updateStudioContactInfoRequest.getStudioPlace());
        contactInfoMapper.updateByStudioSelective(contactInfo);
    }

    public List<StudioAdminResponse> getStudioAdminResponse(Integer studioId, Integer userId,
                                                            String type, String searchContent) {
        checkSuperAdmin(userId, studioId);
        //根据type来判断搜索的管理员的类型
        int roleId = "super".equals(type) ? StudioRoleEnum.SUPER_ADMIN.getRoleId() :
                StudioRoleEnum.DEPARTMENT_ADMIN.getRoleId();
        if (StringUtils.hasLength(searchContent)) {
            return ValidUtil.checkPhoneNumber(searchContent) ?
                    userStudioMapper.searchAdminByStudioAndPhone(studioId, roleId, searchContent) :
                    userStudioMapper.searchAdminByStudioAndNameFuzzy(studioId, roleId, searchContent);
        } else {
            return userStudioMapper.selectAdminByStudio(studioId, roleId);
        }
    }

    public void updateStudioRole(UpdateStudioRoleRequest updateStudioRoleRequest, Integer userId) {
        int studioId = updateStudioRoleRequest.getStudioId();
        checkSuperAdmin(userId,studioId);
        //根据搜索内容获得userId
        int memberId = getUserIdBySearchContent(updateStudioRoleRequest.getSearchContent());
        UserStudio userStudio = userStudioMapper.selectByUserAndStudio(memberId, studioId);
        //检查用户是否是工作室成员
        if (userStudio == null){
            throw new CustomException(CustomExceptionType.NORMAL_ERROR,ExceptionMessage.USER_NOT_IN_STUDIO);
        }
        //如果更新的是superAdmin且用户已经是超级管理员了
        if (updateStudioRoleRequest.getRoleId() == StudioRoleEnum.SUPER_ADMIN.getRoleId() &&
                userStudio.getRoleId() == StudioRoleEnum.SUPER_ADMIN.getRoleId()){
            throw new CustomException(CustomExceptionType.NORMAL_ERROR,ExceptionMessage.USER_ALREADY_SUPER_ADMIN);
        }
        //如果更新的是admin
        if (updateStudioRoleRequest.getRoleId() == StudioRoleEnum.DEPARTMENT_ADMIN.getRoleId()){
            //若用户不是该部门成员
            if (userStudio.getDepartmentId() != updateStudioRoleRequest.getDepartmentId()){
                throw new CustomException(CustomExceptionType.NORMAL_ERROR,ExceptionMessage.USER_NOT_IN_THAT_DEPARTMENT);
            }
            //若用户已经是该部门的管理员了
            if (userStudio.getRoleId() == StudioRoleEnum.DEPARTMENT_ADMIN.getRoleId()){
                throw new CustomException(CustomExceptionType.NORMAL_ERROR,ExceptionMessage.USER_ALREADY_ADMIN);
            }
        }
        userStudioMapper.updateRoleByUserAndStudio(memberId,studioId,updateStudioRoleRequest.getRoleId());
    }

    private boolean isCreator(int userId, int studioId) {
        return mapper.selectCreatorByPrimaryKey(studioId) == userId;
    }

    private boolean isSuperAdmin(int userId, int studioId) {
        UserStudio userStudio = userStudioMapper.selectByUserAndStudio(userId, studioId);
        return userStudio != null && userStudio.getRoleId() == StudioRoleEnum.SUPER_ADMIN.getRoleId();
    }

    private boolean isDepartmentAdmin(int userId, int studioId, int departmentId) {
        UserStudio userStudio = userStudioMapper.selectByUserAndStudio(userId, studioId);
        return userStudio != null && userStudio.getDepartmentId() == departmentId &&
                userStudio.getRoleId() == StudioRoleEnum.DEPARTMENT_ADMIN.getRoleId();
    }

    private boolean isMember(int userId, int studioId) {
        return userStudioMapper.selectByUserAndStudio(userId, studioId) != null;
    }

    private void checkSuperAdmin(int userId, int studioId) {
        if (!isSuperAdmin(userId, studioId)) {
            throw new CustomException(CustomExceptionType.PERMISSION_ERROR, ExceptionMessage.NOT_STUDIO_SUPER_ADMIN);
        }
    }

    private int getUserIdBySearchContent(String searchContent){
        //memberId设置成Integer类型为了防止mysql中查询为空返回为null
        Integer memberId = ValidUtil.checkPhoneNumber(searchContent) ?
                userInfoMapper.selectUserIdByPhoneNumber(searchContent) :
                userInfoMapper.selectUserIdByName(searchContent);
        if (memberId == null || memberId == 0){
            throw new CustomException(CustomExceptionType.NORMAL_ERROR,ExceptionMessage.USER_NOT_EXIST);
        }
        return memberId;
    }
}
