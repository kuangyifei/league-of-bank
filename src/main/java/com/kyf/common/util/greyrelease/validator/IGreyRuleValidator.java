package com.kyf.common.util.greyrelease.validator;

import com.kyf.common.util.greyrelease.ClientProof;
import com.kyf.common.util.greyrelease.GreyRuleDescription;
import com.kyf.common.util.greyrelease.ProofOperationType;

import java.util.Set;

/**
 * Created by kuangyifei on 17-3-18.
 */
public interface IGreyRuleValidator {
    /**
     * 验证证据内容是否满足规则要求
     * 请不要在除了{@link com.kyf.common.util.greyrelease.GreyRule#pass(ClientProof)} 以外的地方调用该方法！！！
     * @param ruleDescription
     * @param proofContent
     * @return
     */
    boolean validate(GreyRuleDescription ruleDescription, String proofContent);

    /**
     * 验证器支持的操作类型
     * @return
     */
    Set<ProofOperationType> supportedOperations();
}
