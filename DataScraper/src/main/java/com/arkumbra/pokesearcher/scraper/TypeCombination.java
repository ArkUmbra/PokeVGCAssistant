package com.arkumbra.pokesearcher.scraper;

import java.util.*;

public class TypeCombination {
  private final List<TypeValue> types;

  public TypeCombination(TypeValue... typeValues) {
    types = new ArrayList<>(new HashSet<>(List.of(typeValues)));
  }

  public List<TypeValue> getTypes() {
    return types;
  }
}
