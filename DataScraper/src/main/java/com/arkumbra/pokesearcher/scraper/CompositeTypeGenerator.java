package com.arkumbra.pokesearcher.scraper;

import java.util.ArrayList;
import java.util.List;
public class CompositeTypeGenerator {

  public List<CompositeTypeEffectiveness> generateEffectivenessForAllTypes() {
    List<CompositeTypeEffectiveness> results = new ArrayList<>();

    for (TypeValue defendingType1 : TypeValue.values()) {
      for (TypeValue defendingType2 : TypeValue.values()) {
        CompositeTypeEffectiveness composite = new CompositeTypeEffectiveness(new TypeCombination(defendingType1, defendingType2));
        results.add(composite);
      }
    }

    return results;
  }
}
