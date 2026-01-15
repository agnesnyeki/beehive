package com.nycsagnes.beehive.controller;

import com.nycsagnes.beehive.dto.incoming.HiveCreateUpdateCommand;
import com.nycsagnes.beehive.service.HiveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/web/hives")
@Slf4j
public class WebHiveController {

    public final HiveService hiveService;

    public WebHiveController(HiveService hiveService) {
        this.hiveService = hiveService;
    }

    @GetMapping
    public String listHives(Model model) {
        log.info("Web Request, GET /web/hives");
        model.addAttribute("hives", hiveService.findAll());
        return "hiveTemplates/hives";
    }

    @GetMapping("/new")
    public String showHiveForm(Model model) {
        log.info("Web Request, GET /web/hives/new");
        model.addAttribute("hive", new HiveCreateUpdateCommand());
        return "hiveTemplates/hiveForm";
    }

    @PostMapping
    public String saveHive(HiveCreateUpdateCommand command) {
        log.info("Web Request, POST /web/hives, body: " + command.toString());
        hiveService.save(command);
        return "redirect:/web/hives";
    }
}
