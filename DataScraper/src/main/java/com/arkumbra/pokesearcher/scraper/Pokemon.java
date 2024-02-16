package com.arkumbra.pokesearcher.scraper;

import java.util.Set;

public class Pokemon {
  String idName;
  Name name;
  Pokedex pokedex;
  Set<TypeValue> types;
  Moveset moveset;

  @Override
  public String toString() {
    return name + System.lineSeparator() +
        "Type ->" + System.lineSeparator() + types + System.lineSeparator() +
        "Pokedex -> " + System.lineSeparator() + pokedex + System.lineSeparator() +
        "Moveset -> " + System.lineSeparator() + moveset
        ;
  }
}
