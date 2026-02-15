package com.nycsagnes.beehive.config;


import com.nycsagnes.beehive.domain.Bee;
import com.nycsagnes.beehive.dto.outgoing.BeeInfo;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Converter<LocalDate, Long> ageConverter = context -> {
            LocalDate birthDate = context.getSource();
            if (birthDate == null) return 0L;
            return ChronoUnit.DAYS.between(birthDate, LocalDate.now());
        };
        Converter<Bee, Long> hiveIdConverter = context -> {
            Bee bee = context.getSource();
            if (bee == null || bee.getHive() == null) {
                return null;
            }
            return bee.getHive().getId();
        };
        modelMapper.typeMap(Bee.class, BeeInfo.class).addMappings(mapper -> {
            mapper.using(hiveIdConverter).map(src -> src, BeeInfo::setHiveId);
            mapper.using(ageConverter).map(Bee::getBirthdate, BeeInfo::setAgeInDays);
        });

        return modelMapper;
    }
}
