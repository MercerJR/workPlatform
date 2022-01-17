package com.project.workplatform.data.enums;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/17 18:44
 */
public enum GroupRoleEnum {

    /**
     * 成员
     */
    MEMBER(0,"成员"),

    /**
     * 管理员
     */
    ADMIN(1,"管理员"),

    /**
     * 群主
     */
    CREATOR(2,"群主");

    private final int roleId;

    private final String role;

    GroupRoleEnum(int roleId,String role){
        this.roleId = roleId;
        this.role = role;
    }

    public int getRoleId() {
        return roleId;
    }

    public String getRole() {
        return role;
    }
}
