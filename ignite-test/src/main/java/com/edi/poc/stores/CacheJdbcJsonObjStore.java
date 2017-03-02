package com.edi.poc.stores;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.cache.Cache.Entry;
import javax.cache.integration.CacheLoaderException;
import javax.cache.integration.CacheWriterException;

import org.apache.ignite.IgniteLogger;
import org.apache.ignite.cache.store.CacheStore;
import org.apache.ignite.cache.store.CacheStoreSession;
import org.apache.ignite.lang.IgniteBiInClosure;
import org.apache.ignite.resources.CacheStoreSessionResource;
import org.apache.ignite.resources.LoggerResource;

import com.edi.poc.utils.RMDBManager;
import com.edi.poc.utils.ResultSetUtils;

public class CacheJdbcJsonObjStore<Long, JSONObject> implements CacheStore<Long, com.alibaba.fastjson.JSONObject> {

    /** Store session. */
    @CacheStoreSessionResource
    private CacheStoreSession ses;
    
    /** Auto-injected logger instance. */
    @LoggerResource
    protected IgniteLogger log;

    @Override
    public com.alibaba.fastjson.JSONObject load(Long key) throws CacheLoaderException {
        System.out.println(">>> Store load [key=" + key + ']');

        try(Connection conn = connection()){
            try (PreparedStatement st = conn.prepareStatement("select * from PERSONS where id = ?")) {
                st.setString(1, key.toString());
    
                ResultSet rs = st.executeQuery();
    
                return ResultSetUtils.toJSONObject(rs);
            }
        }
        catch (Exception e) {
            throw new CacheLoaderException("Failed to load object [key=" + key + ']', e);
        }
    }

    @Override
    public Map<Long, com.alibaba.fastjson.JSONObject> loadAll(Iterable<? extends Long> keys) throws CacheLoaderException {
        assert keys != null;

        Map<Long, com.alibaba.fastjson.JSONObject> loaded = new HashMap<>();

        for (Long key : keys) {
            com.alibaba.fastjson.JSONObject v = load(key);

            if (v != null)
                loaded.put(key, v);
        }

        return loaded;
    }

    @Override
    public void write(Entry<? extends Long, ? extends com.alibaba.fastjson.JSONObject> entry) throws CacheWriterException {
        Long key = entry.getKey();
        com.alibaba.fastjson.JSONObject val = entry.getValue();

        System.out.println(">>> Store write [key=" + key + ", val=" + val + ']');

        try {
            Connection conn = connection();

            int updated;

            // Try update first. If it does not work, then try insert.
            // Some databases would allow these to be done in one 'upsert' operation.
            try (PreparedStatement st = conn.prepareStatement(
                "update PERSONS set firstName = ?, lastName = ? where id = ?")) {
                st.setString(1, val.getFirstName());
                st.setString(2, val.getLastName());
                st.setLong(3, val.getId());

                updated = st.executeUpdate();
            }

            // If update failed, try to insert.
            if (updated == 0) {
                try (PreparedStatement st = conn.prepareStatement(
                    "insert into PERSONS (id, firstName, lastName) values (?, ?, ?)")) {
                    st.setLong(1, val.getId());
                    st.setString(2, val.getFirstName()); 
                    st.setString(3, val.getLastName());

                    st.executeUpdate();
                }
            }
        }
        catch (SQLException | IOException e) {
            throw new CacheWriterException("Failed to write object [key=" + key + ", val=" + val + ']', e);
        }
    }

    @Override
    public void writeAll(Collection<Entry<? extends Long, ? extends com.alibaba.fastjson.JSONObject>> entries) throws CacheWriterException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void delete(Object key) throws CacheWriterException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void deleteAll(Collection<?> keys) throws CacheWriterException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void loadCache(IgniteBiInClosure<Long, com.alibaba.fastjson.JSONObject> clo, Object... args) throws CacheLoaderException {
        // TODO Auto-generated method stub
        
    }

    // Opens JDBC connection and attaches it to the ongoing
    // session if within a transaction.
    private Connection connection() throws SQLException, IOException  {
      if (ses.isWithinTransaction()) {
        Connection conn = ses.attachment();

        if (conn == null) {
          conn = openConnection(false);

          // Store connection in the session, so it can be accessed
          // for other operations within the same transaction.
          ses.attach(conn);
        }

        return conn;
      }
      // Transaction can be null in case of simple load or put operation.
      else
        return openConnection(true);
    }

    // Opens JDBC connection.
    private Connection openConnection(boolean autocommit) throws SQLException, IOException {
      // Open connection to your RDBMS systems (Oracle, MySQL, Postgres, DB2, Microsoft SQL, etc.)
      Connection conn = RMDBManager.INSTANCE.getConnection();

      conn.setAutoCommit(autocommit);

      return conn;
    }
    @Override
    public void sessionEnd(boolean commit) {
        try (Connection conn = ses.attachment()) {
            if (conn != null && ses.isWithinTransaction()) {
              if (commit)
                conn.commit();
              else
                conn.rollback();
            }
          }
          catch (SQLException e) {
            throw new CacheWriterException("Failed to end store session.", e);
          }
    }

}
