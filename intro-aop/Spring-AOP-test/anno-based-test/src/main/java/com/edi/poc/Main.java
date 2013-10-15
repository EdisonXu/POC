package com.edi.poc;

import java.text.NumberFormat;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.edi.poc.aop.MyAspect;
import com.edi.poc.ifc.HALL_NAME;
import com.edi.poc.ifc.Hall;
import com.edi.poc.statistic.Statistic;

@ComponentScan
@Configuration
@EnableAspectJAutoProxy
public class Main {
	
	@Bean
	public MyAspect myAspect()
	{
		return new MyAspect();
	}
	
	@Bean(name="dinoHall")
	public Hall getDinoHall()
	{
		return new DinoHall();
	}

	public static void main(String[] args) {
		//ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		ApplicationContext ctx = new AnnotationConfigApplicationContext(Main.class);
		Zoo zoo = (Zoo) ctx.getBean(Zoo.class);
		zoo.init("People");
		//Hall dinoHall = (Hall)ctx.getBean("dinoHall");
		Hall dinoHall = (Hall)ctx.getBean(Hall.class);
		zoo.addHall(HALL_NAME.DINOSAUR, dinoHall);
		Tourer jack = new Tourer("Jack");
		zoo.open();
		jack.visit(zoo, HALL_NAME.DINOSAUR);
		System.out.println("Current traffic: " + Statistic.getTotalTraffic());
		NumberFormat currency = NumberFormat.getCurrencyInstance();
		System.out.println("Income of " + HALL_NAME.DINOSAUR + ": "+ currency.format(Statistic.getIncome(HALL_NAME.DINOSAUR)));
		zoo.close();
	}
}
