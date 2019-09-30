package net.fxft.webgateway.license;


/**
 * LicenseException
 */

public class LicenseException extends RuntimeException {
    private static final long serialVersionUID = 8985745653029286582L;

    public LicenseException(String message) {
        super(message);
    }

    public LicenseException(String message, Throwable e) {
        super(message, e);
    }

}