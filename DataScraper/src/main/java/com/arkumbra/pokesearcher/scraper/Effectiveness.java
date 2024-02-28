package com.arkumbra.pokesearcher.scraper;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public enum Effectiveness {
  // Special considerations for zero damage effectiveness so we move its rank outside of the operations
  // of increase/decrease
  noEffect("0", 99),

  superEffective4x("4", 2),
  superEffective2x("2", 1),
  effective("1", 0),
  notVeryEffective2("0.5", -1),
  notVeryEffective4("0.25", -2)
  ;

  private final BigDecimal effectiveness;
  private final int rank;
  private static final Map<Integer, Effectiveness> rankedStates = new HashMap<>();

  static {
    for (Effectiveness value : Effectiveness.values()) {
      rankedStates.put(value.rank, value);
    }
  }

  Effectiveness(String effectiveness, int rank) {
    this.effectiveness = new BigDecimal(effectiveness);
    this.rank = rank;
  }

  public BigDecimal getEffectiveness() {
    return effectiveness;
  }

  public Effectiveness increase() {
    return rankedStates.get(rank + 1);
  }

  public Effectiveness decrease() {
    return rankedStates.get(rank - 1);
  }

  public Effectiveness merge(Effectiveness that) {
    if (this == noEffect || that == noEffect) {
      return Effectiveness.noEffect;
    }

    if (that.rank > 0)
      return increase();
    else
      return decrease();
  }

}
