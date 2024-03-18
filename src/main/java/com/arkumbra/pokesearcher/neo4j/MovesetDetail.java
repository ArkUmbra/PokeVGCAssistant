package com.arkumbra.pokesearcher.neo4j;

import com.arkumbra.pokesearcher.scraper.Move;
import com.arkumbra.pokesearcher.scraper.MoveAcquire;
import com.arkumbra.pokesearcher.scraper.Pair;

import java.util.List;

public class MovesetDetail {
  private final List<Pair<MoveAcquire, Move>> levelUpMoves;
  private final List<Pair<MoveAcquire, Move>> tmMoves;
  private final List<Move> eggMoves;

  public MovesetDetail(
      List<Pair<MoveAcquire, Move>> levelUpMoves,
      List<Pair<MoveAcquire, Move>> tmMoves,
      List<Move> eggMoves) {

    this.levelUpMoves = levelUpMoves;
    this.tmMoves = tmMoves;
    this.eggMoves = eggMoves;
  }

  public List<Pair<MoveAcquire, Move>> getLevelUpMoves() {
    return levelUpMoves;
  }

  public List<Pair<MoveAcquire, Move>> getTmMoves() {
    return tmMoves;
  }

  public List<Move> getEggMoves() {
    return eggMoves;
  }
}
