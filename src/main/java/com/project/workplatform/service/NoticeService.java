package com.project.workplatform.service;

import com.project.workplatform.dao.FriendApplyMapper;
import com.project.workplatform.dao.GroupApplyMapper;
import com.project.workplatform.dao.StudioApplyMapper;
import com.project.workplatform.dao.UserInfoMapper;
import com.project.workplatform.data.response.notice.NoticeResponse;
import com.project.workplatform.pojo.FriendApply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zengjingran
 */
@Service
@Transactional(rollbackFor = RuntimeException.class)
public class NoticeService {

    @Autowired
    private FriendApplyMapper friendApplyMapper;

    @Autowired
    private GroupApplyMapper groupApplyMapper;

    @Autowired
    private StudioApplyMapper studioApplyMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    public List<NoticeResponse> getNoticeList(Integer userId) {
        List<NoticeResponse> responseList = new ArrayList<>();
        List<FriendApply> friendApplyList = friendApplyMapper.selectByUser(userId);
        for (FriendApply apply : friendApplyList){
            //如果自己是申请人并且申请并未被回复
            if (apply.getUserId().equals(userId) && apply.getTag() == 0){
                continue;
            }
            NoticeResponse response = new NoticeResponse();
            response.setNoticeType(0);
            //分为自己是申请人和自己是被申请人两种情况
            if (apply.getUserId().equals(userId)){
                String targetName = userInfoMapper.selectByUser(apply.getTargetId()).getName();
                String res = apply.getTag() == 1 ? "同意了你的好友请求" : "拒绝了你的好友请求";
                String title = targetName + res;
                response.setNoticeTitle(title);
                response.setNoticeContent("");
                response.setNeedDeal(false);
            }else {
                String applyName = userInfoMapper.selectByUser(apply.getUserId()).getName();
                if (apply.getTag() == 0){
                    String title = applyName + "请求添加你为好友";
                    response.setNoticeTitle(title);
                    response.setNoticeContent(apply.getApplyMessage());
                    response.setNeedDeal(true);
                }else {
                    String res = apply.getTag() == 1 ? "已同意" : "已拒绝";
                    String title = res + applyName + "的好友申请";
                    response.setNoticeTitle(title);
                    response.setNoticeContent("");
                    response.setNeedDeal(false);
                }
            }
            responseList.add(response);
        }
        return responseList;
    }
}
