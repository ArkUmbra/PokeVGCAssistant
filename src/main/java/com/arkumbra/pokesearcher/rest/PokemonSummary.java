package com.arkumbra.pokesearcher.rest;

import com.arkumbra.pokesearcher.scraper.BaseStats;
import com.arkumbra.pokesearcher.scraper.Name;
import com.arkumbra.pokesearcher.scraper.TypeValue;

import java.util.Collection;
import java.util.LinkedHashSet;

public class PokemonSummary {
  private final String idName;
  private final Name name;
  private final String formName;
  private final Collection<TypeValue> types;
  private final BaseStats baseStats;

  public PokemonSummary(String idName, Name name, String formName, Collection<TypeValue> types, BaseStats baseStats) {
    this.idName = idName;
    this.name = name;
    this.formName = formName;
    this.types = types;
    this.baseStats = baseStats;
  }

  public String getIdName() {
    return idName;
  }

  public Name getName() {
    return name;
  }

  public String getFormName() {
    return formName;
  }

  public Collection<TypeValue> getTypes() {
    return types;
  }

  public BaseStats getBaseStats() {
    return baseStats;
  }
}
