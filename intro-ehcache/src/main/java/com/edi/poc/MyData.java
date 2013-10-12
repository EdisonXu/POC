package com.edi.poc;

import java.io.Serializable;

public class MyData implements Serializable{

	private static final long serialVersionUID = -5869359889148593353L;
	
	long id;
	String attribute;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getAttribute() {
		return attribute;
	}
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	@Override
	public String toString() {
		return "MyData [id=" + id + ", attribute=" + attribute + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((attribute == null) ? 0 : attribute.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MyData other = (MyData) obj;
		if (attribute == null) {
			if (other.attribute != null)
				return false;
		} else if (!attribute.equals(other.attribute))
			return false;
		if (id != other.id)
			return false;
		return true;
	}
	public MyData(long id, String attribute) {
		super();
		this.id = id;
		this.attribute = attribute;
	}
	
	public MyData() {
		super();
	}
	
}
