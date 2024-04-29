package com.arkumbra.pokesearcher.controller;

import com.arkumbra.pokesearcher.neo4j.MoveDetail;
import com.arkumbra.pokesearcher.neo4j.MoveSummary;
import com.arkumbra.pokesearcher.neo4j.Neo4jClient;
import com.arkumbra.pokesearcher.neo4j.search.SearchByNameRequest;
import com.arkumbra.pokesearcher.neo4j.search.SearchByNameResponse;
import com.arkumbra.pokesearcher.rest.AbilityDetail;
import com.arkumbra.pokesearcher.rest.PokemonSummary;
import com.arkumbra.pokesearcher.scraper.Ability;
import com.arkumbra.pokesearcher.scraper.AbilitySummary;
import jakarta.websocket.server.PathParam;
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

  @GetMapping("/ability")
  public String getAllAbilities(Model model) {
    List<AbilitySummary> allAbilities = neo4jClient.getAllAbilities();
    model.addAttribute("numOfAbilities", allAbilities.size());
    model.addAttribute("abilitySummaries", allAbilities);
    return "abilities-overview";
  }

  @GetMapping("/ability/{abilityId}")
  public String getAbilityDetail(Model model, @PathVariable("abilityId") String abilityId) {
    AbilityDetail abilityDetail = neo4jClient.getAbilityDetail(abilityId);
    model.addAttribute("abilityDetail", abilityDetail);
    return "abilities-detail";
  }

  @GetMapping("/move")
  public String getAllMoves(Model model) {
    List<MoveSummary> allMoves = neo4jClient.getMovesSummary();
    model.addAttribute("numOfMoves", allMoves.size());
    model.addAttribute("moveSummaries", allMoves);
    return "moves-overview";
  }

  @GetMapping("/move/{moveId}")
  public String getMoveDetail(Model model, @PathVariable("moveId") String moveId) {
    MoveDetail moveDetail = neo4jClient.getMoveDetail(moveId);
    model.addAttribute("moveDetail", moveDetail);
    return "move-detail";
  }

  @GetMapping("/pokemon")
  public String getAllPokemon(Model model) {
    List<PokemonSummary> allPokemon = neo4jClient.getAllPokemon();
    model.addAttribute("numOfPokemon", allPokemon.size());
    model.addAttribute("pokemonSummaries", allPokemon);
    return "pokemon-overview";
  }

  @GetMapping("/pokemon/{pokemonId}")
  public String getPokemonDetail(
      Model model, @PathVariable("pokemonId") String pokemonId, @PathParam("formName") String formName) {

    PokemonDetail abilityDetail = neo4jClient.getPokemonDetail(pokemonId, formName);
    model.addAttribute("pokemonDetail", abilityDetail);
    return "pokemon-detail";
  }

  @GetMapping("/search/{nameContains}")
  public String searchByName(Model model, @PathVariable("nameContains") String nameContains) {
    SearchByNameResponse response = neo4jClient.searchAllElementsByName(
        new SearchByNameRequest(nameContains)
    );

    System.out.println(response);

    model.addAttribute("result", response);
    model.addAttribute("searchText", nameContains);
    return "search-by-name";
  }
}
