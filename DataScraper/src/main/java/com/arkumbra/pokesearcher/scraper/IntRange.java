package com.arkumbra.pokesearcher.scraper;

public class IntRange {
  private int leftBoundInclusive;
  private int rightBoundInclusive;

  public IntRange(int leftBoundInclusive, int rightBoundInclusive) {
    this.leftBoundInclusive = leftBoundInclusive;
    this.rightBoundInclusive = rightBoundInclusive;
  }

  public int getLeftBoundInclusive() {
    return leftBoundInclusive;
  }

  public int getRightBoundInclusive() {
    return rightBoundInclusive;
  }
}
