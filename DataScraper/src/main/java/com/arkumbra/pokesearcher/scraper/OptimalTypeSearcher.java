package com.arkumbra.pokesearcher.scraper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class OptimalTypeSearcher {

  public List<Pair<TypeValue, TypeValue>> findTypesWhichResist(TypeValue... attackingTypes) {
    List<CompositeTypeEffectiveness> compositeTypes = new CompositeTypeGenerator().generateEffectivenessForAllTypes();
    List<Pair<TypeValue, TypeValue>> results = new ArrayList<>();

    for (CompositeTypeEffectiveness compositeType : compositeTypes) {
      if (Arrays.stream(attackingTypes).allMatch(compositeType::takesReducedDamageFrom)) {
        List<TypeValue> types = compositeType.getCompositeTypes().getTypes();
        results.add(new Pair<>(types.get(0), types.get(1)));
      }
    }

    return results;
  }
}
