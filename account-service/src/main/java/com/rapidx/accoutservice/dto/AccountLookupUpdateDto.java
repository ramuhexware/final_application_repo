package com.rapidx.accoutservice.dto;

import com.rapidx.accoutservice.entity.UcsCntprtyAcctSt;
import com.rapidx.accoutservice.entity.UcsCntprtyAcctRoleSt;
import java.util.List;
import java.util.Map;

public class AccountLookupUpdateDto {
    private List<UcsOrgtnRoleDTO> ucsOrgtnRoleDTOs;
    private List<UcsProdtDTO> ucsProdtDTOs;
    private List<UcsLineOfBusinessDTO> ucsLineOfBusinessDTOs;
    private Map<String, List<UcsOrgtnRoleDTO>> maplistUcsOrgtnRoleDTOs;
    private Map<String, List<UcsProdtDTO>> maplistUcsProdtDTOs;
    private List<UcsCntprtyAcctSt> ucsCntprtyAcctSt;
    private List<UcsCntprtyAcctRoleSt> ucsCntprtyAcctRoleSt;
    private ResponseStatusDTO respSts;

    public AccountLookupUpdateDto() {}

    public List<UcsOrgtnRoleDTO> getUcsOrgtnRoleDTOs() {
        return ucsOrgtnRoleDTOs;
    }

    public void setUcsOrgtnRoleDTOs(List<UcsOrgtnRoleDTO> ucsOrgtnRoleDTOs) {
        this.ucsOrgtnRoleDTOs = ucsOrgtnRoleDTOs;
    }

    public List<UcsProdtDTO> getUcsProdtDTOs() {
        return ucsProdtDTOs;
    }

    public void setUcsProdtDTOs(List<UcsProdtDTO> ucsProdtDTOs) {
        this.ucsProdtDTOs = ucsProdtDTOs;
    }

    public List<UcsLineOfBusinessDTO> getUcsLineOfBusinessDTOs() {
        return ucsLineOfBusinessDTOs;
    }

    public void setUcsLineOfBusinessDTOs(List<UcsLineOfBusinessDTO> ucsLineOfBusinessDTOs) {
        this.ucsLineOfBusinessDTOs = ucsLineOfBusinessDTOs;
    }

    public Map<String, List<UcsOrgtnRoleDTO>> getMaplistUcsOrgtnRoleDTOs() {
        return maplistUcsOrgtnRoleDTOs;
    }

    public void setMaplistUcsOrgtnRoleDTOs(Map<String, List<UcsOrgtnRoleDTO>> maplistUcsOrgtnRoleDTOs) {
        this.maplistUcsOrgtnRoleDTOs = maplistUcsOrgtnRoleDTOs;
    }

    public Map<String, List<UcsProdtDTO>> getMaplistUcsProdtDTOs() {
        return maplistUcsProdtDTOs;
    }

    public void setMaplistUcsProdtDTOs(Map<String, List<UcsProdtDTO>> maplistUcsProdtDTOs) {
        this.maplistUcsProdtDTOs = maplistUcsProdtDTOs;
    }

    public List<UcsCntprtyAcctSt> getUcsCntprtyAcctSt() {
        return ucsCntprtyAcctSt;
    }

    public void setUcsCntprtyAcctSt(List<UcsCntprtyAcctSt> ucsCntprtyAcctSt) {
        this.ucsCntprtyAcctSt = ucsCntprtyAcctSt;
    }

    public List<UcsCntprtyAcctRoleSt> getUcsCntprtyAcctRoleSt() {
        return ucsCntprtyAcctRoleSt;
    }

    public void setUcsCntprtyAcctRoleSt(List<UcsCntprtyAcctRoleSt> ucsCntprtyAcctRoleSt) {
        this.ucsCntprtyAcctRoleSt = ucsCntprtyAcctRoleSt;
    }

    public ResponseStatusDTO getRespSts() {
        return respSts;
    }

    public void setRespSts(ResponseStatusDTO respSts) {
        this.respSts = respSts;
    }
}
