package com.kyf.common.util.greyrelease;

import com.kyf.common.util.greyrelease.validator.IGreyRuleValidator;
import com.kyf.common.util.greyrelease.validator.IGreyRuleValidatorFactory;

/**
 * Created by kuangyifei on 17-3-18.
 */
public class GreyRuleFactory {
    /**
     * 创建灰度规则工厂方法
     * @param description
     * @param validatorFactory
     * @return
     */
    public static GreyRule buildGreyRule(GreyRuleDescription description, IGreyRuleValidatorFactory validatorFactory)
            throws UnsupportedOperationException {
        if (description == null || validatorFactory == null) {
            return null;
        }

        IGreyRuleValidator validator = validatorFactory.buildValidator(description.getProofContentType(), description.getProofOperationType());
        if (validator != null
                && validator.supportedOperations() != null
                && !validator.supportedOperations().contains(description.getProofOperationType())) {
            throw new UnsupportedOperationException("validator: " + validator + " does not support operation: " +
                    description.getProofOperationType() + " !!!");
        }

        GreyRule rule = new GreyRule();
        rule.setDescription(description);
        rule.setValidator(validator);

        return rule;
    }
}
