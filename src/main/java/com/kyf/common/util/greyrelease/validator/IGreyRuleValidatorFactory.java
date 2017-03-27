package com.kyf.common.util.greyrelease.validator;

import com.kyf.common.util.greyrelease.ProofContentType;
import com.kyf.common.util.greyrelease.ProofOperationType;

/**
 * Created by kuangyifei on 17-3-18.
 */
public interface IGreyRuleValidatorFactory {
    IGreyRuleValidator buildValidator(ProofContentType proofContentType, ProofOperationType proofOperationType);
}
