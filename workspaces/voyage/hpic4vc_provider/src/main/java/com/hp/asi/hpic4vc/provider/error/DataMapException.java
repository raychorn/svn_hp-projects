package com.hp.asi.hpic4vc.provider.error;

public class DataMapException extends Exception {
    private static final long serialVersionUID = 1L;

    public DataMapException() {
        super();
    }

    public DataMapException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataMapException(String message) {
        super(message);
    }

    public DataMapException(Throwable cause) {
        super(cause);
    }
}