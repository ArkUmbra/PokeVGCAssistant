package com.arkumbra.pokesearcher.rest;

import com.arkumbra.pokesearcher.scraper.Ability;

import java.util.List;

public class AbilityDetail {
  private final Ability ability;
  private final List<PokemonSummary> pokemonWithAbility;
  private final List<PokemonSummary> pokemonWithHiddenAbility;

  public AbilityDetail(Ability ability, List<PokemonSummary> pokemonWithAbility, List<PokemonSummary> pokemonWithHiddenAbility) {
    this.ability = ability;
    this.pokemonWithAbility = pokemonWithAbility;
    this.pokemonWithHiddenAbility = pokemonWithHiddenAbility;
  }

  public Ability getAbility() {
    return ability;
  }

  public List<PokemonSummary> getPokemonWithAbility() {
    return pokemonWithAbility;
  }

  public List<PokemonSummary> getPokemonWithHiddenAbility() {
    return pokemonWithHiddenAbility;
  }
}
