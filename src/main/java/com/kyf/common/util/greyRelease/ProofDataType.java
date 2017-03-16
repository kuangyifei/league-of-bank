package com.kyf.common.util.greyRelease;

/**
 * 用程序中的变量来表示证据时，变量的数据类型
 * Created by kuangyifei on 17-3-16.
 */
public enum ProofDataType {
    INTEGER(0),     // 整数，不限位数
    FLOAT(1),       // 浮点数，不限位数
    STRING(2),      // 普通字符串
    BOOLEAN(3),     // 布尔型
    REGULER_EXP(4), // 正则表达式
    JSON_STRING(5)  // JSON 字符串
    ;

    private int id;
    ProofDataType(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}
