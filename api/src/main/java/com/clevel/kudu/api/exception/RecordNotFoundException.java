package com.clevel.kudu.api.exception;

import javax.ejb.ApplicationException;

@ApplicationException(rollback=true)
public class RecordNotFoundException extends Exception {

    public RecordNotFoundException(String message) {
        super(message);
    }
}
