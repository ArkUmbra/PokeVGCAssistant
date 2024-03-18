package com.arkumbra.pokesearcher.controller;

import com.arkumbra.pokesearcher.neo4j.AbilitiesDetail;
import com.arkumbra.pokesearcher.neo4j.MovesetDetail;
import com.arkumbra.pokesearcher.rest.OtherFormsSummary;
import com.arkumbra.pokesearcher.scraper.*;

import java.util.Collection;

public class PokemonDetail {

  private final String pokemonId;
  private final Name name;
  private final Collection<TypeValue> types;
  private final BaseStats baseStats;
  private final String formName;
  private final OtherFormsSummary otherForms;
  private final AbilitiesDetail abilities;
  private final MovesetDetail moveset;
  private final PokedexNumber pokedexNumber;

  public PokemonDetail(String pokemonId, Name name, Collection<TypeValue> types,
                       BaseStats baseStats, String formName,
                       OtherFormsSummary otherForms, AbilitiesDetail abilities,
                       MovesetDetail moveset, PokedexNumber pokedexNumber) {
    this.pokemonId = pokemonId;
    this.name = name;
    this.types = types;
    this.baseStats = baseStats;
    this.formName = formName;
    this.otherForms = otherForms;
    this.abilities = abilities;
    this.moveset = moveset;
    this.pokedexNumber = pokedexNumber;
  }

  public String getPokemonId() {
    return pokemonId;
  }

  public Name getName() {
    return name;
  }

  public Collection<TypeValue> getTypes() {
    return types;
  }

  public BaseStats getBaseStats() {
    return baseStats;
  }

  public String getFormName() {
    return formName;
  }

  public OtherFormsSummary getOtherForms() {
    return otherForms;
  }

  public AbilitiesDetail getAbilities() {
    return abilities;
  }

  public MovesetDetail getMoveset() {
    return moveset;
  }

  public PokedexNumber getPokedexNumber() {
    return pokedexNumber;
  }
}
