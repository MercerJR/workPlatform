package com.project.workplatform.service;

import com.project.workplatform.dao.StudioApplyMapper;
import com.project.workplatform.dao.StudioContactInfoMapper;
import com.project.workplatform.dao.StudioDepartmentMapper;
import com.project.workplatform.dao.StudioMapper;
import com.project.workplatform.dao.UserInfoMapper;
import com.project.workplatform.dao.UserMapper;
import com.project.workplatform.dao.UserStudioMapper;
import com.project.workplatform.data.Constant;
import com.project.workplatform.data.enums.StudioRoleEnum;
import com.project.workplatform.data.request.studio.ApplyJoinStudioRequest;
import com.project.workplatform.data.request.studio.ChangeDepartmentRequest;
import com.project.workplatform.data.request.studio.CreateDepartmentRequest;
import com.project.workplatform.data.request.studio.CreateStudioRequest;
import com.project.workplatform.data.request.studio.DealStudioApplyRequest;
import com.project.workplatform.data.request.studio.DeleteDepartmentRequest;
import com.project.workplatform.data.request.studio.DistributeLeaderRequest;
import com.project.workplatform.data.request.studio.InitInviteCodeRequest;
import com.project.workplatform.data.request.studio.InviteJoinStudioRequest;
import com.project.workplatform.data.request.studio.UpdateDepartmentRequest;
import com.project.workplatform.data.request.studio.UpdateStudioContactInfoRequest;
import com.project.workplatform.data.request.studio.UpdateStudioInfoRequest;
import com.project.workplatform.data.request.studio.UpdateStudioRoleRequest;
import com.project.workplatform.data.response.studio.DepartmentMemberResponse;
import com.project.workplatform.data.response.studio.DepartmentMemberTreeResponse;
import com.project.workplatform.data.response.studio.DepartmentResponse;
import com.project.workplatform.data.response.studio.StudioAdminResponse;
import com.project.workplatform.data.response.studio.StudioBaseInfoResponse;
import com.project.workplatform.data.response.studio.StudioContactInfoResponse;
import com.project.workplatform.data.response.studio.StudioInfoResponse;
import com.project.workplatform.data.response.studio.StudioPeopleInfoResponse;
import com.project.workplatform.exception.CustomException;
import com.project.workplatform.exception.CustomExceptionType;
import com.project.workplatform.exception.ExceptionMessage;
import com.project.workplatform.pojo.Studio;
import com.project.workplatform.pojo.StudioApply;
import com.project.workplatform.pojo.StudioContactInfo;
import com.project.workplatform.pojo.StudioDepartment;
import com.project.workplatform.pojo.User;
import com.project.workplatform.pojo.UserInfo;
import com.project.workplatform.pojo.UserStudio;
import com.project.workplatform.util.RandomUtil;
import com.project.workplatform.util.ValidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void create(Integer userId, CreateStudioRequest createStudioRequest) {
        Studio studio = new Studio();
        studio.setStudioName(createStudioRequest.getStudioName());
        studio.setStudioAbbreviation(createStudioRequest.getStudioAbbreviation());
        studio.setCreatorId(userId);
        studio.setClassify(createStudioRequest.getClassify());

        //TODO ????????????????????????????????????
        User user = new User();
        user.setPhoneNumber("1");
        user.setPassword("1");
        userMapper.insert(user);
        int helperId = userMapper.getLastInsertId();
        UserInfo helperInfo = userInfoMapper.selectByUser(helperId);
        helperInfo.setName(createStudioRequest.getStudioAbbreviation() + "?????????");
        userInfoMapper.updateByPrimaryKeySelective(helperInfo);
        //TODO ???????????????????????????????????????????????????userId?????????studio??????
        studio.setHelperId(helperId);
        mapper.insertSelective(studio);

        int studioId = mapper.getLastInsertId();

        //??????????????????
        StudioDepartment department = new StudioDepartment();
        department.setDepartmentName(studio.getStudioAbbreviation());
        department.setStudioId(studioId);
        departmentMapper.insertSelective(department);

        int departmentId = departmentMapper.getLastInsertId();

        //??????userStudio
        UserStudio userStudio = new UserStudio();
        userStudio.setUserId(userId);
        userStudio.setStudioId(studioId);
        userStudio.setDepartmentId(departmentId);
        userStudio.setRoleId(StudioRoleEnum.SUPER_ADMIN.getRoleId());
        userStudioMapper.insertSelective(userStudio);

        //???????????????????????????
        StudioContactInfo studioContactInfo = new StudioContactInfo();
        studioContactInfo.setStudioId(studioId);
        contactInfoMapper.insertSelective(studioContactInfo);

    }

    /**
     * ?????????inviteCode?????????mysql?????????????????????????????????redis?????????????????????????????????????????????????????????????????????????????????key??????????????????
     * ????????????????????????redis??????????????????????????????????????????????????????????????????????????????????????????????????????redis?????????
     * ???????????????????????????????????????redis???????????????????????????????????????????????????????????????
     * ?????????????????????????????????????????????????????????????????????????????????redis???????????????key????????????????????????mysql?????????????????????????????????????????????????????????????????????
     */
    public String initInviteCode(int userId, InitInviteCodeRequest initInviteCodeRequest) {
        int studioId = initInviteCodeRequest.getStudioId();
        checkSuperAdmin(userId, initInviteCodeRequest.getStudioId());
        String inviteCode = null;
        Boolean exist = null;
        //????????????????????????
        do {
            inviteCode = RandomUtil.randomString(8);
            exist = redisTemplate.hasKey(Constant.REDIS_INVITE_CODE_KEY_PREFIX + inviteCode);
        } while (exist != null && exist);

        //????????????????????????????????????????????????????????????????????????????????????????????????
        String oldInviteCode = mapper.selectByPrimaryKey(studioId).getInviteCode();
        if (oldInviteCode != null) {
            redisTemplate.delete(Constant.REDIS_INVITE_CODE_KEY_PREFIX + oldInviteCode);
        }

        //??????????????????MySQL
        mapper.updateInviteCodeByPrimaryKey(inviteCode, studioId);

        //?????????????????????????????????????????????Redis
        String redisKey = Constant.REDIS_INVITE_CODE_KEY_PREFIX + inviteCode;
        redisTemplate.opsForValue().set(redisKey, studioId,
                Duration.ofDays(Constant.INVITE_CODE_EXPIRE_DAY));
        return inviteCode;
    }

    public String getInviteCode(Integer studioId, Integer userId) {
        String inviteCode = mapper.selectByPrimaryKey(studioId).getInviteCode();
        String redisKey = Constant.REDIS_INVITE_CODE_KEY_PREFIX + inviteCode;
        Integer value = (Integer) redisTemplate.opsForValue().get(redisKey);
        if (studioId.equals(value)){
            return inviteCode;
        }else {
            return "?????????????????????????????????????????????????????????";
        }
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
        //TODO ??????????????????????????????

    }

    public void createDepartment(Integer userId, CreateDepartmentRequest createDepartmentRequest) {
        checkSuperAdmin(userId, createDepartmentRequest.getStudioId());
        Integer parentDepartmentId = departmentMapper.selectIdByDepartmentName(
                createDepartmentRequest.getParentDepartmentName());
        if (parentDepartmentId == null) {
            throw new CustomException(CustomExceptionType.NORMAL_ERROR, ExceptionMessage.PARENT_DEPARTMENT_NAME_WRONG);
        }
        StudioDepartment parentDepartment = departmentMapper.selectByPrimaryKey(parentDepartmentId);
        //??????????????????
        String departmentName = parentDepartment.getDepartmentName() +
                "/" + createDepartmentRequest.getDepartmentName();
        //?????????????????????????????????????????????????????????
        if (departmentMapper.selectIdByDepartmentName(departmentName) != null) {
            throw new CustomException(CustomExceptionType.NORMAL_ERROR, ExceptionMessage.DEPARTMENT_ALREADY_EXIST);
        }

        StudioDepartment department = new StudioDepartment();
        department.setDepartmentName(departmentName);
        department.setStudioId(createDepartmentRequest.getStudioId());
        department.setParentDepartmentId(parentDepartmentId);
        department.setType(createDepartmentRequest.getType());
        departmentMapper.insertSelective(department);

        //????????????????????????id??????????????????id?????????????????????????????????
        Integer departmentId = departmentMapper.selectIdByDepartmentName(departmentName);
        String childrenDepartmentId = parentDepartment.getChildrenDepartmentId();
        String newChildrenDepartmentId;
        //???????????????????????????????????????????????????????????????????????????????????????????????????
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
        //??????department??????????????????user_studio?????????
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
        if (studioId == 0) {
            throw new CustomException(CustomExceptionType.NORMAL_ERROR, ExceptionMessage.STUDIO_ID_INVALID);
        }
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
        //??????type????????????????????????????????????
        int roleId = "super".equals(type) ? StudioRoleEnum.SUPER_ADMIN.getRoleId() :
                StudioRoleEnum.DEPARTMENT_ADMIN.getRoleId();
        //???????????????????????????????????????????????????????????????
        if (StringUtils.hasLength(searchContent)) {
            //????????????????????????????????????select???????????????
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
        //????????????????????????userId
        Integer memberId = updateStudioRoleRequest.getUserId();
        memberId = (memberId == null || memberId == 0) ?
                getUserIdByInsideAlias(updateStudioRoleRequest.getInsideAlias()) : memberId;
        checkSelf(memberId, userId);
        UserStudio userStudio = userStudioMapper.selectByUserAndStudio(memberId, studioId);
        //????????????????????????????????????
        if (userStudio == null) {
            throw new CustomException(CustomExceptionType.NORMAL_ERROR, ExceptionMessage.USER_NOT_IN_STUDIO);
        }
        //??????????????????superAdmin????????????????????????????????????
        if (updateStudioRoleRequest.getRoleId() == StudioRoleEnum.SUPER_ADMIN.getRoleId() &&
                userStudio.getRoleId() == StudioRoleEnum.SUPER_ADMIN.getRoleId()) {
            throw new CustomException(CustomExceptionType.NORMAL_ERROR, ExceptionMessage.USER_ALREADY_SUPER_ADMIN);
        }
        if (updateStudioRoleRequest.getRoleId() == StudioRoleEnum.DEPARTMENT_ADMIN.getRoleId()) {
            String departmentName = departmentMapper.selectByPrimaryKey(
                    userStudio.getDepartmentId()).getDepartmentName();
            //??????????????????????????????
            if (!departmentName.equals(updateStudioRoleRequest.getDepartmentName())) {
                throw new CustomException(CustomExceptionType.NORMAL_ERROR, ExceptionMessage.USER_NOT_IN_THAT_DEPARTMENT);
            }
            //??????????????????admin????????????????????????????????????
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
     * ???????????????????????????????????????????????????????????????????????????
     *
     * @param departmentId
     * @param list
     */
    private void getCascadeDepartmentList(int departmentId, List<DepartmentResponse> list) {
        StudioDepartment studioDepartment = departmentMapper.selectByPrimaryKey(departmentId);
        //???departmentResponse??????
        DepartmentResponse departmentResponse = new DepartmentResponse();
        departmentResponse.setIndex(list.size());
        departmentResponse.setDepartmentId(studioDepartment.getId());
        departmentResponse.setDepartmentName(studioDepartment.getDepartmentName());
        departmentResponse.setPeopleNumber(studioDepartment.getPeopleNumber());
        departmentResponse.setLeader(getLeaders(studioDepartment.getId()));
        list.add(departmentResponse);

        //??????????????????
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

    public List<DepartmentMemberTreeResponse> getDepartmentMemberTreeList(int studioId, Integer userId) {
        UserStudio userStudio = userStudioMapper.selectByUserAndStudio(userId, studioId);
        Integer departmentId = userStudio.getDepartmentId();
        List<DepartmentMemberTreeResponse> list = new ArrayList<>();
        getCascadeDepartmentTreeList(departmentId, list);
        return list;
    }

    private void getCascadeDepartmentTreeList(Integer departmentId, List<DepartmentMemberTreeResponse> list) {
        //??????????????????list???
        StudioDepartment department = departmentMapper.selectByPrimaryKey(departmentId);
        DepartmentMemberTreeResponse item = new DepartmentMemberTreeResponse();
        item.setId(departmentId * -1);
        item.setLabel(department.getDepartmentName());
        List<DepartmentMemberTreeResponse> children = new ArrayList<>();
        //???????????????????????????????????????children???
        List<DepartmentMemberResponse> departmentMemberResponses = userStudioMapper.selectMemberByDepartment(departmentId);
        for (DepartmentMemberResponse member : departmentMemberResponses) {
            DepartmentMemberTreeResponse memberItem = new DepartmentMemberTreeResponse();
            memberItem.setId(member.getUserId());
            memberItem.setLabel(member.getInsideAlias());
            children.add(memberItem);
        }
        //???????????????????????????
        String childrenDepartmentId = department.getChildrenDepartmentId();
        if (StringUtils.hasLength(childrenDepartmentId)) {
            String[] childrenDepartmentList = childrenDepartmentId.split(",");
            //????????????????????????????????????????????????children???
            for (String id : childrenDepartmentList) {
                int childDepartmentId = Integer.parseInt(id);
                getCascadeDepartmentTreeList(childDepartmentId, children);
            }
        }
        item.setChildren(children);
        list.add(item);
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
        //?????????????????????????????????????????????
        userStudioMapper.updateDepartmentInfoByUserAndStudio(departmentId, StudioRoleEnum.MEMBER.getRoleId(),
                changeDepartmentRequest.getUserId(), changeDepartmentRequest.getStudioId());
        departmentMapper.decreasePeopleNumber(changeDepartmentRequest.getOldDepartmentId(), 1);
        departmentMapper.increasePeopleNumber(departmentId, 1);
    }

    public void updateDepartment(UpdateDepartmentRequest updateDepartmentRequest, Integer userId) {
        checkSuperAdmin(userId, updateDepartmentRequest.getStudioId());
        Integer newParentDepartmentId = null;
        StudioDepartment newParentDepartment = null;
        //?????????????????????????????????????????????????????????id
        if (StringUtils.hasLength(updateDepartmentRequest.getParentDepartmentName())) {
            newParentDepartmentId = departmentMapper.selectIdByDepartmentName(
                    updateDepartmentRequest.getParentDepartmentName());
            //??????????????????????????????????????????????????????
            newParentDepartment = departmentMapper.selectByPrimaryKey(newParentDepartmentId);
            if (newParentDepartment == null) {
                throw new CustomException(CustomExceptionType.NORMAL_ERROR, ExceptionMessage.PARENT_DEPARTMENT_NAME_WRONG);
            }
            if (!departmentMapper.selectByPrimaryKey(newParentDepartmentId).getStudioId().
                    equals(updateDepartmentRequest.getStudioId())) {
                throw new CustomException(CustomExceptionType.NORMAL_ERROR, ExceptionMessage.PARENT_DEPARTMENT_DO_NOT_BELONG_STUDIO);
            }
        }
        StudioDepartment department = departmentMapper.selectByPrimaryKey(updateDepartmentRequest.getDepartmentId());
        if (newParentDepartmentId != null) {
            //??????????????????????????????
            String childrenStr = newParentDepartment.getChildrenDepartmentId();
            //???????????????????????????????????????????????????","
            if (StringUtils.hasLength(childrenStr)) {
                childrenStr = childrenStr + "," + updateDepartmentRequest.getDepartmentId();
            } else {
                childrenStr = String.valueOf(updateDepartmentRequest.getDepartmentId());
            }
            newParentDepartment.setChildrenDepartmentId(childrenStr);
            //??????????????????????????????
            StudioDepartment oldParentDepartment = departmentMapper.selectByPrimaryKey(department.getParentDepartmentId());
            String childrenStr2 = oldParentDepartment.getChildrenDepartmentId();
            String[] children = childrenStr2.split(",");
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < children.length; i++) {
                int child = Integer.parseInt(children[i]);
                if (child == updateDepartmentRequest.getDepartmentId()) {
                    if (i == children.length - 1 && builder.length() > 0) {
                        builder.deleteCharAt(builder.length() - 1);
                    }
                    continue;
                }
                if (i == children.length - 1) {
                    builder.append(child);
                } else {
                    builder.append(child).append(",");
                }
            }
            oldParentDepartment.setChildrenDepartmentId(builder.toString());
            departmentMapper.updateByPrimaryKeySelective(oldParentDepartment);
            departmentMapper.updateByPrimaryKeySelective(newParentDepartment);
        }
        //??????????????????
        String departmentName = department.getDepartmentName();
        if (StringUtils.hasLength(updateDepartmentRequest.getDepartmentName())) {
            departmentName = updateDepartmentRequest.getDepartmentName();
            if (newParentDepartmentId != null) {
                departmentName = updateDepartmentRequest.getParentDepartmentName() + "/" + departmentName;
            } else {
                String oldDepartmentName = departmentMapper.selectByPrimaryKey(department.getParentDepartmentId()).
                        getDepartmentName();
                departmentName = oldDepartmentName + "/" + departmentName;
            }
        } else {
            String[] split = department.getDepartmentName().split("/");
            String departmentNameSuffix = split[split.length - 1];
            if (newParentDepartmentId != null) {
                departmentName = updateDepartmentRequest.getParentDepartmentName() + "/" + departmentNameSuffix;
            }
        }
        department.setDepartmentName(departmentName);
        department.setParentDepartmentId(newParentDepartmentId);
        departmentMapper.updateByPrimaryKeySelective(department);
    }

    public void deleteDepartment(DeleteDepartmentRequest deleteDepartmentRequest, Integer userId) {
        //TODO ???????????????????????????????????????????????????????????????departmentId??????????????????id??????????????????
        checkSuperAdmin(userId, deleteDepartmentRequest.getStudioId());
        checkTopDepartment(deleteDepartmentRequest.getDepartmentId());
        StudioDepartment department = departmentMapper.selectByPrimaryKey(deleteDepartmentRequest.getDepartmentId());
        String childrenStr = department.getChildrenDepartmentId();
        int parentDepartmentId = department.getParentDepartmentId();
        StudioDepartment parentDepartment = departmentMapper.selectByPrimaryKey(parentDepartmentId);
        //??????????????????
        String[] children = null;
        if (childrenStr != null) {
            children = childrenStr.split(",");
            for (String child : children) {
                int childId = Integer.parseInt(child);
                StudioDepartment childDepartment = departmentMapper.selectByPrimaryKey(childId);
                String[] split = childDepartment.getDepartmentName().split("/");
                String departmentName = parentDepartment.getDepartmentName() + "/" + split[split.length - 1];
                childDepartment.setParentDepartmentId(parentDepartmentId);
                childDepartment.setDepartmentName(departmentName);
                departmentMapper.updateByPrimaryKeySelective(childDepartment);
            }
        }
        //??????????????????
        String childrenStr2 = parentDepartment.getChildrenDepartmentId();
        StringBuilder builder = new StringBuilder();
        if (childrenStr2 != null) {
            String[] children2 = childrenStr2.split(",");
            for (int i = 0; i < children2.length; i++) {
                int child = Integer.parseInt(children2[i]);
                if (child == deleteDepartmentRequest.getDepartmentId()) {
                    if (i == children2.length - 1 && builder.length() > 0) {
                        builder.deleteCharAt(builder.length() - 1);
                    }
                    continue;
                }
                if (i == children2.length - 1) {
                    builder.append(child);
                } else {
                    builder.append(child).append(",");
                }
            }
        }
        if (childrenStr != null) {
            //????????????childrenStr2??????????????????builder????????????????????????????????????
            if (builder.length() > 0) {
                builder.append(",");
            }
            for (int i = 0; i < children.length; i++) {
                int child = Integer.parseInt(children[i]);
                if (i == children.length - 1) {
                    builder.append(child);
                } else {
                    builder.append(child).append(",");
                }
            }
        }
        parentDepartment.setChildrenDepartmentId(builder.toString());
        departmentMapper.updateByPrimaryKeySelective(parentDepartment);
        //??????????????????
        List<UserStudio> userStudioList = userStudioMapper.selectByDepartmentId(deleteDepartmentRequest.getDepartmentId());
        for (UserStudio record : userStudioList) {
            record.setDepartmentId(parentDepartmentId);
            userStudioMapper.updateByPrimaryKeySelective(record);
        }
        departmentMapper.deleteByPrimaryKey(department.getId());
    }

    /**
     * ???????????????????????????????????????????????????????????????????????????List<DepartmentMemberResponse>
     *
     * @param searchContent
     * @param studioId
     * @return List<DepartmentMemberResponse>
     */
    public List<DepartmentMemberResponse> searchMember(String searchContent, Integer studioId) {
        return userStudioMapper.selectMemberByNameFuzzyAndStudio(searchContent, studioId);
    }

    public List<DepartmentMemberResponse> getDepartmentMemberList(int departmentId, Integer userId) {
        return userStudioMapper.selectMemberByDepartment(departmentId);
    }

    public UserStudio getUserStudioInfo(Integer userId, Integer studioId) {
        return userStudioMapper.selectByUserAndStudio(userId, studioId);
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
        if (department != null && department.getDepartmentName().split("/").length == 1) {
            throw new CustomException(CustomExceptionType.NORMAL_ERROR, ExceptionMessage.CAN_NOT_DEAL_TOP_DEPARTMENT);
        }
    }

    private Integer getUserIdByInsideAlias(String searchContent) {
        //memberId?????????Integer??????????????????mysql????????????????????????null
        Integer memberId = ValidUtil.checkPhoneNumber(searchContent) ?
                userInfoMapper.selectUserIdByPhoneNumber(searchContent) :
                userInfoMapper.selectUserIdByName(searchContent);
        if (memberId == null || memberId == 0) {
            throw new CustomException(CustomExceptionType.NORMAL_ERROR, ExceptionMessage.USER_NOT_EXIST);
        }
        return memberId;
    }
}
