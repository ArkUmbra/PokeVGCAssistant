package com.arkumbra.pokesearcher.scraper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PokedexNumber {
  Map<PokedexRegion, Integer> dexNumbers = new HashMap<>();

  public PokedexNumber() { }

  public PokedexNumber(int natDexNum) {
    dexNumbers.put(PokedexRegion.National, natDexNum);
  }

  public Integer getNational() {
    return get(PokedexRegion.National);
  }

  public void put(PokedexRegion region, int num) {
    dexNumbers.put(region, num);
  }

  public Integer get(PokedexRegion region) {
    return dexNumbers.get(region);
  }

  public boolean in(PokedexRegion region) {
    return dexNumbers.containsKey(region) && dexNumbers.get(region) != null;
  }

  public int size() {
    return (int)Arrays.stream(PokedexRegion.values()).map(r -> in(r) ? 1 : 0).count();
  }

  @Override
  public String toString() {
    return "Pokedex{" +
        "dexNumbers=" + dexNumbers +
        '}';
  }
}