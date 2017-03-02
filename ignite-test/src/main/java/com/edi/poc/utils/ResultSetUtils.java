package com.edi.poc.utils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.edi.poc.domain.COL_TYPE;
import com.edi.poc.domain.ColumnInfo;


public class ResultSetUtils {

    public static JSONArray toJSONArray(ResultSet rs) throws SQLException, InterruptedException {
        return convertToJSONArray(rs, null);
    }

    public static JSONArray toJSONArrayDefaultEmpty(ResultSet rs) throws SQLException, InterruptedException {
        return convertToJSONArray(rs, "");
    }

    private static JSONArray convertToJSONArray(ResultSet rs, String defaultValue) throws SQLException, InterruptedException {
        final JSONArray data = new JSONArray();

        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        List<Object> head = new ArrayList<Object>();
        // convert head
        for (int i = 0; i < columnCount; i++) {
            String columnName = metaData.getColumnLabel(i + 1);
            int columnType = metaData.getColumnType(i + 1);
            ColumnInfo info = getColumnInfo(columnName, columnType);
            head.add(info);
        }

        while (rs.next()) {
            JSONObject row = new JSONObject();
            for (int i = 0; i < columnCount; i++) {
                ColumnInfo info = (ColumnInfo) head.get(i);
                int dataIndex = i + 1;
                if (rs.getObject(dataIndex) == null) {
                    row.put(info.getColumnName(), defaultValue == null ? null : "");
                } else {
                    Object val = rs.getObject(dataIndex);
                    if(val instanceof Long)
                        row.put(info.getColumnName(), String.valueOf(val));
                    else if(val instanceof Double){//精度问题
//                      DecimalFormat df= new DecimalFormat("0"); //格式化,取整
//                      String fmtVal = df.format(val);
//                      if(new BigDecimal(val.toString()).compareTo(new BigDecimal(fmtVal)) == 0) {
//                          row.put(info.getColumnName(), new BigDecimal(fmtVal).toPlainString());
//                      } else {
//                          row.put(info.getColumnName(), new BigDecimal(rs.getObject(dataIndex).toString()).toPlainString());
//                      }
                        DecimalFormat df= new DecimalFormat("#.####################");
                        row.put(info.getColumnName(), df.format(val));
                    }else{
                        row.put(info.getColumnName(), rs.getObject(dataIndex));
                    }
                }
            }
            data.add(row);
        }
        return data;
    }

    public static JSONObject toJSONObject(ResultSet rs) throws Exception {
        final JSONObject data = new JSONObject();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        while (rs.next()) {
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnLabel(i);
                data.put(columnName, rs.getObject(i));
            }
        }
        return data;
    }

    public static JSONObject toJSONObjectDefaultEmpty(ResultSet rs) throws Exception {
        final JSONObject data = new JSONObject();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        while (rs.next()) {
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnLabel(i);
                if (rs.getObject(i) == null) {
                    data.put(columnName,"");
                } else {
                    Object val = rs.getObject(i);
                    if(val instanceof Long){
                        data.put(columnName, String.valueOf(val));
                    }else if(val instanceof Double){
                        DecimalFormat df= new DecimalFormat("#.####################");
                        data.put(columnName, df.format(val));
//                      if(new BigDecimal(val.toString()).compareTo(new BigDecimal(fmtVal)) == 0) {
//                          data.put(columnName, new BigDecimal(fmtVal).toPlainString());
//                      } else {
//                          data.put(columnName, new BigDecimal(rs.getObject(i).toString()).toPlainString());
//                      }
                    }else{
                        data.put(columnName,val);
                    }
                }
            }
        }
        return data;
    }
    
    private static ColumnInfo getColumnInfo(String columnName, int columnType) {
        ColumnInfo info = new ColumnInfo(columnName);
        if (columnType == Types.CHAR || columnType == Types.VARCHAR || columnType == Types.LONGVARCHAR) {
            info.setColumnType(COL_TYPE.COLTYPE_STRING);
        } else if (columnType == Types.DECIMAL || columnType == Types.DOUBLE || columnType == Types.FLOAT || columnType == Types.REAL) {
            info.setColumnType(COL_TYPE.COLTYPE_DOUBLE);
        } else if (columnType == Types.INTEGER || columnType == Types.SMALLINT || columnType == Types.NUMERIC || columnType == Types.TINYINT) {
            info.setColumnType(COL_TYPE.COLTYPE_INT);
        } else if (columnType == Types.BIGINT) {
            info.setColumnType(COL_TYPE.COLTYPE_LONG);
        } else if (columnType == Types.BLOB || columnType == Types.CLOB || columnType == Types.NCLOB || columnType == Types.BINARY || columnType == Types.VARBINARY || columnType == Types.LONGVARBINARY) {
            info.setColumnType(COL_TYPE.COLTYPE_RAW);
        } else {
            info.setColumnType(COL_TYPE.COLTYPE_UNKNOW);
        }
        return info;
    }
}
