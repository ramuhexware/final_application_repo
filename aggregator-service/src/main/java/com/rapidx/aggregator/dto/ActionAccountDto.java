package com.rapidx.aggregator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActionAccountDto {
    private Long id;
    private String name;
    private Integer orgId;
    private String searchKey;
}
