package com.nycsagnes.beehive.dto.outgoing;

import com.nycsagnes.beehive.domain.BeeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BeeInfo {

    private Long id;

    private BeeType beeType;

    private String beeName;

    private Long hiveId;

    private String hiveName;

    private Long ageInDays;

}
