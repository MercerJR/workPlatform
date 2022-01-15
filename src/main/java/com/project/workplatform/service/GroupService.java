package com.project.workplatform.service;

import com.project.workplatform.dao.GroupApplyMapper;
import com.project.workplatform.dao.GroupMapper;
import com.project.workplatform.dao.UserGroupMapper;
import com.project.workplatform.data.request.group.ApplyJoinGroupRequest;
import com.project.workplatform.data.request.group.CreateGroupRequest;
import com.project.workplatform.data.request.group.UpdateGroupRequest;
import com.project.workplatform.exception.CustomException;
import com.project.workplatform.exception.CustomExceptionType;
import com.project.workplatform.exception.ExceptionMessage;
import com.project.workplatform.pojo.Group;
import com.project.workplatform.pojo.GroupApply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        //TODO 除了校验用户是否为群聊创始人身份外，还考虑是否需要加入管理员身份的校验
        if (mapper.selectCreatorByGroup(updateGroupRequest.getGroupId()) != userId){
            throw new CustomException(CustomExceptionType.PERMISSION_ERROR, ExceptionMessage.NOT_ADMIN);
        }
        Group group = new Group();
        group.setId(updateGroupRequest.getGroupId());
        group.setGroupName(updateGroupRequest.getGroupName());
        group.setClassify(updateGroupRequest.getClassify());
        mapper.updateByPrimaryKeySelective(group);
    }

    public void applyJoin(Integer userId, ApplyJoinGroupRequest applyJoinGroupRequest) {
        if (userGroupMapper.selectByUserAndGroup(userId,applyJoinGroupRequest.getGroupId()) != null){
            throw new CustomException(CustomExceptionType.NORMAL_ERROR,ExceptionMessage.ALREADY_GROUP_MEMBER);
        }
        GroupApply apply = new GroupApply();
        apply.setUserId(userId);
        apply.setGroupId(applyJoinGroupRequest.getGroupId());
        apply.setApplyMessage(applyJoinGroupRequest.getApplyMessage());
        groupApplyMapper.insertSelective(apply);
    }
}
