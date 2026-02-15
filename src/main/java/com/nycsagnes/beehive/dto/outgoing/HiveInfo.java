package com.nycsagnes.beehive.dto.outgoing;

import com.nycsagnes.beehive.domain.BeeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HiveInfo {

    private Long id;

    private String hiveName;

    private List<BeeInfo> bees = new ArrayList<>();

    public BeeInfo getQueen() {
        if (bees == null) return null;
        return bees.stream()
                .filter(b -> b.getBeeType() == BeeType.QUEEN)
                .findFirst()
                .orElse(null);
    }

    public List<BeeInfo> getDrones() {
        if (bees == null) return new ArrayList<>();
        return bees.stream()
                .filter(b -> b.getBeeType() == BeeType.DRONE)
                .collect(Collectors.toList());
    }


    public List<BeeInfo> getWorkers() {
        if (bees == null) return new ArrayList<>();
        return bees.stream()
                .filter(b -> b.getBeeType() == BeeType.WORKER)
                .collect(Collectors.toList());
    }

}
