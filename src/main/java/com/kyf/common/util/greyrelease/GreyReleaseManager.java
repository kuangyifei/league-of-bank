package com.kyf.common.util.greyrelease;

import com.alibaba.fastjson.JSON;
import com.kyf.common.util.greyrelease.validator.DefaultGreyRuleValidatorFactory;
import com.kyf.common.util.greyrelease.validator.IGreyRuleValidatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by kuangyifei on 17-3-15.
 */
public class GreyReleaseManager {
    private static Logger logger = LoggerFactory.getLogger(GreyReleaseManager.class);

    /**
     * 灰度规则列表
     */
    private List<GreyRule> greyRules = new LinkedList<>();

    /**
     * 要发布的产品
     */
    private Product product;

    public GreyReleaseManager() {

    }

    /**
     * 判断提供的证据是否满足对应产品的灰度规则的要求
     * @param proof
     * @param product
     * @return
     */
    public boolean satisfy(Product product, ClientProof proof) {
        if (!checkNecessaryProductInfo(product)) {
            return false;
        }

        if (!checkNecessaryProductInfo(this.product)) {
            logger.warn("product in grey release manager does not have all necessary info: product={}", JSON.toJSONString(this.product));
            return false;
        }

        if (!this.product.equals(product)) {
            return false;
        }

        return pass(proof);
    }

    /**
     * 产品 id 和版本都存在才返回 true
     * @param product
     * @return
     */
    private boolean checkNecessaryProductInfo(Product product) {
        if (product == null) {
            return false;
        }

        if (product.getId() == null) {
            return false;
        }

        if (product.getVersion() == null) {
            return false;
        }

        return true;
    }

    /**
     * 给定的证据能否通过 灰度规则列表{@link #greyRules} 中所有关联的规则的验证
     * @param proof
     * @return
     */
    public boolean pass(ClientProof proof) {
        // 不存在规则，不管有没有拿出证据都无条件通过
        if (greyRules == null || greyRules.isEmpty()) {
            return true;
        }

        // 没有提供证据
        if (proof == null) {
            return false;
        }

        boolean hasAssociatedRules = false;
        for (GreyRule rule: greyRules) {
            if (rule == null || rule.getDescription() == null) {
                continue;
            }
            String proofName = rule.getDescription().getProofName();
            if (proofName == null) {
                proofName = "";
            }
            String clientProofName = proof.getName();
            if (clientProofName == null) {
                clientProofName = "";
            }
            // 证据名相同的情况下才做判断
            if (proofName.equals(clientProofName)) {
                hasAssociatedRules = true;
                if (!rule.pass(proof)) {
                    return false;
                }
            }
        }

        if (!hasAssociatedRules) {
            // 灰度规则列表中没有找到与ClientProof关联的规则
            logger.warn("no rule has been found for proofName: {}, please add required rules or check you client proof name first!", proof.getName());
            return false;
        }
        return true;
    }

    public GreyRule createGreyRule(GreyRuleDescription description) throws UnsupportedOperationException {
        return GreyRuleFactory.buildGreyRule(description, new DefaultGreyRuleValidatorFactory());
    }

    public GreyRule createGreyRule(GreyRuleDescription description, IGreyRuleValidatorFactory validatorFactory)
            throws UnsupportedOperationException {
        return GreyRuleFactory.buildGreyRule(description, validatorFactory);
    }

    /**
     * 要么全部创建，要么一个也不创建
     * @param descriptions
     * @return
     * @throws UnsupportedOperationException
     */
    public List<GreyRule> createGreyRules(List<GreyRuleDescription> descriptions) throws UnsupportedOperationException {
        if (descriptions == null) {
            return null;
        }
        List<GreyRule> tmp = new ArrayList<>();
        IGreyRuleValidatorFactory validatorFactory = new DefaultGreyRuleValidatorFactory();
        for (GreyRuleDescription description: descriptions) {
            GreyRule rule = GreyRuleFactory.buildGreyRule(description, validatorFactory);
            tmp.add(rule);
        }
        return tmp;
    }

    /**
     * 要么全部创建，要么一个也不创建
     * @param descriptions
     * @return
     * @throws UnsupportedOperationException
     */
    public List<GreyRule> createGreyRules(List<GreyRuleDescription> descriptions, List<IGreyRuleValidatorFactory> factories)
            throws UnsupportedOperationException {
        if (descriptions == null) {
            return null;
        }
        if (factories == null || descriptions.size() != factories.size()) {
            logger.error("factories = {}, descriptions.size() = {}, factories.size() = {} ！",
                    factories, descriptions.size(), factories.size());
            return null;
        }
        List<GreyRule> tmp = new ArrayList<>();
        for (int i = 0; i < descriptions.size(); ++i) {
            GreyRule rule = GreyRuleFactory.buildGreyRule(descriptions.get(i), factories.get(i));
            tmp.add(rule);
        }
        return tmp;
    }

    public boolean createAndAddGreyRule(GreyRuleDescription description) throws UnsupportedOperationException {
        GreyRule rule = GreyRuleFactory.buildGreyRule(description, new DefaultGreyRuleValidatorFactory());
        if (rule == null) {
            return false;
        }
        return greyRules.add(rule);
    }

    public boolean createAndAddGreyRule(GreyRuleDescription description, IGreyRuleValidatorFactory validatorFactory)
            throws UnsupportedOperationException {
        GreyRule rule = GreyRuleFactory.buildGreyRule(description, validatorFactory);
        if (rule == null) {
            return false;
        }
        return greyRules.add(rule);
    }

    /**
     * 要么全部创建，要么一个也不创建
     * @param descriptions
     * @return
     * @throws UnsupportedOperationException
     */
    public boolean createAndAddGreyRules(List<GreyRuleDescription> descriptions) throws UnsupportedOperationException {
        if (descriptions == null) {
            return false;
        }
        List<GreyRule> tmp = new ArrayList<>();
        IGreyRuleValidatorFactory validatorFactory = new DefaultGreyRuleValidatorFactory();
        for (GreyRuleDescription description: descriptions) {
            GreyRule rule = GreyRuleFactory.buildGreyRule(description, validatorFactory);
            tmp.add(rule);
        }
        return greyRules.addAll(tmp);
    }

    /**
     * 要么全部创建，要么一个也不创建
     * @param descriptions
     * @return
     * @throws UnsupportedOperationException
     */
    public boolean createAndAddGreyRules(List<GreyRuleDescription> descriptions, List<IGreyRuleValidatorFactory> factories)
            throws UnsupportedOperationException {
        if (descriptions == null) {
            return false;
        }
        if (factories == null || descriptions.size() != factories.size()) {
            logger.error("factories = {}, descriptions.size() = {}, factories.size() = {} ！",
                    factories, descriptions.size(), factories.size());
            return false;
        }
        List<GreyRule> tmp = new ArrayList<>();
        for (int i = 0; i < descriptions.size(); ++i) {
            GreyRule rule = GreyRuleFactory.buildGreyRule(descriptions.get(i), factories.get(i));
            tmp.add(rule);
        }
        return greyRules.addAll(tmp);
    }

    public boolean addGreyRule(GreyRule rule) {
        if (rule == null) {
            return false;
        }
        return greyRules.add(rule);
    }

    public boolean addGreyRules(List<GreyRule> rules) {
        if (rules == null) {
            return false;
        }
        return greyRules.addAll(rules);
    }

    public void removeGreyRule(String id) {
        if (id == null) {
            return;
        }
        List<GreyRule> tmp = new LinkedList<>();
        for (GreyRule rule: greyRules) {
            if (rule != null && rule.getDescription() != null && id.equals(rule.getDescription().getRuleId())) {
                continue;
            }
            tmp.add(rule);
        }
        greyRules = tmp;
    }


    public void removeGreyRule(GreyRuleDescription description) {
        if (description == null) {
            return;
        }
        removeGreyRule(description.getRuleId());
    }

    public void removeGreyRule(GreyRule rule) {
        if (rule == null) {
            return;
        }
        removeGreyRule(rule.getDescription());
    }

    public int removeNullGreyRules() {
        List<GreyRule> tmp = new LinkedList<>();
        for (GreyRule rule: greyRules) {
            if (rule == null) {
                continue;
            }
            tmp.add(rule);
        }
        int cnt = greyRules.size() - tmp.size();
        greyRules = tmp;
        return cnt;
    }

    public List<GreyRule> getGreyRules() {
        return greyRules;
    }

    public void setGreyRules(List<GreyRule> greyRules) {
        this.greyRules = greyRules;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
