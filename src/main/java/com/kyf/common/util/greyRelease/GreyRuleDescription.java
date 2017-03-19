package com.kyf.common.util.greyRelease;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kuangyifei on 17-3-16.
 */
public class GreyRuleDescription {
    /**
     * 规则标识
     */
    private String ruleId;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 证据名称
     */
    private String proofName;

    /**
     * 陈述证据的内容的类型
     */
    private ProofContentType proofContentType;

    /**
     * 判断证据是否满足规则要求时，需要对证据采取的操作类型
     */
    private ProofOperationType proofOperationType;

    /**
     * 可以通过规则的证据，给出列表中的任何一个即可
     */
    private List<String> validProofs;

    public GreyRuleDescription() {
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getProofName() {
        return proofName;
    }

    public void setProofName(String proofName) {
        this.proofName = proofName;
    }

    public ProofContentType getProofContentType() {
        return proofContentType;
    }

    public void setProofContentType(ProofContentType proofContentType) {
        this.proofContentType = proofContentType;
    }

    public ProofOperationType getProofOperationType() {
        return proofOperationType;
    }

    public void setProofOperationType(ProofOperationType proofOperationType) {
        this.proofOperationType = proofOperationType;
    }

    public List<String> getValidProofs() {
        return validProofs;
    }

    public void setValidProofs(List<String> validProofs) {
        this.validProofs = validProofs;
    }

    public boolean appendValidProof(String proof) {
        if (this.validProofs == null) {
            this.validProofs = new ArrayList<String>();
        }

        return this.validProofs.add(proof);
    }

    public boolean removeValidProof(String proof) {
        if (this.validProofs != null) {
            return this.validProofs.remove(proof);
        }
        return false;
    }
}
