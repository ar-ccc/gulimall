package com.arccc.gulimall.search.entity;

import com.arccc.common.validator.group.AddGroup;
import com.arccc.common.validator.group.UpdateGroup;
import com.arccc.common.validator.group.UpdateStatus;
import com.arccc.common.validator.valid.ListValue;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * 品牌
 * 
 * @author arccc
 * @email c23zxcvbnm@163.com
 * @date 2022-06-03 10:04:22
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	@TableId
	@Null(message = "新增不能带有ID", groups = {AddGroup.class})
	@NotNull(message = "修改不能没有ID",groups = {UpdateGroup.class})
	@NotNull(message = "")
	private Long brandId;
	/**
	 * 品牌名
	 */
	@NotBlank(message = "品牌名不能为空！！",groups = {AddGroup.class})
	@Length(min = 1,message = "品牌名不能为空",groups = {UpdateGroup.class})
	private String name;
	/**
	 * 品牌logo地址
	 */
	@URL(message = "logo必须是一个合法的url地址",groups = {UpdateGroup.class,AddGroup.class})
	@NotNull(message = "logo地址不能为空",groups = {AddGroup.class})
	@Length(min = 1,message = "logo地址不能为空",groups = {UpdateGroup.class})
	private String logo;
	/**
	 * 介绍
	 */
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	@ListValue(message = "必须选择指定的值",vals = {0,1},groups = {AddGroup.class, UpdateStatus.class})
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	@Pattern(regexp = "^[a-zA-Z]$",message = "首字母必须是一个字母",groups = {UpdateGroup.class,AddGroup.class})
	@NotNull(message = "检索首字母不能为空",groups = {AddGroup.class})
	private String firstLetter;
	/**
	 * 排序
	 */
	@Min(value = 0,message = "排序必须大于等于0",groups = {UpdateGroup.class,AddGroup.class})
	@NotNull(message = "排序不能为空",groups = {AddGroup.class})
	private Integer sort;

}
