package com.atguigu.vo.process;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class ProcessQueryVo {

	private String keyword;
	private Long userId;

	@TableField("process_template_id")
	private Long processTemplateId;

	private Long processTypeId;
	private String createTimeBegin;
	private String createTimeEnd;
	private Integer status;


}