package com.nycsagnes.beehive.controller;

import com.nycsagnes.beehive.dto.incoming.BeeCreateUpdateCommand;
import com.nycsagnes.beehive.service.BeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/web/bees")
@Slf4j
public class WebBeeController {

    private final BeeService beeService;


    public WebBeeController(BeeService beeService) {
        this.beeService = beeService;
    }

    @GetMapping
    public String listBeesPage(Model model) {
        log.info("Http Request, GET /web/bees");
        model.addAttribute("bees", beeService.findAll());
        return "bees";
    }

    @GetMapping ("/create")
    public String createBeePage(Model model) {
        log.info("Http Request, GET /web/bees/create");
        model.addAttribute("bee", new BeeCreateUpdateCommand());
        return "create-bee";
    }

    @PostMapping ("/create")
    public String createBeeSubmit(@ModelAttribute ("bee") BeeCreateUpdateCommand bee) {
        log.info("Http Request, POST /web/bees/create");
        beeService.save(bee);
        return "redirect:/web/bees";
    }
}
