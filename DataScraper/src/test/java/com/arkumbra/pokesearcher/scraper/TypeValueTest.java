package com.arkumbra.pokesearcher.scraper;

import org.junit.jupiter.api.Test;

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
}
