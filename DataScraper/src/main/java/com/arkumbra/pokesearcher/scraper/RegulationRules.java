package com.arkumbra.pokesearcher.scraper;

public class RegulationRules {
  private String id;
  private String regulationName;
  private Schedule schedule;
  private ValidPokemon validPokemon;
  private ValidPokemon restrictedPokemon;
  private int numberOfRestricted;
  private Integer setLevel;
  private boolean itemsAllowed;
  private boolean duplicateItemsAllowed;
  private BattleTimers battleTimers;

  public RegulationRules(
      String id, String regulationName, Schedule schedule,
      ValidPokemon validPokemon, ValidPokemon restrictedPokemon,
      int numberOfRestricted, Integer setLevel,
      boolean itemsAllowed, boolean duplicateItemsAllowed,
      BattleTimers battleTimers) {

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

  public ValidPokemon getValidPokemon() {
    return validPokemon;
  }

  public ValidPokemon getRestrictedPokemon() {
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
