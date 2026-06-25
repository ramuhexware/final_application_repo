package com.rapidx.accoutservice.specification;

import com.rapidx.accoutservice.entity.UCSliofBusCd;
import com.rapidx.accoutservice.entity.UcsCntPrtyAcctRoleAssn;
import com.rapidx.accoutservice.entity.UserCntrPrtyAcct;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserCntrPrtyAcctSpecification {

    public static Specification<UserCntrPrtyAcct> filterAccounts(
            Integer orgId,
            String roleStatus,
            String lineOfBusiness
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (orgId != null) {
                predicates.add(cb.equal(root.get("idOrgtn"), orgId));
            }

            if (roleStatus != null && !roleStatus.trim().isEmpty()) {
                predicates.add(cb.equal(
                        cb.lower(root.get("ucsCntPrtyAcctRoleSts")),
                        roleStatus.toLowerCase()
                ));
            }

            if (lineOfBusiness != null && !lineOfBusiness.trim().isEmpty()) {
                Join<UserCntrPrtyAcct, UcsCntPrtyAcctRoleAssn> roleAssnJoin = root.join("ucsCntPrtyAcctRoleAssn");
                Join<UcsCntPrtyAcctRoleAssn, UCSliofBusCd> lineOfBusJoin = roleAssnJoin.join("uCSliofBusCd");
                predicates.add(cb.equal(
                        cb.lower(lineOfBusJoin.get("liOfBus")),
                        lineOfBusiness.toLowerCase()
                ));
            }

            // To avoid duplicate rows when doing joins in criteria query
            if (query != null) {
                query.distinct(true);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

