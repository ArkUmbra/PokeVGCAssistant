package com.arkumbra.pokesearcher.scraper;

import java.util.*;

public class RegulationValidPokemon {
  private final List<IntRange> ranges = new ArrayList<>();

  public RegulationValidPokemon put(int pokedexNumber) {
    return put(new IntRange(pokedexNumber, pokedexNumber));
  }

  public RegulationValidPokemon put(IntRange pokedexNumberRange) {
    ranges.add(pokedexNumberRange);
    return this;
  }


  public List<Integer> getToIntegerList() {
    List<Integer> ints = new ArrayList<>();

    ranges.forEach(intRange -> {
      for (int i = intRange.getLeftBoundInclusive(); i <= intRange.getRightBoundInclusive(); i++)
        ints.add(i);
    });

    return ints;
  }

}
