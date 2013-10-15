package com.edi.poc.statistic;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.edi.poc.ifc.HALL_NAME;

public class Statistic {

	private static int totalTraffic = 0;
	
	private static Map<HALL_NAME, BigDecimal> incomeOfHalls = new HashMap<HALL_NAME, BigDecimal>();
	
	public static void increaseTotalTraffic()
	{
		totalTraffic ++;
	}
	
	public static int getTotalTraffic()
	{
		return totalTraffic;
	}
	
	public static BigDecimal getIncome(HALL_NAME hallName)
	{
		BigDecimal currentAmt = incomeOfHalls.get(hallName);
		if(currentAmt!=null)
			return currentAmt;
		return BigDecimal.valueOf(0);
	}
	
	public static void increaseIncome(HALL_NAME hallName, BigDecimal amount)
	{
		BigDecimal currentAmount = incomeOfHalls.get(hallName);
		if(currentAmount==null)
			currentAmount = amount;
		else
			currentAmount = currentAmount.add(amount);
		incomeOfHalls.put(hallName, currentAmount);
	}
}
