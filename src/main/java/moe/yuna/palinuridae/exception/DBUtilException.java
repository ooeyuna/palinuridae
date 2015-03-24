package moe.yuna.palinuridae.exception;

/**
 *
 * @author rika
 */
public class DBUtilException extends Exception{
    public DBUtilException(){

    }
    public DBUtilException(String message) {
        super(message);
    }

    public DBUtilException(Exception ex) {
        super(ex);
    }
    public DBUtilException(String message,Exception ex) {
        super(message,ex);
    }
}
