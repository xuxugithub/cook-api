package com.cooking.dto;

import lombok.Data;

import javax.validation.constraints.*;

/**
 * 用户注册请求
 */
@Data
public class UserRegisterRequest {
    
    @NotBlank(message = "姓名不能为空")
    @Size(max = 10, message = "姓名长度不能超过10位")
    private String name;
    
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, message = "密码长度不能少于8位")
    @Pattern(regexp = "^(?!\\d+$).+$", message = "密码不能为纯数字")
    private String password;
}
