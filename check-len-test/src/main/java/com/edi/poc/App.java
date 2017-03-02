package com.edi.poc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.edi.poc.PTable.DmlType;

/**
 * Hello world!
 *
 */
public class App 
{
    
    public static long test(DataGenerator gen){
        int stNum = 10;
        List<PTable> list = new ArrayList<PTable>();
        for(int i=0;i<stNum;i++){
            PTable table = new PTable("ST_TEST");
            table.setDmlType(DmlType.INSERT);
            table.addColumn("1", gen.genRandomString());
            list.add(table);
        }
        long begin = System.currentTimeMillis();
        RMDBUtil.executeSql(list);
        return System.currentTimeMillis() - begin;
    }
    
    public static void main( String[] args )
    {
        Map map = new HashMap<>();
        map.put("c3p0.driverClass", "com.mysql.jdbc.Driver");
        map.put("c3p0.jdbcUrl", "jdbc:mysql://10.1.110.21:3306/test?useUnicode=true&characterEncoding=UTF-8");
        map.put("c3p0.user", "root");
        map.put("c3p0.password", "111111");
        map.put("c3p0.minPoolSize", "10");
        map.put("c3p0.acquireIncrement", "10");
        map.put("c3p0.autoCommitOnClose", "true");
        map.put("c3p0.idleConnectionTestPeriod","10");
        
        RMDBManager.getInstance().init(map);
        DataGenerator gen = new DataGenerator();
        for(int i=0;i<5;i++){
            System.out.println("测试"+i+"执行时间："+test(gen));
        }
        
    }
}
