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
import java.util.*;
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
        Integer parentDepartmentId = departmentMapper.selectIdByDepartmentName(
                createDepartmentRequest.getParentDepartmentName());
        if (parentDepartmentId == null) {
            throw new CustomException(CustomExceptionType.NORMAL_ERROR, ExceptionMessage.PARENT_DEPARTMENT_NAME_WRONG);
        }
        StudioDepartment parentDepartment = departmentMapper.selectByPrimaryKey(parentDepartmentId);
        //拼接部门名称
        String departmentName = parentDepartment.getDepartmentName() +
                "/" + createDepartmentRequest.getDepartmentName();
        //校验部门是否已经存在，存在则不重复创建
        if (departmentMapper.selectIdByDepartmentName(departmentName) != null) {
            throw new CustomException(CustomExceptionType.NORMAL_ERROR, ExceptionMessage.DEPARTMENT_ALREADY_EXIST);
        }

        StudioDepartment department = new StudioDepartment();
        department.setDepartmentName(departmentName);
        department.setStudioId(createDepartmentRequest.getStudioId());
        department.setParentDepartmentId(parentDepartmentId);
        department.setType(createDepartmentRequest.getType());
        departmentMapper.insertSelective(department);

        //查询新创建的部门id。因为是自增id，所以需要通过查询获取
        Integer departmentId = departmentMapper.selectIdByDepartmentName(departmentName);
        String childrenDepartmentId = parentDepartment.getChildrenDepartmentId();
        String newChildrenDepartmentId;
        //若上级部门已经有下属部门，则拼接在其后，否则直接赋值给下属部门字段
        if (StringUtils.hasLength(childrenDepartmentId)) {
            newChildrenDepartmentId = childrenDepartmentId + "," + departmentId;
        } else {
            newChildrenDepartmentId = String.valueOf(departmentId);
        }
        parentDepartment.setChildrenDepartmentId(newChildrenDepartmentId);
        departmentMapper.updateByPrimaryKeySelective(parentDepartment);
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
                                                            String type, String searchContent, String select) {
        checkSuperAdmin(userId, studioId);
        //根据type来判断搜索的管理员的类型
        int roleId = "super".equals(type) ? StudioRoleEnum.SUPER_ADMIN.getRoleId() :
                StudioRoleEnum.DEPARTMENT_ADMIN.getRoleId();
        //如果搜索框有内容，则进行搜索，否则返回全部
        if (StringUtils.hasLength(searchContent)) {
            //只有查询部门管理员时才会select才会被赋值
            if ("department".equals(select)) {
                return userStudioMapper.selectAdminByDepartmentNameFuzzy(studioId, roleId, searchContent);
            }
            return ValidUtil.checkPhoneNumber(searchContent) ?
                    userStudioMapper.selectAdminByStudioAndPhone(studioId, roleId, searchContent) :
                    userStudioMapper.selectAdminByStudioAndNameFuzzy(studioId, roleId, searchContent);
        } else {
            return userStudioMapper.selectAdminByStudio(studioId, roleId);
        }
    }

    public void updateStudioRole(UpdateStudioRoleRequest updateStudioRoleRequest, Integer userId) {
        int studioId = updateStudioRoleRequest.getStudioId();
        checkSuperAdmin(userId, studioId);
        //根据搜索内容获得userId
        Integer memberId = updateStudioRoleRequest.getUserId();
        memberId = (memberId == null || memberId == 0) ?
                getUserIdByInsideAlias(updateStudioRoleRequest.getInsideAlias()) : memberId;
        checkSelf(memberId, userId);
        UserStudio userStudio = userStudioMapper.selectByUserAndStudio(memberId, studioId);
        //检查用户是否是工作室成员
        if (userStudio == null) {
            throw new CustomException(CustomExceptionType.NORMAL_ERROR, ExceptionMessage.USER_NOT_IN_STUDIO);
        }
        //如果更新的是superAdmin且用户已经是超级管理员了
        if (updateStudioRoleRequest.getRoleId() == StudioRoleEnum.SUPER_ADMIN.getRoleId() &&
                userStudio.getRoleId() == StudioRoleEnum.SUPER_ADMIN.getRoleId()) {
            throw new CustomException(CustomExceptionType.NORMAL_ERROR, ExceptionMessage.USER_ALREADY_SUPER_ADMIN);
        }
        if (updateStudioRoleRequest.getRoleId() == StudioRoleEnum.DEPARTMENT_ADMIN.getRoleId()) {
            String departmentName = departmentMapper.selectByPrimaryKey(
                    userStudio.getDepartmentId()).getDepartmentName();
            //若用户不是该部门成员
            if (!departmentName.equals(updateStudioRoleRequest.getDepartmentName())) {
                throw new CustomException(CustomExceptionType.NORMAL_ERROR, ExceptionMessage.USER_NOT_IN_THAT_DEPARTMENT);
            }
            //如果更新的是admin且用户已经是部门管理员了
            if (userStudio.getRoleId() == StudioRoleEnum.DEPARTMENT_ADMIN.getRoleId()) {
                throw new CustomException(CustomExceptionType.NORMAL_ERROR, ExceptionMessage.USER_ALREADY_ADMIN);
            }
        }
        userStudioMapper.updateRoleByUserAndStudio(memberId, studioId, updateStudioRoleRequest.getRoleId());
    }

    public List<DepartmentResponse> getDepartmentList(int studioId, Integer userId) {
        List<DepartmentResponse> list = new ArrayList<>();
        StudioDepartment topDepartment = departmentMapper.selectDepartmentByStudioAndParent(studioId, 0);
        getCascadeDepartmentList(topDepartment.getId(), list);
        return list;
    }

    /**
     * 通过递归的方式（类似于递归多叉树）级联获取部门列表
     *
     * @param departmentId
     * @param list
     */
    private void getCascadeDepartmentList(int departmentId, List<DepartmentResponse> list) {
        StudioDepartment studioDepartment = departmentMapper.selectByPrimaryKey(departmentId);
        //为departmentResponse赋值
        DepartmentResponse departmentResponse = new DepartmentResponse();
        departmentResponse.setIndex(list.size());
        departmentResponse.setDepartmentId(studioDepartment.getId());
        departmentResponse.setDepartmentName(studioDepartment.getDepartmentName());
        departmentResponse.setPeopleNumber(studioDepartment.getPeopleNumber());
        departmentResponse.setLeader(getLeaders(studioDepartment.getId()));
        list.add(departmentResponse);

        //获取孩子部门
        if (!StringUtils.hasLength(studioDepartment.getChildrenDepartmentId())) {
            return;
        }
        String childrenDepartmentId = studioDepartment.getChildrenDepartmentId();
        String[] children = childrenDepartmentId.split(",");
        for (String id : children) {
            int childDepartmentId = Integer.parseInt(id);
            getCascadeDepartmentList(childDepartmentId, list);
        }
    }

    private String getLeaders(Integer departmentId) {
        List<String> list = userStudioMapper.selectLeaderNameByDepartment(departmentId);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i == list.size() - 1) {
                builder.append(list.get(i));
            } else {
                builder.append(list.get(i)).append(",");
            }
        }
        return builder.toString();
    }

    public void changeDepartment(ChangeDepartmentRequest changeDepartmentRequest, Integer userId) {
        checkSelf(changeDepartmentRequest.getUserId(), userId);
        checkSuperAdmin(userId, changeDepartmentRequest.getStudioId());
        Integer departmentId = departmentMapper.selectIdByDepartmentName(changeDepartmentRequest.getDepartmentName());
        if (departmentId == null || departmentId == 0) {
            throw new CustomException(CustomExceptionType.NORMAL_ERROR, ExceptionMessage.DEPARTMENT_NAME_WRONG);
        }
        //默认转换部门之后角色为部门成员
        userStudioMapper.updateDepartmentInfoByUserAndStudio(departmentId, StudioRoleEnum.MEMBER.getRoleId(),
                changeDepartmentRequest.getUserId(), changeDepartmentRequest.getStudioId());
        departmentMapper.decreasePeopleNumber(changeDepartmentRequest.getOldDepartmentId(), 1);
        departmentMapper.increasePeopleNumber(departmentId, 1);
    }

    public void updateDepartment(UpdateDepartmentRequest updateDepartmentRequest, Integer userId) {
        checkSuperAdmin(userId, updateDepartmentRequest.getStudioId());
        Integer newParentDepartmentId = null;
        StudioDepartment newParentDepartment = null;
        //如果上级部门名称不为空，则查询上级部门id
        if (StringUtils.hasLength(updateDepartmentRequest.getParentDepartmentName())) {
            newParentDepartmentId = departmentMapper.selectIdByDepartmentName(
                    updateDepartmentRequest.getParentDepartmentName());
            //校验新输入的上级部门是否属于该工作室
            newParentDepartment = departmentMapper.selectByPrimaryKey(newParentDepartmentId);
            if (newParentDepartment == null){
                throw new CustomException(CustomExceptionType.NORMAL_ERROR,ExceptionMessage.PARENT_DEPARTMENT_NAME_WRONG);
            }
            if (!departmentMapper.selectByPrimaryKey(newParentDepartmentId).getStudioId().
                    equals(updateDepartmentRequest.getStudioId())) {
                throw new CustomException(CustomExceptionType.NORMAL_ERROR, ExceptionMessage.PARENT_DEPARTMENT_DO_NOT_BELONG_STUDIO);
            }
        }
        StudioDepartment department = departmentMapper.selectByPrimaryKey(updateDepartmentRequest.getDepartmentId());
        if (newParentDepartmentId != null){
            //更新新的上级部门信息
            String childrenStr = newParentDepartment.getChildrenDepartmentId();
            //如果新部门已经有下级部门，则先添加","
            if (StringUtils.hasLength(childrenStr)){
                childrenStr = childrenStr + "," + updateDepartmentRequest.getDepartmentId();
            }else{
                childrenStr = String.valueOf(updateDepartmentRequest.getDepartmentId());
            }
            newParentDepartment.setChildrenDepartmentId(childrenStr);
            //更新旧的上级部门信息
            StudioDepartment oldParentDepartment = departmentMapper.selectByPrimaryKey(department.getParentDepartmentId());
            String childrenStr2 = oldParentDepartment.getChildrenDepartmentId();
            String[] children = childrenStr2.split(",");
            StringBuilder builder = new StringBuilder();
            for (int i = 0;i < children.length;i++){
                int child = Integer.parseInt(children[i]);
                if (child == updateDepartmentRequest.getDepartmentId()){
                    if (i == children.length - 1 && builder.length() > 0){
                        builder.deleteCharAt(builder.length() - 1);
                    }
                    continue;
                }
                if (i == children.length - 1){
                    builder.append(child);
                }else {
                    builder.append(child).append(",");
                }
            }
            oldParentDepartment.setChildrenDepartmentId(builder.toString());
            departmentMapper.updateByPrimaryKeySelective(oldParentDepartment);
            departmentMapper.updateByPrimaryKeySelective(newParentDepartment);
        }
        //更新部门信息
        String departmentName = department.getDepartmentName();
        if (StringUtils.hasLength(updateDepartmentRequest.getDepartmentName())){
            departmentName = updateDepartmentRequest.getDepartmentName();
            if (newParentDepartmentId != null){
                departmentName = updateDepartmentRequest.getParentDepartmentName() + "/" + departmentName;
            }else{
                String oldDepartmentName = departmentMapper.selectByPrimaryKey(department.getParentDepartmentId()).
                        getDepartmentName();
                departmentName = oldDepartmentName + "/" + departmentName;
            }
        }else{
            String[] split = department.getDepartmentName().split("/");
            String departmentNameSuffix = split[split.length - 1];
            if (newParentDepartmentId != null){
                departmentName = updateDepartmentRequest.getParentDepartmentName() + "/" + departmentNameSuffix;
            }
        }
        department.setDepartmentName(departmentName);
        department.setParentDepartmentId(newParentDepartmentId);
        departmentMapper.updateByPrimaryKeySelective(department);
    }

    public void deleteDepartment(DeleteDepartmentRequest deleteDepartmentRequest, Integer userId) {
        //TODO 更新下级部门，更新上级部门，更改部门成员的departmentId为上级部门的id，删除该部门
        checkSuperAdmin(userId,deleteDepartmentRequest.getStudioId());
        checkTopDepartment(deleteDepartmentRequest.getDepartmentId());
        StudioDepartment department = departmentMapper.selectByPrimaryKey(deleteDepartmentRequest.getDepartmentId());
        String childrenStr = department.getChildrenDepartmentId();
        int parentDepartmentId = department.getParentDepartmentId();
        StudioDepartment parentDepartment = departmentMapper.selectByPrimaryKey(parentDepartmentId);
        //更新下级部门
        String[] children = null;
        if (childrenStr != null){
            children = childrenStr.split(",");
            for (String child : children){
                int childId = Integer.parseInt(child);
                StudioDepartment childDepartment = departmentMapper.selectByPrimaryKey(childId);
                String[] split = childDepartment.getDepartmentName().split("/");
                String departmentName = parentDepartment.getDepartmentName() + "/" + split[split.length - 1];
                childDepartment.setParentDepartmentId(parentDepartmentId);
                childDepartment.setDepartmentName(departmentName);
                departmentMapper.updateByPrimaryKeySelective(childDepartment);
            }
        }
        //更新上级部门
        String childrenStr2 = parentDepartment.getChildrenDepartmentId();
        StringBuilder builder = new StringBuilder();
        if (childrenStr2 != null){
            String[] children2 = childrenStr2.split(",");
            for (int i = 0;i < children2.length;i++){
                int child = Integer.parseInt(children2[i]);
                if (child == deleteDepartmentRequest.getDepartmentId()){
                    if (i == children2.length - 1 && builder.length() > 0){
                        builder.deleteCharAt(builder.length() - 1);
                    }
                    continue;
                }
                if (i == children2.length - 1){
                    builder.append(child);
                }else {
                    builder.append(child).append(",");
                }
            }
        }
        if (childrenStr != null){
            //如果上面childrenStr2不为空，说明builder中已经有内容，则添加逗号
            if (builder.length() > 0){
                builder.append(",");
            }
            for (int i = 0;i < children.length;i++){
                int child = Integer.parseInt(children[i]);
                if (i == children.length - 1){
                    builder.append(child);
                }else {
                    builder.append(child).append(",");
                }
            }
        }
        parentDepartment.setChildrenDepartmentId(builder.toString());
        departmentMapper.updateByPrimaryKeySelective(parentDepartment);
        //更新部门成员
        List<UserStudio> userStudioList = userStudioMapper.selectByDepartmentId(deleteDepartmentRequest.getDepartmentId());
        for (UserStudio record : userStudioList){
            record.setDepartmentId(parentDepartmentId);
            userStudioMapper.updateByPrimaryKeySelective(record);
        }
        departmentMapper.deleteByPrimaryKey(department.getId());
    }

    /**
     * 此处因为返回内容和部门成员列表返回的一致，因此沿用List<DepartmentMemberResponse>
     * @param searchContent
     * @param studioId
     * @return List<DepartmentMemberResponse>
     */
    public List<DepartmentMemberResponse> searchMember(String searchContent, Integer studioId) {
        return userStudioMapper.selectMemberByNameFuzzyAndStudio(searchContent,studioId);
    }

    public List<DepartmentMemberResponse> getDepartmentMemberList(int departmentId, Integer userId) {
        return userStudioMapper.selectMemberByDepartment(departmentId);
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

    private void checkSelf(int memberId, int userId) {
        if (memberId == userId) {
            throw new CustomException(CustomExceptionType.NORMAL_ERROR, ExceptionMessage.CAN_NOT_DEAL_YOURSELF);
        }
    }

    private void checkTopDepartment(Integer departmentId) {
        StudioDepartment department = departmentMapper.selectByPrimaryKey(departmentId);
        if (department != null && department.getDepartmentName().split("/").length == 1){
            throw new CustomException(CustomExceptionType.NORMAL_ERROR,ExceptionMessage.CAN_NOT_DEAL_TOP_DEPARTMENT);
        }
    }

    private Integer getUserIdByInsideAlias(String searchContent) {
        //memberId设置成Integer类型为了防止mysql中查询为空返回为null
        Integer memberId = ValidUtil.checkPhoneNumber(searchContent) ?
                userInfoMapper.selectUserIdByPhoneNumber(searchContent) :
                userInfoMapper.selectUserIdByName(searchContent);
        if (memberId == null || memberId == 0) {
            throw new CustomException(CustomExceptionType.NORMAL_ERROR, ExceptionMessage.USER_NOT_EXIST);
        }
        return memberId;
    }
}
