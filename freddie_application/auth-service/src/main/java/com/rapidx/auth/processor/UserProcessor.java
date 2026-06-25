package com.rapidx.auth.processor;

import com.rapidx.auth.dto.UserDto;
import com.rapidx.auth.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@Slf4j
public class UserProcessor {

    public UserDto transformToDto(User entity) {
        if (entity == null) {
            return null;
        }
        log.debug("Transforming User entity id: {} to DTO", entity.getId());

        return new UserDto(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getRoles().stream()
                        .map(r -> r.getName().name())
                        .collect(Collectors.toList())
        );
    }
}
