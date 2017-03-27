package com.kyf.common.util.greyrelease;

/**
 * Created by kuangyifei on 17-3-15.
 */
public class ClientProof {
    /**
     * 证据名称
     */
    private String name;

    /**
     * 证据的内容陈述
     */
    private String content;

    /**
     * {@link #content} 的类型
     */
    private ProofContentType contentType;

    public ClientProof() {
    }

    public ClientProof(String name, String content, ProofContentType contentType) {
        this.name = name;
        this.content = content;
        this.contentType = contentType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ProofContentType getContentType() {
        return contentType;
    }

    public void setContentType(ProofContentType contentType) {
        this.contentType = contentType;
    }
}
