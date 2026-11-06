package com.arkumbra.pokesearcher.controller;

import com.arkumbra.pokesearcher.controller.model.ScrapeRegulationUrlRequest;
import com.arkumbra.pokesearcher.homenews.PokemonChampionsEligiblePokemonWebAppScraper;
import com.arkumbra.pokesearcher.homenews.PokemonHomeNewsRegulationScraper;
import com.arkumbra.pokesearcher.model.PokemonWithSpriteRef;
import com.arkumbra.pokesearcher.model.RegulationEligiblePokemon;
import com.arkumbra.pokesearcher.neo4jnew.NewNeo4jClient;
import com.arkumbra.pokesearcher.scraper.Regulation;
import com.arkumbra.pokesearcher.scraper.RegulationRules;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/regulation")
@SessionAttributes({"regulationRules", "regulationEligiblePokemon"})
public class RegulationController {

  @Autowired
  private NewNeo4jClient neo4jClient;

//  public Neo4jClient neo4jClient = new Neo4jClient("bolt://localhost:7687", "neo4j", "password");

  private static final PokemonHomeNewsRegulationScraper regScraper = new PokemonHomeNewsRegulationScraper();
  private static final PokemonChampionsEligiblePokemonWebAppScraper pokeScraper = new PokemonChampionsEligiblePokemonWebAppScraper();

  @ModelAttribute("regulationRules")
  public RegulationRules defaultRegulationRules() {
    return new RegulationRules();
  }

  @GetMapping//("/")
  public String index(Model model) {

    var regSummaries = neo4jClient.getAllRegulationSummaries();

    model.addAttribute("regSummaries", regSummaries);
    return "regulation/index";
  }

  @GetMapping("/create")
  public String getCreateRegulation(Model model) {
    model.addAttribute("scrapeUrlRequest", new ScrapeRegulationUrlRequest());
    return "regulation/create/index";
  }

  @PostMapping("/scrapeUrl")
  public String scrapeRegulationDetailsFromUrl(@ModelAttribute ScrapeRegulationUrlRequest request, Model model) {
    var regRules = regScraper.scrape(request.getOfficialLink());
    var regulationEligiblePokemon = pokeScraper.scrapeMonsAndSpriteWithJsoup(regRules.getEligiblePokemonUrl());

    model.addAttribute("regulationRules", regRules);
    model.addAttribute("regulationEligiblePokemon", regulationEligiblePokemon);

    return "regulation/create/fromScraped";
  }

  @PostMapping("/create")
  public String saveNewRegulation(
      @ModelAttribute("regulationRules") RegulationRules regulationRules,
      Model model,
      SessionStatus sessionStatus
  ) {


    var regulation = new Regulation(
        regulationRules,
        (RegulationEligiblePokemon) model.getAttribute("regulationEligiblePokemon")
    );

    neo4jClient.storeRegulation(regulation);

    return "redirect:/regulation";
  }

  @GetMapping("/{id}")
  public String viewRegulation(@PathVariable("id") String id, Model model) {
    Regulation regulation = neo4jClient.getRegulationById(id);
    if (regulation == null) {
      return "redirect:/regulation";
    }

    RegulationRules rules = regulation.regulationRules();
    RegulationEligiblePokemon eligiblePokemon = regulation.eligiblePokemon();

    List<PokemonWithSpriteRef> allMons = eligiblePokemon != null && eligiblePokemon.mons() != null
        ? eligiblePokemon.mons()
        : Collections.emptyList();

    List<PokemonWithSpriteRef> restrictedMons = allMons.stream()
        .filter(PokemonWithSpriteRef::restricted)
        .toList();

    List<PokemonWithSpriteRef> standardMons = allMons.stream()
        .filter(m -> !m.restricted())
        .toList();

    model.addAttribute("regulation", regulation);
    model.addAttribute("rules", rules);
    model.addAttribute("eligiblePokemon", eligiblePokemon);
    model.addAttribute("spriteSheetUrl", eligiblePokemon != null ? eligiblePokemon.spriteSheetUrl() : "");
    model.addAttribute("allPokemon", allMons);
    model.addAttribute("restrictedPokemon", restrictedMons);
    model.addAttribute("standardPokemon", standardMons);
    model.addAttribute("totalPokemonCount", allMons.size());
    model.addAttribute("hasRestricted", !restrictedMons.isEmpty() || (rules != null && rules.getNumberOfRestricted() > 0));

    return "regulation/detail";
  }

  @PostMapping("/{id}/activate")
  public String activateRegulation(
      @PathVariable("id") String id,
      @RequestParam(value = "redirect", required = false) String redirect
  ) {
    neo4jClient.setActiveRegulation(id);
    if ("detail".equalsIgnoreCase(redirect)) {
      return "redirect:/regulation/" + id;
    }
    return "redirect:/regulation";
  }

  @PostMapping("/{id}/delete")
  public String deleteRegulation(@PathVariable("id") String id) {
    neo4jClient.deleteRegulation(id);
    return "redirect:/regulation";
  }

  @PostMapping("/delete")
  public String deleteRegulationByParam(@RequestParam("id") String id) {
    neo4jClient.deleteRegulation(id);
    return "redirect:/regulation";
  }

}
