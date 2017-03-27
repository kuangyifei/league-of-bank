package com.kyf.common.util.greyrelease;

import com.kyf.common.util.greyrelease.validator.IGreyRuleValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 灰度规则
 * Created by kuangyifei on 17-3-15.
 */
public class GreyRule {
    private static Logger logger = LoggerFactory.getLogger(GreyRule.class);
    /**
     * 规则的详细描述
     */
    private GreyRuleDescription description;

    /**
     * 规则验证器
     */
    private IGreyRuleValidator validator;

    public GreyRule() {
    }

    public boolean pass(ClientProof proof) {
        if (proof == null) {
            logger.warn("proof should not be null!");
            return false;
        }

        try {
            // 检查证据名称是否一致
            if (!this.description.getProofName().equals(proof.getName())) {
                return false;
            }
            // 检查证据的内容的类型是否一致
            if (this.description.getProofContentType() != proof.getContentType()) {
                return false;
            }
            // 检查证据中是否有实质内容
            if (proof.getContent() == null) {
                return false;
            }
            // 检查规则中是否设定了有效的证据，若没有设定，则意味着不论提供了什么证据，都不允许通过
            if (this.description.getValidProofs() == null) {
                return false;
            }
            boolean hasValidProof = false;
            for (String validProof: this.description.getValidProofs()) {
                if (validProof != null) {
                    hasValidProof = true;
                    break;
                }
            }
            if (!hasValidProof) {
                return false;
            }
            // 检查操作结果
            return validator.validate(this.description, proof.getContent());
        } catch (Exception e) {
            logger.warn("grey rule description or validator may be null! description={}, validator={}",
                    this.description, this.validator);
            return false;
        }
    }

    public GreyRuleDescription getDescription() {
        return description;
    }

    public void setDescription(GreyRuleDescription description) {
        this.description = description;
    }

    public IGreyRuleValidator getValidator() {
        return validator;
    }

    public void setValidator(IGreyRuleValidator validator) {
        this.validator = validator;
    }
}
