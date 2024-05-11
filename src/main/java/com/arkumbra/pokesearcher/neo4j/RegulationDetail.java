package com.arkumbra.pokesearcher.neo4j;

import com.arkumbra.pokesearcher.rest.PokemonSummary;
import com.arkumbra.pokesearcher.scraper.BattleTimers;
import com.arkumbra.pokesearcher.scraper.Schedule;

import java.util.List;

public class RegulationDetail {
  private String id;
  private String regulationName;
  private Schedule schedule;
  private List<PokemonSummary> validPokemon;
  private List<PokemonSummary> restrictedPokemon;
  private int numberOfRestricted;
  private Integer setLevel;
  private boolean itemsAllowed;
  private boolean duplicateItemsAllowed;
  private BattleTimers battleTimers;

  public RegulationDetail(
      String id, String regulationName, Schedule schedule,
      List<PokemonSummary> validPokemon, List<PokemonSummary> restrictedPokemon,
      int numberOfRestricted, Integer setLevel, boolean itemsAllowed,
      boolean duplicateItemsAllowed, BattleTimers battleTimers) {

    this.id = id;
    this.regulationName = regulationName;
    this.schedule = schedule;
    this.validPokemon = validPokemon;
    this.restrictedPokemon = restrictedPokemon;
    this.numberOfRestricted = numberOfRestricted;
    this.setLevel = setLevel;
    this.itemsAllowed = itemsAllowed;
    this.duplicateItemsAllowed = duplicateItemsAllowed;
    this.battleTimers = battleTimers;
  }

  public String getId() {
    return id;
  }

  public String getRegulationName() {
    return regulationName;
  }

  public Schedule getSchedule() {
    return schedule;
  }

  public List<PokemonSummary> getValidPokemon() {
    return validPokemon;
  }

  public List<PokemonSummary> getRestrictedPokemon() {
    return restrictedPokemon;
  }

  public int getNumberOfRestricted() {
    return numberOfRestricted;
  }

  public Integer getSetLevel() {
    return setLevel;
  }

  public boolean isItemsAllowed() {
    return itemsAllowed;
  }

  public boolean isDuplicateItemsAllowed() {
    return duplicateItemsAllowed;
  }

  public BattleTimers getBattleTimers() {
    return battleTimers;
  }
}
