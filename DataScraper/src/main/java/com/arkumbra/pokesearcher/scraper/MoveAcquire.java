package com.arkumbra.pokesearcher.scraper;

public class MoveAcquire {
  MoveAcquireType acquireType;
  int numberValue;

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
      return "" + numberValue;
    } else if (acquireType == MoveAcquireType.TM) {
      return "TM" + numberValue;
    } else {
      return acquireType.toString();
    }
  }
}
