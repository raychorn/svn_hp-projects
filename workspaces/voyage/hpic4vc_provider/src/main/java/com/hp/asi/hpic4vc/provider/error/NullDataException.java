package com.hp.asi.hpic4vc.provider.error;

public class NullDataException extends Exception {
    private static final long serialVersionUID = 1L;

    public NullDataException() {
        super();
    }

    public NullDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public NullDataException(String message) {
        super(message);
    }

    public NullDataException(Throwable cause) {
        super(cause);
    }
}
