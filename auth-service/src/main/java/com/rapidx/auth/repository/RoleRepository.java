package com.rapidx.auth.repository;

import com.rapidx.auth.entity.Role;
import com.rapidx.auth.entity.RoleName;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import java.util.Optional;

public interface RoleRepository extends PagingAndSortingRepository<Role, Long>, JpaSpecificationExecutor<Role> {
    Optional<Role> findByName(RoleName name);
}
