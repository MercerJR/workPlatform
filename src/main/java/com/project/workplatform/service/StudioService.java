package com.project.workplatform.service;

import com.project.workplatform.dao.StudioApplyMapper;
import com.project.workplatform.dao.StudioMapper;
import com.project.workplatform.dao.UserStudioMapper;
import com.project.workplatform.data.Constant;
import com.project.workplatform.data.request.studio.ApplyJoinStudioRequest;
import com.project.workplatform.data.request.studio.CreateStudioRequest;
import com.project.workplatform.data.request.studio.InitInviteCodeRequest;
import com.project.workplatform.data.request.studio.InviteJoinStudioRequest;
import com.project.workplatform.data.response.studio.StudioInfoResponse;
import com.project.workplatform.exception.CustomException;
import com.project.workplatform.exception.CustomExceptionType;
import com.project.workplatform.exception.ExceptionMessage;
import com.project.workplatform.pojo.Studio;
import com.project.workplatform.pojo.StudioApply;
import com.project.workplatform.pojo.UserStudio;
import com.project.workplatform.util.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

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
    private RedisTemplate<String,Object> redisTemplate;

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
    public String initInviteCode(int userId, InitInviteCodeRequest initInviteCodeRequest){
        int studioId = initInviteCodeRequest.getStudioId();
        if (!checkSuperAdmin(userId,studioId)){
            throw new CustomException(CustomExceptionType.PERMISSION_ERROR, ExceptionMessage.NOT_STUDIO_SUPER_ADMIN);
        }
        String inviteCode = null;
        Boolean exist = null;
        //防止重复的邀请码
        do {
            inviteCode = RandomUtil.randomString(8);
            exist = redisTemplate.hasKey(Constant.REDIS_INVITE_CODE_KEY_PREFIX + inviteCode);
        }while (exist != null && exist);

        //先检查工作室有没有旧的且没过期的邀请码，若有则先将其在缓存中删除
        String oldInviteCode = mapper.selectByPrimaryKey(studioId).getInviteCode();
        if (oldInviteCode != null){
            redisTemplate.delete(Constant.REDIS_INVITE_CODE_KEY_PREFIX + oldInviteCode);
        }

        //将邀请码存入MySQL
        mapper.updateInviteCodeByPrimaryKey(inviteCode,studioId);

        //将邀请码及邀请码的过期时间存入Redis
        String redisKey = Constant.REDIS_INVITE_CODE_KEY_PREFIX + inviteCode;
        redisTemplate.opsForValue().set(redisKey, String.valueOf(studioId),
                Duration.ofDays(initInviteCodeRequest.getExpireDay()));
        return inviteCode;
    }

    public Integer checkInviteCode(String inviteCode){
        String redisKey = Constant.REDIS_INVITE_CODE_KEY_PREFIX + inviteCode;
        String redisValue = (String) redisTemplate.opsForValue().get(redisKey);
        if (redisValue == null){
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
        if (checkDepartmentAdmin(userId,inviteJoinStudioRequest.getStudioId(),
                inviteJoinStudioRequest.getDepartmentId())){
            throw new CustomException(CustomExceptionType.PERMISSION_ERROR,ExceptionMessage.NOT_STUDIO_ADMIN);
        }
        StudioApply apply = new StudioApply();
        apply.setUserId(inviteJoinStudioRequest.getInviteUserId());
        apply.setStudioId(inviteJoinStudioRequest.getStudioId());
        apply.setDepartmentId(inviteJoinStudioRequest.getDepartmentId());
        apply.setApplyTag(1);
        applyMapper.insertSelective(apply);
    }

    public StudioInfoResponse getStudioInfo(int userId,int studioId) {
        if (!checkMember(userId, studioId)){
            throw new CustomException(CustomExceptionType.PERMISSION_ERROR,ExceptionMessage.NOT_STUDIO_MEMBER);
        }
        return mapper.selectStudioInfoByPrimaryKey(studioId);
    }

    private boolean checkCreator(int userId,int studioId){
        return mapper.selectCreatorByPrimaryKey(studioId) == userId;
    }

    private boolean checkSuperAdmin(int userId,int studioId){
        UserStudio userStudio = userStudioMapper.selectByUserAndStudio(userId, studioId);
        return userStudio != null && userStudio.getAdminTag() == 2;
    }

    private boolean checkDepartmentAdmin(int userId,int studioId,int departmentId){
        UserStudio userStudio = userStudioMapper.selectByUserAndStudio(userId, studioId);
        return userStudio != null && userStudio.getDepartmentId() == departmentId &&
                userStudio.getAdminTag() == 1;
    }

    private boolean checkMember(int userId,int studioId){
        return userStudioMapper.selectByUserAndStudio(userId,studioId) != null;
    }

}
