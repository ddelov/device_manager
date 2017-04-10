package com.estafet.openshift.util;

/**
 * Created by Delcho Delov on 04.04.17.
 */

import com.estafet.openshift.config.Constants;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionProvider {
		private static Connection con=null;
		private static final Logger log = Logger.getLogger(ConnectionProvider.class);
		static{
				try{
						log.debug("ConnectionProvider static initializer");
						Class.forName(Constants.DRIVER);
						log.info("============ DB Connection URL: "+ Constants.CONNECTION_URL);
				}catch(Exception e){
						log.error(e.getMessage(), e);
				}
		}

		public static Connection getCon(){
				try {
						if(con==null || con.isClosed()){
								con=DriverManager.getConnection(Constants.CONNECTION_URL, Constants.USERNAME, Constants.PASSWORD);
								log.debug("Connection established");
								con.setAutoCommit(false);
						}
				} catch (SQLException e) {
						log.error(e.getMessage(), e);
				}
				log.debug("returns connection: " + con);
				return con;
		}

}