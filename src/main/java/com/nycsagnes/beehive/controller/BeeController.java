package com.nycsagnes.beehive.controller;

import com.nycsagnes.beehive.domain.Bee;
import com.nycsagnes.beehive.dto.incoming.BeeCreateUpdateCommand;
import com.nycsagnes.beehive.dto.outgoing.BeeInfo;
import com.nycsagnes.beehive.service.BeeService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bees")
@Slf4j
public class BeeController {

    private final BeeService beeService;

    public BeeController(BeeService beeService) {
        this.beeService = beeService;
    }

    @PostMapping
    public ResponseEntity<BeeInfo> save (@Valid @RequestBody BeeCreateUpdateCommand command){
        log.info("Http Request, POST /api/bees, body: " + command.toString());
        BeeInfo beeInfo = beeService.save(command);
        return new ResponseEntity<>(beeInfo, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BeeInfo>> findAll (){
        log.info("Http Request, GET /api/bees");
        List<BeeInfo> bees = beeService.findAll();
        return new ResponseEntity<>(bees, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BeeInfo> findById (@PathVariable("id") Long id){
        log.info("Http Request, GET /api/bees/id");
        BeeInfo bee = beeService.findById(id);
        return new ResponseEntity<>(bee, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BeeInfo> updateBee (@PathVariable("id") Long id,
                                              @RequestBody @Valid BeeCreateUpdateCommand command){
        BeeInfo updatedBee = beeService.updateBee(id, command);
        return new ResponseEntity<>(updatedBee, HttpStatus.OK);
    }
}
