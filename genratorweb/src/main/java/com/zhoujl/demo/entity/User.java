package com.zhoujl.demo.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户表
 * @Author: zjl
 * @company: 北京汉唐智创科技有限公司
 * @time: 2022-02-14 14:00:00
 * @see: com.zhoujl.demo.rpcservice.entity
 * @Version: 1.0
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class User implements Serializable {

	@Id
	@Column(name = "id", nullable = false)
	private String id;

	@ApiModelProperty("帐号")
	@Column(name = "account", nullable = false)
	private String account;

	@ApiModelProperty("密码")
	@Column(name = "password", nullable = false)
	private String password;

	@ApiModelProperty("昵称")
	@Column(name = "username", nullable = false)
	private String username;

	@ApiModelProperty("注册时间")
	@Column(name = "reg_time", nullable = false)
	private Date regTime;

}