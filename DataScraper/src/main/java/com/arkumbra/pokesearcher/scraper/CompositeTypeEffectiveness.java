package com.arkumbra.pokesearcher.scraper;

import java.util.*;

public class CompositeTypeEffectiveness {

  private final Map<Effectiveness, Set<TypeValue>> effectivenessWhenAttackedBy = new HashMap<>();
  private final Map<TypeValue, Effectiveness> byType = new HashMap<>();
  private final Set<TypeValue> types;

  public CompositeTypeEffectiveness(TypeValue... theseTypes) {
    types = Set.of(theseTypes);
    for (TypeValue value : TypeValue.values())
      byType.put(value, Effectiveness.effective);

    for (TypeValue type : theseTypes) {
      TypeEffectiveness typeEffectiveness = TypeEffectiveness.of(type);

      for (TypeValue immunity : typeEffectiveness.getImmunities()) {
        byType.put(immunity, Effectiveness.noEffect);
      }

      for (TypeValue resistance : typeEffectiveness.getResistances()) {
        Effectiveness currentCalculated = byType.remove(resistance);
        byType.put(resistance, currentCalculated == Effectiveness.noEffect ?
            currentCalculated : currentCalculated.decrease());
      }

      for (TypeValue weakness : typeEffectiveness.getWeaknesses()) {
        Effectiveness currentCalculated = byType.remove(weakness);
        byType.put(weakness, currentCalculated == Effectiveness.noEffect ?
            currentCalculated : currentCalculated.increase());
      }
    }

    for (Effectiveness effectiveness : Effectiveness.values()) {
      effectivenessWhenAttackedBy.put(effectiveness, new HashSet<>());
    }

    byType.forEach((typeValue, effectiveness) -> {
      effectivenessWhenAttackedBy.get(effectiveness).add(typeValue);
    });

  }

  public Set<TypeValue> getTypesByEffectiveness(Effectiveness effectiveness) {
    return effectivenessWhenAttackedBy.get(effectiveness);
  }

  public Map<TypeValue, Effectiveness> getEffectivenessByType() {
    return byType;
  }

  public Set<TypeValue> getCompositeTypes() {
    return types;
  }

  @Override
  public String toString() {
    return "CompositeTypeEffectiveness{" +
        "types=" + types +
        ", byType=" + byType +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CompositeTypeEffectiveness that = (CompositeTypeEffectiveness) o;
    return Objects.equals(types, that.types);
  }

  @Override
  public int hashCode() {
    return Objects.hash(types);
  }
}
