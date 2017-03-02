package com.edi.poc;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.sql.rowset.serial.SerialBlob;

import org.apache.commons.codec.digest.DigestUtils;

import com.edi.poc.PTable.Column;
import com.edi.poc.PTable.CombinedSearchCriteria;
import com.edi.poc.PTable.CombinedSearchCriteria.CombinedType;
import com.edi.poc.PTable.SearchCriteria;
import com.edi.poc.PTable.SearchCriteria.ComparisonOperator;

/**
 * Helper class to execute sql.
 * @author Edison Xu
 *
 * Dec 10, 2013
 */
public class RMDBUtil {

    static final boolean debug = false;
    
    public static boolean executeSql(List<PTable> tables){
        boolean result = false;
        
        Connection conn = null;
        PTable table = null;
        try {
            conn = RMDBManager.getInstance().getConnection();
            conn.setAutoCommit(false);
            //Statement stmt = conn.createStatement();
            for(PTable t:tables)
            {
                table = t;
                PreparedStatement ps = buildStatement(t, conn);
                ps.execute();
            }
            conn.commit();
            result = true;
            System.out.println("Execute sql successful");
        } catch (SQLException e1) {
            String errorTableName = "";
            if(table!=null)
                errorTableName = table.getName();
            System.err.println("Failed to execute sql for table:"+errorTableName);
            try {
                if(conn!=null)
                    conn.rollback();
            } catch (SQLException e) {
                System.err.println("Failed to rollback");
            }
        } catch(Exception e){
            System.err.println("Failed to execute sql");
        } finally{
            if(conn!=null)
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.out.println("Failed to close db connection");
                }
        }
        return result;
    }
    
    public static PreparedStatement buildStatement(PTable table, Connection conn) throws SQLException {
        PreparedStatement ps = null;
        switch(table.getDmlType())
        {
        case INSERT:
            ps =  generateInsertDML(table, conn);
            break;
        case UPDATE:
            ps = generateUpdateDML(table, conn);
            break;
        case DELETE:
            ps =  generateDeleteDML(table, conn);
            break;
        default :
            throw new IllegalArgumentException("Unsupported dml type!");
        }
        return ps;
    }
    
    /**
     * Generate DML for INSERT
     * @param msg
     * @throws SQLException 
     */
    private static PreparedStatement generateInsertDML(PTable table, Connection conn) throws SQLException {
        StringBuilder sb = new StringBuilder("INSERT INTO ");
        // table name
        sb.append(table.getName()).append(" (");
        
        // column name
        List<PTable.Column> columns = table.getColumns();
        for(int i=0;i<columns.size();i++)
        {
            String columnName = columns.get(i).getColumnName();
            if(i!=columns.size()-1)
                sb.append("`").append(columnName).append("`, ");
            else
                sb.append("`").append(columnName).append("`");
        }
        
        sb.append(") values (");
        
        // column value
        for(int i=0;i<columns.size();i++)
        {
            if(i!=columns.size()-1)
                sb.append("?").append(", ");
            else
                sb.append("?");
        }
        sb.append(")");
        String sql = sb.toString();
        PreparedStatement ps = conn.prepareStatement(sql);
        if(columns.size()==0){
            System.out.println("No columns found to insert! Ignore this invalid request: " + sql);
            return ps;
        }
        String[] parts = null;
        StringBuilder printInfo = null; // use new String(splitedArray[]) to avoid split->subString memory issue
        if(debug){
            parts = sql.split("\\?");
            printInfo = new StringBuilder();
        }
        int counter = 0;
        do{
            if(debug){
                printInfo.append(parts[counter]);
            }
            Column column = columns.get(counter);
            Object columnValue = column.getColumnValue();
            if(column.isRawType())
            {
                Blob blob = new SerialBlob((byte[])columnValue);
                ps.setBlob(counter+1, blob);
                if(debug)
                    printInfo.append(new String((byte[])columnValue));
                continue;
            }
            String strValue = String.valueOf(columnValue); // protocol buffer ensure that columnValue will not be NULL
            if(strValue.length()==0){
                ps.setObject(counter+1, null);
                if(debug){
                    printInfo.append("null");
                }
            }
            else{
                ps.setObject(counter+1, strValue);
                if(debug){
                    printInfo.append(strValue);
                }
            }
            
        }while(++counter<columns.size());
        
        if(debug){
            printInfo.append(parts[counter]);
            System.err.println("Sql to execute:\r\n" + printInfo.toString() + "\r\n");
        }
        return ps;
    }
    
    /**
     * Generate DML for UPDATE
     * @param msg
     * @throws SQLException 
     */
    private static PreparedStatement generateUpdateDML(PTable table, Connection conn) throws SQLException {
        StringBuilder sb = new StringBuilder("UPDATE ");
        // table name
        sb.append(table.getName()).append(" SET ");
        
        // column name
        List<PTable.Column> columns = table.getColumns();
        for(int i=0;i<columns.size();i++)
        {
            String columnName = columns.get(i).getColumnName();
            if(i!=columns.size()-1)
            {
                sb.append("`").append(columnName).append("`=").append("?,");
            }
            else
            {
                sb.append("`").append(columnName).append("`=").append("?");
            }
        }
        // search criteria
        List<String> scValues = new ArrayList<>();
        String criteria = getSearchCriteria(table.getCombinedSearchCriteria(), scValues);
        if(criteria!=null)
        {
            sb.append(" WHERE ").append(criteria);
        }
        String sql = sb.toString();
        PreparedStatement ps = conn.prepareStatement(sql);
        String[] parts = null;
        StringBuilder printInfo = null;
        if(debug){
            parts = sql.split("\\?");
            printInfo = new StringBuilder(); // use new String(splitedArray[]) to avoid split->subString memory issue
        }
        if(columns.size()==0){
            System.out.println("No columns found to insert! Ignore this invalid request: " + sql);
            return ps;
        }
        
        int counter = 1;
        for(int i=0;i<columns.size();i++,counter++)
        {
            Column column = columns.get(i);
            Object columnValue = column.getColumnValue();
            if(debug)
                printInfo.append(parts[i]);
            
            if(column.isRawType())
            {
                ps.setBytes(counter, (byte[])columnValue);
                if(debug)
                    printInfo.append(new String((byte[])columnValue));
                continue;
            }
            String strValue = columnValue.toString();
            if(strValue.length()==0)
            {
                ps.setObject(counter, null);
                if(debug)
                    printInfo.append("null");
            }
            else
            {
                ps.setObject(counter, strValue);
                if(debug)
                    printInfo.append(columnValue);
            }
            
        }
        for(int i=0;i<scValues.size();i++,counter++)
        {
            String scValue = scValues.get(i);
            if(debug)
                printInfo.append(parts[counter-1]);
            
            if(scValue.length()==0)
            {
                ps.setObject(counter, null);
                if(debug)
                    printInfo.append("null");   
            }
            else
            {
                ps.setObject(counter, scValue);
                if(debug)
                    printInfo.append(scValue);
            }
        }
        if(debug){
            if(counter-1<parts.length)
                printInfo.append(parts[counter-1]);
            System.err.println("Sql to execute:\r\n" + printInfo.toString() + "\r\n");
        }
        
        return ps;
    }
    
    private static String getSearchCriteria(CombinedSearchCriteria csc, List<String> scValues) {
        String criteria = null;
        if(csc!=null)
        {
            StringBuilder sb = new StringBuilder();
            if(csc.getSearchCriteria()!=null)
            {
                SearchCriteria sc = csc.getSearchCriteria();
                sb.append("`").append(sc.getKey()).append("`").append(sc.getCompOperator().getSymbol());
                /*sb.append("'").append(sc.getValue()).append("'");*/
                sb.append("?");
                scValues.add(sc.getValue());
            }
            
            if(!csc.getAndList().isEmpty())
            {
                String leftString = getSearchCriteria(csc.getAndList().get(0), scValues);
                if(leftString!=null)
                {
                    sb.append("(").append(leftString).append(" AND ");
                }
                String rightString = getSearchCriteria(csc.getAndList().get(1), scValues);
                if(rightString!=null)
                {
                    sb.append(rightString).append(") ");
                }
            }
            
            if(!csc.getOrList().isEmpty())
            {
                String leftString = getSearchCriteria(csc.getOrList().get(0), scValues);
                if(leftString!=null)
                {
                    sb.append("(").append(leftString).append(" OR ");
                }
                String rightString = getSearchCriteria(csc.getOrList().get(1), scValues);
                if(rightString!=null)
                {
                    sb.append(rightString).append(") ");
                }
            }
            String ret = sb.toString();
            if(ret.length()>0)
                criteria = ret;
        }
        return criteria;
    }
    
    /**
     * Generate DML for DELETE
     * @param msg
     * @throws SQLException 
     */
    private static PreparedStatement generateDeleteDML(PTable table, Connection conn) throws SQLException {
        StringBuilder sb = new StringBuilder("DELETE FROM ");
        // table name
        sb.append(table.getName());
        List<String> scValues = new ArrayList<>();
        // search criteria
        String criteria = getSearchCriteria(table.getCombinedSearchCriteria(), scValues);
        if(criteria!=null)
        {
            sb.append(" WHERE ").append(criteria);
        }
        
        String sql = sb.toString();
        PreparedStatement ps = conn.prepareStatement(sql);
        if(criteria==null){
            if(debug)
                System.err.println("Sql to execute:\r\n" + sql + "\r\n");
            return ps;
        }
        String[] parts = null;
        StringBuilder printInfo = null;
        if(debug){
            parts = sql.split("\\?");
            printInfo = new StringBuilder();
        }
        int counter = 0;
        do{
            if(debug)
                printInfo.append(parts[counter]);
            String scValue = scValues.get(counter);
            if(scValue.length()==0){
                ps.setObject(counter+1, null);
                if(debug)
                    printInfo.append("null");
            }else{
                ps.setObject(counter+1, scValue);
                if(debug)
                    printInfo.append(scValue);
            }
        }while(++counter<scValues.size());
        
        if(debug){
            if(counter<parts.length)
                printInfo.append(parts[counter]);
            System.err.println("Sql to execute:\r\n" + printInfo.toString() + "\r\n");
        }
        return ps;
    }
    

    public static String genTableKey(PTable table, List<Column> colList, String searchCriteria){
        // NULL check
        if(table==null || colList==null || colList.isEmpty())
            return null;
        Collections.sort(colList, new Comparator<Column>() {
            @Override
            public int compare(Column o1, Column o2) {
                return o1.getColumnName().compareTo(o2.getColumnName());
            }
        });
        StringBuilder sb = new StringBuilder(table.getName()).append(",");
        for(int i=0;i<colList.size();i++){
            Column each = colList.get(i);
            sb.append(each.getColumnName());
            if(i<colList.size()-1)
                sb.append(",");
        }
        if(searchCriteria!=null && searchCriteria.length()>0)
            sb.append(",").append(searchCriteria);
        String sql = sb.toString();
        System.err.println(sql);
        return DigestUtils.md5Hex(sql);
    }
    
    public static void main(String[] args) {
        PTable a = new PTable("a");
        a.addColumn("F1", "test");
        a.addColumn("F2", "test");
        a.setCombinedSearchCriteria(new CombinedSearchCriteria(new CombinedSearchCriteria(new SearchCriteria("F2", ComparisonOperator.EQUAL, "2")), CombinedType.OR, new CombinedSearchCriteria(new CombinedSearchCriteria(new SearchCriteria("F2", ComparisonOperator.EQUAL, "2")), CombinedType.OR, new CombinedSearchCriteria(new SearchCriteria("F3", ComparisonOperator.EQUAL, "1")))));
        List<String> ascValues = new ArrayList<>();
        String asc = getSearchCriteria(a.getCombinedSearchCriteria(), ascValues);
        System.out.println(asc);
        PTable b = new PTable("a");
        b.addColumn("F2", "test");
        b.addColumn("F1", "test");
        b.setCombinedSearchCriteria(new CombinedSearchCriteria(new CombinedSearchCriteria(new SearchCriteria("F2", ComparisonOperator.EQUAL, "2")), CombinedType.OR, new CombinedSearchCriteria(new CombinedSearchCriteria(new SearchCriteria("F2", ComparisonOperator.EQUAL, "2")), CombinedType.OR, new CombinedSearchCriteria(new SearchCriteria("F3", ComparisonOperator.EQUAL, "1")))));
        List<String> bscValues = new ArrayList<>();
        String bsc = getSearchCriteria(b.getCombinedSearchCriteria(), bscValues);
        System.out.println(bsc);
        System.out.println(genTableKey(a, a.getColumns(), asc));
        System.out.println(genTableKey(b, b.getColumns(), bsc));
    }
    
    /*public static void main(String[] args) throws SQLException {
        PTable t1 = new PTable("PT123");
        t1.setDmlType(DmlType.UPDATE);
        t1.addColumn("F1", "zzz");
        //t1.setCombinedSearchCriteria(new CombinedSearchCriteria(new SearchCriteria("F2", ComparisonOperator.EQUAL, "2")));
        t1.setCombinedSearchCriteria(new CombinedSearchCriteria(new CombinedSearchCriteria(new SearchCriteria("F2", ComparisonOperator.EQUAL, "2")), CombinedType.OR, new CombinedSearchCriteria(new CombinedSearchCriteria(new SearchCriteria("F2", ComparisonOperator.EQUAL, "2")), CombinedType.OR, new CombinedSearchCriteria(new SearchCriteria("F3", ComparisonOperator.EQUAL, "1")))));
        Connection conn  = null;
        LOGGER.setLevel(Level.TRACE);
        buildStatement(t1, conn);
    }*/
}
