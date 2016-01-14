package com.mn.tiger.datastorage.db.upgrade;

import com.mn.tiger.datastorage.db.annotation.Column;
import com.mn.tiger.datastorage.db.annotation.Id;
import com.mn.tiger.datastorage.db.annotation.Table;

/**
 * 业务表信息表对应的entity类
 * 主要用于记录业务表名称与对应的entity类全路径
 * 
 * @since 2014年3月13日
 */
@Table(name="TigerTables")
public class TigerTables {
	// 业务表名称
	@Id
	@Column(column="name")
	private String name;
	
	// 业务表对应的class类路径
	@Column(column="class_path")
	private String classPath;
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the classPath
	 */
	public String getClassPath() {
		return classPath;
	}
	/**
	 * @param classPath the classPath to set
	 */
	public void setClassPath(String classPath) {
		this.classPath = classPath;
	}
}
