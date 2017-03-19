package com.kyf.common.util.greyRelease.validator;

import com.kyf.common.util.greyRelease.ClientProof;
import com.kyf.common.util.greyRelease.GreyRuleDescription;
import com.kyf.common.util.greyRelease.ProofOperationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by kuangyifei on 17-3-18.
 */
public class NumberRuleValidator implements IGreyRuleValidator {
    private static Logger logger = LoggerFactory.getLogger(NumberRuleValidator.class);

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
     * 请不要在除了{@link com.kyf.common.util.greyRelease.GreyRule#pass(ClientProof)} 以外的地方调用该方法！！！
     * @param ruleDescription
     * @param proofContent
     * @return
     */
    @Override
    public boolean validate(GreyRuleDescription ruleDescription, String proofContent) {
        BigDecimal bdProof;
        try {
            bdProof = new BigDecimal(proofContent);
        } catch (Exception e) {
            logger.error("证据内容（{}）不是合法的数字串！", proofContent);
            return false;
        }

        boolean hasRealValidProofs = false;
        BigDecimal bdValidProof;
        for (String validProof : ruleDescription.getValidProofs()) {
            try {
                bdValidProof = new BigDecimal(validProof);
            } catch (Exception e) {
                logger.error("规则中的有效证据内容（{}）不是合法的数字串！", validProof);
                continue;
            }
            hasRealValidProofs = true;
            switch (ruleDescription.getProofOperationType()) {
                case EQUAL:
                    if (bdProof.compareTo(bdValidProof) == 0) {
                        return true;
                    }
                    break;
                case LESS_THAN:
                    if (bdProof.compareTo(bdValidProof) >= 0) {
                        return false;
                    }
                    break;
                case LESS_THAN_OR_EQUAL:
                    if (bdProof.compareTo(bdValidProof) > 0) {
                        return false;
                    }
                    break;
                case GREATER_THAN:
                    if (bdProof.compareTo(bdValidProof) <= 0) {
                        return false;
                    }
                    break;
                case GREATER_THAN_OR_EQUAL:
                    if (bdProof.compareTo(bdValidProof) < 0) {
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
        if (!hasRealValidProofs || ruleDescription.getProofOperationType() == ProofOperationType.EQUAL) {
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
