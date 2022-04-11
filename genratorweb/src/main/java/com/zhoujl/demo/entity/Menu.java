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
 * 菜单表
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
@Table(name = "menu")
public class Menu implements Serializable {

	@Id
	@Column(name = "id", nullable = false)
	private String id;

	@ApiModelProperty("菜单的名字")
	@Column(name = "menu_name", nullable = false)
	private String menuName;

	@ApiModelProperty("菜单等级")
	@Column(name = "menu_level", nullable = false)
	private String menuLevel;

	@ApiModelProperty("父类菜单id")
	@Column(name = "parent_menu_id", nullable = false)
	private String parentMenuId;

}