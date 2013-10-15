package com.edi.poc.aop;

import java.math.BigDecimal;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import com.edi.poc.ifc.HALL_NAME;
import com.edi.poc.statistic.Statistic;

@Aspect
public class MyAspect {

	@Before("execution(* com.edi.poc.Zoo.enter(..))")
	public void before(JoinPoint joinPoint)
	{
		System.out.println("Before method...");
		Statistic.increaseTotalTraffic();
	}
	
	@After("execution(* com.edi.poc.ifc.Hall.visit(..))")
	public void after(JoinPoint joinPoint)
	{
		System.out.println("After return...");
		Statistic.increaseIncome(HALL_NAME.DINOSAUR, BigDecimal.valueOf(2l));
	}
}
