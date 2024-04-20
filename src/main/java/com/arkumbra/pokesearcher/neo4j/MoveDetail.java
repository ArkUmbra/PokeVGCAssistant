package com.arkumbra.pokesearcher.neo4j;

import com.arkumbra.pokesearcher.rest.PokemonSummary;
import com.arkumbra.pokesearcher.scraper.Move;

import java.util.List;

public class MoveDetail {

  private final Move move;
  private final List<PokemonSummary> pokemonWithMove;

  public MoveDetail(Move move, List<PokemonSummary> pokemonWithMove) {
    this.move = move;
    this.pokemonWithMove = pokemonWithMove;
  }

  public Move getMove() {
    return move;
  }

  public List<PokemonSummary> getPokemonWithMove() {
    return pokemonWithMove;
  }

}
