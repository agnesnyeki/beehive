package com.nycsagnes.beehive.controller;

import com.nycsagnes.beehive.domain.BeeType;
import com.nycsagnes.beehive.dto.incoming.BeeCreateUpdateCommand;
import com.nycsagnes.beehive.dto.outgoing.BeeInfo;
import com.nycsagnes.beehive.dto.outgoing.HiveInfo;
import com.nycsagnes.beehive.service.BeeService;
import com.nycsagnes.beehive.service.HiveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/web/bees")
@Slf4j
public class WebBeeController {

    private final BeeService beeService;
    private final HiveService hiveService;


    public WebBeeController(BeeService beeService, HiveService hiveService) {
        this.beeService = beeService;
        this.hiveService = hiveService;
    }

    @GetMapping
    public String listBeesPage(@RequestParam(required = false) String filter, Model model) {
        log.info("Http Request, GET /web/bees" + (filter != null ? "?filter=" + filter : ""));
        model.addAttribute("bees", beeService.findFiltered(filter));
        return "beeTemplates/bees";
    }

    @GetMapping("/{id}")
    public String beeDetailsPage(@PathVariable("id") Long id, Model model) {
        log.info("Http Request, GET /web/bees/" + id);
        model.addAttribute("bee", beeService.findById(id));
        return "beeTemplates/beeDetails";
    }

    @GetMapping ("/create")
    public String createBeePage(@RequestParam(required = false) Long hiveId,
                                @RequestParam(required = false) BeeType beeType,
                                Model model) {
        log.info("Http Request, GET /web/bees/create");
        BeeCreateUpdateCommand command = new BeeCreateUpdateCommand();
        if (hiveId != null) {
            command.setHiveId(hiveId);
        }
        if (beeType != null) {
            command.setBeeType(beeType);
        }
        model.addAttribute("bee", command);
        return "beeTemplates/beeForm";
    }

    @PostMapping ("/create")
    public String createBeeSubmit(@ModelAttribute ("bee") BeeCreateUpdateCommand bee, Model model) {
        log.info("Http Request, POST /web/bees/create");

        if (bee.getHiveId() != null) {
            // 2. LÉPÉS VÉGE: Ha már van kaptár (vagy direkt kaptárból indítottuk a létrehozást)
            // Itt történik a VÉGLEGES MENTÉS az adatbázisba!
            beeService.save(bee);
            return "redirect:/web/hives/" + bee.getHiveId();
        } else {
            // 1. LÉPÉS VÉGE: Nincs még kaptár! NE MENTSÜK EL, csak vigyük tovább az adatokat.
            // Lekérjük a szabad kaptárakat a megadott méh típus alapján:
            List<HiveInfo> availableHives = hiveService.findAvailableHives(bee.getBeeType());

            // Átadjuk az eddig kitöltött adatokat (név, típus) a 2. oldalnak
            model.addAttribute("movingBee", bee);
            model.addAttribute("availableHives", availableHives);
            model.addAttribute("isNewBee", true); // Ezzel jelezzük a HTML-nek, hogy ez egy VADONATÚJ méh!

            return "beeTemplates/beeMove"; // Újrahasznosítjuk a költöztető sablont
        }
    }

    @GetMapping("/move-in/{id}")
    public String moveInBeePage(@PathVariable("id") Long id, Model model) {
        log.info("Http Request, GET /web/bees/move-in/" + id);
        BeeInfo movingBee = beeService.findById(id);
        List<HiveInfo> availableHives = hiveService.findAvailableHives(movingBee.getBeeType());
        BeeCreateUpdateCommand beeMoveCommand = new BeeCreateUpdateCommand();
        beeMoveCommand.setId(movingBee.getId());
        beeMoveCommand.setBeeName(movingBee.getBeeName());
        beeMoveCommand.setBeeType(movingBee.getBeeType());
        model.addAttribute("movingBee", beeMoveCommand);
        model.addAttribute("availableHives", availableHives);
        return "beeTemplates/beeMove";
    }

    @PostMapping("/move-in")
    public String moveInBeeSubmit(@ModelAttribute ("movingBee") BeeCreateUpdateCommand beeMoveCommand) {
        Long id = beeMoveCommand.getId();
        log.info("Http Request, POST /web/bees/move-in/" + id);
        BeeInfo updatedBee = beeService.updateBee(id, beeMoveCommand);
        if (updatedBee.getHiveId() != null) {
            return "redirect:/web/hives/" + updatedBee.getHiveId();
        } else {
            return "redirect:/web/bees";
        }
    }

    @PostMapping("/evict/{id}")
    public String evictBee(@PathVariable("id") Long id) {
        log.info("Http Request, POST /web/bees/evict/" + id);
        Long hiveId = beeService.evictBee(id);
        if (hiveId != null) {
            return "redirect:/web/hives/" + hiveId;
        } else {
            return "redirect:/web/bees";
        }
    }

    @PostMapping ("/execute/{id}")
    public String deleteBee(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        log.info("Http Request, POST /web/bees/execute/" + id);
        BeeInfo executedBee = beeService.findById(id);
        Long hiveId = executedBee.getHiveId();
        String beeName = executedBee.getBeeName();
        BeeType beetype = executedBee.getBeeType();
        beeService.deleteBee(id);
        if (executedBee.getBeeType() == BeeType.QUEEN) {
            redirectAttributes.addFlashAttribute("message", "Assassination successful: Queen " + beeName + " is dead. The hive has been emptied!");
        } else {
            redirectAttributes.addFlashAttribute("message", beeName + " has departed for the eternal bee pastures.");
        }
        if (hiveId != null) {
            return "redirect:/web/hives/" + hiveId;
        } else {
            return "redirect:/web/bees";
        }
    }

    @Profile("dev")
    @GetMapping("/admin/randomize-ages")
    public String randomizeAges() {
        beeService.generateRandomBirthDates();
        return "redirect:/web/bees";
    }

}
