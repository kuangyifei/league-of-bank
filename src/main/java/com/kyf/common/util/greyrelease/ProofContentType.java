package com.kyf.common.util.greyrelease;

/**
 * 陈述证据的内容的类型
 * Created by kuangyifei on 17-3-16.
 */
public enum ProofContentType {
    INTEGER(0),     // 整数串，例如："1234930"，不限位数
    FLOAT(1),       // 浮点数串，例如："12352.33323"，不限位数
    PLAIN_TEXT(2),  // 普通文本串，例如："adb323llfaldgfghd"
    BOOLEAN(3),     // 布尔值，不区分大小写，例如："true"、"False"，最终值为：Boolean.valueOf(content)
    JSON_STRING(4)  // JSON 字符串，例如："{\"country\": \"中国\", \"province\": \"广东\", \"country\": \"深圳\"}"
    ;

    private int id;
    ProofContentType(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}
