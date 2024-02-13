package com.arkumbra.pokesearcher.scraper;

public class Pair<L, R> {
  L left;
  R right;

  public Pair(L left, R right) {
    this.left = left;
    this.right = right;
  }

  @Override
  public String toString() {
    return "{" + left +
        ", " + right +
        "}";
  }
}