package com.arkumbra.pokesearcher.scraper;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Abilities {
  private final List<String> abilityNameId;
  private final String hiddenAbilityNameId;

  public Abilities(List<String> abilityNameId, String hiddenAbilityNameId) {
    this.abilityNameId = abilityNameId;
    this.hiddenAbilityNameId = hiddenAbilityNameId;
  }

  public List<String> getAbilityNameId() {
    return abilityNameId;
  }

  public Optional<String> getHiddenAbilityNameId() {
    return Optional.ofNullable(hiddenAbilityNameId);
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Abilities abilities = (Abilities) o;
    return Objects.equals(abilityNameId, abilities.abilityNameId) && Objects.equals(hiddenAbilityNameId, abilities.hiddenAbilityNameId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(abilityNameId, hiddenAbilityNameId);
  }

  @Override
  public String toString() {
    return "Abilities{" +
        "abilityNameId=" + abilityNameId +
        ", hiddenAbilityNameId='" + hiddenAbilityNameId + '\'' +
        '}';
  }
}
