package com.arkumbra.pokesearcher.scraper;

import java.util.HashMap;
import java.util.Map;

public class Pokedex {
  Map<String, Integer> dexNumbers = new HashMap<String, Integer>();

  @Override
  public String toString() {
    return "Pokedex{" +
        "dexNumbers=" + dexNumbers +
        '}';
  }
}