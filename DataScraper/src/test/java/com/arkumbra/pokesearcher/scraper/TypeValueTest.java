package com.arkumbra.pokesearcher.scraper;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TypeValueTest {

  @Test
  public void testTypeEffectiveness() {
    assertEquals(Effectiveness.superEffective2x, TypeValue.fire.effectivenessAgainst(TypeValue.grass));
    assertEquals(Effectiveness.superEffective4x, TypeValue.fire.effectivenessAgainst(TypeValue.grass, TypeValue.bug));
    assertEquals(Effectiveness.notVeryEffective2, TypeValue.fire.effectivenessAgainst(TypeValue.water));
    assertEquals(Effectiveness.notVeryEffective2, TypeValue.fire.effectivenessAgainst(TypeValue.fire));
    assertEquals(Effectiveness.notVeryEffective4, TypeValue.fire.effectivenessAgainst(TypeValue.fire, TypeValue.water));

    assertEquals(Effectiveness.noEffect, TypeValue.electric.effectivenessAgainst(TypeValue.ground));
    assertEquals(Effectiveness.superEffective2x, TypeValue.electric.effectivenessAgainst(TypeValue.flying));

    assertEquals(Effectiveness.noEffect, TypeValue.normal.effectivenessAgainst(TypeValue.ghost));
    assertEquals(Effectiveness.noEffect, TypeValue.fighting.effectivenessAgainst(TypeValue.ghost));
  }

  @Test
  public void testCompositeWeaknesses() {
    CompositeTypeEffectiveness composite = new CompositeTypeEffectiveness(TypeValue.water, TypeValue.ground);

    assertEquals(Set.of(TypeValue.electric),
        composite.getTypesByEffectiveness(Effectiveness.noEffect));
    assertEquals(Set.of(TypeValue.grass),
        composite.getTypesByEffectiveness(Effectiveness.superEffective4x));
    assertEquals(Set.of(),
        composite.getTypesByEffectiveness(Effectiveness.superEffective2x));
    assertEquals(Set.of(TypeValue.normal, TypeValue.water, TypeValue.ice, TypeValue.fighting, TypeValue.ground, TypeValue.flying, TypeValue.psychic, TypeValue.bug, TypeValue.ghost, TypeValue.dragon, TypeValue.dark, TypeValue.fairy),
        composite.getTypesByEffectiveness(Effectiveness.effective));
    assertEquals(Set.of(TypeValue.fire, TypeValue.poison, TypeValue.rock, TypeValue.steel),
        composite.getTypesByEffectiveness(Effectiveness.notVeryEffective2));
    assertEquals(Set.of(),
        composite.getTypesByEffectiveness(Effectiveness.notVeryEffective4));

    CompositeTypeEffectiveness tripleComposite = new CompositeTypeEffectiveness(TypeValue.ghost, TypeValue.normal, TypeValue.dark);
    assertEquals(Set.of(TypeValue.normal, TypeValue.fighting, TypeValue.ghost, TypeValue.psychic),
        tripleComposite.getTypesByEffectiveness(Effectiveness.noEffect));
  }


  @Test
  public void testFindTypesWithOnlyOneWeakness() {
    for (TypeValue defendingType1 : TypeValue.values()) {
      for (TypeValue defendingType2 : TypeValue.values()) {
        if (defendingType1 == defendingType2) continue;

        CompositeTypeEffectiveness composite = new CompositeTypeEffectiveness(defendingType1, defendingType2);
        Set<TypeValue> se = composite.getTypesByEffectiveness(Effectiveness.superEffective2x);
        Set<TypeValue> dblSe = composite.getTypesByEffectiveness(Effectiveness.superEffective4x);

        if (se.size() + dblSe.size() == 1) {
          System.out.println(composite.toString());
        }
      }
    }
  }

  @Test
  public void testFindTypesWithTheMostWeaknesses() {
    TreeMap<Integer, Set<CompositeTypeEffectiveness>> ranking = new TreeMap();

    for (TypeValue defendingType1 : TypeValue.values()) {
      for (TypeValue defendingType2 : TypeValue.values()) {
        if (defendingType1 == defendingType2) continue;

        CompositeTypeEffectiveness composite = new CompositeTypeEffectiveness(defendingType1, defendingType2);
        Set<TypeValue> se = composite.getTypesByEffectiveness(Effectiveness.superEffective2x);
        Set<TypeValue> dblSe = composite.getTypesByEffectiveness(Effectiveness.superEffective4x);

        int noOfWeaknesses = se.size() + dblSe.size();
        if (! ranking.containsKey(noOfWeaknesses))
          ranking.put(noOfWeaknesses, new HashSet<>());
        ranking.get(noOfWeaknesses).add(composite);
      }
    }

    for (Map.Entry<Integer, Set<CompositeTypeEffectiveness>> items : ranking.subMap(5, 100).entrySet()) {
      System.out.println("Weaknesses " + items.getKey() + " -> " + System.lineSeparator() + "  " +
          items.getValue().stream().map(comp ->
              comp.getCompositeTypes().toString()).collect(Collectors.joining(System.lineSeparator() + "  ")));
    }
  }

}
