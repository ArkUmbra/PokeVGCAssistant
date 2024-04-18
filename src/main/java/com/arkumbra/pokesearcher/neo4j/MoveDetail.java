package com.arkumbra.pokesearcher.neo4j;

import com.arkumbra.pokesearcher.scraper.Move;

public class MoveDetail {

  private final Move move;

  public MoveDetail(Move move) {
    this.move = move;
  }

  public Move getMove() {
    return move;
  }

}
