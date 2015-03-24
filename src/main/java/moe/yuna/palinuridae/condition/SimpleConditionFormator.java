package moe.yuna.palinuridae.condition;

import moe.yuna.palinuridae.condition.annotation.Like;
import moe.yuna.palinuridae.condition.exception.ConditionNotFoundException;
import moe.yuna.palinuridae.condition.exception.OrderByInfo;
import moe.yuna.palinuridae.core.Selector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by rika on 2015/1/14.
 */
public class SimpleConditionFormator implements ConditionFormator {

    public final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public <T extends Condition> Selector format(T condition, Selector selector) throws ConditionNotFoundException {
        long time = System.currentTimeMillis();
        List values = new ArrayList<>();
        List<ExpressionInfo> list = ExpressionInfoHolder.getExpressions(condition.getClass())
                .stream()
                .sorted((a, b) -> {
                    return a.getOrder() - b.getOrder();
                })
                .filter(a -> {
                    try {
                        if (!a.isAnnotation() && condition.getFreeFieldPolicy().equals(Condition.FreeFieldPolicy.Ignore)) {
                            return false;
                        }
                        Object value = a.getGetMethod().invoke(condition);
                        if (value != null) {
                            if (a.getField().getAnnotation(Like.class) != null) {
                                values.add("%" + value + "%");
                            } else {
                                values.add(value);
                            }
                            return true;
                        }
                        return false;
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                        throw new RuntimeException("condition:" + condition.toString(), ex);
                    }
                })
                .collect(Collectors.toList());
        Optional<OrderByInfo> orderByInfo = ExpressionInfoHolder.getOrderByInfo(condition.getClass());
        String orderBy = "";
        try {
            if (orderByInfo.isPresent()) {
                Object result = orderByInfo.get().getGetMethod().invoke(condition);
                if (result != null && result instanceof String) {
                    orderBy = (String) result;
                }
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new RuntimeException("condition:" + condition.toString(), ex);
        }
        selector.setOrderBy(" " + orderBy + " ");
        if (list.isEmpty()) {
            selector.setWhereExpression("");
            selector.setValues(values);
            return selector;
        }
        StringBuilder sb = new StringBuilder(" where ");
        if (list.size() == 1) {
            selector.setWhereExpression(sb.append(list.get(0).getExpression()).toString());
            selector.setValues(values);
            return selector;
        }
        list.forEach(a -> {
            sb.append(a.getExpression()).append(" and ");
        });
        sb.delete(sb.length() - 6, sb.length() - 1);
        selector.setValues(values);
        selector.setWhereExpression(sb.toString());
        log.debug("condition format process:" + (System.currentTimeMillis() - time) + "ms");
        return selector;
    }

}
