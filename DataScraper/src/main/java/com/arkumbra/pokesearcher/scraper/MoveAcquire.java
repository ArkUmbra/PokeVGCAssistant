package com.arkumbra.pokesearcher.scraper;

import java.util.Objects;

public class MoveAcquire {
  MoveAcquireType acquireType;
  int numberValue;

  private MoveAcquire() {}

  public MoveAcquire(MoveAcquireType acquireType) {
    this.acquireType = acquireType;
  }

  public MoveAcquire(MoveAcquireType acquireType, int numberValue) {
    this.acquireType = acquireType;
    this.numberValue = numberValue;
  }

  public static MoveAcquire of(String value) {
    MoveAcquire moveAcquire = new MoveAcquire();
    if (value.equals("Evolve")) {
      moveAcquire.acquireType = MoveAcquireType.Evolve;
    } else if (value.equals("—")) {
      moveAcquire.acquireType = MoveAcquireType.Relearner;
    } else if (value.startsWith("TM")) {
      moveAcquire.acquireType = MoveAcquireType.TM;
      moveAcquire.numberValue = Integer.parseInt(value.split("TM")[1]);
    } else {
      moveAcquire.acquireType = MoveAcquireType.Level;
      moveAcquire.numberValue = Integer.parseInt(value);
    }

    return moveAcquire;
  }

  @Override
  public String toString() {
    if (acquireType == MoveAcquireType.Level) {
      return
          ("0".repeat(3 - String.valueOf(numberValue).length()))
          + numberValue;
    } else if (acquireType == MoveAcquireType.TM) {
      return "TM" +
          ("0".repeat(3 - String.valueOf(numberValue).length()))
          + numberValue;
    } else {
      return acquireType.toString();
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MoveAcquire that = (MoveAcquire) o;
    return numberValue == that.numberValue && acquireType == that.acquireType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(acquireType, numberValue);
  }
}
