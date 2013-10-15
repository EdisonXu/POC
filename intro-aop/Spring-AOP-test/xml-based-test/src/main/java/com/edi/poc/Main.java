package com.edi.poc;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.edi.poc.ifc.HALL_NAME;
import com.edi.poc.ifc.Hall;
import com.edi.poc.statistic.Statistic;

public class Main {

	public static void main(String[] args) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		Zoo zoo = (Zoo) ctx.getBean("zoo");
		Hall dinoHall = (Hall)ctx.getBean("dinoHall");
		zoo.addHall(HALL_NAME.DINOSAUR, dinoHall);
		Tourer jack = (Tourer)ctx.getBean("jack");
		zoo.open();
		jack.visit(zoo, HALL_NAME.DINOSAUR);
		System.out.println("Current traffic: " + Statistic.getTotalTraffic());
		NumberFormat currency = NumberFormat.getCurrencyInstance();
		System.out.println("Income of " + HALL_NAME.DINOSAUR + ": "+ currency.format(Statistic.getIncome(HALL_NAME.DINOSAUR)));
		zoo.close();
	}
}
