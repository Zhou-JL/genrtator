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

/**
 * 用户角色表
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
@Table(name = "user_role")
public class UserRole implements Serializable {

	@Id
	@Column(name = "id", nullable = false)
	private String id;

	@ApiModelProperty("用户id")
	@Column(name = "user_id", nullable = false)
	private String userId;

	@ApiModelProperty("角色id")
	@Column(name = "role_id", nullable = false)
	private String roleId;

}