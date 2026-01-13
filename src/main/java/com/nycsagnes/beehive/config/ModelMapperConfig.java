package com.nycsagnes.beehive.config;


import com.nycsagnes.beehive.domain.Bee;
import com.nycsagnes.beehive.dto.outgoing.BeeInfo;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper Modelmapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.typeMap(Bee.class, BeeInfo.class).addMappings(mapper -> {
            mapper.map(src -> src.getHive().getId(), BeeInfo::setHiveId);
        });
        return modelMapper;
    }
}
