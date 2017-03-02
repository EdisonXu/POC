package com.edi.poc.stores;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.cache.Cache;
import javax.cache.integration.CacheLoaderException;
import javax.cache.integration.CacheWriterException;

import org.apache.ignite.IgniteException;
import org.apache.ignite.cache.store.CacheStore;
import org.apache.ignite.cache.store.CacheStoreAdapter;
import org.apache.ignite.cache.store.CacheStoreSession;
import org.apache.ignite.lang.IgniteBiInClosure;
import org.apache.ignite.resources.CacheStoreSessionResource;

import com.edi.poc.domain.Person;
import com.edi.poc.utils.RMDBManager;

/**
 * Example of {@link CacheStore} implementation that uses JDBC
 * transaction with cache transactions and maps {@link Long} to {@link Person}.
 */
public class CacheJdbcPersonStore extends CacheStoreAdapter<Long, Person> {

    /** Store session. */
    @CacheStoreSessionResource
    private CacheStoreSession ses;

    /**
     * Constructor.
     *
     * @throws IgniteException If failed.
     * @throws IOException 
     */
    public CacheJdbcPersonStore() throws IgniteException, IOException {
        prepareDb();
    }

    /**
     * Prepares database for example execution. This method will create a
     * table called "PERSONS" so it can be used by store implementation.
     *
     * @throws IgniteException If failed.
     * @throws IOException 
     */
    private void prepareDb() throws IgniteException, IOException {
        try (Connection conn = RMDBManager.INSTANCE.getConnection()) {
            conn.createStatement().execute(
                "create table if not exists PERSONS (" +
                "id BIGINT unique, firstName varchar(255), lastName varchar(255))");
        }
        catch (SQLException e) {
            throw new IgniteException("Failed to create database table.", e);
        }
    }

    /** {@inheritDoc} */
    @Override public Person load(Long key) {
        System.out.println(">>> Store load [key=" + key + ']');

        try(Connection conn = connection()){
            try (PreparedStatement st = conn.prepareStatement("select * from PERSONS where id = ?")) {
                st.setString(1, key.toString());
    
                ResultSet rs = st.executeQuery();
    
                return rs.next() ? new Person(rs.getLong(1), rs.getString(2), rs.getString(3)) : null;
            }
        }
        catch (SQLException | IOException e) {
            throw new CacheLoaderException("Failed to load object [key=" + key + ']', e);
        }
    }

    /** {@inheritDoc} */
    @Override 
    public void write(Cache.Entry<? extends Long, ? extends Person> entry) {
        Long key = entry.getKey();
        Person val = entry.getValue();

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

    /** {@inheritDoc} */
    @Override 
    public void delete(Object key) {
        System.out.println(">>> Store delete [key=" + key + ']');

        try(Connection conn = connection()){
            try (PreparedStatement st = conn.prepareStatement("delete from PERSONS where id=?")) {
                st.setLong(1, (Long)key);
    
                st.executeUpdate();
            }
        }
        catch (SQLException | IOException e) {
            throw new CacheWriterException("Failed to delete object [key=" + key + ']', e);
        }
    }

    /** {@inheritDoc} */
    @Override 
    public void loadCache(IgniteBiInClosure<Long, Person> clo, Object... args) {
        if (args == null || args.length == 0 || args[0] == null)
            throw new CacheLoaderException("Expected entry count parameter is not provided.");

        final int entryCnt = (Integer)args[0];

        
        try(Connection conn = connection()){
            try (PreparedStatement stmt = conn.prepareStatement("select * from PERSONS limit ?")) {
                stmt.setInt(1, entryCnt);

                ResultSet rs = stmt.executeQuery();

                int cnt = 0;

                while (rs.next()) {
                    Person person = new Person(rs.getLong(1), rs.getString(2), rs.getString(3));

                    clo.apply(person.getId(), person);

                    cnt++;
                }

                System.out.println(">>> Loaded " + cnt + " values into cache.");
            }
        }
        catch (SQLException | IOException e) {
            throw new CacheLoaderException("Failed to load values from cache store.", e);
        }
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