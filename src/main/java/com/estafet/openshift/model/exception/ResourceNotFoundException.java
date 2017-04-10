package com.estafet.openshift.model.exception;

/**
 * Created by Delcho Delov on 07.04.17.
 */
public class ResourceNotFoundException extends DMException {
		public ResourceNotFoundException(String message, Exception e) {
				super(message, e);
		}

		public ResourceNotFoundException(String s) {
				super(s);
		}
}