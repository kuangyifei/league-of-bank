package com.kyf.lob.util.tests;

import com.kyf.common.util.greyrelease.*;
import com.kyf.common.util.greyrelease.UnsupportedOperationException;

/**
 * Created by kuangyifei on 17-3-17.
 */
public class GreyReleaseTest {
    public static void main(String[] args) throws UnsupportedOperationException {
        GreyReleaseManager manager = new GreyReleaseManager();

        GreyRuleDescription description = new GreyRuleDescription();
        description.setProofName("数字串");
        description.setProofContentType(ProofContentType.PLAIN_TEXT);
        description.setProofOperationType(ProofOperationType.MATCH);
        description.appendValidProof("^\\d+$");

        manager.createAndAddGreyRule(description);

        ClientProof proof = new ClientProof();
        proof.setName("数字串");
        proof.setContent("42993");
        proof.setContentType(ProofContentType.PLAIN_TEXT);

        System.out.println(manager.pass(proof));
    }
}
