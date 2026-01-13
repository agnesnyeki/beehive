package com.nycsagnes.beehive.dto.incoming;

import com.nycsagnes.beehive.domain.BeeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BeeCreateUpdateCommand {

    @NotNull
    private BeeType beeType;

    @NotBlank
    private String beeName;

    @NotNull
    private Long hiveId;
}
