package com.edi.poc;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * @author Edison Xu
 * 
 *         Dec 4, 2013
 */
public class RMDBManager {

	private static ComboPooledDataSource cpds = null;
	private static boolean inited = false;
	
	private RMDBManager(){}
	
	private static class InnerHolder{
		static RMDBManager manager = new RMDBManager();
	}
	
	public synchronized void init(Map map)
	{
		if(inited) // avoid duplicate init
			return;
		Properties p = convertMaptoProperties(map);
		cpds = new ComboPooledDataSource(true);
		cpds.setDataSourceName("mydatasource");
		//cpds.setJdbcUrl("jdbc:mysql://10.1.110.21:3306/metadata?useUnicode=true&amp;characterEncoding=utf8");
		cpds.setJdbcUrl(p.getProperty("c3p0.jdbcUrl"));
		try {
			cpds.setDriverClass(p.getProperty("c3p0.driverClass"));
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		cpds.setUser(p.getProperty("c3p0.user"));
		cpds.setPassword(p.getProperty("c3p0.password"));
		cpds.setMaxPoolSize(Integer.valueOf(p.getProperty("c3p0.maxPoolSize", "100").trim()));
		cpds.setMinPoolSize(Integer.valueOf(p.getProperty("c3p0.minPoolSize", "100").trim()));
		cpds.setAcquireIncrement(Integer.valueOf(p.getProperty("c3p0.acquireIncrement", "100").trim()));
		cpds.setAcquireRetryAttempts(Integer.valueOf(p.getProperty("c3p0.acquireRetryAttempts", "30").trim()));
		cpds.setAcquireRetryDelay(Integer.valueOf(p.getProperty("c3p0.acquireRetryDelay", "1000").trim()));
		cpds.setAutoCommitOnClose(Boolean.valueOf(p.getProperty("c3p0.autoCommitOnClose", "false")));
		cpds.setIdleConnectionTestPeriod(Integer.valueOf(p.getProperty("c3p0.idleConnectionTestPeriod", "0").trim()));
		cpds.setInitialPoolSize(Integer.valueOf(p.getProperty("c3p0.initialPoolSize", "5").trim()));
		cpds.setMaxIdleTime(Integer.valueOf(p.getProperty("c3p0.maxIdleTime", "10").trim()));
		cpds.setNumHelperThreads(Integer.valueOf(p.getProperty("c3p0.numHelperThreads", "3").trim()));
		inited = true;
		
		// retrieve the connection from the pool to avoid the latency for the 1st time insert
		int poolSize = cpds.getMinPoolSize();
		ExecutorService pool = Executors.newFixedThreadPool(poolSize);
		for(int i=0;i<poolSize;i++)
		{
			pool.submit(new Runnable() {
				
				@Override
				public void run() {
					Connection conn = RMDBManager.getInstance().getConnection();
					try {
						conn.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}
		pool.shutdown();
		try {
			pool.awaitTermination(2, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	public static RMDBManager getInstance(){  
        return InnerHolder.manager;
    }  
      
    public Connection  getConnection(){
        try {
            return cpds.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static void main(String[] args) throws InterruptedException {
    	final long begin = System.currentTimeMillis();
		Map map = new HashMap<>();
		map.put("c3p0.driverClass", "com.mysql.jdbc.Driver");
		map.put("c3p0.jdbcUrl", "jdbc:mysql://10.1.110.21:3306/pbq?useUnicode=true&characterEncoding=UTF-8");
		map.put("c3p0.user", "root");
		map.put("c3p0.password", "111111");
		map.put("c3p0.minPoolSize", "10");
		map.put("c3p0.acquireIncrement", "10");
		map.put("c3p0.autoCommitOnClose", "true");
		map.put("c3p0.idleConnectionTestPeriod","10");
		
		RMDBManager.getInstance().init(map);
		ExecutorService pool = Executors.newFixedThreadPool(20);
		int msg = 10;
		final Queue<Long> latencies = new ConcurrentLinkedQueue<>();
		for(int i=0;i<msg;i++)
		{
			final List<String> sqlList = new ArrayList<String>();
			sqlList.add("insert into STORM_P_TEST (Id, Init_time) values ('" + i + "','" + System.currentTimeMillis() + "')");
			final int j = i;
			pool.submit(new Runnable() {
				
				@Override
				public void run() {
					long start = System.currentTimeMillis();
					Connection conn = null;
					try {
						conn = RMDBManager.getInstance().getConnection();
						//System.out.println("Get conn cost: " + (System.currentTimeMillis() - start));
						conn.setAutoCommit(false);
						/*Statement stmt = conn.createStatement();*/
						String sql = "UPDATE PT9999 SET `FV`=?,`F1`='? WHERE (`F2`=? AND `F3`=?) ";
						//String sql = "insert into STORM_P_TEST (Id, Init_time) values (?,?)";
						PreparedStatement ps = conn.prepareStatement(sql);
						StringBuilder debugInfo = new StringBuilder("Sql to execute:\r\n");
						/*for(String sql:sqlList)
						{
							stmt.addBatch(sql);
							debugInfo.append(sql).append(";\r\n");
						}
						stmt.executeBatch();*/
						ps.setObject(1, String.valueOf(j));
						ps.setObject(2, String.valueOf(System.currentTimeMillis()));
						ps.execute();
						conn.commit();
						//System.out.println("Execute sql successful");
						long end = System.currentTimeMillis();
						System.err.println("Single send cost: " + (end - start));
						latencies.offer(Long.valueOf(end - start));
					} catch (SQLException e1) {
						e1.printStackTrace();
						try {
							if(conn!=null)
								conn.rollback();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}finally{
						if(conn!=null)
							try {
								conn.close();
							} catch (SQLException e) {
								e.printStackTrace();
							}
					}
				}
			});
			//Thread.sleep(500);
		}
		pool.shutdown();
		pool.awaitTermination(2, TimeUnit.MINUTES);
		System.err.println("Total cost: " + (System.currentTimeMillis() - begin));
		long sum =0;
		int c = 0;
		for(Long each:latencies)
		{
			/*if(c==0)
			{
				c++;
				continue;
			}*/
			
			sum += each;
		}
		System.err.println("Avarage latency: " + (sum/msg));
	}
    
    public Properties convertMaptoProperties(Map map)
    {
        Properties p = new Properties();
        for(Object key:map.keySet())
        {
            Object value = map.get(key);
            if(value!=null)
                p.put(key, value);
                
        }
        return p;
    }
}
