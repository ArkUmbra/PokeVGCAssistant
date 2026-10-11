package com.arkumbra.pokesearcher.controller;

import com.arkumbra.pokesearcher.controller.model.ScrapeRegulationUrlRequest;
import com.arkumbra.pokesearcher.homenews.PokemonChampionsEligiblePokemonWebAppScraper;
import com.arkumbra.pokesearcher.homenews.PokemonHomeNewsRegulationScraper;
import com.arkumbra.pokesearcher.neo4j.MoveDetail;
import com.arkumbra.pokesearcher.neo4j.MoveSummary;
import com.arkumbra.pokesearcher.neo4j.Neo4jClient;
import com.arkumbra.pokesearcher.neo4j.RegulationDetail;
import com.arkumbra.pokesearcher.neo4j.search.SearchByNameRequest;
import com.arkumbra.pokesearcher.neo4j.search.SearchByNameResponse;
import com.arkumbra.pokesearcher.rest.AbilityDetail;
import com.arkumbra.pokesearcher.rest.PokemonSummary;
import com.arkumbra.pokesearcher.scraper.AbilitySummary;
import com.arkumbra.pokesearcher.scraper.RegulationRules;
import jakarta.websocket.server.PathParam;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/regulation")
@SessionAttributes({"regulationRules", "eligiblePokemonResponse"})
public class RegulationController {

//  public Neo4jClient neo4jClient = new Neo4jClient("bolt://localhost:7687", "neo4j", "password");

  private static final PokemonHomeNewsRegulationScraper regScraper = new PokemonHomeNewsRegulationScraper();
  private static final PokemonChampionsEligiblePokemonWebAppScraper pokeScraper = new PokemonChampionsEligiblePokemonWebAppScraper();

  @ModelAttribute("regulationRules")
  public RegulationRules defaultRegulationRules() {
    return new RegulationRules();
  }

  @GetMapping//("/")
  public String index(Model model) {
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
    var eligiblePokemonResponse = pokeScraper.scrapeMonsAndSpriteWithJsoup(regRules.getEligiblePokemonUrl());

    model.addAttribute("regulationRules", regRules);
    model.addAttribute("eligiblePokemonResponse", eligiblePokemonResponse);

    return "regulation/create/fromScraped";
  }

  @PostMapping("/create")
  public String saveNewRegulation(
      @ModelAttribute("regulationRules") RegulationRules regulationRules,
      Model model,
      SessionStatus sessionStatus
  ) {

    return "regulation/create/fromScraped";
  }




}
