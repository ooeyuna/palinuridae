package moe.yuna.palinuridae.condition;

/**
 * Created by rika on 2015/1/14.
 */
public interface Condition {
    public enum FreeFieldPolicy{
        Ignore,All
    }

    default FreeFieldPolicy getFreeFieldPolicy(){
        return FreeFieldPolicy.All;
    }
}
