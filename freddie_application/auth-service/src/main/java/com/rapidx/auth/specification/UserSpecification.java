package com.rapidx.auth.specification;

import com.rapidx.auth.entity.Role;
import com.rapidx.auth.entity.RoleName;
import com.rapidx.auth.entity.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

    public static Specification<User> filterUsers(
            String username,
            String email,
            String roleName
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (username != null && !username.trim().isEmpty()) {
                predicates.add(cb.like(
                        cb.lower(root.get("username")),
                        "%" + username.toLowerCase() + "%"
                ));
            }

            if (email != null && !email.trim().isEmpty()) {
                predicates.add(cb.like(
                        cb.lower(root.get("email")),
                        "%" + email.toLowerCase() + "%"
                ));
            }

            if (roleName != null && !roleName.trim().isEmpty()) {
                Join<User, Role> roleJoin = root.join("roles");
                try {
                    RoleName enumRole = RoleName.valueOf(roleName.toUpperCase());
                    predicates.add(cb.equal(roleJoin.get("name"), enumRole));
                } catch (IllegalArgumentException e) {
                    try {
                        RoleName enumRolePrefixed = RoleName.valueOf("ROLE_" + roleName.toUpperCase());
                        predicates.add(cb.equal(roleJoin.get("name"), enumRolePrefixed));
                    } catch (IllegalArgumentException ex) {
                        predicates.add(cb.disjunction());
                    }
                }
            }

            if (query != null) {
                query.distinct(true);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
