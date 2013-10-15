package com.edi.poc.aop;

import java.lang.reflect.Method;

import org.springframework.aop.MethodBeforeAdvice;

import com.edi.poc.statistic.Statistic;

public class MyBeforeMethod implements MethodBeforeAdvice {

	public void before(Method arg0, Object[] arg1, Object arg2)
			throws Throwable {
		System.out.println("Before method...");
		Statistic.increaseTotalTraffic();
	}

}
