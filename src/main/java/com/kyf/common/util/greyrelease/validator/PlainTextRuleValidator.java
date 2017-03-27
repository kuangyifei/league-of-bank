package com.kyf.common.util.greyrelease.validator;

import com.kyf.common.util.greyrelease.ClientProof;
import com.kyf.common.util.greyrelease.GreyRuleDescription;
import com.kyf.common.util.greyrelease.ProofOperationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by kuangyifei on 17-3-18.
 */
public class PlainTextRuleValidator implements IGreyRuleValidator {
    private static Logger logger = LoggerFactory.getLogger(PlainTextRuleValidator.class);

    private static Set<ProofOperationType> supportedOperationSet = new HashSet<>(
            Arrays.asList(new ProofOperationType[]
                    {
                            ProofOperationType.EQUAL,
                            ProofOperationType.LESS_THAN,
                            ProofOperationType.LESS_THAN_OR_EQUAL,
                            ProofOperationType.GREATER_THAN,
                            ProofOperationType.GREATER_THAN_OR_EQUAL
                    }));

    /**
     * 验证证据内容是否满足规则要求
     * 请不要在除了{@link com.kyf.common.util.greyrelease.GreyRule#pass(ClientProof)} 以外的地方调用该方法！！！
     * @param ruleDescription
     * @param proofContent
     * @return
     */
    @Override
    public boolean validate(GreyRuleDescription ruleDescription, String proofContent) {
        for (String validProof : ruleDescription.getValidProofs()) {
            switch (ruleDescription.getProofOperationType()) {
                case EQUAL:
                    if (proofContent.equals(validProof)) {
                        return true;
                    }
                    break;
                case LESS_THAN:
                    if (proofContent.compareTo(validProof) >= 0) {
                        return false;
                    }
                    break;
                case LESS_THAN_OR_EQUAL:
                    if (proofContent.compareTo(validProof) > 0) {
                        return false;
                    }
                    break;
                case GREATER_THAN:
                    if (proofContent.compareTo(validProof) <= 0) {
                        return false;
                    }
                    break;
                case GREATER_THAN_OR_EQUAL:
                    if (proofContent.compareTo(validProof) < 0) {
                        return false;
                    }
                    break;
                default:
                    logger.error("灰度规则有错！规则{}（ID: {}）接受的证据类型是{}，不应该对其进行{}操作！",
                            ruleDescription.getRuleName(),
                            ruleDescription.getRuleId(),
                            ruleDescription.getProofContentType(),
                            ruleDescription.getProofOperationType());
                    return false;
            }
        }
        if (ruleDescription.getProofOperationType() == ProofOperationType.EQUAL) {
            return false;
        }
        return true;
    }

    /**
     * 验证器支持的操作类型
     *
     * @return
     */
    @Override
    public Set<ProofOperationType> supportedOperations() {
        return supportedOperationSet;
    }
}
