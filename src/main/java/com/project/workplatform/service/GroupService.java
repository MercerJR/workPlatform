package com.project.workplatform.service;

import com.project.workplatform.dao.GroupApplyMapper;
import com.project.workplatform.dao.GroupMapper;
import com.project.workplatform.dao.UserGroupMapper;
import com.project.workplatform.data.Constant;
import com.project.workplatform.data.enums.GroupRoleEnum;
import com.project.workplatform.data.request.group.*;
import com.project.workplatform.data.response.group.ApplyUserResponse;
import com.project.workplatform.data.response.group.GroupInfoResponse;
import com.project.workplatform.data.response.group.GroupResponse;
import com.project.workplatform.data.response.group.MemberResponse;
import com.project.workplatform.exception.CustomException;
import com.project.workplatform.exception.CustomExceptionType;
import com.project.workplatform.exception.ExceptionMessage;
import com.project.workplatform.pojo.Group;
import com.project.workplatform.pojo.GroupApply;
import com.project.workplatform.pojo.UserGroup;
import com.project.workplatform.util.DateFormatUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/15 15:34
 */
@Service
@Transactional(rollbackFor = RuntimeException.class)
public class GroupService {

    @Autowired
    private GroupMapper mapper;

    @Autowired
    private UserGroupMapper userGroupMapper;

    @Autowired
    private GroupApplyMapper groupApplyMapper;

    public void createGroup(Integer userId, CreateGroupRequest createGroupRequest) {
        Group group = new Group();
        group.setGroupName(createGroupRequest.getGroupName());
        group.setCreatorId(userId);
        group.setType(createGroupRequest.isInside() ? 1 : 0);
        group.setClassify(createGroupRequest.getClassify());

        //TODO 如果是工作室内部群聊，将查询该用户的所属工作室并赋值到group的studio_id属性中。若该用户未加入任何工作室，则报错
        if (createGroupRequest.isInside()){
            int studioId = 0;
            group.setStudioId(studioId);
        }

        mapper.insertSelective(group);
    }

    public void updateGroupInfo(Integer userId, UpdateGroupRequest updateGroupRequest) {
        //校验用户是否为该群的群主
        if (!checkCreator(userId,updateGroupRequest.getGroupId())){
            throw new CustomException(CustomExceptionType.PERMISSION_ERROR,ExceptionMessage.NOT_CREATOR);
        }
        Group group = new Group();
        group.setId(updateGroupRequest.getGroupId());
        group.setGroupName(updateGroupRequest.getGroupName());
        group.setClassify(updateGroupRequest.getClassify());
        mapper.updateByPrimaryKeySelective(group);
    }

    public void applyJoin(Integer userId, ApplyJoinGroupRequest applyJoinGroupRequest) {
        //校验用户是否已经是群聊成员
        if (checkUserInGroup(userId,applyJoinGroupRequest.getGroupId())){
            throw new CustomException(CustomExceptionType.NORMAL_ERROR,ExceptionMessage.ALREADY_GROUP_MEMBER);
        }
        GroupApply apply = new GroupApply();
        apply.setUserId(userId);
        apply.setGroupId(applyJoinGroupRequest.getGroupId());
        apply.setApplyMessage(applyJoinGroupRequest.getApplyMessage());
        groupApplyMapper.insertSelective(apply);
    }

    public List<ApplyUserResponse> getApplyList(Integer userId, Integer groupId) {
        //校验用户是否为该群聊的管理员
        if (!checkAdmin(userId,groupId)){
            throw new CustomException(CustomExceptionType.PERMISSION_ERROR,ExceptionMessage.NOT_ADMIN);
        }
        return groupApplyMapper.selectApplyList(groupId);
    }

    public void approveApply(Integer userId, ApproveApplyRequest approveApplyRequest) {
        GroupApply groupApply = groupApplyMapper.selectByPrimaryKey(approveApplyRequest.getApplyId());
        //检查请求是否已经被处理过
        if (groupApply.getTag() != 0){
            throw new CustomException(CustomExceptionType.NORMAL_ERROR,ExceptionMessage.APPLY_ALREADY_DEAL);
        }
        //校验用户是否为该群聊的管理员
        if (!checkAdmin(userId,groupApply.getGroupId())){
            throw new CustomException(CustomExceptionType.PERMISSION_ERROR,ExceptionMessage.NOT_ADMIN);
        }

        if (approveApplyRequest.isResponse()){
            //校验用户是否已经是群聊成员
            if (checkUserInGroup(groupApply.getUserId(),groupApply.getGroupId())){
                throw new CustomException(CustomExceptionType.NORMAL_ERROR,ExceptionMessage.ALREADY_GROUP_MEMBER);
            }
            UserGroup userGroup = new UserGroup();
            userGroup.setUserId(groupApply.getUserId());
            userGroup.setGroupId(groupApply.getGroupId());
            userGroupMapper.insertSelective(userGroup);
        }
        //更新apply
        int tag = approveApplyRequest.isResponse() ? 1 : 2;
        groupApplyMapper.updateTag(approveApplyRequest.getApplyId(),tag);
    }

    public GroupInfoResponse getGroupInfo(int groupId) {
        Group group = mapper.selectByPrimaryKey(groupId);
        if (group == null){
            throw new CustomException(CustomExceptionType.NORMAL_ERROR,ExceptionMessage.GROUP_NOT_EXIST);
        }
        GroupInfoResponse groupInfo = new GroupInfoResponse();
        groupInfo.setGroupName(group.getGroupName());
        groupInfo.setType(group.getType() == 0 ? Constant.OUTSIDE_GROUP : Constant.INNER_GROUP);
        groupInfo.setGroupName(group.getGroupName());
        groupInfo.setClassify(group.getClassify());
        groupInfo.setCreateTime(DateFormatUtil.getStringDateByDate(group.getCreateTime()));
        return groupInfo;
    }

    public List<MemberResponse> getMemberList(Integer userId, int groupId) {
        if (userGroupMapper.selectByUserAndGroup(userId,groupId) == null){
            throw new CustomException(CustomExceptionType.NORMAL_ERROR,ExceptionMessage.NOT_IN_GROUP);
        }
        return userGroupMapper.selectMemberByGroup(groupId);
    }

    public List<GroupResponse> getGroupList(Integer userId) {
        return userGroupMapper.selectByUser(userId);
    }

    public void updateRole(Integer userId, UpdateRoleRequest updateRoleRequest) {
        //检查操作用户是不是群主
        if (!checkCreator(userId,updateRoleRequest.getGroupId())){
            throw new CustomException(CustomExceptionType.PERMISSION_ERROR,ExceptionMessage.NOT_CREATOR);
        }
        //检查被操作的用户是否还在群聊中
        if (!checkUserInGroup(updateRoleRequest.getMemberId(),updateRoleRequest.getGroupId())){
            throw new CustomException(CustomExceptionType.NORMAL_ERROR,ExceptionMessage.USER_NOT_IN_GROUP);
        }
        userGroupMapper.updateRoleByUserAndGroup(updateRoleRequest.getRoleId(),
                updateRoleRequest.getMemberId(),updateRoleRequest.getGroupId());
    }

    private boolean checkUserInGroup(int userId,int groupId){
        return userGroupMapper.selectByUserAndGroup(userId, groupId) != null;
    }

    private boolean checkAdmin(int userId,int groupId){
        return userGroupMapper.selectByUserAndGroup(userId,groupId).getRoleId() != GroupRoleEnum.MEMBER.getRoleId();
    }

    private boolean checkCreator(int userId,int groupId){
        return userGroupMapper.selectByUserAndGroup(userId,groupId).getRoleId() == GroupRoleEnum.CREATOR.getRoleId();
    }

}
