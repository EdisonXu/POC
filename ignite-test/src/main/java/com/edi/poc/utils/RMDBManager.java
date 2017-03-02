package com.edi.poc.utils;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public enum RMDBManager {
    
    INSTANCE;
    
    private static ComboPooledDataSource cpds = null;
    private static boolean inited = false;
    
    public synchronized void init() throws IOException
    {
        if(inited) // avoid duplicate init
            return;
        cpds = new ComboPooledDataSource(true);
        cpds.setDataSourceName("mydatasource");
        cpds.setJdbcUrl("jdbc:mysql://10.1.110.21:3306/test?useUnicode=true&characterEncoding=UTF-8");
        try {
            cpds.setDriverClass("com.mysql.jdbc.Driver");
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
        cpds.setUser("root");
        cpds.setPassword("111111");
        cpds.setMinPoolSize(10);
        cpds.setAcquireIncrement(10);
        cpds.setAutoCommitOnClose(true);
        cpds.setIdleConnectionTestPeriod(10);
        inited = true;
        
        // retrieve the connection from the pool to avoid the latency for the 1st time insert
        int poolSize = cpds.getMinPoolSize();
        ExecutorService pool = Executors.newFixedThreadPool(poolSize);
        for(int i=0;i<poolSize;i++)
        {
            pool.submit(new Runnable() {
                
                @Override
                public void run() {
                    try {
                        Connection conn = getConnection();
                        conn.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
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
      
    public Connection  getConnection() throws IOException{
        try {
            if(!inited)
                init();
            return cpds.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public DataSource getDataSource(){
        return cpds;
    }
}
