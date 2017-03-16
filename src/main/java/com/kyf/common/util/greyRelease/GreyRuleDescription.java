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
     * 用程序中的变量来表示证据时，变量的数据类型
     */
    private ProofDataType proofDataType;

    /**
     * 判断证据是否满足规则要求时，需要对变量采取的操作类型
     */
    private ProofOperationType proofOperationType;

    /**
     * 可以通过规则的证据，给出列表中的任何一个即可
     */
    private List<String> validProofs;

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

    public ProofDataType getProofDataType() {
        return proofDataType;
    }

    public void setProofDataType(ProofDataType proofDataType) {
        this.proofDataType = proofDataType;
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
