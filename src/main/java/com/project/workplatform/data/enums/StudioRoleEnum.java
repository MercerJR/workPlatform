package com.project.workplatform.data.enums;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/23 11:22
 */
public enum StudioRoleEnum {
    /**
     * 成员
     */
    MEMBER(0,"成员"),

    /**
     * 部门管理员
     */
    DEPARTMENT_ADMIN(1,"部门管理员"),

    /**
     * 超级管理员
     */
    CREATOR(2,"超级管理员");

    private final int roleId;

    private final String role;

    StudioRoleEnum(int roleId,String role){
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
