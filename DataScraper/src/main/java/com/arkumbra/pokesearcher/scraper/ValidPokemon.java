package com.arkumbra.pokesearcher.scraper;

import java.util.*;

public class ValidPokemon {
  private final Map<PokedexRegion, List<IntRange>> ranges = new HashMap<>();

  public ValidPokemon() {
  }

  public ValidPokemon put(PokedexRegion region, int pokedexNumber) {
    return put(region, new IntRange(pokedexNumber, pokedexNumber));
  }

  public ValidPokemon put(PokedexRegion region, IntRange pokedexNumberRange) {
    ranges.computeIfAbsent(region, (a) -> new ArrayList<>())
        .add(pokedexNumberRange);
    return this;
  }

  public ValidPokemon replace(PokedexRegion region, List<IntRange> pokedexNumberRanges) {
    ranges.put(region, pokedexNumberRanges);
    return this;
  }

  public Map<PokedexRegion, List<IntRange>> getRanges() {
    return ranges;
  }

  public List<IntRange> getBy(PokedexRegion region) {
    return ranges.getOrDefault(region, new ArrayList<>());
  }

  public List<Integer> getToIntegerList(PokedexRegion region) {
    List<Integer> ints = new ArrayList<>();

    getBy(region).forEach(intRange -> {
      for (int i = intRange.getLeftBoundInclusive(); i <= intRange.getRightBoundInclusive(); i++)
        ints.add(i);
    });

    return ints;
  }

  public void removeRange(PokedexRegion region, IntRange pokedexNumberRange) {
    LinkedList<IntRange> recreated = new LinkedList<>();


    for (IntRange intRange : ranges.get(region)) {
      if (intRange.overlaps(pokedexNumberRange)) {
        recreated.addAll(intRange.split(pokedexNumberRange));
      }
    }

    ranges.put(region, recreated);
  }
}
