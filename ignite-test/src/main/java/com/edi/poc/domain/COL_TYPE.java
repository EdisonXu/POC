package com.edi.poc.domain;

public enum COL_TYPE {
	// COLTYPE_ERROR(-1), // 错误类型
	COLTYPE_STRING('0'), // 字符串
	COLTYPE_INT('1'), // 整数
	COLTYPE_DOUBLE('2'), // 浮点
	COLTYPE_RAW('3'), // 二进制
	COLTYPE_LONG('1'), COLTYPE_UNKNOW('3');
	private COL_TYPE(char value) {
		this.value = value;
	}

	private char value = 0;

	public char getValue() {
		return value;
	}
}
