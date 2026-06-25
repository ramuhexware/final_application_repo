package com.rapidx.accoutservice.processor;

import com.rapidx.accoutservice.dto.ActionAccountDto;
import com.rapidx.accoutservice.entity.UserCntrPrtyAcct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UserCntrPrtyAcctProcessor {

    private static final Logger log = LoggerFactory.getLogger(UserCntrPrtyAcctProcessor.class);

    public ActionAccountDto transformToDto(UserCntrPrtyAcct entity) {
        if (entity == null) {
            return null;
        }
        log.debug("Transforming UserCntrPrtyAcct entity id: {} to DTO", entity.getId());
        
        return new ActionAccountDto(
                entity.getId(),
                entity.getNameCntprtyAcct(),
                entity.getIdOrgtn(),
                entity.getNameCntprtyAcct() + " | Org:" + entity.getIdOrgtn()
        );
    }
}
