package net.fxft.webgateway.route;


//@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "session timeout.")
public class SessionTimeoutException extends RuntimeException {

    public SessionTimeoutException(String msg) {
        super(msg);
    }


}
