package com.arkumbra.pokesearcher.controller;

import com.arkumbra.pokesearcher.neo4j.Neo4jClient;
import com.arkumbra.pokesearcher.rest.AbilityDetail;
import com.arkumbra.pokesearcher.scraper.Ability;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
//@RequestMapping("/pokesearcher/")
public class PokeSearcherController {

  public Neo4jClient neo4jClient = new Neo4jClient("bolt://localhost:7687", "neo4j", "password");

  @GetMapping("/abilities")
  public String getAllAbilities(Model model) {
    System.out.println("Reached controller");
//    model.addAttribute("test", "Hello world");

    List<Ability> allAbilities = neo4jClient.getAllAbilities();
    model.addAttribute("numOfAbilities", allAbilities.size());
    model.addAttribute("abilities", allAbilities);
    return "abilities-overview";
  }

  @GetMapping("/ability/{abilityId}")
  public String getAbilityDetail(Model model, @PathVariable("abilityId") String abilityId) {
    AbilityDetail abilityDetail = neo4jClient.getAbilityDetail(abilityId);
    model.addAttribute("abilityDetail", abilityDetail);
    return "abilities-detail";
  }
}
