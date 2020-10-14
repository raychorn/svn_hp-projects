package com.hp.asi.hpic4vc.provider.error;

public class CommunicationException extends Exception {
    private static final long serialVersionUID = 1L;

    public CommunicationException() {
        super();
    }

    public CommunicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommunicationException(String message) {
        super(message);
    }

    public CommunicationException(Throwable cause) {
        super(cause);
    }
}