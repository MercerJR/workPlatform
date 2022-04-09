package com.project.workplatform.service;

import com.project.workplatform.dao.StudioMapper;
import com.project.workplatform.dao.TodoMapper;
import com.project.workplatform.dao.UserInfoMapper;
import com.project.workplatform.data.WsMessageResponse;
import com.project.workplatform.data.enums.WsMsgTargetTypeEnum;
import com.project.workplatform.data.request.chatInfo.UpdateChatListRequest;
import com.project.workplatform.data.request.todo.AddTodoRequest;
import com.project.workplatform.data.request.todo.DeleteTodoRequest;
import com.project.workplatform.data.response.todo.TodoPageResponse;
import com.project.workplatform.data.response.todo.TodoResponse;
import com.project.workplatform.exception.CustomException;
import com.project.workplatform.exception.CustomExceptionType;
import com.project.workplatform.exception.ExceptionMessage;
import com.project.workplatform.pojo.Todo;
import com.project.workplatform.util.DateFormatUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author zengjingran
 */
@Service
public class TodoService {

    @Autowired
    private TodoMapper mapper;

    @Autowired
    private StudioMapper studioMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private ChatInfoService chatInfoService;

    public void addTodo(AddTodoRequest addTodoRequest, Integer userId) {
        //校验用户是否进入工作室
        checkInStudio(addTodoRequest.getStudioId());
        //TODO 【建表】构造todo数据结构，并存入mysql
        Todo todo = new Todo();
        todo.setTitile(addTodoRequest.getTitle());
        todo.setDescribe(addTodoRequest.getDescribe());
        todo.setDay(DateFormatUtil.getStringDateByDate(addTodoRequest.getDay(),DateFormatUtil.DAY_FORMAT));
        todo.setStartTime(addTodoRequest.getStartTime());
        todo.setEndTime(addTodoRequest.getEndTime());
        todo.setOriginatorId(userId);
        todo.setStudioId(addTodoRequest.getStudioId());
        StringBuilder builder = new StringBuilder();
        Set<Integer> memberSet = addTodoRequest.getMemberSet();
        Iterator<Integer> iterator = memberSet.iterator();
        while (iterator.hasNext()){
            builder.append(iterator.next());
            if (iterator.hasNext()){
                builder.append(",");
            }
        }
        todo.setParticipantId(builder.toString());
        mapper.insertSelective(todo);
        //调用公众号发布方法，采用伪WS方式
        publicUserSendMessage(addTodoRequest);
    }

    private void checkInStudio(Integer studioId) {
        if (studioId == null || studioId <= 0) {
            throw new CustomException(CustomExceptionType.NORMAL_ERROR, ExceptionMessage.PLEASE_ENTER_STUDIO);
        }
    }

    public void publicUserSendMessage(AddTodoRequest addTodoRequest) {
        int chatId = studioMapper.selectByPrimaryKey(addTodoRequest.getStudioId()).getHelperId();
        Set<Integer> memberSet = addTodoRequest.getMemberSet();

        //更新用户聊天列表,并更新用户与公众号聊天记录
        UpdateChatListRequest updateChatListRequest = new UpdateChatListRequest(chatId, 2);
        String time = DateFormatUtil.getStringDateByMiles(System.currentTimeMillis(), DateFormatUtil.MINUTE_FORMAT);
        String content = "hello，你有新的待办项目了，请前往 待办事项 查看";
        for (Integer memberId : memberSet) {
            if (memberId < 0){
                continue;
            }
            //更新用户聊天列表
            chatInfoService.updateChatList(updateChatListRequest, memberId);
            //更新用户聊天记录
            WsMessageResponse messageResponse = new WsMessageResponse();
            messageResponse.setSenderId(chatId);
            messageResponse.setTargetId(memberId);
            messageResponse.setContent(content);
            messageResponse.setTime(time);
            messageResponse.setTargetType(WsMsgTargetTypeEnum.PUBLIC_USER.getType());
            chatInfoService.insertPersonalMsgRecord(messageResponse);
        }
    }

    public void deleteTodo(DeleteTodoRequest deleteTodoRequest, Integer userId) {
        //TODO 判断todo的发起人是否是当前用户，否则报警
        Integer originatorId = mapper.selectByPrimaryKey(deleteTodoRequest.getTodoId()).getOriginatorId();
        if (!userId.equals(originatorId)) {
            throw new CustomException(CustomExceptionType.NORMAL_ERROR, ExceptionMessage.NOT_ORIGINATOR);
        }
        //TODO 执行mysql删除操作
        mapper.deleteByPrimaryKey(deleteTodoRequest.getTodoId());
    }

    public TodoPageResponse getTodoList(Integer studioId, Integer userId) {
        checkInStudio(studioId);
        List<Todo> todoList = mapper.selectByStudio(studioId);
        List<TodoResponse> todoResponseList = new ArrayList<>();
        List<TodoResponse> currentTodoResponseList = new ArrayList<>();
        for (Todo todo : todoList){
            TodoResponse todoResponse = new TodoResponse();
            todoResponse.setTodoId(todo.getId());
            todoResponse.setTitle(todo.getTitile());
            todoResponse.setDescribe(todo.getDescribe());
            todoResponse.setOriginatorId(todo.getOriginatorId());
            todoResponse.setOriginator(userInfoMapper.selectByUser(todo.getOriginatorId()).getName());
            String timePeriod = todo.getDay() + " " + todo.getStartTime() + "-" + todo.getEndTime();
            todoResponse.setTimePeriod(timePeriod);
            StringBuilder participantBuilder = new StringBuilder();
            String[] participantList = todo.getParticipantId().split(",");
            for (int i = 0;i < participantList.length;i++){
                int participantId = Integer.parseInt(participantList[i]);
                String participantName = userInfoMapper.selectByUser(participantId).getName();
                participantBuilder.append(participantName);
                if (i < participantList.length - 1){
                    participantBuilder.append(",");
                }
            }
            todoResponse.setParticipants(participantBuilder.toString());
            todoResponseList.add(todoResponse);
            if (todoInTime(todo.getDay(),todo.getStartTime(),todo.getEndTime())){
                currentTodoResponseList.add(todoResponse);
            }
        }
        return new TodoPageResponse(todoResponseList,currentTodoResponseList);
    }

    private boolean todoInTime(String day, String startTime, String endTime) {
        long currentTimeMillis = System.currentTimeMillis();
        String milesToDay = DateFormatUtil.getStringDateByMiles(currentTimeMillis, DateFormatUtil.DAY_FORMAT);
        if (milesToDay.equals(day)){
            String currentTime = DateFormatUtil.getStringDateByMiles(currentTimeMillis, DateFormatUtil.HOUR_MINUTE_FORMAT);
            int currentTimeNumber = Integer.parseInt(currentTime);
            int startTimeNumber = Integer.parseInt(startTime.split(":")[0] + startTime.split(":")[1]);
            int endTimeNumber = Integer.parseInt(endTime.split(":")[0] + endTime.split(":")[1]);
            return currentTimeNumber >= startTimeNumber && currentTimeNumber <= endTimeNumber;
        }
        return false;
    }
}
