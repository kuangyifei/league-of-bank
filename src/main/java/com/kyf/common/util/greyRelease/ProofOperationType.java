package com.kyf.common.util.greyRelease;

/**
 * 判断证据是否满足规则要求时，需要对证据采取的操作类型
 * Created by kuangyifei on 17-3-16.
 */
public enum  ProofOperationType {
    EQUAL(0),                   // 相等操作
    LESS_THAN(1),               // 小于操作
    LESS_THAN_OR_EQUAL(2),      // 小于或等于操作
    GREATER_THAN(3),            // 大于操作
    GREATER_THAN_OR_EQUAL(4),   // 大于或操作
    MATCH(5),                   // 正则表达式类型采用匹配操作
    RESOLVE_AND_VALIDATE(6)     // JSON 字符串类型采用先解析然后验证操作
    ;

    private int id;

    ProofOperationType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
