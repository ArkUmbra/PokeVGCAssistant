package com.arkumbra.pokesearcher.scraper;

import java.util.Objects;

public class EffectChance {

  public static final EffectChance NONE = new EffectChance(0);

  private Integer chance;

  public EffectChance(Integer chance) {
    this.chance = chance;
  }

  public boolean hasChance() {
    return chance != null;
  }

  public Integer getChance() {
    return chance;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    EffectChance that = (EffectChance) o;
    return Objects.equals(chance, that.chance);
  }

  @Override
  public int hashCode() {
    return Objects.hash(chance);
  }
}
