package com.example.xianzhiyun.entity;

public class CommunityCategory {
    private Long id;
    private String categoryKey;
    private String name;
    private Integer sort;
    private Integer status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCategoryKey() { return categoryKey; }
    public void setCategoryKey(String categoryKey) { this.categoryKey = categoryKey; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}