package com.arkumbra.pokesearcher.scraper.model;

import com.arkumbra.pokesearcher.scraper.Ability;
import com.arkumbra.pokesearcher.scraper.Pokemon;
import com.arkumbra.pokesearcher.scraper.Game;

import java.util.List;

public class Pokedex {
  private final Game game;
  private final List<Pokemon> allPokemon;
  private final List<Ability> allAbilities;

  public Pokedex(Game game, List<Pokemon> allPokemon, List<Ability> allAbilities) {
    this.game = game;
    this.allPokemon = allPokemon;
    this.allAbilities = allAbilities;
  }

  public Game getGame() {
    return game;
  }

  public List<Pokemon> getAllPokemon() {
    return allPokemon;
  }

  public List<Ability> getAllAbilities() {
    return allAbilities;
  }
}
