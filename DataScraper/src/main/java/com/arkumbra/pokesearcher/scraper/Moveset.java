package com.arkumbra.pokesearcher.scraper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Moveset {
  private final List<Pair<MoveAcquire, String>> levelUpMoves = new ArrayList<>();
  private final List<Pair<MoveAcquire, String>> tmMoves = new ArrayList<>();
  private final List<String> eggMoves = new ArrayList<>();

  @Override
  public String toString() {
    return "LEVEL UP -> " + System.lineSeparator() +
        levelUpMoves.stream().map(pair -> pair.toString() + System.lineSeparator()).collect(Collectors.joining()) +
        System.lineSeparator() + "TMs -> " + System.lineSeparator() +
        tmMoves.stream().map(pair -> pair.toString() + System.lineSeparator()).collect(Collectors.joining()) +
        System.lineSeparator() + "EGG MOVES -> " + System.lineSeparator() +
        eggMoves.stream().map(s -> s + System.lineSeparator()).collect(Collectors.joining())
        ;
  }

  public void addLevelUpMoves(List<Pair<MoveAcquire, String>> levelUpMoves) {
    this.levelUpMoves.addAll(levelUpMoves);
  }

  public void addTmMoves(List<Pair<MoveAcquire, String>> tmMoves) {
    this.tmMoves.addAll(tmMoves);
  }

  public void addEggMoves(List<String> eggMoves) {
    this.eggMoves.addAll(eggMoves);
  }

  public List<Pair<MoveAcquire, String>> getLevelUpMoves() {
    return levelUpMoves;
  }

  public List<Pair<MoveAcquire, String>> getTmMoves() {
    return tmMoves;
  }

  public List<String> getEggMoves() {
    return eggMoves;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Moveset moveset = (Moveset) o;
    return Objects.equals(levelUpMoves, moveset.levelUpMoves) && Objects.equals(tmMoves, moveset.tmMoves) && Objects.equals(eggMoves, moveset.eggMoves);
  }

  @Override
  public int hashCode() {
    return Objects.hash(levelUpMoves, tmMoves, eggMoves);
  }
}
