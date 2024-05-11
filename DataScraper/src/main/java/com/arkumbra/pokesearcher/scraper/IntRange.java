package com.arkumbra.pokesearcher.scraper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

  public void setRightBoundInclusive(int rightBoundInclusive) {
    this.rightBoundInclusive = rightBoundInclusive;
  }

  public boolean includes(int number) {
    return leftBoundInclusive <= number && rightBoundInclusive >= number;
  }

  public boolean overlaps(IntRange intRange) {
    return includes(intRange.leftBoundInclusive) || includes(intRange.rightBoundInclusive) ||
        intRange.includes(this.leftBoundInclusive) || intRange.includes(this.rightBoundInclusive);
  }

  public List<IntRange> split(IntRange intRange) {
    List<IntRange> result = new ArrayList<>();

    if (intRange.leftBoundInclusive <= this.leftBoundInclusive) {
      if (intRange.rightBoundInclusive >= this.rightBoundInclusive) {
        // completely covers this, so this is deleted, do nothing
      } else {
        result.add(new IntRange(intRange.rightBoundInclusive + 1, this.rightBoundInclusive));
      }
    } else {
      if (intRange.rightBoundInclusive >= this.rightBoundInclusive) {
        // just take left side
        result.add(new IntRange(this.leftBoundInclusive, intRange.leftBoundInclusive - 1));
      } else {
        // int range sits in the middle of this, so split either side of it
        result.add(new IntRange(this.leftBoundInclusive, intRange.leftBoundInclusive - 1));
        result.add(new IntRange(intRange.rightBoundInclusive + 1, this.rightBoundInclusive));
      }
    }

    return result;
  }

  @Override
  public String toString() {
    return "IntRange{" +
        leftBoundInclusive +
        " ~ " +
        rightBoundInclusive +
        "}";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    IntRange intRange = (IntRange) o;
    return leftBoundInclusive == intRange.leftBoundInclusive && rightBoundInclusive == intRange.rightBoundInclusive;
  }

  @Override
  public int hashCode() {
    return Objects.hash(leftBoundInclusive, rightBoundInclusive);
  }
}
