package com.edi.poc.domain;

import java.io.Serializable;
import java.util.List;


public class ColumnInfo implements Serializable {

	String columnName;

	COL_TYPE columnType;

	List<ColumnInfo> repeatList;

	Boolean isRawFile = false;

	Boolean isZlib = false;

	int precision = 4;

	public ColumnInfo(String columnName) {

		this.columnName = columnName;
	}

	public String getColumnName() {

		return columnName;
	}

	public COL_TYPE getColumnType() {

		return columnType;
	}

	public void setColumnName(String columnName) {

		this.columnName = columnName;
	}

	public void setColumnType(COL_TYPE columnType) {

		this.columnType = columnType;
	}

	public List<ColumnInfo> getRepeatList() {

		return repeatList;
	}

	public void setRepeatList(List<ColumnInfo> repeatList) {

		this.repeatList = repeatList;
	}

	public Boolean getIsRawFile() {

		return isRawFile;
	}

	public void setIsRawFile(Boolean isRawFile) {

		this.isRawFile = isRawFile;
	}

	public Boolean getIsZlib() {

		return isZlib;
	}

	public void setIsZlib(Boolean isZlib) {

		this.isZlib = isZlib;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	public int getPrecision() {
		return precision;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof ColumnInfo) {
			String columnName2 = ((ColumnInfo) obj).getColumnName();
			if (columnName2 != null) {
				return columnName2.equals(this.columnName);
			}
		}
		return super.equals(obj);
	}
}
