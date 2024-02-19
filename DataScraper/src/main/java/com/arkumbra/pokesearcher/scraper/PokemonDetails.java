package com.arkumbra.pokesearcher.scraper;

import java.util.LinkedHashSet;
import java.util.Set;

public class PokemonDetails {
  private Name name;
  private Pokedex pokedex;
  private LinkedHashSet<TypeValue> types;
  private Moveset moveset;
  private String formRegion;

  @Override
  public String toString() {
    return "PokemonDetails{" +
        "name=" + name +
        ", pokedex=" + pokedex +
        ", types=" + types +
        ", moveset=" + moveset +
        ", formRegion='" + formRegion + '\'' +
        '}';
  }

  public Name getName() {
    return name;
  }

  public void setName(Name name) {
    this.name = name;
  }

  public Pokedex getPokedex() {
    return pokedex;
  }

  public void setPokedex(Pokedex pokedex) {
    this.pokedex = pokedex;
  }

  public LinkedHashSet<TypeValue> getTypes() {
    return types;
  }

  public void setTypes(LinkedHashSet<TypeValue> types) {
    this.types = types;
  }

  public Moveset getMoveset() {
    return moveset;
  }

  public void setMoveset(Moveset moveset) {
    this.moveset = moveset;
  }

  public String getFormRegion() {
    return formRegion;
  }

  public void setFormRegion(String formRegion) {
    this.formRegion = formRegion;
  }
}
