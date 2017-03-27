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
public class BooleanRuleValidator implements IGreyRuleValidator {
    private static Logger logger = LoggerFactory.getLogger(BooleanRuleValidator.class);

    private static Set<ProofOperationType> supportedOperationSet = new HashSet<>(
            Arrays.asList(new ProofOperationType[]
                    {ProofOperationType.EQUAL}));

    /**
     * 验证证据内容是否满足规则要求
     * 请不要在除了{@link com.kyf.common.util.greyrelease.GreyRule#pass(ClientProof)} 以外的地方调用该方法！！！
     * @param ruleDescription
     * @param proofContent
     * @return
     */
    @Override
    public boolean validate(GreyRuleDescription ruleDescription, String proofContent) {
        switch (ruleDescription.getProofOperationType()) {
            case EQUAL:
                if (proofContent == null) {
                    return false;
                }
                boolean bProof = Boolean.valueOf(proofContent);
                for (String validProof : ruleDescription.getValidProofs()) {
                    if (Boolean.valueOf(validProof) == bProof) {
                        return true;
                    }
                }
                return false;
            default: // 布尔类型不应该支持除相等以外的其它操作，返回false是为了在规则本身有错的情况下不放行任何客户端，以防造成大的影响
                logger.error("灰度规则有错！规则{}（ID: {}）接受的证据类型是{}，不应该对其进行{}操作！",
                        ruleDescription.getRuleName(),
                        ruleDescription.getRuleId(),
                        ruleDescription.getProofContentType(),
                        ruleDescription.getProofOperationType());
                return false;
        }
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
