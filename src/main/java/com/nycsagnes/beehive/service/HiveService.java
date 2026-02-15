package com.nycsagnes.beehive.service;

import com.nycsagnes.beehive.domain.Bee;
import com.nycsagnes.beehive.domain.BeeType;
import com.nycsagnes.beehive.domain.Hive;
import com.nycsagnes.beehive.dto.incoming.HiveCreateUpdateCommand;
import com.nycsagnes.beehive.dto.outgoing.BeeInfo;
import com.nycsagnes.beehive.dto.outgoing.HiveInfo;
import com.nycsagnes.beehive.exception.HiveNotFoundException;
import com.nycsagnes.beehive.repository.HiveRepository;
import jakarta.validation.constraints.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class HiveService {

    private final ModelMapper modelMapper;
    private final HiveRepository hiveRepository;


    public HiveService(ModelMapper modelMapper, HiveRepository hiveRepository) {
        this.modelMapper = modelMapper;
        this.hiveRepository = hiveRepository;

    }

    public HiveInfo save(HiveCreateUpdateCommand command) {
        Hive hive = modelMapper.map(command, Hive.class);
        hiveRepository.save(hive);
        return modelMapper.map(hive, HiveInfo.class);
    }

    public HiveInfo findById(@NotNull Long hiveId) {
        Hive hiveEntity = hiveRepository.findById(hiveId).orElseThrow(() -> new HiveNotFoundException(hiveId));
        return modelMapper.map(hiveEntity, HiveInfo.class);
    }

    public List<HiveInfo> findAll() {
        return hiveRepository.findAll().stream()
                .map(hive -> modelMapper.map(hive, HiveInfo.class))
                .toList();
    }

    public Hive findEntityById(@NotNull Long hiveId) {
        return hiveRepository.findById(hiveId).orElseThrow(() -> new HiveNotFoundException(hiveId));
    }

    public List<HiveInfo> findAvailableHives(BeeType type) {
        return hiveRepository.findAll().stream()
                .filter(hive -> isHiveSuitableForBee(hive, type))
                .map(hive -> modelMapper.map(hive, HiveInfo.class))
                .toList();
    }

    private boolean isHiveSuitableForBee(Hive hive, BeeType type) {
        boolean hasQueen = hive.getBees().stream().anyMatch(b -> b.getBeeType() == BeeType.QUEEN);
        long currentCount = hive.getBees().stream()
                .filter(b -> b.getBeeType() == type)
                .count();
        return switch (type) {
            case QUEEN -> !hasQueen;
            case WORKER -> {
                if (!hasQueen) {
                    yield false;
                }
                yield currentCount < hive.getCapacity() - 3;
            }
            case DRONE -> {
                if (!hasQueen) {
                    yield false;
                }
                yield currentCount < 2;
            }
            default -> false;
        };
    }

    public HiveInfo convertToHiveInfo(Hive hiveEntity) {
        HiveInfo hiveInfo = new HiveInfo();
        hiveInfo.setId(hiveEntity.getId());
        hiveInfo.setHiveName(hiveEntity.getHiveName());
        List<BeeInfo> beeInfos = hiveEntity.getBees().stream()
                .map(beeEntity -> {
                    BeeInfo info = new BeeInfo();
                    info.setId(beeEntity.getId());
                    info.setBeeName(beeEntity.getBeeName());
                    info.setBeeType(beeEntity.getBeeType());
                    info.setHiveId(hiveEntity.getId());
                    return info;
                })
                .collect(Collectors.toList());
        beeInfos.sort(Comparator.comparingInt(b -> getPriority(b.getBeeType())));
        hiveInfo.setBees(beeInfos);
        return hiveInfo;
    }

    @Transactional
    public void deleteHive(Long hiveId) {
        Hive hive = findEntityById(hiveId);
        for (Bee bee : hive.getBees()) {
            bee.setHive(null);
        }
        hiveRepository.deleteById(hiveId);
    }

    private int getPriority(BeeType type) {
        if (type == BeeType.QUEEN) return 1;
        if (type == BeeType.DRONE) return 2;
        return 3;
    }

}
