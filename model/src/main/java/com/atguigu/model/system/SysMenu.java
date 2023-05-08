package com.atguigu.model.system;

import com.atguigu.model.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

@Data
@TableName("sys_menu")
public class SysMenu extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@TableField("parent_id")
	private Long parentId;

	@TableField("name")
	private String name;

	@TableField("type")
	private Integer type;

	@TableField("path")
	private String path;

	@TableField("component")
	private String component;

	@TableField("perms")
	private String perms;

	@TableField("icon")
	private String icon;

	@TableField("sort_value")
	private Integer sortValue;

	@TableField("status")
	private Integer status;

	// 下级列表
	@TableField(exist = false)
	private List<SysMenu> children;
	//是否选中
	@TableField(exist = false)
	private boolean isSelect;
}

