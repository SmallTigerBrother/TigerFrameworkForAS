package com.mn.tiger.datastorage.db.upgrade;

/**
 * 
 * 数据库对象
 * 
 * @since 2014年2月26日
 */
public class DatabaseObject {
	/**
	 * 对象名称
	 */
	private String name;
	/**
	 * 对象类型
	 */
	private String type;
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
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
}
