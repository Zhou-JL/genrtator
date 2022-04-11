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
 * 角色表
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
@Table(name = "role")
public class Role implements Serializable {

	@Id
	@Column(name = "id", nullable = false)
	private String id;

	@ApiModelProperty("角色解释")
	@Column(name = "explanation", nullable = false)
	private String explanation;

	@ApiModelProperty("角色名称")
	@Column(name = "name", nullable = false)
	private String name;

}