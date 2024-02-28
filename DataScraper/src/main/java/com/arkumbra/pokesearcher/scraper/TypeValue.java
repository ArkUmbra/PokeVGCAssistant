package com.arkumbra.pokesearcher.scraper;

public enum TypeValue {
  grass,
  water,
  fire,
  electric,
  normal,
  fighting,
  ice,
  psychic,
  ground,
  rock,
  steel,
  dragon,
  dark,
  fairy,
  poison,
  ghost,
  flying,
  bug
  ;

  public Effectiveness effectivenessAgainst(TypeValue... opposingTypes) {
    return TypeEffectiveness.of(this).against(opposingTypes);
  }

}