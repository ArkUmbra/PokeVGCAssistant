package com.arkumbra.pokesearcher.rest;

import com.arkumbra.pokesearcher.scraper.BaseStats;
import com.arkumbra.pokesearcher.scraper.Name;
import com.arkumbra.pokesearcher.scraper.PokedexNumber;
import com.arkumbra.pokesearcher.scraper.TypeValue;

import java.util.Collection;

public class PokemonSummary {
  private final String idName;
  private final Name name;
  private final String formName;
  private final Collection<TypeValue> types;
  private final BaseStats baseStats;
  private final int nationalDexNumber;

  public PokemonSummary(
      String idName, Name name, String formName,
      Collection<TypeValue> types, BaseStats baseStats,
      int nationalDexNumber) {

    this.idName = idName;
    this.name = name;
    this.formName = formName;
    this.types = types;
    this.baseStats = baseStats;
    this.nationalDexNumber = nationalDexNumber;
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

  public int getNationalDexNumber() {
    return nationalDexNumber;
  }
}
