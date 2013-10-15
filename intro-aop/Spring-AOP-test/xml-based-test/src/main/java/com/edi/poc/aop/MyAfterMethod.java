package com.edi.poc.aop;

import java.lang.reflect.Method;
import java.math.BigDecimal;

import org.springframework.aop.AfterReturningAdvice;

import com.edi.poc.ifc.HALL_NAME;
import com.edi.poc.statistic.Statistic;

public class MyAfterMethod implements AfterReturningAdvice{

	public void afterReturning(Object returnValue, Method method,
			Object[] args, Object target) throws Throwable {
		System.out.println("After return...");
		
		Statistic.increaseIncome(HALL_NAME.DINOSAUR, BigDecimal.valueOf(2l));
	}

}
