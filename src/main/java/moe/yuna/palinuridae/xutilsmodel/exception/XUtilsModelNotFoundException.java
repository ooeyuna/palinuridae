package moe.yuna.palinuridae.xutilsmodel.exception;

/**
 * Created by rika on 2015/1/8.
 */
public class XUtilsModelNotFoundException extends Exception {
    public XUtilsModelNotFoundException(){
        super();
    }
    public XUtilsModelNotFoundException(String message){
        super(message);
    }
    public XUtilsModelNotFoundException(Exception ex, String message){
        super(message, ex);
    }
}
