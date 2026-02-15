package com.nycsagnes.beehive.service;


import com.nycsagnes.beehive.domain.Bee;
import com.nycsagnes.beehive.domain.BeeType;
import com.nycsagnes.beehive.domain.Hive;
import com.nycsagnes.beehive.dto.incoming.BeeCreateUpdateCommand;
import com.nycsagnes.beehive.dto.outgoing.BeeInfo;
import com.nycsagnes.beehive.exception.BeeNotFoundException;
import com.nycsagnes.beehive.repository.BeeRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Service
@Transactional
public class BeeService {

    private final ModelMapper modelMapper;
    private final BeeRepository beeRepository;
    private final HiveService hiveService;

    public BeeService(ModelMapper modelMapper, BeeRepository beeRepository, HiveService hiveService) {
        this.modelMapper = modelMapper;
        this.beeRepository = beeRepository;
        this.hiveService = hiveService;
    }

    public BeeInfo save(BeeCreateUpdateCommand command) {
        Bee bee = modelMapper.map(command, Bee.class);
        Hive hive = hiveService.findEntityById(command.getHiveId());
        bee.setHive(hive);
        bee.setBirthdate(LocalDate.now());
        beeRepository.save(bee);
        return modelMapper.map(bee, BeeInfo.class);
    }

    public BeeInfo findById(@NotNull Long id) {
        Bee bee = beeRepository.findById(id)
                .orElseThrow(() -> new BeeNotFoundException(id));
        BeeInfo info = modelMapper.map(bee, BeeInfo.class);
        if (bee.getHive() != null) {
            info.setHiveName(bee.getHive().getHiveName());
        } else {
            info.setHiveName("Hajléktalan");
        }
        return info;
    }

    public List<BeeInfo> findAll() {
        return beeRepository.findAll().stream()
                .map(bee -> {
                    BeeInfo info = modelMapper.map(bee, BeeInfo.class);
                    if (bee.getHive() != null) {
                        info.setHiveName(bee.getHive().getHiveName());
                    } else {
                        info.setHiveName("Hajléktalan");
                    }
                    return info;
                })
                .toList();
    }

    public BeeInfo updateBee(Long id, @Valid BeeCreateUpdateCommand command) {
        Bee existingBee = beeRepository.findById(id).orElseThrow(() -> new BeeNotFoundException(id));
        modelMapper.map(command, existingBee);
        Hive hive = hiveService.findEntityById(command.getHiveId());
        existingBee.setHive(hive);
        return modelMapper.map(existingBee, BeeInfo.class);
    }

    @Transactional
    public Long evictBee(Long beeId) {
        Bee bee = beeRepository.findById(beeId)
                .orElseThrow(() -> new EntityNotFoundException("Nincs ilyen méh: " + beeId));
        Long oldHiveId = (bee.getHive() != null) ? bee.getHive().getId() : null;
        if (oldHiveId == null) {
            return null;
        } else if (bee.getBeeType() == BeeType.QUEEN) {
            for (Bee everyBeedy : bee.getHive().getBees()) {
                everyBeedy.setHive(null);
                beeRepository.save(everyBeedy);
            }
        } else {
            bee.setHive(null);
            beeRepository.save(bee);
        }
        return oldHiveId;
    }

    public void generateRandomBirthDates() {
        List<Bee> allBees = beeRepository.findAll();
        Random random = new Random();
        for (Bee bee : allBees) {
            if (bee.getBirthdate() == null) {
                int daysOld = random.nextInt(60) + 1;
                bee.setBirthdate(LocalDate.now().minusDays(daysOld));
                beeRepository.save(bee);
            }
        }
    }

    public void deleteBee(Long id) {
        Bee bee = beeRepository.findById(id)
                .orElseThrow(() -> new BeeNotFoundException(id));
        if (bee.getBeeType() == BeeType.QUEEN && bee.getHive() != null) {
            for (Bee everyBeedy : bee.getHive().getBees()) {
                everyBeedy.setHive(null);
                beeRepository.save(everyBeedy);
            }
        }
        beeRepository.delete(bee);
    }
}
