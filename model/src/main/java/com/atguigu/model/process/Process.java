package com.atguigu.model.process;

import com.atguigu.model.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("oa_process")
public class Process extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@TableField("process_code")
	private String processCode;

	@TableField("user_id")
	private Long userId;

	@TableField("process_template_id")
	private Long processTemplateId;

	@TableField("process_type_id")
	private Long processTypeId;

	@TableField("title")
	private String title;

	@TableField("description")
	private String description;

	@TableField("form_values")
	private String formValues;

	@TableField("process_instance_id")
	private String processInstanceId;

	@TableField("current_auditor")
	private String currentAuditor;

	@TableField("status")
	private Integer status;
}