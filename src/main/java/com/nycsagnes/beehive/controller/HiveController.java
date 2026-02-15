package com.nycsagnes.beehive.controller;

import com.nycsagnes.beehive.dto.incoming.BeeCreateUpdateCommand;
import com.nycsagnes.beehive.dto.incoming.HiveCreateUpdateCommand;
import com.nycsagnes.beehive.dto.outgoing.BeeInfo;
import com.nycsagnes.beehive.dto.outgoing.HiveInfo;
import com.nycsagnes.beehive.service.HiveService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hives")
@Slf4j
public class HiveController {

    private final HiveService hiveService;

    public HiveController(HiveService hiveService) {
        this.hiveService = hiveService;
    }

    @PostMapping
    public ResponseEntity<HiveInfo> save (@Valid @RequestBody HiveCreateUpdateCommand command){
        log.info("Http Request, POST /api/hives, body: " + command.toString());
        HiveInfo hiveInfo= hiveService.save(command);
        return new ResponseEntity<>(hiveInfo, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<HiveInfo>> findAll (){
        log.info("Http Request, GET /api/hives");
        List<HiveInfo> hives = hiveService.findAll();
        return new ResponseEntity<>(hives, HttpStatus.OK);
    }

    @DeleteMapping ("/{id}")
    public ResponseEntity<Void> deleteHive(@PathVariable Long id) {
        log.info("Http Request, DELETE /api/hives/{id}");
        hiveService.deleteHive(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }
}
