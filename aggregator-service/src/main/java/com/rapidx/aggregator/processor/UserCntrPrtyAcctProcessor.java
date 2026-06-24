package com.rapidx.aggregator.processor;

import com.rapidx.aggregator.dto.ActionAccountDto;
import com.rapidx.aggregator.entity.UserCntrPrtyAcct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserCntrPrtyAcctProcessor {

    public ActionAccountDto transformToDto(UserCntrPrtyAcct entity) {
        if (entity == null) {
            return null;
        }
        log.debug("Transforming UserCntrPrtyAcct entity id: {} to DTO", entity.getId());

        return ActionAccountDto.builder()
                .id(entity.getId())
                .name(entity.getNameCntprtyAcct())
                .orgId(entity.getIdOrgtn())
                .searchKey(entity.getNameCntprtyAcct() + " | Org:" + entity.getIdOrgtn())
                .build();
    }
}
