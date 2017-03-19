package com.kyf.common.util.greyRelease.validator;

import com.kyf.common.util.greyRelease.ProofContentType;
import com.kyf.common.util.greyRelease.ProofOperationType;

/**
 * Created by kuangyifei on 17-3-18.
 */
public interface IGreyRuleValidatorFactory {
    IGreyRuleValidator buildValidator(ProofContentType proofContentType, ProofOperationType proofOperationType);
}
