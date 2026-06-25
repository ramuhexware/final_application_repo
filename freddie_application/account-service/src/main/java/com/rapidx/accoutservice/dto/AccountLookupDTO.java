package com.rapidx.accoutservice.dto;

import com.rapidx.accoutservice.entity.UCSliofBusCd;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountLookupDTO {
    private List<UCSliofBusCd> linesOfBusiness;
}
