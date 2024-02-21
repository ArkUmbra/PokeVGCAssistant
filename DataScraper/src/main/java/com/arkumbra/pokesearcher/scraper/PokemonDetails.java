package com.arkumbra.pokesearcher.scraper;

import java.util.LinkedHashSet;

public class PokemonDetails {
  private LinkedHashSet<TypeValue> types;
  private BaseStats baseStats;
  private Moveset moveset;
  private String formName;

  @Override
  public String toString() {
    return "PokemonDetails{" +
        "types=" + types +
        ", baseStats=" + baseStats +
        ", moveset=" + moveset +
        ", formName='" + formName + '\'' +
        '}';
  }

  public LinkedHashSet<TypeValue> getTypes() {
    return types;
  }

  public void setTypes(LinkedHashSet<TypeValue> types) {
    this.types = types;
  }

  public BaseStats getBaseStats() {
    return baseStats;
  }

  public void setBaseStats(BaseStats baseStats) {
    this.baseStats = baseStats;
  }

  public Moveset getMoveset() {
    return moveset;
  }

  public void setMoveset(Moveset moveset) {
    this.moveset = moveset;
  }

  public String getFormName() {
    return formName;
  }

  public void setFormName(String formName) {
    this.formName = formName;
  }
}
