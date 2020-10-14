package com.hp.asi.hpic4vc.provider.error;

public class InitializationException extends Exception {
    private static final long serialVersionUID = 1L;

    public InitializationException() {
        super();
    }

    public InitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InitializationException(String message) {
        super(message);
    }

    public InitializationException(Throwable cause) {
        super(cause);
    }

}
