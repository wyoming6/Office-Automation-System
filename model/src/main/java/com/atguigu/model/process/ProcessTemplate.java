package com.atguigu.model.process;

import com.atguigu.model.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("oa_process_template")
public class ProcessTemplate extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@TableField("name")
	private String name;

	@TableField("icon_url")
	private String iconUrl;

	@TableField("process_type_id")
	private Long processTypeId;

	@TableField("form_props")
	private String formProps;

	@TableField("form_options")
	private String formOptions;

	@TableField("description")
	private String description;

	@TableField("process_definition_key")
	private String processDefinitionKey;

	@TableField("process_definition_path")
	private String processDefinitionPath;

	@TableField("process_model_id")
	private String processModelId;

	@TableField("status")
	private Integer status;

	@TableField(exist = false)
	private String processTypeName;
}