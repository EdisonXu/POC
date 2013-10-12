package com.edi.poc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DummyDataBase {

	private static Map<Long,MyData> map = new ConcurrentHashMap<Long,MyData>();
	
	public static boolean add(MyData data)
	{
		if(map.containsKey(data.getId()))
			return false;
		map.put(Long.valueOf(data.getId()), data);
		return true;
	}
	
	public static boolean remove(MyData data)
	{
		MyData record = map.get(data.getId());
		if(record==null)
			return false;
		map.remove(data.getId());
		return true;
	}
	
	public static MyData find(Long id)
	{
		return map.get(id);
	}
	
}
