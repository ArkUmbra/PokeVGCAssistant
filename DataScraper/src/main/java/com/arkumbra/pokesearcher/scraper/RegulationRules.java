package com.arkumbra.pokesearcher.scraper;

public class RegulationRules {
  private String id;
  private String regulationName;
  private String eligiblePokemonUrl;
  private Schedule schedule;
//  private ValidPokemon validPokemon;
//  private ValidPokemon restrictedPokemon;
  private int numberOfRestricted;
  private Integer setLevel;
  private boolean itemsAllowed;
  private boolean duplicateItemsAllowed;
  private BattleTimers battleTimers = new BattleTimers();

  public RegulationRules() {
  }

  public RegulationRules(
      String id,
      String regulationName,
      String eligiblePokemonUrl,
      Schedule schedule,
      int numberOfRestricted,
      Integer setLevel,
      boolean itemsAllowed,
      boolean duplicateItemsAllowed,
      BattleTimers battleTimers
  ) {
    this.id = id;
    this.regulationName = regulationName;
    this.eligiblePokemonUrl = eligiblePokemonUrl;
    this.schedule = schedule;
    this.numberOfRestricted = numberOfRestricted;
    this.setLevel = setLevel;
    this.itemsAllowed = itemsAllowed;
    this.duplicateItemsAllowed = duplicateItemsAllowed;
    this.battleTimers = battleTimers;
  }

  //  public RegulationRules(
//      String id, String regulationName, String eligiblePokemonUrl, Schedule schedule,
//      ValidPokemon validPokemon, ValidPokemon restrictedPokemon,
//      int numberOfRestricted, Integer setLevel,
//      boolean itemsAllowed, boolean duplicateItemsAllowed,
//      BattleTimers battleTimers) {
//
//    this.id = id;
//    this.regulationName = regulationName;
//    this.eligiblePokemonUrl = eligiblePokemonUrl;
//    this.schedule = schedule;
//    this.validPokemon = validPokemon;
//    this.restrictedPokemon = restrictedPokemon;
//    this.numberOfRestricted = numberOfRestricted;
//    this.setLevel = setLevel;
//    this.itemsAllowed = itemsAllowed;
//    this.duplicateItemsAllowed = duplicateItemsAllowed;
//    this.battleTimers = battleTimers;
//  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getRegulationName() {
    return regulationName;
  }

  public void setRegulationName(String regulationName) {
    this.regulationName = regulationName;
  }

  public String getEligiblePokemonUrl() {
    return eligiblePokemonUrl;
  }

  public void setEligiblePokemonUrl(String eligiblePokemonUrl) {
    this.eligiblePokemonUrl = eligiblePokemonUrl;
  }

  public Schedule getSchedule() {
    return schedule;
  }

  public void setSchedule(Schedule schedule) {
    this.schedule = schedule;
  }



  public int getNumberOfRestricted() {
    return numberOfRestricted;
  }

  public void setNumberOfRestricted(int numberOfRestricted) {
    this.numberOfRestricted = numberOfRestricted;
  }

  public Integer getSetLevel() {
    return setLevel;
  }

  public void setSetLevel(Integer setLevel) {
    this.setLevel = setLevel;
  }

  public boolean isItemsAllowed() {
    return itemsAllowed;
  }

  public void setItemsAllowed(boolean itemsAllowed) {
    this.itemsAllowed = itemsAllowed;
  }

  public boolean isDuplicateItemsAllowed() {
    return duplicateItemsAllowed;
  }

  public void setDuplicateItemsAllowed(boolean duplicateItemsAllowed) {
    this.duplicateItemsAllowed = duplicateItemsAllowed;
  }

  public BattleTimers getBattleTimers() {
    return battleTimers;
  }

  public void setBattleTimers(BattleTimers battleTimers) {
    this.battleTimers = battleTimers;
  }


}
