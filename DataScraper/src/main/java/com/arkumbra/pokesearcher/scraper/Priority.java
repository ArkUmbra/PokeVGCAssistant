package com.arkumbra.pokesearcher.scraper;

public enum Priority {
  Standard(0),
  PlusOne(1),
  PlusTwo(2),
  PlusThree(3),
  PlusFour(4),
  PlusFive(5),
  MinusOne(-1),
  MinusTwo(-2),
  MinusThree(-3),
  MinusFour(-4),
  MinusFive(-5),
  MinusSix(-6),
  MinusSeven(-7)
  ;

  private int priority;

  Priority(int priority) {
    this.priority = priority;
  }

  public static Priority ofNumber(int value) {
    for (Priority priority : Priority.values()) {
      if (priority.priority == value)
        return priority;
    }

    throw new RuntimeException("Unsupported priority value provided [" + value + "]");
  }
}
