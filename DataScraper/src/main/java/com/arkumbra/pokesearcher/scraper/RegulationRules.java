package com.arkumbra.pokesearcher.scraper;

public class RegulationRules {
  private String regulationName;
  private Schedule schedule;
  private ValidPokemon validPokemon;
  private Integer setLevel;
  private boolean itemsAllowed;
  private boolean duplicateItemsAllowed;
  private BattleTimers battleTimers;

  public RegulationRules(
      String regulationName,
      Schedule schedule, ValidPokemon validPokemon,
      Integer setLevel, boolean itemsAllowed,
      boolean duplicateItemsAllowed,
      BattleTimers battleTimers) {
    this.regulationName = regulationName;
    this.schedule = schedule;
    this.validPokemon = validPokemon;
    this.setLevel = setLevel;
    this.itemsAllowed = itemsAllowed;
    this.duplicateItemsAllowed = duplicateItemsAllowed;
    this.battleTimers = battleTimers;
  }

  public String getRegulationName() {
    return regulationName;
  }

  public Schedule getSchedule() {
    return schedule;
  }

  public ValidPokemon getValidPokemon() {
    return validPokemon;
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
