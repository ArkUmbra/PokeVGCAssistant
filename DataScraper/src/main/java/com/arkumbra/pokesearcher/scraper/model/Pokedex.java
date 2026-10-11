package com.arkumbra.pokesearcher.scraper.model;

import com.arkumbra.pokesearcher.scraper.Pokemon;
import com.arkumbra.pokesearcher.scraper.Game;

import java.util.List;

public class Pokedex {
  private final Game game;
  private final List<Pokemon> allPokemon;

  public Pokedex(Game game, List<Pokemon> allPokemon) {
    this.game = game;
    this.allPokemon = allPokemon;
  }

  public Game getGame() {
    return game;
  }

  public List<Pokemon> getAllPokemon() {
    return allPokemon;
  }

}
