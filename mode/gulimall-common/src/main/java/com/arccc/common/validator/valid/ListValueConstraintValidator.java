package com.arccc.common.validator.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *  自定义校验器，用于校验ListValue
 */
public class ListValueConstraintValidator implements ConstraintValidator<ListValue,Integer> {
    private Set<Integer> set = new LinkedHashSet<>();
    //初始化方法
    @Override
    public void initialize(ListValue constraintAnnotation) {
        for (int val : constraintAnnotation.vals()) {
            set.add(val);
        }
    }

    /**
     *
     * @param value 需要校验的值
     * @param context 上下文环境信息
     *
     * @return
     */
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {

        return set.contains(value);
    }
}
