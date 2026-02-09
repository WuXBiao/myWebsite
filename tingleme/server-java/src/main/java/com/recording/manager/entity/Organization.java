package com.recording.manager.entity;

import javax.persistence.*;

/**
 * 机构实体
 */
@Entity
@Table(name = "organizations")
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 机构编码
     */
    @Column(name = "org_code", nullable = false, unique = true)
    private String orgCode;

    /**
     * 机构名称
     */
    @Column(name = "org_name", nullable = false)
    private String orgName;

    /**
     * 上级机构编码
     */
    @Column(name = "parent_org_code")
    private String parentOrgCode;

    public Organization() {
    }

    public Organization(String orgCode, String orgName, String parentOrgCode) {
        this.orgCode = orgCode;
        this.orgName = orgName;
        this.parentOrgCode = parentOrgCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getParentOrgCode() {
        return parentOrgCode;
    }

    public void setParentOrgCode(String parentOrgCode) {
        this.parentOrgCode = parentOrgCode;
    }
}
