package com.arkumbra.pokesearcher.scraper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidPokemon {
  private Map<PokedexRegion, List<IntRange>> ranges = new HashMap<>();

  public void put(PokedexRegion region, IntRange pokedexNumberRange) {
    ranges.computeIfAbsent(region, (a) -> new ArrayList<>()).add(pokedexNumberRange);
  }

  public Map<PokedexRegion, List<IntRange>> getRanges() {
    return ranges;
  }
}
