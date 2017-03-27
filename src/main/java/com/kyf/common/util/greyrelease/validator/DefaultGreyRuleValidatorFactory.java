package com.kyf.common.util.greyrelease.validator;

import com.kyf.common.util.greyrelease.ProofContentType;
import com.kyf.common.util.greyrelease.ProofOperationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by kuangyifei on 17-3-18.
 */
public class DefaultGreyRuleValidatorFactory implements IGreyRuleValidatorFactory {
    private static Logger logger = LoggerFactory.getLogger(DefaultGreyRuleValidatorFactory.class);

    @Override
    public IGreyRuleValidator buildValidator(ProofContentType proofContentType, ProofOperationType proofOperationType) {
        if (proofContentType == null) {
            return null;
        }
        switch (proofContentType) {
            case INTEGER:
            case FLOAT:
                return new NumberRuleValidator();
            case BOOLEAN:
                return new BooleanRuleValidator();
            case PLAIN_TEXT:
                if (proofOperationType == ProofOperationType.MATCH) {
                    return new RegularExpRuleValidator();
                }
                return new PlainTextRuleValidator();
            case JSON_STRING:
                return new JsonRuleValidator();
            default:
                logger.warn("unable to build a grey rule validator for proof content type: {} !!!", proofContentType);
                return null;
        }
    }
}
