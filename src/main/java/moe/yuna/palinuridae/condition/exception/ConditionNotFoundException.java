package moe.yuna.palinuridae.condition.exception;

/**
 * Created by rika on 2015/1/14.
 */
public class ConditionNotFoundException extends Exception{
    public ConditionNotFoundException(){
        super();
    }
    public ConditionNotFoundException(String message){
        super(message);
    }
    public ConditionNotFoundException(Exception ex, String message){
        super(message, ex);
    }

}
