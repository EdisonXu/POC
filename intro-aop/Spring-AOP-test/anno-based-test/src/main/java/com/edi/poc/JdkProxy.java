package com.edi.poc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.edi.poc.ifc.Hall;

public class JdkProxy implements InvocationHandler{

	private Object target;

	public Object getProxy(Object target)
	{
		this.target = target;
		
		return Proxy.newProxyInstance(
				target.getClass().getClassLoader(), 
				target.getClass().getInterfaces(), 
				this);
	}
	
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		System.out.println("Before ...");
		// Add those operations whatever you want before it really goes
		Object result = method.invoke(target, args);
		// Add those operations whatever you want before it really leave
		System.out.println("After ...");
		return result;
	}
	
	public static void main(String[] args) {
		Hall dinoHall = new DinoHall();
		JdkProxy proxy = new JdkProxy();
		Hall dinoHallProxy = (Hall)proxy.getProxy(dinoHall);
		dinoHallProxy.open();
	}
}
