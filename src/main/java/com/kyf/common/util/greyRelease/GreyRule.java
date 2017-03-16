package com.kyf.common.util.greyRelease;

/**
 * Created by kuangyifei on 17-3-15.
 */
public interface GreyRule {
    GreyRuleDescription getDescription();
    boolean pass(ClientProof proof);
}
