/**
 * 
 */
package com.edi.poc;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.ddlutils.Platform;
import org.apache.ddlutils.PlatformFactory;
import org.apache.ddlutils.io.DatabaseIO;
import org.apache.ddlutils.model.Database;

/**
 * @author Edison Xu
 * 
 *         Nov 6, 2013
 */
public class MyDDBUtil {

	public Database readDatabaseFromXML(String fileName) {
		return new DatabaseIO().read(fileName);
	}

	public void changeDatabase(DataSource dataSource, Database targetModel,
			boolean alterDb) {
		Platform platform = PlatformFactory
				.createNewPlatformInstance(dataSource);

		if (alterDb) {
			platform.alterTables(targetModel, false);
		} else {
			platform.createTables(targetModel, false, false);
		}
	}

	public void insertData(DataSource dataSource, Database database) {
		Platform platform = PlatformFactory
				.createNewPlatformInstance(dataSource);

		// "author" is a table of the model
		DynaBean author = database.createDynaBeanFor("author", false);

		// "name" and "whatever" are columns of table "author"
		author.set("name", "James");
		author.set("whatever", new Integer(1234));

		platform.insert(database, author);
	}
	
	public static void main(String[] args) throws Exception {
		MyDDBUtil util = new MyDDBUtil();
		Properties p = new Properties();
        p.setProperty("driverClassName", "com.mysql.jdbc.Driver");
        p.setProperty("url", "jdbc:mysql://10.1.110.21:3306/metadata?useUnicode=true&characterEncoding=UTF-8");
        p.setProperty("password", "111111");
        p.setProperty("username", "root");
		BasicDataSource dataSource = (BasicDataSource) BasicDataSourceFactory.createDataSource(p);
		Database db = util.readDatabaseFromXML("table.xml");
		util.changeDatabase(dataSource, db, false);
		
	}
}
