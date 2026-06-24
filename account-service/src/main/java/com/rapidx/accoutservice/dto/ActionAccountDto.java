package com.rapidx.accoutservice.dto;

public class ActionAccountDto {

    private Long id;
    private String name;
    private Integer orgId;
    private String searchKey;

    public ActionAccountDto() {}

    public ActionAccountDto(Long id, String name, Integer orgId, String searchKey) {
        this.id = id;
        this.name = name;
        this.orgId = orgId;
        this.searchKey = searchKey;
    }

    public static ActionAccountDto createAccount(Object[] row) {
        if (row == null || row.length < 3) {
            return null;
        }
        
        Long id = null;
        if (row[0] != null) {
            if (row[0] instanceof Number) {
                id = ((Number) row[0]).longValue();
            } else {
                id = Long.parseLong(row[0].toString());
            }
        }
        
        String name = row[1] != null ? row[1].toString() : null;
        
        Integer orgId = null;
        if (row[2] != null) {
            if (row[2] instanceof Number) {
                orgId = ((Number) row[2]).intValue();
            } else {
                orgId = Integer.parseInt(row[2].toString());
            }
        }
        
        String searchKey = (row.length > 3 && row[3] != null) ? row[3].toString() : null;

        return new ActionAccountDto(id, name, orgId, searchKey);
    }

    // Manual Getters/Setters to prevent Lombok warnings
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }
}
