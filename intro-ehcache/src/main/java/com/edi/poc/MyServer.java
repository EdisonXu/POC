package com.edi.poc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public class MyServer {

	private List<MyData> list;
	private Cache myCache;
	private CacheManager manager;
	
	public MyServer()
	{
		initCache();
		createData();
	}
	
	public void shutdown()
	{
		manager.shutdown();
	}
	
	private void initCache()
	{
		manager = CacheManager.create();
		manager.addCache("testCache");
		myCache = manager.getCache("testCache");
	}
	
	private void createData()
	{
		list = new ArrayList<MyData>();
		
		for(int i=0;i<1000;i++)
		{
			MyData data = new MyData(Long.valueOf(i),String.valueOf(i));
			list.add(data);
			DummyDataBase.add(data);
		}
	}
	
	public long test()
	{
		long begin = System.currentTimeMillis();
		
		for(MyData d:list)
		{
			Element e = myCache.get(d.getId());
			if(e!=null)
			{
				MyData cachedData = (MyData)e.getObjectValue();
				if(cachedData!=null) continue;
			}
			myCache.put(new Element(d.getId(), d));
			PersistUtil.findData(d.getId());
		}
		
		long end = System.currentTimeMillis();
		return (end - begin);
	}
	
	public long testThough()
	{
		long begin = System.currentTimeMillis();
		
		Map<Long, MyData> map = new ConcurrentHashMap();
		
		for(MyData d:list)
		{
			// simulate the cache lookup process
			if(map.get(d.getId())==null)
				map.put(Long.valueOf(d.getId()), d);
			DummyDataBase.find(d.getId());
		}
		
		long end = System.currentTimeMillis();
		return (end - begin);
	}
	
	public static void main(String[] args) {
		MyServer server = new MyServer();
		System.out.println("Before using cache, time cost is: " + server.test() + "ms.");
		System.out.println("After using cache, time cost is: " + server.test() + "ms.");
		System.out.println("Read from memo, time cost is: " + server.testThough() + "ms.");
		server.shutdown();
	}
}
