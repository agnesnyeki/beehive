package com.nycsagnes.beehive.service;


import com.nycsagnes.beehive.domain.Hive;
import com.nycsagnes.beehive.dto.incoming.HiveCreateUpdateCommand;
import com.nycsagnes.beehive.dto.outgoing.BeeInfo;
import com.nycsagnes.beehive.dto.outgoing.HiveInfo;
import com.nycsagnes.beehive.exception.HiveNotFoundException;
import com.nycsagnes.beehive.repository.BeeRepository;
import com.nycsagnes.beehive.repository.HiveRepository;
import jakarta.validation.constraints.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class HiveService {

    private final ModelMapper modelMapper;
    private final HiveRepository hiveRepository;

    public HiveService (ModelMapper modelMapper, HiveRepository hiveRepository){
        this.modelMapper = modelMapper;
        this.hiveRepository = hiveRepository;
    }

    public HiveInfo save(HiveCreateUpdateCommand command){
        Hive hive = modelMapper.map(command, Hive.class);
        hiveRepository.save(hive);
        return modelMapper.map(hive, HiveInfo.class);
    }

    public Hive findById(@NotNull Long hiveId) {
        return hiveRepository.findById(hiveId).orElseThrow(() -> new HiveNotFoundException(hiveId));
    }

    public List<HiveInfo> findAll() {
        return hiveRepository.findAll().stream()
                .map(hive -> modelMapper.map(hive, HiveInfo.class))
                .toList();
    }
}
