package com.itheima.sh.stock.pojo.vo;

/**
 * @author by itheima
 * @Date 2021/12/21
 * @Description
 */
public enum ResponseCode{
    ERROR(0,"操作失败"),
    SUCCESS(1,"操作成功"),
    DATA_ERROR(0,"参数异常"),
    NO_RESPONSE_DATA(0,"无响应数据"),
    CHECK_CODE_NOT_EMPTY(0,"验证码不能为空"),
    CHECK_CODE_ERROR(0,"验证码错误"),
    USERNAME_OR_PASSWORD_ERROR(0,"用户名或密码错误"),
    ACCOUNT_EXISTS_ERROR(0,"该账号已存在"),
    TOKEN_ERROR(2,"用户未登录，请先登录"),
    NOT_PERMISSION(3,"没有权限访问该资源"),
    OPERATION_MENU_PERMISSION_CATALOG_ERROR(0,"操作后的菜单类型是目录，所属菜单必须为默认顶级菜单或者目录"),
    OPERATION_MENU_PERMISSION_MENU_ERROR(0,"操作后的菜单类型是菜单，所属菜单必须为目录类型"),
    OPERATION_MENU_PERMISSION_BTN_ERROR(0,"操作后的菜单类型是按钮，所属菜单必须为菜单类型"),
    OPERATION_MENU_PERMISSION_URL_CODE_NULL(0,"菜单权限的按钮标识不能为空"),
    ROLE_PERMISSION_RELATION(0, "该菜单权限存在子集关联，不允许删除");
    private int code;//状态码
    private String message;//消息

    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
