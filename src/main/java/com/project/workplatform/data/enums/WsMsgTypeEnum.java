package com.project.workplatform.data.enums;

public enum WsMsgTypeEnum {

    /**
     * 授权
     */
    AUTH(0,"授权"),

    /**
     * 普通消息
     */
    TEXT_MSG(1,"文字消息"),

    /**
     * 错误消息类型
     */
    NOT_EXIST(-1,"错误消息类型");

    private final int type;

    private final String describe;

    WsMsgTypeEnum(int type,String describe){
        this.type = type;
        this.describe = describe;
    }

    public int getType() {
        return type;
    }

    public String getDescribe() {
        return describe;
    }

    public static WsMsgTypeEnum valueOfEnum(Integer selectType) {
        if (selectType == null) {
            return NOT_EXIST;
        }
        for (WsMsgTypeEnum selectTypeEnum : WsMsgTypeEnum.values()) {
            if (selectTypeEnum.getType() == selectType) {
                return selectTypeEnum;
            }
        }
        return NOT_EXIST;
    }

}
