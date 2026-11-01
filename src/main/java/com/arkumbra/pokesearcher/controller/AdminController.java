package com.arkumbra.pokesearcher.controller;

import com.arkumbra.pokesearcher.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

  private final AdminService adminService;

  @Autowired
  public AdminController(AdminService adminService) {
    this.adminService = adminService;
  }

  @GetMapping
  public String adminPage(Model model) {
    model.addAttribute("isRunning", adminService.isRunning());
    model.addAttribute("currentTask", adminService.getCurrentTask());
    model.addAttribute("statusMessage", adminService.getStatusMessage());
    model.addAttribute("lastCompletedMessage", adminService.getLastCompletedMessage());
    model.addAttribute("lastErrorMessage", adminService.getLastErrorMessage());
    model.addAttribute("pokemonCount", adminService.getPokemonCount());
    model.addAttribute("abilityCount", adminService.getAbilityCount());
    return "admin/index";
  }

  @GetMapping("/status")
  @ResponseBody
  public Map<String, Object> getStatus() {
    Map<String, Object> status = new HashMap<>();
    status.put("running", adminService.isRunning());
    status.put("currentTask", adminService.getCurrentTask() != null ? adminService.getCurrentTask() : "");
    status.put("statusMessage", adminService.getStatusMessage() != null ? adminService.getStatusMessage() : "");
    status.put("lastCompletedMessage", adminService.getLastCompletedMessage() != null ? adminService.getLastCompletedMessage() : "");
    status.put("lastErrorMessage", adminService.getLastErrorMessage() != null ? adminService.getLastErrorMessage() : "");
    status.put("pokemonCount", adminService.getPokemonCount());
    status.put("abilityCount", adminService.getAbilityCount());
    return status;
  }

  @PostMapping("/refresh-dataset")
  public String refreshWholeDataset(RedirectAttributes redirectAttributes) {
    boolean started = adminService.triggerRefreshWholeDataset();
    if (started) {
      redirectAttributes.addFlashAttribute("successMessage", "Started refresh of whole dataset in background.");
    } else {
      redirectAttributes.addFlashAttribute("warningMessage", "A scraping task is already running.");
    }
    return "redirect:/admin";
  }

  @PostMapping("/scrape-abilities")
  public String scrapeAbilities(RedirectAttributes redirectAttributes) {
    boolean started = adminService.triggerScrapeAllAbilities();
    if (started) {
      redirectAttributes.addFlashAttribute("successMessage", "Started scraping all abilities in background.");
    } else {
      redirectAttributes.addFlashAttribute("warningMessage", "A scraping task is already running.");
    }
    return "redirect:/admin";
  }
}
