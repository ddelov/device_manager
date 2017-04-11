package com.estafet.openshift.model.exception;

import java.sql.SQLException;

/**
 * Created by Delcho Delov on 11.04.17.
 */
public class DMSQLException extends DMException {
		public DMSQLException(String message, SQLException e) {
				super(message, e);
		}
}
