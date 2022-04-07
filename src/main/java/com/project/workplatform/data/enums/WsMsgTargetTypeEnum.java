package com.project.workplatform.data.enums;

public enum WsMsgTargetTypeEnum {

    /**
     * 单人聊天信息
     */
    PERSONAL(0,"单人聊天信息"),

    /**
     * 群聊信息
     */
    GROUP(1,"群聊信息"),

    /**
     * 公众号信息
     */
    PUBLIC_USER(2,"公众号信息"),

    /**
     * 错误消息类型
     */
    WRONG_TARGET(-1,"错误类型");

    private final int targetType;

    private final String describe;

    WsMsgTargetTypeEnum(int targetType,String describe){
        this.targetType = targetType;
        this.describe = describe;
    }

    public int getType() {
        return targetType;
    }

    public String getDescribe() {
        return describe;
    }

    public static WsMsgTargetTypeEnum valueOfEnum(Integer selectType) {
        if (selectType == null) {
            return WRONG_TARGET;
        }
        for (WsMsgTargetTypeEnum selectTypeEnum : WsMsgTargetTypeEnum.values()) {
            if (selectTypeEnum.getType() == selectType) {
                return selectTypeEnum;
            }
        }
        return WRONG_TARGET;
    }
    
}
