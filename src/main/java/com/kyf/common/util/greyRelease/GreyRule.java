package com.kyf.common.util.greyRelease;

/**
 * Created by kuangyifei on 17-3-15.
 */
public interface GreyRule {
    boolean pass(ClientProof proof);
}
