package com.edi.poc.clients;

import static org.apache.ignite.cache.CacheAtomicityMode.TRANSACTIONAL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import javax.cache.configuration.Factory;
import javax.cache.configuration.FactoryBuilder;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheTypeMetadata;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlQuery;
import org.apache.ignite.cache.store.CacheStoreSessionListener;
import org.apache.ignite.cache.store.jdbc.CacheJdbcStoreSessionListener;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.transactions.Transaction;

import com.edi.poc.domain.Person;
import com.edi.poc.stores.CacheJdbcPersonStore;
import com.edi.poc.utils.RMDBManager;

/**
 * Demonstrates usage of cache with underlying persistent store configured.
 * <p>
 * This example uses {@link CacheJdbcPersonStore} as a persistent store.
 * <p>
 * Remote nodes can be started with {@link ExampleNodeStartup} in another JVM which will
 * start node with {@code examples/config/example-ignite.xml} configuration.
 */
public class CacheJdbcStoreExample {
    /** Cache name. */
    private static final String CACHE_NAME = CacheJdbcStoreExample.class.getSimpleName();

    /** Heap size required to run this example. */
    public static final int MIN_MEMORY = 1024 * 1024 * 1024;

    /** Number of entries to load. */
    private static final int ENTRY_COUNT = 100_000;

    /** Global person ID to use across entire example. */
    private static final Long id = Math.abs(UUID.randomUUID().getLeastSignificantBits());

    /**
     * Executes example.
     *
     * @param args Command line arguments, none required.
     * @throws IgniteException If example execution failed.
     * @throws IOException 
     */
    public static void main(String[] args) throws IgniteException, IOException {
        //test1();
        test2();
    }
    
    private static void test2() throws IOException{
        checkMinMemory(MIN_MEMORY);
        RMDBManager.INSTANCE.init();
        
        try (Ignite ignite = Ignition.start("example-cache.xml")) {
            CacheConfiguration<Long, Person> cacheCfg = new CacheConfiguration<>(CACHE_NAME);

            // Set atomicity as transaction, since we are showing transactions in example.
            //cacheCfg.setAtomicityMode(TRANSACTIONAL);

            // Configure JDBC store.
            cacheCfg.setCacheStoreFactory(FactoryBuilder.factoryOf(CacheJdbcPersonStore.class));

            // Configure JDBC session listener.
            cacheCfg.setCacheStoreSessionListenerFactories(new Factory<CacheStoreSessionListener>() {
                @Override public CacheStoreSessionListener create() {
                    CacheJdbcStoreSessionListener lsnr = new CacheJdbcStoreSessionListener();

                    lsnr.setDataSource(RMDBManager.INSTANCE.getDataSource());

                    return lsnr;
                }
            });

            cacheCfg.setReadThrough(true);
            cacheCfg.setWriteThrough(true);
            //cacheCfg.setIndexedTypes(Long.class, Person.class);
            
            Collection<CacheTypeMetadata> types = new ArrayList<>();
            CacheTypeMetadata type  = new CacheTypeMetadata();
            type.setKeyType(Long.class.getName());
            type.setValueType(Person.class);
            Map<String, Class<?>> qryFlds = type.getQueryFields();
            qryFlds.put("id", Long.class);
            qryFlds.put("firstName", String.class);
            qryFlds.put("lastName", String.class);
            types.add(type);
            cacheCfg.setTypeMetadata(types);
            
            try (IgniteCache<Long, Person> cache = ignite.getOrCreateCache(cacheCfg)) {
                cache.loadCache(null, ENTRY_COUNT);
                //cache.put(id, new Person(id, "Isaac", "Newton"));
                
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                
                SqlQuery<Long, Person> sql = new SqlQuery<Long, Person>(Person.class, "where lastName = ?");
                
                try (QueryCursor<javax.cache.Cache.Entry<Long, Person>> cursor = cache.query(sql.setArgs("Newton"))) {
                  for (javax.cache.Cache.Entry<Long, Person> e : cursor)
                    System.out.println(e.getValue().toString());
                }
            }
            
        }
    }
    
    private static void test1() throws IOException{
        checkMinMemory(MIN_MEMORY);
        RMDBManager.INSTANCE.init();
        // To start ignite with desired configuration uncomment the appropriate line.
        try (Ignite ignite = Ignition.start("example-ignite.xml")) {
            System.out.println();
            System.out.println(">>> Cache store example started.");

            CacheConfiguration<Long, Person> cacheCfg = new CacheConfiguration<>(CACHE_NAME);

            // Set atomicity as transaction, since we are showing transactions in example.
            cacheCfg.setAtomicityMode(TRANSACTIONAL);

            // Configure JDBC store.
            cacheCfg.setCacheStoreFactory(FactoryBuilder.factoryOf(CacheJdbcPersonStore.class));

            // Configure JDBC session listener.
            cacheCfg.setCacheStoreSessionListenerFactories(new Factory<CacheStoreSessionListener>() {
                @Override public CacheStoreSessionListener create() {
                    CacheJdbcStoreSessionListener lsnr = new CacheJdbcStoreSessionListener();

                    lsnr.setDataSource(RMDBManager.INSTANCE.getDataSource());

                    return lsnr;
                }
            });

            cacheCfg.setReadThrough(true);
            cacheCfg.setWriteThrough(true);

            try (IgniteCache<Long, Person> cache = ignite.getOrCreateCache(cacheCfg)) {
                // Make initial cache loading from persistent store. This is a
                // distributed operation and will call CacheStore.loadCache(...)
                // method on all nodes in topology.
                loadCache(cache);

                // Start transaction and execute several cache operations with
                // read/write-through to persistent store.
                executeTransaction(cache);
            }
            /*try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }*/
        }
    }

    /**
     * Makes initial cache loading.
     *
     * @param cache Cache to load.
     */
    private static void loadCache(IgniteCache<Long, Person> cache) {
        long start = System.currentTimeMillis();

        // Start loading cache from persistent store on all caching nodes.
        cache.loadCache(null, ENTRY_COUNT);

        long end = System.currentTimeMillis();

        System.out.println(">>> Loaded " + cache.size() + " keys with backups in " + (end - start) + "ms.");
    }

    /**
     * Executes transaction with read/write-through to persistent store.
     *
     * @param cache Cache to execute transaction on.
     */
    private static void executeTransaction(IgniteCache<Long, Person> cache) {
        try (Transaction tx = Ignition.ignite().transactions().txStart()) {
            Person val = cache.get(id);

            System.out.println("Read value: " + val);

            val = cache.getAndPut(id, new Person(id, "Isaac", "Newton"));

            System.out.println("Overwrote old value: " + val);

            val = cache.get(id);

            System.out.println("Read value: " + val);

            tx.commit();
        }

        System.out.println("Read value after commit: " + cache.get(id));
    }
    
    /**
     * Exits with code {@code -1} if maximum memory is below 90% of minimally allowed threshold.
     *
     * @param min Minimum memory threshold.
     */
    public static void checkMinMemory(long min) {
        long maxMem = Runtime.getRuntime().maxMemory();

        if (maxMem < .85 * min) {
            System.err.println("Heap limit is too low (" + (maxMem / (1024 * 1024)) +
                "MB), please increase heap size at least up to " + (min / (1024 * 1024)) + "MB.");

            System.exit(-1);
        }
    }
}