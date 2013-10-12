package com.edi.poc;

public class PersistUtil {

	private static final int DELAY_COUNTER = 1000000;
	
	public static boolean addData(MyData data)
	{
		delay();
		return DummyDataBase.add(data);
	}
	
	public static boolean removeData(MyData data)
	{
		delay();
		return DummyDataBase.remove(data);
	}
	
	public static MyData findData(Long id)
	{
		delay();
		return DummyDataBase.find(id);
	}
	
	private static void delay()
	{
		int i= DELAY_COUNTER;
		// delay
		while(i-- >0){}
	}
	
	
}
