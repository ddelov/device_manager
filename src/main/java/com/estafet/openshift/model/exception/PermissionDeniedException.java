package com.estafet.openshift.model.exception;

/**
 * Created by Delcho Delov on 19.1.2017 г..
 */
public class PermissionDeniedException extends DMException {
    public PermissionDeniedException(String message, Exception e) {
        super(message, e);
    }

}
