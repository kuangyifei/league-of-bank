package com.kyf.common.util.greyRelease;


/**
 * Created by kuangyifei on 17-3-19.
 */
public class Product {
    /**
     * 产品标识
     */
    private String id;

    /**
     * 产品名称
     */
    private String name;

    /**
     * 产品版本
     */
    private String version;

    public Product() {
    }

    public Product(String id, String name, String version) {
        this.id = id;
        this.name = name;
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Product)) {
            return false;
        }
        Product other = (Product) obj;
        if (this.getId() != other.getId() || this.getVersion() != other.getVersion()) {
            return false;
        }
        return true;
    }
}
