package com.arkumbra.pokesearcher.scraper;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Abilities {
  private final List<String> standardAbilityNameIds;
  private final String hiddenAbilityNameId;
  private final String specialAbilityNameId;

  public Abilities(List<String> standardAbilityNameIds, String hiddenAbilityNameId) {
    this.standardAbilityNameIds = standardAbilityNameIds;
    this.hiddenAbilityNameId = hiddenAbilityNameId;
    this.specialAbilityNameId = null;
  }

  public Abilities(List<String> standardAbilityNameIds, String hiddenAbilityNameId, String specialAbilityNameId) {
    this.standardAbilityNameIds = standardAbilityNameIds;
    this.hiddenAbilityNameId = hiddenAbilityNameId;
    this.specialAbilityNameId = specialAbilityNameId;
  }

  public List<String> getStandardAbilityNameIds() {
    return standardAbilityNameIds;
  }

  public Optional<String> getHiddenAbilityNameId() {
    return Optional.ofNullable(hiddenAbilityNameId);
  }

  public Optional<String> getSpecialAbilityNameId() {
    return Optional.ofNullable(specialAbilityNameId);
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Abilities abilities = (Abilities) o;
    return Objects.equals(standardAbilityNameIds, abilities.standardAbilityNameIds) &&
        Objects.equals(hiddenAbilityNameId, abilities.hiddenAbilityNameId) &&
        Objects.equals(specialAbilityNameId, abilities.specialAbilityNameId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(standardAbilityNameIds, hiddenAbilityNameId, specialAbilityNameId);
  }

  @Override
  public String toString() {
    return "Abilities{" +
        "abilityNameId=" + standardAbilityNameIds +
        ", hiddenAbilityNameId='" + hiddenAbilityNameId + '\'' +
        ", specialAbilityNameId='" + specialAbilityNameId + '\'' +
        '}';
  }
}
