package com.nycsagnes.beehive.service;


import com.nycsagnes.beehive.domain.Bee;
import com.nycsagnes.beehive.domain.Hive;
import com.nycsagnes.beehive.dto.incoming.BeeCreateUpdateCommand;
import com.nycsagnes.beehive.dto.outgoing.BeeInfo;
import com.nycsagnes.beehive.exception.BeeNotFoundException;
import com.nycsagnes.beehive.repository.BeeRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BeeService {

    private final ModelMapper modelMapper;
    private final BeeRepository beeRepository;
    private final HiveService hiveService;

    public BeeService (ModelMapper modelMapper, BeeRepository beeRepository, HiveService hiveService){
        this.modelMapper = modelMapper;
        this.beeRepository = beeRepository;
        this.hiveService = hiveService;
    }

    public BeeInfo save(BeeCreateUpdateCommand command){
        Bee bee = modelMapper.map(command, Bee.class);
        Hive hive = hiveService.findById(command.getHiveId());
        bee.setHive(hive);
        beeRepository.save(bee);
        return modelMapper.map(bee, BeeInfo.class);
    }

    public BeeInfo findById(@NotNull Long id) {
        Bee bee = beeRepository.findById(id).orElseThrow(() -> new BeeNotFoundException(id));
        return modelMapper.map(bee, BeeInfo.class);
    }

    public List<BeeInfo> findAll() {
        return beeRepository.findAll().stream()
                .map(bee -> modelMapper.map(bee, BeeInfo.class))
                .toList();
    }

    public BeeInfo updateBee(Long id, @Valid BeeCreateUpdateCommand command) {
        Bee existingBee = beeRepository.findById(id).orElseThrow(() -> new BeeNotFoundException(id));
        modelMapper.map(command, existingBee);
        Hive hive = hiveService.findById(command.getHiveId());
        existingBee.setHive(hive);
        return modelMapper.map(existingBee, BeeInfo.class);
    }
}
