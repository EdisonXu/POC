package com.edi.poc.domain;

import java.io.Serializable;

public class User implements Serializable{

	private static final long serialVersionUID = -7315428680078676546L;
	
	private int id;
	private String name = null;
	private String password = null;
	private int age;
	private String sex=null;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", standard=" + password
				+ ", age=" + age + ", sex=" + sex + "]";
	}
	
	
}
